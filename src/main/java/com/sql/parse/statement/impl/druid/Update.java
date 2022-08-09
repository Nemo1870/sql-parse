package com.sql.parse.statement.impl.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.druid.ExpressionBuilder;
import com.sql.parse.model.*;
import com.sql.parse.schema.UpdateItem;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.druid.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Update语法操作类
 */
public class Update extends com.sql.parse.statement.Update {
    private SQLUpdateStatement update;

    public Update(SQLUpdateStatement update) {
        this.update = update;
    }

    public Update(String tableName) {
        SQLUpdateStatement update = new SQLUpdateStatement();
        SQLExprTableSource table = new SQLExprTableSource(tableName);
        update.setTableSource(table);
        this.update = update;
    }

    @Override
    public String getSql(boolean safe) {
        Update myUpdate = this.clone();
        /*where条件为空时，添加1=2的条件表达式*/
        if (safe && myUpdate.getUpdate().getWhere() == null) {
            myUpdate.where("1 = 2");
        }
        return SQLUtils.toSQLString(myUpdate.getUpdate());
    }

    public SQLUpdateStatement getUpdate() {
        return this.update;
    }

    /**
     * @title: addColumn
     * @desc 新增update数据
     */
    public Update addColumn(UpdateItem updateItem) {
        SQLUpdateSetItem setItem = new SQLUpdateSetItem();
        setItem.setColumn(ExpressionBuilder.parse(updateItem.getColumnName()));
        setItem.setValue(ExpressionBuilder.parse(updateItem.getExpression()));
        update.addItem(setItem);
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
        SQLExpr expression = ExpressionBuilder.parse(expr);
        where(expression);
        return this;
    }

    public Update where(SQLExpr where) {
        SQLExpr oldWhere = update.getWhere();
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
        SQLExpr expression = ExpressionBuilder.parse(expr);
        and(expression);
        return this;
    }

    public Update and(SQLExpr where) {
        SQLExpr oldWhere = update.getWhere();
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
        SQLExpr expression = ExpressionBuilder.parse(expr);
        or(expression);
        return this;
    }

    public Update or(SQLExpr where) {
        SQLExpr oldWhere = update.getWhere();
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
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, SqlParseConfig.getDbType());
        SQLUpdateStatement update = (SQLUpdateStatement) sqlStatementList.get(0);
        return new Update(update);
    }

    public Update clone() {
        return new Update(this.update.clone());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        SQLExpr where = update.getWhere();
        AsInterceptorUtil.checkExpression(where, columnInterceptor, tableInterceptor, list);
        SQLTableSource from = update.getFrom();
        AsInterceptorUtil.checkSQLTableSource(from, columnInterceptor, tableInterceptor, list);
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
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem item : items) {
            SQLExpr child = ArgInterceptorUtil.checkExpression(item.getValue(), interceptor);
            item.setValue(child);
        }
        SQLExpr where = update.getWhere();
        if (where != null) {
            SQLExpr child = ArgInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        SQLTableSource from = update.getFrom();
        ArgInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void expressionInterceptor(Consumer<Expression> interceptor) {
        SQLExpr where = update.getWhere();
        if (where != null) {
            SQLExpr child = ExpressionInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        SQLTableSource from = update.getFrom();
        ExpressionInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void columnInterceptor(Consumer<Column> interceptor) {
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem item : items) {
            SQLExpr child = ColumnInterceptorUtil.checkExpression(item.getColumn(), interceptor);
            item.setColumn(child);
        }
        SQLExpr where = update.getWhere();
        if (where != null) {
            SQLExpr child = ColumnInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        SQLTableSource from = update.getFrom();
        ColumnInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem item : items) {
            SQLExpr child = OwnerInterceptorUtil.checkExpression(item.getColumn(), interceptor);
            item.setColumn(child);
        }
        SQLExpr where = update.getWhere();
        if (where != null) {
            SQLExpr child = OwnerInterceptorUtil.checkExpression(where, interceptor);
            update.setWhere(child);
        }
        SQLTableSource from = update.getFrom();
        OwnerInterceptorUtil.checkSQLTableSource(from, interceptor);
    }
}
