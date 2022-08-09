package com.sql.parse.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.statement.Statement;
import com.sql.parse.statement.impl.jsqlparser.Delete;
import com.sql.parse.statement.impl.jsqlparser.Insert;
import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.statement.impl.jsqlparser.Update;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc sql解析器
 */
public class SqlParser {
    private Statement statement;

    private String type;

    public SqlParser(String sql) {
        String normalizedSql = sql.toUpperCase().trim();
        if (normalizedSql.indexOf("SELECT") == 0) {
            statement = com.sql.parse.statement.Select.parse(sql);
            type = "SELECT";
        } else if (normalizedSql.indexOf("INSERT") == 0) {
            statement = com.sql.parse.statement.Insert.parse(sql);
            type = "INSERT";
        } else if (normalizedSql.indexOf("UPDATE") == 0) {
            statement = com.sql.parse.statement.Update.parse(sql);
            type = "UPDATE";
        } else if (normalizedSql.indexOf("DELETE") == 0) {
            statement = com.sql.parse.statement.Delete.parse(sql);
            type = "DELETE";
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_010);
        }
    }

    public String getType() {
        return type;
    }

    public Statement getStatement() {
        return statement;
    }

    /**
     * @title: getTableNames
     * @desc 获取表名
     */
    public List<String> getTableNames() {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                if ("SELECT".equals(type)) {
                    Select select = (Select) statement;
                    return tablesNamesFinder.getTableList(select.getSelect());
                } else if ("INSERT".equals(type)) {
                    Insert insert = (Insert) statement;
                    return tablesNamesFinder.getTableList(insert.getInsert());
                } else if ("UPDATE".equals(type)) {
                    Update update = (Update) statement;
                    return tablesNamesFinder.getTableList(update.getUpdate());
                } else if ("DELETE".equals(type)) {
                    Delete delete = (Delete) statement;
                    return tablesNamesFinder.getTableList(delete.getDelete());
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            case "com.alibaba.druid":{
                List<String> list = new ArrayList<>();
                if ("SELECT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Select select = (com.sql.parse.statement.impl.druid.Select) statement;
                    SchemaStatVisitor visitor = new SchemaStatVisitor(SqlParseConfig.getDbType());
                    select.getSelect().accept(visitor);
                    Map<TableStat.Name, TableStat> tables = visitor.getTables();
                    Set<TableStat.Name> tableNameSet = tables.keySet();
                    for (TableStat.Name name : tableNameSet) {
                        String tableName = name.getName();
                        list.add(tableName);
                    }
                    return list;
                } else if ("INSERT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Insert insert = (com.sql.parse.statement.impl.druid.Insert) statement;
                    SQLInsertStatement i = insert.getInsert();
                    list.add(i.getTableName().getSimpleName());
                    if (insert.getInsert().getQuery() != null) {
                        SchemaStatVisitor visitor = new SchemaStatVisitor(SqlParseConfig.getDbType());
                        insert.getInsert().getQuery().accept(visitor);
                        Map<TableStat.Name, TableStat> tables = visitor.getTables();
                        Set<TableStat.Name> tableNameSet = tables.keySet();
                        for (TableStat.Name name : tableNameSet) {
                            String tableName = name.getName();
                            list.add(tableName);
                        }
                    }
                    return list;
                } else if ("UPDATE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Update update = (com.sql.parse.statement.impl.druid.Update) statement;
                    list.add(update.getUpdate().getTableName().getSimpleName());
                    return list;
                } else if ("DELETE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Delete delete = (com.sql.parse.statement.impl.druid.Delete) statement;
                    list.add(delete.getDelete().getTableName().getSimpleName());
                    return list;
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    /**
     * @title: getTableName
     * @desc 获取表名（单个）
     */
    public String getTableName() {
        return getTableNames().get(0);
    }

    /**
     * @title: getColumnNames
     * @desc 获取列名
     */
    public List<String> getColumnNames() {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                List<String> list = new ArrayList<>();
                if ("SELECT".equals(type)) {
                    Select select = (Select) statement;
                    PlainSelect plainSelect = (PlainSelect)select.getSelect().getSelectBody();
                    List<SelectItem> selectItems = plainSelect.getSelectItems();
                    for (SelectItem selectItem : selectItems) {
                        SelectExpressionItem expression = (SelectExpressionItem) selectItem;
                        if (expression.getExpression() instanceof Column) {
                            Column column = (Column) expression.getExpression();
                            list.add(column.getColumnName());
                        }
                    }
                    return list;
                } else if ("INSERT".equals(type)) {
                    Insert insert = (Insert) statement;
                    List<Column> columns = insert.getInsert().getColumns();
                    if (columns == null) {
                        return list;
                    }
                    for (Column column : columns) {
                        list.add(column.getColumnName());
                    }
                    return list;
                } else if ("UPDATE".equals(type)) {
                    Update update = (Update) statement;
                    List<Column> columns = update.getUpdate().getColumns();
                    for (Column column : columns) {
                        list.add(column.getColumnName());
                    }
                    return list;
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            case "com.alibaba.druid":{
                List<String> list = new ArrayList<>();
                if ("SELECT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Select select = (com.sql.parse.statement.impl.druid.Select) statement;
                    SQLSelectQueryBlock queryBlock = select.getSelect().getSelect().getQueryBlock();
                    List<SQLSelectItem> items = queryBlock.getSelectList();
                    for (SQLSelectItem item : items) {
                        SQLExpr column = item.getExpr();
                        if (column instanceof SQLIdentifierExpr) {
                            list.add(((SQLIdentifierExpr) column).getName());
                        } else if (column instanceof SQLPropertyExpr) {
                            list.add(((SQLPropertyExpr) column).getName());
                        }
                    }
                    return list;
                } else if ("INSERT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Insert insert = (com.sql.parse.statement.impl.druid.Insert) statement;
                    List<SQLExpr> columns = insert.getInsert().getColumns();
                    for (SQLExpr column : columns) {
                        if (column instanceof SQLIdentifierExpr) {
                            list.add(((SQLIdentifierExpr) column).getName());
                        } else if (column instanceof SQLPropertyExpr) {
                            list.add(((SQLPropertyExpr) column).getName());
                        }
                    }
                    return list;
                } else if ("UPDATE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Update update = (com.sql.parse.statement.impl.druid.Update) statement;
                    List<SQLUpdateSetItem> items = update.getUpdate().getItems();
                    for (SQLUpdateSetItem item : items) {
                        SQLExpr column = item.getColumn();
                        if (column instanceof SQLIdentifierExpr) {
                            list.add(((SQLIdentifierExpr) column).getName());
                        } else if (column instanceof SQLPropertyExpr) {
                            list.add(((SQLPropertyExpr) column).getName());
                        }
                    }
                    return list;
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    /**
     * @title: getWhere
     * @desc 获取where条件
     */
    public String getWhere() {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                if ("SELECT".equals(type)) {
                    Select select = (Select) statement;
                    PlainSelect plainSelect = (PlainSelect)select.getSelect().getSelectBody();
                    return plainSelect.getWhere().toString();
                }  else if ("UPDATE".equals(type)) {
                    Update update = (Update) statement;
                    return update.getUpdate().getWhere().toString();
                } else if ("DELETE".equals(type)) {
                    Delete delete = (Delete) statement;
                    return delete.getDelete().getWhere().toString();
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            case "com.alibaba.druid":{
                if ("SELECT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Select select = (com.sql.parse.statement.impl.druid.Select) statement;
                    return select.getSelect().getSelect().getQueryBlock().getWhere().toString();
                }  else if ("UPDATE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Update update = (com.sql.parse.statement.impl.druid.Update) statement;
                    return update.getUpdate().getWhere().toString();
                } else if ("DELETE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Delete delete = (com.sql.parse.statement.impl.druid.Delete) statement;
                    return delete.getDelete().getWhere().toString();
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    /**
     * @title: getAliasOrColumnNames
     * @desc 获取别名，若不存在则获取列名
     */
    public List<String> getAliasOrColumnNames() {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                if ("SELECT".equals(type)) {
                    List<String> list = new ArrayList<String>();
                    Select select = (Select) statement;
                    PlainSelect plainSelect = (PlainSelect)select.getSelect().getSelectBody();
                    List<SelectItem> selectItems = plainSelect.getSelectItems();
                    for (SelectItem selectItem : selectItems) {
                        SelectExpressionItem expression = (SelectExpressionItem) selectItem;
                        if (expression.getExpression() instanceof Column) {
                            if (expression.getAlias() != null) {
                                list.add(expression.getAlias().getName());
                            } else {
                                Column column = (Column) expression.getExpression();
                                list.add(column.getColumnName());
                            }
                        } else {
                            if (expression.getAlias() != null) {
                                list.add(expression.getAlias().getName());
                            } else {
                                throw BaseException.errorCode(SqlParseConstant.CODE_016);
                            }
                        }
                    }
                    return list;
                } else if ("INSERT".equals(type)) {
                    List<String> list = new ArrayList<String>();
                    Insert insert = (Insert) statement;
                    List<Column> columns = insert.getInsert().getColumns();
                    if (columns == null) {
                        return list;
                    }
                    for (Column column : columns) {
                        list.add(column.getColumnName());
                    }
                    return list;
                } else if ("UPDATE".equals(type)) {
                    List<String> list = new ArrayList<String>();
                    Update update = (Update) statement;
                    List<Column> columns = update.getUpdate().getColumns();
                    for (Column column : columns) {
                        list.add(column.getColumnName());
                    }
                    return list;
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            case "com.alibaba.druid":{
                List<String> list = new ArrayList<>();
                if ("SELECT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Select select = (com.sql.parse.statement.impl.druid.Select) statement;
                    SQLSelectQueryBlock queryBlock = select.getSelect().getSelect().getQueryBlock();
                    List<SQLSelectItem> items = queryBlock.getSelectList();
                    for (SQLSelectItem item : items) {
                        if (item.getAlias() != null) {
                            list.add(item.getAlias());
                        } else {
                            SQLExpr column = item.getExpr();
                            if (column instanceof SQLIdentifierExpr) {
                                list.add(((SQLIdentifierExpr) column).getName());
                            } else if (column instanceof SQLPropertyExpr) {
                                list.add(((SQLPropertyExpr) column).getName());
                            }
                        }
                    }
                    return list;
                } else if ("INSERT".equals(type)) {
                    com.sql.parse.statement.impl.druid.Insert insert = (com.sql.parse.statement.impl.druid.Insert) statement;
                    List<SQLExpr> columns = insert.getInsert().getColumns();
                    for (SQLExpr column : columns) {
                        if (column instanceof SQLIdentifierExpr) {
                            list.add(((SQLIdentifierExpr) column).getName());
                        } else if (column instanceof SQLPropertyExpr) {
                            list.add(((SQLPropertyExpr) column).getName());
                        }
                    }
                    return list;
                } else if ("UPDATE".equals(type)) {
                    com.sql.parse.statement.impl.druid.Update update = (com.sql.parse.statement.impl.druid.Update) statement;
                    List<SQLUpdateSetItem> items = update.getUpdate().getItems();
                    for (SQLUpdateSetItem item : items) {
                        SQLExpr column = item.getColumn();
                        if (column instanceof SQLIdentifierExpr) {
                            list.add(((SQLIdentifierExpr) column).getName());
                        } else if (column instanceof SQLPropertyExpr) {
                            list.add(((SQLPropertyExpr) column).getName());
                        }
                    }
                    return list;
                } else {
                    throw BaseException.errorCode(SqlParseConstant.CODE_011);
                }
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
