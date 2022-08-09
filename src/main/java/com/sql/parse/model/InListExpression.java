package com.sql.parse.model;

import java.util.List;

/**
 * @date 2022/7/7 16:42
 * @desc IN ()
 */
public class InListExpression extends InExpression {
    private String leftExpression;

    private List<String> itemList;

    public InListExpression(String expression, boolean not, String leftExpression, List<String> itemList) {
        super(expression, not);
        this.leftExpression = leftExpression;
        this.itemList = itemList;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public List<String> getItemList() {
        return itemList;
    }
}
