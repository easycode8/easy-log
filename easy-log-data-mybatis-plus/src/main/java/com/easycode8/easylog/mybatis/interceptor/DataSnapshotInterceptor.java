package com.easycode8.easylog.mybatis.interceptor;


import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.util.LogUtils;
import com.easycode8.easylog.mybatis.handler.DataSnapshotHandler;
import com.easycode8.easylog.mybatis.util.CamelCaseUtils;
import com.easycode8.easylog.mybatis.util.MybatisUtils;
import com.easycode8.easylog.mybatis.util.SqlUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class DataSnapshotInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSnapshotInterceptor.class);

    private final JdbcTemplate jdbcTemplate;
    private final DataSnapshotHandler dataSnapshotHandler;

    public DataSnapshotInterceptor(JdbcTemplate jdbcTemplate, DataSnapshotHandler dataSnapshotHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSnapshotHandler = dataSnapshotHandler;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String sqlId = mappedStatement.getId();
        // 获取mapper接口及方法
        String mapperClassName = sqlId.substring(0, sqlId.lastIndexOf("."));
        Class<?> mapperClass = Class.forName(mapperClassName);

        Method method = MybatisUtils.getMethodBySqlId(sqlId);
        // 判断当前方法是否支持
        if (!dataSnapshotHandler.supports(mapperClass, method)) {
            return invocation.proceed();
        }
        // 解析原始sql
        String title = LogUtils.createDefaultTitle(method, mapperClass);
        Configuration configuration = mappedStatement.getConfiguration();

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取删除或修改前的记录信息
        if (sqlCommandType == SqlCommandType.UPDATE || sqlCommandType == SqlCommandType.DELETE) {
            try {
                String realSql = MybatisUtils.showSql(configuration, boundSql);
                LOGGER.info("[easy-log][{}] sql ==> {}", title, realSql);
                Class entityClass = MybatisUtils.getEntityClassByMapper(mappedStatement, boundSql);
                List originalRecords = getOriginalRecords(sqlCommandType, realSql, entityClass);
                LOGGER.info("[easy-log][{}] original data <== {}", title, JSON.toJSONString(originalRecords));
                dataSnapshotHandler.handle(mappedStatement, boundSql, originalRecords);
            } catch (Exception ex) {
                // 记录数据快照不要影响业务执行
                LOGGER.warn("[easy-log][{}] record DataSnapshot failure cause:{}", title, ex.getMessage(), ex);
            }

        }
        return invocation.proceed();
    }

    /**
     * 根据原始sql 构造修改活着删除前的数据快照
     * @param sqlCommandType
     * @param realSql
     * @return
     */
    private List getOriginalRecords(SqlCommandType sqlCommandType, String realSql, Class entityClass) {
        // 获取删除或修改前的记录信息
        String selectSql = "";
        if (sqlCommandType == SqlCommandType.DELETE) {
            selectSql = SqlUtils.convertDeleteToSelect(realSql);
            List<Map<String, Object>> data = jdbcTemplate.queryForList(selectSql);
            if (CollectionUtils.isEmpty(data)) {
                return new ArrayList<>();
            }
            for (Map<String, Object> map : data) {
                Set<String> keySet = new HashSet<String>(map.keySet());
                keySet.forEach(key->{
                    Object value = map.get(key);
                    map.remove(key);
                    LOGGER.debug(key + " => " + CamelCaseUtils.toCamelCase(key));
                    map.put(CamelCaseUtils.toCamelCase(key), value);
                });
            }
            return data;

        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            selectSql = SqlUtils.convertUpdateToSelect(realSql);
            LOGGER.debug("[easy-log] select original data:{}", selectSql);
            // sql解析失败则放弃取历史值。
            if (StringUtils.isEmpty(selectSql)) {
                return new ArrayList<>();
            }

            return jdbcTemplate.query(selectSql, new CustomObjectRowMapper(entityClass));
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    static class CustomObjectRowMapper<T> extends BeanPropertyRowMapper<T> {

        public CustomObjectRowMapper(Class<T> targetType) {
            super(targetType);
            this.setPrimitivesDefaultedForNullValue(true);
        }

        @Override
        protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
            //System.out.println(pd.getName() + " " + pd.getPropertyType() + " " + super.getColumnValue(rs, index, pd));
            if (!BeanUtils.isSimpleProperty(pd.getPropertyType())) {

                return null;
            }
            return super.getColumnValue(rs, index, pd);
        }
    }
}
