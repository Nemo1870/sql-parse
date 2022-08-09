package com.sql.parse.schema;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc 表格实体
 */
public class Table {
    private String tableName;

    private String alias;

    private boolean useAs;

    public Table(String tableName) {
        this(tableName, null, false);
    }

    public Table(String tableName, String alias) {
        this(tableName, alias, false);
    }

    public Table(String tableName, String alias, boolean useAs) {
        this.tableName = tableName;
        this.alias = alias;
        this.useAs = useAs;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isUseAs() {
        return useAs;
    }
}
