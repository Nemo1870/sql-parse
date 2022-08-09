package com.sql.parse.util.druid;

import com.sql.parse.expression.ValueTypeEnum;
import com.sql.parse.model.Arg;
import com.sql.parse.statement.impl.druid.Select;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 17:22
 * @desc TODO
 */
public class ArgInterceptorUtil {
    public static SQLExpr checkExpression(SQLExpr expression, Consumer<Arg> interceptor) {
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
            select.argInterceptor(interceptor);
            ((SQLInSubQueryExpr) expression).setSubQuery(select.getSelect().getSelect());
            return expression;
        } else if (expression instanceof SQLBigIntExpr) {
            Arg arg = new Arg(String.valueOf(((SQLBigIntExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLBinaryExpr) {
            Arg arg = new Arg(String.valueOf(((SQLBinaryExpr) expression).getValue().longValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } if (expression instanceof SQLBooleanExpr) {
            Arg arg = new Arg(String.valueOf(((SQLBooleanExpr) expression).getValue()), ValueTypeEnum.BOOLEAN);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLCharExpr) {
            Arg arg = new Arg(String.valueOf(((SQLCharExpr) expression).getValue()), ValueTypeEnum.STRING);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLDateExpr) {
            Arg arg = new Arg(((SQLDateExpr) expression).getValue(), ValueTypeEnum.DATE);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLDateTimeExpr) {
            Arg arg = new Arg(((SQLDateTimeExpr) expression).getValue(), ValueTypeEnum.DATETIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLDecimalExpr) {
            Arg arg = new Arg(String.valueOf(((SQLDecimalExpr) expression).getValue().doubleValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLDoubleExpr) {
            Arg arg = new Arg(String.valueOf(((SQLDoubleExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLFloatExpr) {
            Arg arg = new Arg(String.valueOf(((SQLFloatExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLHexExpr) {
            Arg arg = new Arg(new String(((SQLHexExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLIntegerExpr) {
            Arg arg = new Arg(String.valueOf(((Number) ((SQLIntegerExpr) expression).getValue()).intValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLJSONExpr) {
            Arg arg = new Arg(((SQLJSONExpr) expression).getValue(), ValueTypeEnum.STRING);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLNumberExpr) {
            Arg arg = new Arg(String.valueOf(((SQLNumberExpr) expression).getValue().intValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLRealExpr) {
            Arg arg = new Arg(String.valueOf(((SQLRealExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLSmallIntExpr) {
            Arg arg = new Arg(String.valueOf(((SQLSmallIntExpr) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLTimeExpr) {
            Arg arg = new Arg(((SQLTimeExpr) expression).getValue(), ValueTypeEnum.TIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLTimestampExpr) {
            Arg arg = new Arg(((SQLTimestampExpr) expression).getValue(), ValueTypeEnum.DATETIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLTinyIntExpr) {
            Arg arg = new Arg(String.valueOf(((SQLTinyIntExpr) expression).getValue().intValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof SQLMethodInvokeExpr) {
            List<SQLExpr> params = ((SQLMethodInvokeExpr) expression).getArguments();
            for (int i = 0; i < params.size(); i++) {
                SQLExpr expr = params.get(i);
                SQLExpr child = checkExpression(expr, interceptor);
                if (child != null) {
                    ((SQLMethodInvokeExpr) expression).setArgument(i, child);
                }
            }
            return expression;
        } else if (expression instanceof SQLIdentifierExpr) {
            String columnName = ((SQLIdentifierExpr) expression).getLowerName();
            if ("true".equals(columnName) || "false".equals(columnName)) {
                Arg arg = new Arg(columnName, ValueTypeEnum.BOOLEAN);
                interceptor.accept(arg);
                return changeArg(arg);
            } else {
                return expression;
            }
        } else if (expression instanceof SQLNullExpr) {
            Arg arg = new Arg(null, ValueTypeEnum.NULL);
            interceptor.accept(arg);
            return changeArg(arg);
        } else {
            return expression;
        }
    }

    private static SQLExpr changeArg(Arg arg) {
        if (arg.isDisable()) {
            return new SQLNullExpr();
        } else if (arg.getType().resolve() == ValueTypeEnum.NULL.resolve()) {
            return new SQLNullExpr();
        } else if (arg.getType().resolve() == ValueTypeEnum.STRING.resolve()) {
            return new SQLCharExpr(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.NUMBER.resolve()) {
            String expr = arg.getExpression();
            if (expr.contains(".")) {
                return new SQLNumberExpr(Double.valueOf(expr));
            } else {
                return new SQLNumberExpr(Integer.valueOf(expr));
            }
        } else if (arg.getType().resolve() == ValueTypeEnum.DATE.resolve()) {
            return new SQLDateExpr(arg.getExpression());
        }  else if (arg.getType().resolve() == ValueTypeEnum.TIME.resolve()) {
            return new SQLTimeExpr(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.DATETIME.resolve()) {
            return new SQLDateTimeExpr(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.BOOLEAN.resolve()) {
            return new SQLBooleanExpr(Boolean.valueOf(arg.getExpression()));
        } else {
            return null;
        }
    }

    public static void checkSQLTableSource(SQLTableSource sqlTableSource, Consumer<Arg> interceptor) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect sqlSelect = ((SQLSubqueryTableSource) sqlTableSource).getSelect();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.argInterceptor(interceptor);
            ((SQLSubqueryTableSource) sqlTableSource).setSelect(select.getSelect().getSelect());
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getLeft(), interceptor);
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getRight(), interceptor);
            SQLExpr child = checkExpression(((SQLJoinTableSource) sqlTableSource).getCondition(), interceptor);
            ((SQLJoinTableSource) sqlTableSource).setCondition(child);
        }
    }

    public static void checkSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock, Consumer<Arg> interceptor) {
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
