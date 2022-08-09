package com.sql.parse.schema;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc orderBy实体
 */
public class OrderByItem {
    private String columnName;
    private boolean isAsc;

    public OrderByItem(String columnName, boolean isAsc) {
        this.columnName = columnName;
        this.isAsc = isAsc;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isAsc() {
        return isAsc;
    }
}
