package com.sql.parse.model;

/**
 * @date 2022/7/4 11:00
 * @desc AS
 */
public class Alias {
    private boolean disable = false;

    private boolean useAs;

    private String leftExpression;

    private String rightExpression;

    public Alias(String leftExpression, String rightExpression, boolean useAs) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.useAs = useAs;
    }

    public Alias(String leftExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = null;
        this.useAs = false;
        this.disable = true;
    }

    public boolean isUseAs() {
        return useAs;
    }

    public void setUseAs(boolean useAs) {
        this.useAs = useAs;
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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
