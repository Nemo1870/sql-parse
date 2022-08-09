package com.sql.parse.util.druid;

import com.sql.parse.model.Column;
import com.sql.parse.statement.impl.druid.Select;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 11:34
 * @desc TODO
 */
public class ColumnInterceptorUtil {
    public static SQLExpr checkExpression(SQLExpr expression, Consumer<Column> interceptor) {
        if (expression instanceof SQLBinaryOpExpr) {
            SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
            ((SQLBinaryOpExpr) expression).setLeft(child);
            child = checkExpression(((SQLBinaryOpExpr) expression).getRight(), interceptor);
            ((SQLBinaryOpExpr) expression).setRight(child);
            return expression;
        } else if (expression instanceof SQLBetweenExpr) {
            SQLExpr child = checkExpression(((SQLBetweenExpr) expression).getTestExpr(), interceptor);
            ((SQLBetweenExpr) expression).setTestExpr(child);
            child = checkExpression(((SQLBetweenExpr) expression).getBeginExpr(), interceptor);
            ((SQLBetweenExpr) expression).setBeginExpr(child);
            child = checkExpression(((SQLBetweenExpr) expression).getEndExpr(), interceptor);
            ((SQLBetweenExpr) expression).setEndExpr(child);
            return expression;
        } else if (expression instanceof SQLInListExpr) {
            SQLExpr leftExpression = ((SQLInListExpr) expression).getExpr();
            SQLExpr child = checkExpression(leftExpression, interceptor);
            ((SQLInListExpr) expression).setExpr(child);
            List<SQLExpr> rightExpressions = ((SQLInListExpr) expression).getTargetList();
            List<SQLExpr> newExpr = new ArrayList<>();
            for (SQLExpr expr : rightExpressions) {
                child = checkExpression(expr, interceptor);
                if (child != null) {
                    newExpr.add(child);
                }
            }
            ((SQLInListExpr) expression).setTargetList(newExpr);
            return expression;
        } else if (expression instanceof SQLInSubQueryExpr) {
            SQLExpr leftExpression = ((SQLInSubQueryExpr) expression).getExpr();
            SQLExpr child = checkExpression(leftExpression, interceptor);
            ((SQLInSubQueryExpr) expression).setExpr(child);
            SQLSelect subSelect = ((SQLInSubQueryExpr) expression).getSubQuery();
            Select select = Select.parse(SQLUtils.toSQLString(subSelect));
            select.columnInterceptor(interceptor);
            ((SQLInSubQueryExpr) expression).setSubQuery(select.getSelect().getSelect());
            return expression;
        } else if (expression instanceof SQLIdentifierExpr) {
            String columnName = ((SQLIdentifierExpr) expression).getName();
            String lowerColumnName = ((SQLIdentifierExpr) expression).getLowerName();
            if (!"true".equals(lowerColumnName) && !"false".equals(lowerColumnName)) {
                Column column = new Column(
                        null,
                        columnName
                );
                interceptor.accept(column);
                return changeColumn(column);
            } else {
                return expression;
            }
        } else if (expression instanceof SQLPropertyExpr) {
            String tableName = ((SQLPropertyExpr) expression).getOwnerName();
            String columnName = ((SQLPropertyExpr) expression).getName();
            Column column = new Column(
                    tableName,
                    columnName
            );
            interceptor.accept(column);
            return changeColumn(column);
        } else {
            return expression;
        }
    }

    public static void checkSQLTableSource(SQLTableSource sqlTableSource, Consumer<Column> interceptor) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect sqlSelect = ((SQLSubqueryTableSource) sqlTableSource).getSelect();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.columnInterceptor(interceptor);
            ((SQLSubqueryTableSource) sqlTableSource).setSelect(select.getSelect().getSelect());
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getLeft(), interceptor);
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getRight(), interceptor);
            SQLExpr child = checkExpression(((SQLJoinTableSource) sqlTableSource).getCondition(), interceptor);
            ((SQLJoinTableSource) sqlTableSource).setCondition(child);
        }
    }

    public static void checkSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock, Consumer<Column> interceptor) {
        List<SQLSelectItem> selectItems = queryBlock.getSelectList();
        for (SQLSelectItem selectItem : selectItems) {
            SQLExpr child = checkExpression(selectItem.getExpr(), interceptor);
            selectItem.setExpr(child);
        }
        SQLTableSource from = queryBlock.getFrom();
        checkSQLTableSource(from, interceptor);
        SQLExpr where = queryBlock.getWhere();
        SQLExpr child = checkExpression(where, interceptor);
        queryBlock.setWhere(child);
    }

    private static SQLExpr changeColumn(Column column) {
        if (column.getTableName() == null) {
            return new SQLIdentifierExpr(column.getColumnName());
        } else {
            return new SQLPropertyExpr(column.getTableName(), column.getColumnName());
        }
    }
}
