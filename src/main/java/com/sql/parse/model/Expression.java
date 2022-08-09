package com.sql.parse.model;

import com.sql.parse.expression.OperatorTypeEnum;

/**
 * @date 2022/7/6 16:59
 * @desc 表达式
 */
public class Expression {
    private boolean disable = false;

    private OperatorTypeEnum type;

    private String expression;

    public Expression(String expression, OperatorTypeEnum type) {
        this.type = type;
        this.expression = expression;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public OperatorTypeEnum getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
