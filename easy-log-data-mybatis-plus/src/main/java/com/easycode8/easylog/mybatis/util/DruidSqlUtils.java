package com.easycode8.easylog.mybatis.util;


import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 根据数据建表语句生成对象数据
 * @author myron
 */
public class DruidSqlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidSqlUtils.class);



    /**
     * 使用Druid的SQLASTOutputVisitor类来递归遍历WHERE条件并生成对应的查询语句，可以处理多个条件以及逻辑运算符
     * @param updateSql
     * @return
     */
    public static String getSelectStatementFromUpdate(String updateSql) {
        try {
            // 解析SQL更新语句
            SQLStatement stmt = SQLUtils.parseStatements(updateSql, "mysql").get(0);
            if (stmt instanceof SQLUpdateStatement) {
                SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
                // 获取更新表名
                String tableName = updateStmt.getTableName().toString();//getTableName().getSimpleName()方式会忽略库db.xxx

                // 获取更新条件
                SQLExpr whereExpr = updateStmt.getWhere();
                if (whereExpr != null) {
                    // 使用SQLASTOutputVisitor生成对应的查询语句
                    SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(new StringBuilder(), DbType.mysql);
                    whereExpr.accept(visitor);
                    String whereSql = visitor.getAppender().toString();

                    // 构造对应的查询语句
                    String selectSql = String.format("SELECT * FROM %s WHERE %s", tableName, whereSql);
                    return selectSql;
                }
            }
        } catch (Exception e) {
            // handle exception
            LOGGER.warn("解析sql失败:{}", e.getMessage());
        }
        return null;
    }

    public static String getSelectStatementFromDelete(String deleteSql) {
        try {
            // 解析SQL删除语句
            SQLStatement stmt = SQLUtils.parseStatements(deleteSql, "mysql").get(0);
            if (stmt instanceof SQLDeleteStatement) {
                SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
                // 获取删除表名
                String tableName = deleteStmt.getTableName().toString();

                // 获取删除条件
                SQLExpr whereExpr = deleteStmt.getWhere();
                if (whereExpr != null) {
                    // 使用SQLASTOutputVisitor生成对应的查询语句
                    SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(new StringBuilder(), DbType.of("mysql"));
                    whereExpr.accept(visitor);
                    String whereSql = visitor.getAppender().toString();

                    // 构造对应的查询语句
                    String selectSql = String.format("SELECT * FROM %s WHERE %s", tableName, whereSql);
                    return selectSql;
                }
            }
        } catch (Exception e) {
            // handle exception
            LOGGER.warn("解析sql失败:{}", e.getMessage());
        }
        return null;
    }


    public static void main(String[] args) throws IOException {

        System.out.println("====测试更新语句转换为查询语句====");
        String updateSql = "UPDATE account SET name='ss', username='sss' WHERE id=10000 and age=1";
        System.out.println(getSelectStatementFromUpdate(updateSql));

        System.out.println("====测试删除语句转换为查询语句====");
        String deleteSql = "DELETE FROM movies WHERE id>2 and age=1";
        System.out.println(getSelectStatementFromDelete(deleteSql));

        System.out.println("====测试子查询条件====");
        String deleteSql2 = "DELETE FROM movies t WHERE t.id>2 or t.age=(select age from user where id=2)";
        System.out.println(getSelectStatementFromDelete(deleteSql2));
    }
}
