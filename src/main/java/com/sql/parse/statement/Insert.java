package com.sql.parse.statement;

import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.schema.InsertItem;
import com.sql.parse.util.SqlParseConstant;

import java.util.List;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Insert语法操作类
 */
public abstract class Insert extends Statement {
    @Override
    public abstract String getSql(boolean safe);

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public abstract Insert addColumn(String column);

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public abstract Insert addColumn(String column, String value);

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public abstract Insert addColumn(InsertItem insertItem);

    /**
     * @title: addValue
     * @desc 新增insert数据列
     */
    public abstract Insert addValueColumn(String value);

    /**
     * @title: addValue
     * @desc 新增insert数据条数
     */
    public abstract Insert addValue(String... valueColumns);

    /**
     * @title: addValue
     * @desc 新增insert数据条数
     */
    public abstract Insert addValue(List<String> value);

    /**
     * @title: addColumn
     * @desc 新增insert数据子查询
     */
    public abstract Insert setSelect(Select select);

    public Insert setSelect(String select) {
        return setSelect(Select.parse(select));
    }

    /**
     * @title: parse
     * @desc Insert解析器
     */
    public static Insert parse(String sql) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                try {
                    return com.sql.parse.statement.impl.jsqlparser.Insert.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Insert");
                }
            }
            case "com.alibaba.druid":{
                try {
                    return com.sql.parse.statement.impl.druid.Insert.parse(sql);
                } catch (ClassCastException e) {
                    throw BaseException.errorCode(SqlParseConstant.CODE_020, "Insert");
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
