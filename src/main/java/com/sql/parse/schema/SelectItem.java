package com.sql.parse.schema;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc 查询列实体
 */
public class SelectItem {
    private String columnName;

    private String alias;

    private boolean useAs;

    public SelectItem(String expr) {
        this(expr, null, true);
    }

    public SelectItem(String expr, String alias, boolean useAs) {
        this.columnName = expr;
        this.alias = alias;
        this.useAs = useAs;
    }

    public SelectItem(String expr, String alias) {
        this(expr, alias, false);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isUseAs() {
        return useAs;
    }
}
