package com.sql.parse.statement.impl.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.druid.ExpressionBuilder;
import com.sql.parse.model.*;
import com.sql.parse.schema.InsertItem;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.druid.ColumnInterceptorUtil;
import com.sql.parse.util.druid.OwnerInterceptorUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Insert语法操作类
 */
public class Insert extends com.sql.parse.statement.Insert {
    private SQLInsertStatement insert;

    public Insert(SQLInsertStatement insert) {
        this.insert = insert;
    }

    public Insert(String tableName) {
        SQLInsertStatement insert = new SQLInsertStatement();
        SQLExprTableSource table = new SQLExprTableSource(tableName);
        insert.setTableSource(table);
        this.insert = insert;
    }

    @Override
    public String getSql(boolean safe) {
        if (this.insert.getQuery() == null && (this.insert.getValuesList() == null || this.insert.getValuesList().isEmpty())) {
            throw BaseException.errorCode(SqlParseConstant.CODE_014);
        } else {
            return SQLUtils.toSQLString(this.insert);
        }
    }

    public SQLInsertStatement getInsert() {
        return this.insert;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(String column) {
        insert.addColumn(ExpressionBuilder.parse(column));
        return this;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(String column, String value) {
        return addColumn(new InsertItem(column, value));
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(InsertItem insertItem) {
        if (insert.getQuery() == null) {
            insert.addColumn(ExpressionBuilder.parse(insertItem.getColumnName()));
            List<SQLInsertStatement.ValuesClause> values = insert.getValuesList();
            if (values.isEmpty()) {
                SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
                valuesClause.addValue(ExpressionBuilder.parse(insertItem.getExpression()));
                values.add(valuesClause);
            } else {
                for (SQLInsertStatement.ValuesClause valuesClause : values) {
                    valuesClause.addValue(ExpressionBuilder.parse(insertItem.getExpression()));
                }
            }
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    /**
     * @title: addValue
     * @desc 新增insert数据列
     */
    public Insert addValueColumn(String value) {
        if (insert.getQuery() == null) {
            List<SQLInsertStatement.ValuesClause> values = insert.getValuesList();
            if (values.isEmpty()) {
                SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
                valuesClause.addValue(ExpressionBuilder.parse(value));
                values.add(valuesClause);
            } else {
                for (SQLInsertStatement.ValuesClause valuesClause : values) {
                    valuesClause.addValue(ExpressionBuilder.parse(value));
                }
            }
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    /**
     * @title: addValue
     * @desc 新增insert数据条数
     */
    public Insert addValue(String... valueColumns) {
        return addValue(Arrays.asList(valueColumns));
    }

    public Insert addValue(List<String> value) {
        if (insert.getQuery() == null) {
            List<SQLInsertStatement.ValuesClause> values = insert.getValuesList();
            SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
            for (String valueColumn : value) {
                valuesClause.addValue(ExpressionBuilder.parse(valueColumn));
            }
            values.add(valuesClause);
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据子查询
     */
    public Insert setSelect(com.sql.parse.statement.Select select) {
        if (insert.getValuesList() == null || insert.getValuesList().isEmpty()) {
            Select s = (Select) select;
            insert.setQuery(s.getSelect().getSelect());
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_019);
        }
        return this;
    }

    /**
     * @title: parse
     * @desc Insert解析器
     */
    public static Insert parse(String sql) {
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, SqlParseConfig.getDbType());
        SQLInsertStatement delete = (SQLInsertStatement) sqlStatementList.get(0);
        return new Insert(delete);
    }

    public Insert clone() {
        return new Insert(this.insert.clone());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        SQLSelect sqlSelect = insert.getQuery();
        if (sqlSelect != null) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.asInterceptor(columnInterceptor, tableInterceptor);
            insert.setQuery(((Select) select).getSelect().getSelect());
        }
    }

    public void argInterceptor(Consumer<Arg> interceptor) {
        SQLSelect sqlSelect = insert.getQuery();
        if (sqlSelect != null) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.argInterceptor(interceptor);
            insert.setQuery(((Select) select).getSelect().getSelect());
        }
    }

    public void expressionInterceptor(Consumer<Expression> interceptor) {
        SQLSelect sqlSelect = insert.getQuery();
        if (sqlSelect != null) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.expressionInterceptor(interceptor);
            insert.setQuery(((Select) select).getSelect().getSelect());
        }
    }

    public void columnInterceptor(Consumer<Column> interceptor) {
        List<SQLExpr> columns = insert.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            SQLExpr column = columns.get(i);
            SQLExpr child = ColumnInterceptorUtil.checkExpression(column, interceptor);
            columns.set(i, child);
        }
        SQLSelect sqlSelect = insert.getQuery();
        if (sqlSelect != null) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.columnInterceptor(interceptor);
            insert.setQuery(((Select) select).getSelect().getSelect());
        }
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        List<SQLExpr> columns = insert.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            SQLExpr column = columns.get(i);
            SQLExpr child = OwnerInterceptorUtil.checkExpression(column, interceptor);
            columns.set(i, child);
        }
        SQLSelect sqlSelect = insert.getQuery();
        if (sqlSelect != null) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.ownerInterceptor(interceptor);
            insert.setQuery(((Select) select).getSelect().getSelect());
        }
    }
}
