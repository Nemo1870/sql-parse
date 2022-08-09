package com.sql.parse.model;

public class Change {
    private String oldExpr;

    private String newExpr;

    public Change(String oldExpr, String newExpr) {
        this.oldExpr = oldExpr;
        this.newExpr = newExpr;
    }

    public String getOldExpr() {
        return oldExpr;
    }

    public void setOldExpr(String oldExpr) {
        this.oldExpr = oldExpr;
    }

    public String getNewExpr() {
        return newExpr;
    }

    public void setNewExpr(String newExpr) {
        this.newExpr = newExpr;
    }
}
