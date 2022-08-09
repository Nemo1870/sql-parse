package com.sql.parse.util.druid;

import com.sql.parse.model.Owner;
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
public class OwnerInterceptorUtil {
    public static SQLExpr checkExpression(SQLExpr expression, Consumer<Owner> interceptor) {
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
            select.ownerInterceptor(interceptor);
            ((SQLInSubQueryExpr) expression).setSubQuery(select.getSelect().getSelect());
            return expression;
        } else if (expression instanceof SQLPropertyExpr) {
            String tableName = ((SQLPropertyExpr) expression).getOwnerName();
            Owner owner = new Owner(tableName);
            interceptor.accept(owner);
            return changeOwner(owner, ((SQLPropertyExpr) expression).getName());
        } else if (expression instanceof SQLIdentifierExpr) {
            String lowerColumnName = ((SQLIdentifierExpr) expression).getLowerName();
            if (!"true".equals(lowerColumnName) && !"false".equals(lowerColumnName)) {
                Owner owner = new Owner(null);
                owner.setDisable(true);
                interceptor.accept(owner);
                return changeOwner(owner, ((SQLIdentifierExpr) expression).getName());
            } else {
                return expression;
            }
        } else {
            return expression;
        }
    }

    public static void checkSQLTableSource(SQLTableSource sqlTableSource, Consumer<Owner> interceptor) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect sqlSelect = ((SQLSubqueryTableSource) sqlTableSource).getSelect();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.ownerInterceptor(interceptor);
            ((SQLSubqueryTableSource) sqlTableSource).setSelect(select.getSelect().getSelect());
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getLeft(), interceptor);
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getRight(), interceptor);
            SQLExpr child = checkExpression(((SQLJoinTableSource) sqlTableSource).getCondition(), interceptor);
            ((SQLJoinTableSource) sqlTableSource).setCondition(child);
        } else if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExpr child = checkExpression((((SQLExprTableSource) sqlTableSource).getExpr()), interceptor);
            ((SQLExprTableSource) sqlTableSource).setExpr(child);
        }
    }

    public static void checkSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock, Consumer<Owner> interceptor) {
        List<SQLSelectItem> selectItems = queryBlock.getSelectList();
        for (SQLSelectItem sqlSelectItem : selectItems) {
            SQLExpr child = checkExpression(sqlSelectItem.getExpr(), interceptor);
            sqlSelectItem.setExpr(child);
        }
        SQLTableSource from = queryBlock.getFrom();
        checkSQLTableSource(from, interceptor);
        SQLExpr where = queryBlock.getWhere();
        checkExpression(where, interceptor);
    }

    private static SQLExpr changeOwner(Owner owner, String columnName) {
        if (owner.getName() == null) {
            return new SQLIdentifierExpr(columnName);
        } else {
            return new SQLPropertyExpr(owner.getName(), columnName);
        }
    }
}
