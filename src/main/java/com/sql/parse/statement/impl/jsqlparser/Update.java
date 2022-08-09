package com.sql.parse.statement.impl.jsqlparser;

import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import com.sql.parse.model.Alias;
import com.sql.parse.model.Arg;
import com.sql.parse.model.Change;
import com.sql.parse.model.Owner;
import com.sql.parse.schema.UpdateItem;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Update语法操作类
 */
public class Update extends com.sql.parse.statement.Update {
    private net.sf.jsqlparser.statement.update.Update update;

    public Update(net.sf.jsqlparser.statement.update.Update update) {
        this.update = update;
    }

    public Update(String tableName) {
        net.sf.jsqlparser.statement.update.Update update = new net.sf.jsqlparser.statement.update.Update();
        Table table = new Table(tableName);
        update.setTable(table);
        this.update = update;
    }

    @Override
    public String getSql(boolean safe) {
        Update myUpdate = this.clone();
        /*where条件为空时，添加1=2的条件表达式*/
        if (safe && myUpdate.getUpdate().getWhere() == null) {
            myUpdate.where("1 = 2");
        }
        return myUpdate.getUpdate().toString();
    }

    public net.sf.jsqlparser.statement.update.Update getUpdate() {
        return this.update;
    }

    /**
     * @title: addColumn
     * @desc 新增update数据
     */
    public Update addColumn(UpdateItem updateItem) {
        if (update.getColumns() == null) {
            update.setColumns(new ArrayList<>());
        }
        if (update.getExpressions() == null) {
            update.setExpressions(new ArrayList<>());
        }
        update.getColumns().add(new Column(updateItem.getColumnName()));
        try {
            Expression expression = ExpressionBuilder.parse(updateItem.getExpression());
            update.getExpressions().add(expression);
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_002);
        }
        return this;
    }

    public Update addColumn(String column, String value) {
        return addColumn(new UpdateItem(column, value));
    }

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public Update where(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        where(expression);
        return this;
    }

    public Update where(Expression where) {
        Expression oldWhere = update.getWhere();
        if (oldWhere == null) {
            update.setWhere(where);
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_004);
        }
        return this;
    }

    /**
     * @title: and
     * @desc 移除where条件
     */
    public Update removeAllWhere() {
        update.setWhere(null);
        return this;
    }


    /**
     * @title: and
     * @desc "AND"符号
     */
    public Update and(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        and(expression);
        return this;
    }

    public Update and(Expression where) {
        Expression oldWhere = update.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_005);
        } else {
            update.setWhere(ExpressionBuilder.and(oldWhere, where));
        }
        return this;
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public Update or(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        or(expression);
        return this;
    }

    public Update or(Expression where) {
        Expression oldWhere = update.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_006);
        } else {
            update.setWhere(ExpressionBuilder.or(oldWhere, where));
        }
        return this;
    }

    /**
     * @title: orderBy
     * @desc "ORDER BY"符号
     *//*
    public Update orderBy(String... columnNames) {
        checkColumnNames(Arrays.asList(columnNames));
        List<OrderByElement> orderBys = update.getOrderByElements();
        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (String columnName : columnNames) {
            OrderByElement orderBy = new OrderByElement();
            orderBy.setExpression(new Column(columnName));
            newOrderBys.add(orderBy);
        }
        update.setOrderByElements(newOrderBys);
        return this;
    }

    public Update orderBy(OrderByItem... orderByItems) {
        List<String> columnNames = new ArrayList<String>();
        for (OrderByItem orderByElement : orderByItems) {
            Expression expression = orderByElement.getOrderBy().getExpression();
            if (expression instanceof Column) {
                columnNames.add(((Column) expression).getColumnName());
            }
        }
        checkColumnNames(columnNames);
        List<OrderByElement> orderBys = update.getOrderByElements();
        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (OrderByItem orderByItem : orderByItems) {
            newOrderBys.add(orderByItem.getOrderBy());
        }
        update.setOrderByElements(newOrderBys);
        return this;
    }*/

    /**
     * @title: limit
     * @desc "LIMIT"符号
     *//*
    public Update limit(int offset, int rows) {
        Limit limit = new Limit();
        limit.setRowCount(new LongValue(rows));
        limit.setOffset(new LongValue(offset));
        update.setLimit(limit);
        return this;
    }
    */

    /**
     * @title: parse
     * @desc Update解析器
     */
    public static Update parse(String sql) {
        try {
            net.sf.jsqlparser.statement.update.Update update = (net.sf.jsqlparser.statement.update.Update) CCJSqlParserUtil.parse(sql);
            return new Update(update);
        } catch (JSQLParserException e) {
            throw BaseException.error(e);
        }
    }

    public Update clone() {
        return Update.parse(this.update.toString());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        Expression where = update.getWhere();
        AsInterceptorUtil.checkExpression(where, columnInterceptor, tableInterceptor, list);
        List<Join> joins = update.getJoins();
        AsInterceptorUtil.checkJoins(joins, columnInterceptor, tableInterceptor, list);
        Table table = update.getTable();
        AsInterceptorUtil.checkFrom(table, columnInterceptor, tableInterceptor, list);
        for (Change change : list) {
            ownerInterceptor((owner) -> {
                if ((owner.getName() == null && change.getOldExpr() == null) || (owner.getName().equals(change.getOldExpr()))) {
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
        List<Expression> expressions = update.getExpressions();
        List<Expression> newExpr = new ArrayList<>();
        for (Expression expression : expressions) {
            Expression expr = ArgInterceptorUtil.checkExpression(expression, interceptor);
            newExpr.add(expr);
        }
        update.setExpressions(newExpr);
        Expression where = update.getWhere();
        if (where != null) {
            Expression child = ArgInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        List<Join> joins = update.getJoins();
        ArgInterceptorUtil.checkJoins(joins, interceptor);
        Table table = update.getTable();
        ArgInterceptorUtil.checkFrom(table, interceptor);
    }

    public void expressionInterceptor(Consumer<com.sql.parse.model.Expression> interceptor) {
        Expression where = update.getWhere();
        if (where != null) {
            Expression child = ExpressionInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        List<Join> joins = update.getJoins();
        ExpressionInterceptorUtil.checkJoins(joins, interceptor);
        Table table = update.getTable();
        ExpressionInterceptorUtil.checkFrom(table, interceptor);
    }

    public void columnInterceptor(Consumer<com.sql.parse.model.Column> interceptor) {
        List<Column> columns = update.getColumns();
        for (Column column : columns) {
            com.sql.parse.model.Column newColumn = new com.sql.parse.model.Column(
                    column.getTable() == null ? null : column.getTable().getName(),
                    column.getColumnName()
            );
            interceptor.accept(newColumn);
            column.setColumnName(newColumn.getColumnName());
            if (newColumn.getTableName() == null) {
                column.setTable(null);
            } else if (column.getTable() == null) {
                column.setTable(new Table(newColumn.getTableName()));
            } else {
                column.getTable().setName(newColumn.getTableName());
            }
        }
        List<Expression> expressions = update.getExpressions();
        List<Expression> newExpr = new ArrayList<>();
        for (Expression expression : expressions) {
            Expression expr = ColumnInterceptorUtil.checkExpression(expression, interceptor);
            newExpr.add(expr);
        }
        update.setExpressions(newExpr);
        Expression where = update.getWhere();
        if (where != null) {
            Expression child = ColumnInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        List<Join> joins = update.getJoins();
        ColumnInterceptorUtil.checkJoins(joins, interceptor);
        Table table = update.getTable();
        ColumnInterceptorUtil.checkFrom(table, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        List<Column> columns = update.getColumns();
        for (Column column : columns) {
            Table table = column.getTable();
            Owner newOwner;
            if (table == null) {
                newOwner = new Owner(null);
                newOwner.setDisable(true);
            } else {
                newOwner = new Owner(table.getName());
            }
            interceptor.accept(newOwner);
            if (newOwner.isDisable()) {
                column.setTable(null);
            } else if (column.getTable() == null) {
                column.setTable(new Table(newOwner.getName()));
            } else {
                column.getTable().setName(newOwner.getName());
            }
        }
        List<Expression> expressions = update.getExpressions();
        List<Expression> newExpr = new ArrayList<>();
        for (Expression expression : expressions) {
            Expression expr = OwnerInterceptorUtil.checkExpression(expression, interceptor);
            newExpr.add(expr);
        }
        update.setExpressions(newExpr);
        Expression where = update.getWhere();
        if (where != null) {
            Expression child = OwnerInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        List<Join> joins = update.getJoins();
        OwnerInterceptorUtil.checkJoins(joins, interceptor);
        Table table = update.getTable();
        OwnerInterceptorUtil.checkFrom(table, interceptor);
    }
}
