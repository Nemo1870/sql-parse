package com.sql.parse.statement;

import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.schema.UpdateItem;
import com.sql.parse.util.SqlParseConstant;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Update语法操作类
 */
public abstract class Update extends Statement {
    @Override
    public abstract String getSql(boolean safe);

    /**
     * @title: addColumn
     * @desc 新增update数据
     */
    public abstract Update addColumn(UpdateItem updateItem);

    public abstract Update addColumn(String column, String value);

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public abstract Update where(String expr);

    /**
     * @title: and
     * @desc 移除where条件
     */
    public abstract Update removeAllWhere();

    /**
     * @title: and
     * @desc "AND"符号
     */
    public abstract Update and(String expr);

    /**
     * @title: or
     * @desc "OR"符号
     */
    public abstract Update or(String expr);

    /**
     * @title: orderBy
     * @desc "ORDER BY"符号
     *//*
    public abstract Update orderBy(String... columnNames);

    public abstract Update orderBy(OrderByItem... orderByItems);*/

    /**
     * @title: limit
     * @desc "LIMIT"符号
     *//*
    public  abstractUpdate limit(int offset, int rows);*/

    /**
     * @title: parse
     * @desc Update解析器
     */
    public static Update parse(String sql) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                try {
                    return com.sql.parse.statement.impl.jsqlparser.Update.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Update");
                }
            }
            case "com.alibaba.druid":{
                try {
                    return com.sql.parse.statement.impl.druid.Update.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Update");
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
