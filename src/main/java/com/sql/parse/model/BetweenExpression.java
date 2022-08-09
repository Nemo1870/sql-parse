package com.sql.parse.model;

import com.sql.parse.expression.OperatorTypeEnum;

/**
 * @date 2022/7/8 15:48
 * @desc BETWEEN
 */
public class BetweenExpression extends Expression {
    private String leftExpression;

    private String startExpression;

    private String endExpression;

    private boolean not;

    public BetweenExpression(String expression, boolean not, String leftExpression, String startExpression, String endExpression) {
        super(expression, OperatorTypeEnum.BETWEEN);
        this.leftExpression = leftExpression;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.not = not;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public String getStartExpression() {
        return startExpression;
    }

    public String getEndExpression() {
        return endExpression;
    }

    public boolean isNot() {
        return not;
    }
}
