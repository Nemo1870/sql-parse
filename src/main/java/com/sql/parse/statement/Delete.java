package com.sql.parse.statement;

import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.util.SqlParseConstant;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Delete语法操作类
 */
public abstract class Delete extends Statement {
    @Override
    public abstract String getSql(boolean safe);

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public abstract Delete where(String expr);

    /**
     * @title: and
     * @desc 移除where条件
     */
    public abstract Delete removeAllWhere();

    /**
     * @title: and
     * @desc "AND"符号
     */
    public abstract Delete and(String expr);

    /**
     * @title: or
     * @desc "OR"符号
     */
    public abstract Delete or(String expr);

//    /**
//     * @title: orderBy
//     * @desc "ORDER BY"符号
//     */
//    public abstract Delete orderBy(String... columnNames);
//
//    /**
//     * @title: limit
//     * @desc "LIMIT"符号
//     */
//    public abstract Delete limit(int rows);

    /**
     * @title: parse
     * @desc Delete解析器
     */
    public static Delete parse(String sql) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                try {
                    return com.sql.parse.statement.impl.jsqlparser.Delete.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Delete");
                }
            }
            case "com.alibaba.druid":{
                try {
                    return com.sql.parse.statement.impl.druid.Delete.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Delete");
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
