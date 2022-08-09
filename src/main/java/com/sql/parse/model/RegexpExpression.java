package com.sql.parse.model;

import com.sql.parse.expression.OperatorTypeEnum;

/**
 * @date 2022/7/8 16:25
 * @desc RegexpExpression
 */
public class RegexpExpression extends Expression {
    private boolean useRLike;

    private String leftExpression;

    private String rightExpression;

    public RegexpExpression(String expression, boolean useRLike, String leftExpression, String rightExpression) {
        super(expression, OperatorTypeEnum.REGEXP);
        this.useRLike = useRLike;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public boolean isUseRLike() {
        return useRLike;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public String getRightExpression() {
        return rightExpression;
    }
}
