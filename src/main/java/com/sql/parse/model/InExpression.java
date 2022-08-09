package com.sql.parse.model;

import com.sql.parse.expression.OperatorTypeEnum;

/**
 * @date 2022/7/7 19:09
 * @desc IN
 */
public class InExpression extends Expression {
    private boolean not;

    public InExpression(String expression, boolean not) {
        super(expression, OperatorTypeEnum.IN);
        this.not = not;
    }

    public boolean isNot() {
        return not;
    }
}
