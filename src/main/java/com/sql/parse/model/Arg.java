package com.sql.parse.model;

import com.sql.parse.expression.ValueTypeEnum;

/**
 * @date 2022/7/5 17:04
 * @desc 参数
 */
public class Arg {
    private String expression;

    private ValueTypeEnum type;

    private boolean disable = false;

    public Arg(String expression, ValueTypeEnum type) {
        this.expression = expression;
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ValueTypeEnum getType() {
        return type;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
