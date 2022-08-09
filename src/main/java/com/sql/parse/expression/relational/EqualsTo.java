package com.sql.parse.expression.relational;

/**
 * @date 2021/1/22 11:03
 * @desc 等式
 */
public class EqualsTo {
    private String leftExpression;

    private String rightExpression;

    public EqualsTo(String leftExpression, String rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(String leftExpression) {
        this.leftExpression = leftExpression;
    }

    public String getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(String rightExpression) {
        this.rightExpression = rightExpression;
    }
}
