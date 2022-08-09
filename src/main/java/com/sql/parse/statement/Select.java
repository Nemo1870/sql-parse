package com.sql.parse.statement;

import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.schema.OrderByItem;
import com.sql.parse.schema.SelectItem;
import com.sql.parse.schema.Table;
import com.sql.parse.util.SqlParseConstant;

/**
 * @date 2019年4月29日上午11:32:02
 * @desc Select语法操作类
 */
public abstract class Select extends Statement {
    /*是否替换所有字段*/
    protected boolean isChangeColumn;

    @Override
    public abstract String getSql(boolean safe);

    /**
     * @title: addColumn
     * @desc: 增加查询字段，支持别名
     */
    public abstract Select addColumn(String expr, String alias);

    public abstract Select addColumn(SelectItem item);

    /**
     * @title: addColumn
     * @desc: 增加查询字段，不支持别名
     */
    public abstract Select addColumn(String expr);

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public abstract Select where(String expr);

    /**
     * @title: removeAllWhere
     * @desc 移除where条件
     */
    public abstract Select removeAllWhere();

    /**
     * @title: and
     * @desc "AND"符号
     */
    public abstract Select and(String expr);

    /**
     * @title: or
     * @desc "OR"符号
     */
    public abstract Select or(String expr);

    /**
     * @title: simpleJoin
     * @desc "select * form a,b"的JOIN方式
     */
    public abstract Select simpleJoin(String tableName);

    public abstract Select simpleJoin(Table table);

    /**
     * @title: join
     * @desc "JOIN"符号
     */
    public abstract Select join(String tableName, EqualsTo on);

    public abstract Select join(Table table, EqualsTo on);

    /**
     * @title: leftJoin
     * @desc "LEFT JOIN"符号
     */
    public abstract Select leftJoin(String tableName, EqualsTo on);

    public abstract Select leftJoin(Table table, EqualsTo on);

    /**
     * @title: rightJoin
     * @desc "RIGHT JOIN"符号
     */
    public abstract Select rightJoin(String tableName, EqualsTo on);

    public abstract Select rightJoin(Table table, EqualsTo on);

    /**
     * @title: innerJoin
     * @desc "INNER JOIN"符号
     */
    public abstract Select innerJoin(String tableName, EqualsTo on);

    public abstract Select innerJoin(Table table, EqualsTo on);

    /**
     * @title: orderBy
     * @desc "ORDER BY"符号
     */
    public abstract Select orderBy(String... columnNames);

    public abstract Select orderBy(OrderByItem... orderByItems);

    /**
     * @title: groupBy
     * @desc "GROUP BY"符号
     */
    public abstract Select groupBy(String... columnNames);

    /**
     * @title: limit
     * @desc "LIMIT"符号
     */
    public abstract Select limit(int offset, int rows);

    public abstract Select limit(int rows);

    /**
     * @title: changeAllColumns
     * @desc 替换所有查询字段
     */
    public abstract Select changeAllColumns(SelectItem... columns);

    public abstract Select changeAllColumns(String... exprs);

    /**
     * @title: changeColumnByColumnName
     * @desc 替换指定的查询字段
     */
    public abstract Select changeColumnByColumnName(String columnName, String expr);

    public abstract Select changeColumnByColumnName(String columnName, String expr, String alias);

    /**
     * @title: parse
     * @desc Select解析器
     */
    public static Select parse(String sql) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                try {
                    return com.sql.parse.statement.impl.jsqlparser.Select.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Select");
                }
            }
            case "com.alibaba.druid":{
                try {
                    return com.sql.parse.statement.impl.druid.Select.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Select");
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
