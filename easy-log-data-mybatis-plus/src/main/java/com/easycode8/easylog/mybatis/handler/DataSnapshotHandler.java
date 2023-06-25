package com.easycode8.easylog.mybatis.handler;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 数据快照处理器
 */
public interface DataSnapshotHandler {

    boolean supports(Class mapperClass, Method method);

    void handle(MappedStatement mappedStatement, BoundSql boundSql, List<Map<String, Object>> originalRecords) throws IllegalAccessException;

}
