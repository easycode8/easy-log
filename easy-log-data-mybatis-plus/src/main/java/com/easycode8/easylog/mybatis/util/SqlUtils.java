package com.easycode8.easylog.mybatis.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {
    public static String convertDeleteToSelect(String deleteSql) {
        // 定义正则表达式匹配模式，支持提取 schema 和表名
        String pattern = "DELETE\\s+FROM\\s+(\\w+(?:\\.\\w+)?)\\s+WHERE\\s+(.+)";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(deleteSql);

        if (matcher.find()) {
            String fullTableName = matcher.group(1);
            String[] tableParts = fullTableName.split("\\.");
            String schema = null;
            String tableName = null;

            if (tableParts.length > 1) {
                schema = tableParts[0];
                tableName = tableParts[1];
            } else {
                tableName = tableParts[0];
            }

            String whereClause = matcher.group(2);

            // 构建 SELECT 语句，包括 schema 和表名
            String selectSql = "SELECT * FROM " + getTableNameWithSchema(schema, tableName) + " WHERE " + whereClause;
            return selectSql;
        }

        return null;
    }

    public static String convertUpdateToSelect(String updateSql) {
        // 定义正则表达式匹配模式，支持提取 schema 和表名
        String pattern = "UPDATE\\s+(\\w+(?:\\.\\w+)?)\\s+SET(.+?)WHERE\\s+(.+)";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(updateSql);

        if (matcher.find()) {
            String fullTableName = matcher.group(1);
            String[] tableParts = fullTableName.split("\\.");
            String schema = null;
            String tableName = null;

            if (tableParts.length > 1) {
                schema = tableParts[0];
                tableName = tableParts[1];
            } else {
                tableName = tableParts[0];
            }

            String setClause = matcher.group(2);
            String whereClause = matcher.group(3);

            // 构建 SELECT 语句，包括 schema 和表名
            String selectSql = "SELECT * FROM " + getTableNameWithSchema(schema, tableName) + " WHERE " + whereClause;
            return selectSql;
        }

        return null;
    }

    private static String getTableNameWithSchema(String schema, String tableName) {
        if (schema != null) {
            return schema + "." + tableName;
        }
        return tableName;
    }

    public static void main(String[] args) {
        String deleteSql = "DELETE FROM schema_name.table_name WHERE condition";

        String selectSql = convertDeleteToSelect(deleteSql);
        System.out.println(selectSql);

        String updateSql = "UPDATE schema_name.table_name SET column1 = value1, column2 = value2 WHERE condition";

        String selectSql1 = convertUpdateToSelect(updateSql);
        System.out.println(selectSql1);
    }
}
