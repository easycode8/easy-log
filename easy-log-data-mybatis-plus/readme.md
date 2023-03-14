# 说明

## mybatis拦截器方式实现
```java
public class RecordInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        SqlCommandType sqlCommandType = ms.getSqlCommandType();

        if (sqlCommandType == SqlCommandType.DELETE || sqlCommandType == SqlCommandType.UPDATE) {
            // 记录信息的逻辑
            String tableName = getTableNameFromSql(sql);
            // 获取删除或修改前的记录信息
            List<Map<String, Object>> originalRecords = getOriginalRecords(tableName, parameterObject);
            // 将信息记录到日志文件或数据库中
            recordLog(tableName, originalRecords);
        }

        return invocation.proceed();
    }

    private String getTableNameFromSql(String sql) {
        // 根据 SQL 语句获取表名
        // ...
    }

    private List<Map<String, Object>> getOriginalRecords(String tableName, Object parameterObject) {
        // 获取删除或修改前的记录信息
        // ...
    }

    private void recordLog(String tableName, List<Map<String, Object>> originalRecords) {
        // 将信息记录到日志文件或数据库中
        // ...
    }
}

```