package com.sql.parse.model;

import com.sql.parse.expression.OperatorTypeEnum;

/**
 * @date 2022/7/8 11:08
 * @desc LIKE
 */
public class LikeExpression extends Expression {
    private String leftExpression;

    private String rightExpression;

    private boolean not;

    public LikeExpression(String expression, boolean not, String leftExpression, String rightExpression) {
        super(expression, OperatorTypeEnum.LIKE);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.not = not;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public String getRightExpression() {
        return rightExpression;
    }

    public boolean isNot() {
        return not;
    }
}
