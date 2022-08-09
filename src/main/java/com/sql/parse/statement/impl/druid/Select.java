package com.sql.parse.statement.impl.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.druid.ExpressionBuilder;
import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.model.*;
import com.sql.parse.schema.OrderByItem;
import com.sql.parse.schema.SelectItem;
import com.sql.parse.schema.Table;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.druid.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月29日上午11:32:02
 * @desc Select语法操作类
 */
public class Select extends com.sql.parse.statement.Select {
    private SQLSelectStatement select;

    public Select(SQLSelectStatement select) {
        this.select = select;
    }

    public Select(Table table) {
        String sql = "select * from " + table.getTableName();
        if (table.getAlias() != null) {
            sql += " " + table.getAlias();
        }
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, SqlParseConfig.getDbType());
        SQLSelectStatement select = (SQLSelectStatement) sqlStatementList.get(0);
        this.select = select;
    }

    public Select(String tableName) {
        this(new Table(tableName));
    }

    /*public Select(Owner table, SelectItem... columns) {
        if(columns.length==0){
            this.select = SelectUtils.buildSelectFromTable(table.getTable());
        }else{
            SelectExpressionItem[] array = new SelectExpressionItem[columns.length];
            for (int i = 0; i < columns.length; i++) {
                array[i] = columns[i].getColumn();
            }
            this.select = SelectUtils.buildSelectFromTableAndSelectItems(table.getTable(), array);
        }
    }*/

    @Override
    public String getSql(boolean safe) {
        /*where条件为空时，添加[rownum<500]或[limit 500]的条件表达式*/
        Select mySelect = this.clone();
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        if (queryBlock.getLimit() == null) {
            boolean flag = true;
            List<SQLSelectItem> selectItems = queryBlock.getSelectList();
            if (selectItems.size() == 1) {
               SQLSelectItem selectItem = selectItems.get(0);
               SQLExpr expression = selectItem.getExpr();
                if (expression instanceof SQLAggregateExpr && "count".equals(((SQLAggregateExpr) expression).getMethodName().toLowerCase())) {
                    flag = false;
                }
            }
            if (queryBlock.getWhere() != null) {
                flag = false;
            }
            if (safe && flag) {
                mySelect.limit(500);
            }
        }
        return SQLUtils.toSQLString(mySelect.getSelect());
    }

    public SQLSelectStatement getSelect() {
        return this.select;
    }

    /**
     * @title: addColumn
     * @desc: 增加查询字段，支持别名
     */
    public Select addColumn(String expr, String alias) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        if(isChangeColumn){
            queryBlock.getSelectList().clear();
            isChangeColumn = false;
        }
        SelectItem item = new SelectItem(expr, alias);
        /*支持别名使用，代替*/
        queryBlock.getSelectList().add(new SQLSelectItem(ExpressionBuilder.parse(item.getColumnName()), item.getAlias()));
        return this;
    }

    public Select addColumn(SelectItem item) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        if(isChangeColumn){
            queryBlock.getSelectList().clear();
            isChangeColumn = false;
        }
        queryBlock.getSelectList().add(new SQLSelectItem(ExpressionBuilder.parse(item.getColumnName()), item.getAlias()));
        return this;
    }

    /**
     * @title: addColumn
     * @desc: 增加查询字段，不支持别名
     */
    public Select addColumn(String expr) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        if(isChangeColumn){
            queryBlock.getSelectList().clear();
            isChangeColumn = false;
        }
        queryBlock.getSelectList().add(new SQLSelectItem(ExpressionBuilder.parse(expr)));
        return this;
    }

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public Select where(SQLExpr where) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        queryBlock.setWhere(where);
        return this;
    }

    public Select where(String expr) {
        SQLExpr expression = ExpressionBuilder.parse(expr);
        where(expression);
        return this;
    }

    /**
     * @title: removeAllWhere
     * @desc 移除where条件
     */
    public Select removeAllWhere() {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        queryBlock.setWhere(null);
        return this;
    }

    /**
     * @title: and
     * @desc "AND"符号
     */
    public Select and(SQLExpr where) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLExpr oldWhere = queryBlock.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_005);
        } else {
            queryBlock.setWhere(ExpressionBuilder.and(oldWhere, where));
        }
        return this;
    }

    public Select and(String expr) {
        SQLExpr expression = ExpressionBuilder.parse(expr);
        and(expression);
        return this;
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public Select or(SQLExpr where) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLExpr oldWhere = queryBlock.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_006);
        } else {
            queryBlock.setWhere(ExpressionBuilder.or(oldWhere, where));
        }
        return this;
    }

    public Select or(String expr) {
        SQLExpr expression = ExpressionBuilder.parse(expr);
        or(expression);
        return this;
    }

    /**
     * @title: simpleJoin
     * @desc "select * form a,b"的JOIN方式
     */
    public Select simpleJoin(String tableName) {
        return simpleJoin(new Table(tableName));
    }

    public Select simpleJoin(Table table) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLJoinTableSource join = new SQLJoinTableSource(
                queryBlock.getFrom(),
                SQLJoinTableSource.JoinType.COMMA,
                new SQLExprTableSource(new SQLIdentifierExpr(table.getTableName()), table.getAlias()),
                null
        );
        return createJoin(join);
    }

    /**
     * @title: join
     * @desc "JOIN"符号
     */
    public Select join(String tableName, EqualsTo on) {
        return join(new Table(tableName), on);
    }

    public Select join(Table table, EqualsTo on) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLJoinTableSource join = new SQLJoinTableSource(
                queryBlock.getFrom(),
                SQLJoinTableSource.JoinType.JOIN,
                new SQLExprTableSource(new SQLIdentifierExpr(table.getTableName()), table.getAlias()),
                new SQLBinaryOpExpr(
                        ExpressionBuilder.parse(on.getLeftExpression()),
                        SQLBinaryOperator.Equality,
                        ExpressionBuilder.parse(on.getRightExpression())
                )
        );
        return createJoin(join);
    }

    /**
     * @title: leftJoin
     * @desc "LEFT JOIN"符号
     */
    public Select leftJoin(String tableName, EqualsTo on) {
        return leftJoin(new Table(tableName), on);
    }

    public Select leftJoin(Table table, EqualsTo on) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLJoinTableSource join = new SQLJoinTableSource(
                queryBlock.getFrom(),
                SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN,
                new SQLExprTableSource(new SQLIdentifierExpr(table.getTableName()), table.getAlias()),
                new SQLBinaryOpExpr(
                        ExpressionBuilder.parse(on.getLeftExpression()),
                        SQLBinaryOperator.Equality,
                        ExpressionBuilder.parse(on.getRightExpression())
                )
        );
        return createJoin(join);
    }

    /**
     * @title: rightJoin
     * @desc "RIGHT JOIN"符号
     */
    public Select rightJoin(String tableName, EqualsTo on) {
        return rightJoin(new Table(tableName), on);
    }

    public Select rightJoin(Table table, EqualsTo on) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLJoinTableSource join = new SQLJoinTableSource(
                queryBlock.getFrom(),
                SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN,
                new SQLExprTableSource(new SQLIdentifierExpr(table.getTableName()), table.getAlias()),
                new SQLBinaryOpExpr(
                        ExpressionBuilder.parse(on.getLeftExpression()),
                        SQLBinaryOperator.Equality,
                        ExpressionBuilder.parse(on.getRightExpression())
                )
        );
        return createJoin(join);
    }

    /**
     * @title: innerJoin
     * @desc "INNER JOIN"符号
     */
    public Select innerJoin(String tableName, EqualsTo on) {
        return innerJoin(new Table(tableName), on);
    }

    public Select innerJoin(Table table, EqualsTo on) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLJoinTableSource join = new SQLJoinTableSource(
                queryBlock.getFrom(),
                SQLJoinTableSource.JoinType.INNER_JOIN,
                new SQLExprTableSource(new SQLIdentifierExpr(table.getTableName()), table.getAlias()),
                new SQLBinaryOpExpr(
                        ExpressionBuilder.parse(on.getLeftExpression()),
                        SQLBinaryOperator.Equality,
                        ExpressionBuilder.parse(on.getRightExpression())
                )
        );
        return createJoin(join);
    }

    private Select createJoin(SQLJoinTableSource join) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        queryBlock.setFrom(join);
        return this;
    }

    /**
     * @title: orderBy
     * @desc "ORDER BY"符号
     */
    public Select orderBy(String... columnNames) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLOrderBy orderBys = queryBlock.getOrderBy();
        SQLOrderBy newOrderBys = new SQLOrderBy();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (String columnName : columnNames) {
            newOrderBys.addItem(ExpressionBuilder.parse(columnName));
        }
        queryBlock.setOrderBy(newOrderBys);
        return this;
    }

    public Select orderBy(OrderByItem... orderByItems) {
        List<String> columnNames = new ArrayList<String>();
        for (OrderByItem orderByElement : orderByItems) {
            SQLExpr expression = ExpressionBuilder.parse(orderByElement.getColumnName());
            if (expression instanceof SQLIdentifierExpr) {
                columnNames.add(((SQLIdentifierExpr) expression).getName());
            } else if (expression instanceof SQLPropertyExpr) {
                columnNames.add(((SQLPropertyExpr) expression).getName());
            }
        }
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLOrderBy orderBys = queryBlock.getOrderBy();
        SQLOrderBy newOrderBys = new SQLOrderBy();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (OrderByItem orderByItem : orderByItems) {
            newOrderBys.addItem(new SQLSelectOrderByItem(ExpressionBuilder.parse(orderByItem.getColumnName()), orderByItem.isAsc() ? SQLOrderingSpecification.ASC : SQLOrderingSpecification.DESC));
        }
        queryBlock.setOrderBy(newOrderBys);
        return this;
    }

    /**
     * @title: groupBy
     * @desc "GROUP BY"符号
     */
    public Select groupBy(String... columnNames) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLSelectGroupByClause groupBys = queryBlock.getGroupBy();
        SQLSelectGroupByClause newGroupBys = new SQLSelectGroupByClause();
        if (groupBys != null ) {
            newGroupBys = groupBys;
        }
        for (String columnName : columnNames) {
            newGroupBys.addItem(ExpressionBuilder.parse(columnName));
        }
        queryBlock.setGroupBy(newGroupBys);
        return this;
    }

    /**
     * @title: limit
     * @desc "LIMIT"符号
     */
    public Select limit(int offset, int rows) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLLimit limit = new SQLLimit(new SQLIntegerExpr(offset), new SQLIntegerExpr(rows));
        queryBlock.setLimit(limit);
        return this;
    }

    public Select limit(int rows) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        SQLLimit limit = new SQLLimit(rows);
        queryBlock.setLimit(limit);
        return this;
    }

    /**
     * @title: changeAllColumns
     * @desc 替换所有查询字段
     */
    public Select changeAllColumns(SelectItem... columns) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        queryBlock.getSelectList().clear();
        for (SelectItem column : columns) {
            addColumn(column);
        }
        return this;
    }

    public Select changeAllColumns(String... exprs) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        queryBlock.getSelectList().clear();
        for (String expr : exprs) {
            addColumn(expr);
        }
        return this;
    }

    /**
     * @title: changeColumnByColumnName
     * @desc 替换指定的查询字段
     */
    public Select changeColumnByColumnName(String columnName, String expr) {
        try {
            SQLExpr expression = ExpressionBuilder.parse(expr);
            return changeColumnByColumnName(columnName, expression);
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_008);
        }
    }

    public Select changeColumnByColumnName(String columnName, SQLExpr expr) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        List<SQLSelectItem> selectItems = queryBlock.getSelectList();
        List<SQLSelectItem> list = new ArrayList<>();
        for (SQLSelectItem selectItem : selectItems) {
            SQLExpr expression = selectItem.getExpr();
            if (expression instanceof SQLIdentifierExpr && columnName.equals(((SQLIdentifierExpr) expression).getName())) {
                list.add(new SQLSelectItem(expr));
            } else if (expression instanceof SQLPropertyExpr && columnName.equals(((SQLPropertyExpr) expression).getName())) {
                list.add(new SQLSelectItem(expr));
            } else {
                list.add(selectItem);
            }
        }
        selectItems.clear();
        for (SQLSelectItem selectItem : list) {
            queryBlock.addSelectItem(selectItem);
        }
        return this;
    }

    public Select changeColumnByColumnName(String columnName, String expr, String alias) {
        try {
            SQLExpr expression = ExpressionBuilder.parse(expr);
            return changeColumnByColumnName(columnName, expression, alias);
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_008);
        }
    }

    public Select changeColumnByColumnName(String columnName, SQLExpr expr, String alias) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        List<SQLSelectItem> selectItems = queryBlock.getSelectList();
        List<SQLSelectItem> list = new ArrayList<>();
        for (SQLSelectItem selectItem : selectItems) {
            SQLExpr expression = selectItem.getExpr();
            if (expression instanceof SQLIdentifierExpr && columnName.equals(((SQLIdentifierExpr) expression).getName())) {
                list.add(new SQLSelectItem(expr, alias));
            } else if (expression instanceof SQLPropertyExpr && columnName.equals(((SQLPropertyExpr) expression).getName())) {
                list.add(new SQLSelectItem(expr, alias));
            } else {
                list.add(selectItem);
            }
        }
        selectItems.clear();
        for (SQLSelectItem selectItem : list) {
            queryBlock.addSelectItem(selectItem);
        }
        return this;
    }

    /**
     * @title: parse
     * @desc Select解析器
     */
    public static Select parse(String sql) {
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, SqlParseConfig.getDbType());
        SQLSelectStatement select = (SQLSelectStatement) sqlStatementList.get(0);
        return new Select(select);
    }

    public Select clone() {
        return new Select(this.select.clone());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        AsInterceptorUtil.checkSQLSelectQueryBlock(queryBlock, columnInterceptor, tableInterceptor, list);
        for (Change change : list) {
            ownerInterceptor((owner) -> {
                if ((owner.isDisable() && change.getOldExpr() == null) || (!owner.isDisable() && owner.getName().equals(change.getOldExpr()))) {
                    if (change.getNewExpr() == null) {
                        owner.setDisable(true);
                    } else {
                        owner.setName(change.getNewExpr());
                    }
                }
            });
        }
    }

    public void argInterceptor(Consumer<Arg> interceptor) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        ArgInterceptorUtil.checkSQLSelectQueryBlock(queryBlock, interceptor);
    }

    public void expressionInterceptor(Consumer<Expression> interceptor) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        ExpressionInterceptorUtil.checkSQLSelectQueryBlock(queryBlock, interceptor);
    }

    public void columnInterceptor(Consumer<Column> interceptor) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        ColumnInterceptorUtil.checkSQLSelectQueryBlock(queryBlock, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        SQLSelectQueryBlock queryBlock = select.getSelect().getQueryBlock();
        OwnerInterceptorUtil.checkSQLSelectQueryBlock(queryBlock, interceptor);
    }
}
