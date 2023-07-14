package com.easycode8.easylog.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.easycode8.easylog.core.LogHolder;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.mybatis.util.MybatisUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MybatisPlusDataSnapshotHandler implements DataSnapshotHandler{

    @Override
    public boolean supports(Class mapperClass, Method method) {
        LogInfo logInfo = LogHolder.peek();
        // 如果没开启日志上下文,忽略数据快照处理
        // TODO 操作对象暴露出去让外部决定是否要执行快照记录
//        Class entityClass = GenericTypeUtils.getGenericParameterType(mapperClass, BaseMapper.class);
        return logInfo != null;
    }

    @Override
    public void handle(MappedStatement mappedStatement, BoundSql boundSql, List<Object> originalRecords) throws IllegalAccessException {
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        LogInfo logInfo = LogHolder.peek();
        if (sqlCommandType == SqlCommandType.DELETE) {
            if (StringUtils.isEmpty(logInfo.getDataSnapshot())) {
                logInfo.setDataSnapshot("deleted:" +JSON.toJSONString(originalRecords));
            } else {
                logInfo.setDataSnapshot(logInfo.getDataSnapshot() + ";" + "deleted:" + JSON.toJSONString(originalRecords));
            }
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            // 获取mapper的实体类型
            Class entityClass = MybatisUtils.getEntityClassByMapper(mappedStatement, boundSql);
            // 获取实体对象
            Object entityObject = MybatisUtils.getEntityObjectByMapper(mappedStatement, boundSql);
//            LOGGER.debug("mapper的实体类型:{}", entityClass);
//            LOGGER.debug("mapper的实体对象:{}", entityObject);
            List<String> dataSnapshot = new ArrayList<>();
            for (Object entity : originalRecords) {
                dataSnapshot.add(JSON.toJSONString(MybatisUtils.compareTowObject(entity, entityObject)));
            }
//            LOGGER.info("[{}] data after update:{}", title, dataSnapshot);

            if (StringUtils.isEmpty(logInfo.getDataSnapshot())) {
                logInfo.setDataSnapshot("updated:" + Strings.join(dataSnapshot, ','));
            } else {
                logInfo.setDataSnapshot(logInfo.getDataSnapshot() + ";" + "updated:" + Strings.join(dataSnapshot, ','));
            }
        }
    }
}
