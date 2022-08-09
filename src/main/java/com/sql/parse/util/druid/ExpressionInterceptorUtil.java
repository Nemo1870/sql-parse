package com.sql.parse.util.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.sql.parse.expression.OperatorTypeEnum;
import com.sql.parse.expression.druid.ExpressionBuilder;
import com.sql.parse.model.*;
import com.sql.parse.statement.impl.druid.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/7 15:37
 * @desc TODO
 */
public class ExpressionInterceptorUtil {
    public static SQLExpr checkExpression(SQLExpr expression, Consumer<Expression> interceptor) {
        if (expression instanceof SQLBinaryOpExpr) {
            if (SQLBinaryOperator.Is.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.IS_NULL
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.IsNot.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.IS_NOT_NULL
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.Equality.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.NotEqual.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.NOT_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.GreaterThan.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.GREATER
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.GreaterThanOrEqual.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.GREATER_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.LessThan.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.MINOR
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.LessThanOrEqual.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                Expression expr = new Expression(
                        SQLUtils.toSQLString(expression),
                        OperatorTypeEnum.MINOR_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.Like.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
                ((SQLBinaryOpExpr) expression).setLeft(child);
                LikeExpression expr = new LikeExpression(
                        SQLUtils.toSQLString(expression),
                        false,
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getLeft()),
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getRight())
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.NotLike.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
                ((SQLBinaryOpExpr) expression).setLeft(child);
                LikeExpression expr = new LikeExpression(
                        SQLUtils.toSQLString(expression),
                        true,
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getLeft()),
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getRight())
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.RegExp.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
                ((SQLBinaryOpExpr) expression).setLeft(child);
                RegexpExpression expr = new RegexpExpression(
                        SQLUtils.toSQLString(expression),
                        false,
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getLeft()),
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getRight())
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (SQLBinaryOperator.RLike.getName().equals(((SQLBinaryOpExpr) expression).getOperator().getName())) {
                SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
                ((SQLBinaryOpExpr) expression).setLeft(child);
                RegexpExpression expr = new RegexpExpression(
                        SQLUtils.toSQLString(expression),
                        true,
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getLeft()),
                        SQLUtils.toSQLString(((SQLBinaryOpExpr) expression).getRight())
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else {
                SQLExpr child = checkExpression(((SQLBinaryOpExpr) expression).getLeft(), interceptor);
                ((SQLBinaryOpExpr) expression).setLeft(child);
                child = checkExpression(((SQLBinaryOpExpr) expression).getRight(), interceptor);
                ((SQLBinaryOpExpr) expression).setRight(child);
                return expression;
            }
        } else if (expression instanceof SQLBetweenExpr) {
            SQLExpr child = checkExpression(((SQLBetweenExpr) expression).getTestExpr(), interceptor);
            ((SQLBetweenExpr) expression).setTestExpr(child);
            Expression expr = new Expression(
                    SQLUtils.toSQLString(expression),
                    OperatorTypeEnum.BETWEEN
            );
            interceptor.accept(expr);
            return ExpressionBuilder.parse(expr.getExpression());
        } else if (expression instanceof SQLInListExpr) {
            SQLExpr leftExpression = ((SQLInListExpr) expression).getExpr();
            SQLExpr child = checkExpression(leftExpression, interceptor);
            ((SQLInListExpr) expression).setExpr(child);
            List<String> list = new ArrayList<>();
            List<SQLExpr> rightExpressions = ((SQLInListExpr) expression).getTargetList();
            for (SQLExpr rightExpression : rightExpressions) {
                list.add(SQLUtils.toSQLString(rightExpression));
            }
            InListExpression expr = new InListExpression(
                    SQLUtils.toSQLString(expression),
                    ((SQLInListExpr) expression).isNot(),
                    SQLUtils.toSQLString(leftExpression),
                    list
            );
            interceptor.accept(expr);
            return ExpressionBuilder.parse(expr.getExpression());
        } else if (expression instanceof SQLInSubQueryExpr) {
            SQLExpr leftExpression = ((SQLInSubQueryExpr) expression).getExpr();
            SQLExpr child = checkExpression(leftExpression, interceptor);
            ((SQLInSubQueryExpr) expression).setExpr(child);
            SQLSelect subSelect = ((SQLInSubQueryExpr) expression).getSubQuery();
            Select select = Select.parse(SQLUtils.toSQLString(subSelect));
            select.expressionInterceptor(interceptor);
            ((SQLInSubQueryExpr) expression).setSubQuery(select.getSelect().getSelect());
            InExpression expr = new InExpression(
                    SQLUtils.toSQLString(expression),
                    ((SQLInSubQueryExpr) expression).isNot()
            );
            interceptor.accept(expr);
            return ExpressionBuilder.parse(expr.getExpression());
        } else {
            return expression;
        }
    }

    public static void checkSQLTableSource(SQLTableSource sqlTableSource, Consumer<Expression> interceptor) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect sqlSelect = ((SQLSubqueryTableSource) sqlTableSource).getSelect();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.expressionInterceptor(interceptor);
            ((SQLSubqueryTableSource) sqlTableSource).setSelect(select.getSelect().getSelect());
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getLeft(), interceptor);
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getRight(), interceptor);
            SQLExpr child = checkExpression(((SQLJoinTableSource) sqlTableSource).getCondition(), interceptor);
            ((SQLJoinTableSource) sqlTableSource).setCondition(child);
        }
    }

    public static void checkSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock, Consumer<Expression> interceptor) {
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
}
