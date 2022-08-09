package com.sql.parse.schema;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc 更新列实体
 */
public class UpdateItem {
    private String columnName;
    private String expression;

    public UpdateItem(String columnName, String expr) {
        this.columnName =columnName;
        this.expression = expr;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getExpression() {
        return expression;
    }
}
