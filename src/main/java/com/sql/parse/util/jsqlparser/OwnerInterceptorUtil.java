package com.sql.parse.util.jsqlparser;

import com.sql.parse.model.Owner;
import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.model.BetweenExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 11:34
 * @desc TODO
 */
public class OwnerInterceptorUtil {
    public static Expression checkExpression(Expression expression, Consumer<Owner> interceptor) {
        if (expression instanceof BinaryExpression) {
            Expression child = checkExpression(((BinaryExpression) expression).getLeftExpression(), interceptor);
            ((BinaryExpression) expression).setLeftExpression(child);
            child = checkExpression(((BinaryExpression) expression).getRightExpression(), interceptor);
            ((BinaryExpression) expression).setRightExpression(child);
            return expression;
        } else if (expression instanceof Parenthesis) {
            Expression child = checkExpression(((Parenthesis) expression).getExpression(), interceptor);
            ((Parenthesis) expression).setExpression(child);
            return expression;
        } else if (expression instanceof InExpression) {
            Expression leftExpression = ((InExpression) expression).getLeftExpression();
            Expression child = checkExpression(leftExpression, interceptor);
            ((InExpression) expression).setLeftExpression(child);
            Expression rightExpression = ((InExpression) expression).getRightExpression();
            ItemsList itemsList = ((InExpression) expression).getRightItemsList();
            if (itemsList == null) {
                child = checkExpression(rightExpression, interceptor);
                ((InExpression) expression).setRightExpression(child);
            } else if (itemsList instanceof ExpressionList) {
                ExpressionList rightExpressionList = ((ExpressionList) ((InExpression) expression).getRightItemsList());
                List<Expression> rightExpressions = rightExpressionList.getExpressions();
                List<Expression> newExpr = new ArrayList<>();
                for (Expression expr : rightExpressions) {
                    child = checkExpression(expr, interceptor);
                    if (child != null) {
                        newExpr.add(child);
                    }
                }
                rightExpressionList.setExpressions(newExpr);
            } else if (itemsList instanceof SubSelect) {
                child = checkExpression((SubSelect) itemsList, interceptor);
                ((InExpression) expression).setRightItemsList((SubSelect) child);
            }
            return expression;
        } else if (expression instanceof SubSelect) {
            com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) expression).getSelectBody().toString());
            select.ownerInterceptor(interceptor);
            ((SubSelect) expression).setSelectBody(select.getSelect().getSelectBody());
            return expression;
        } else if (expression instanceof BetweenExpression) {
            Expression child = checkExpression(((Between) expression).getLeftExpression(), interceptor);
            ((Between) expression).setLeftExpression(child);
            child = checkExpression(((Between) expression).getBetweenExpressionStart(), interceptor);
            ((Between) expression).setBetweenExpressionStart(child);
            child = checkExpression(((Between) expression).getBetweenExpressionEnd(), interceptor);
            ((Between) expression).setBetweenExpressionEnd(child);
            return expression;
        } else if (expression instanceof net.sf.jsqlparser.schema.Column) {
            Owner newOwner;
            if (((net.sf.jsqlparser.schema.Column) expression).getTable() == null) {
                newOwner = new Owner(null);
                newOwner.setDisable(true);
            } else {
                newOwner = new Owner(
                        ((net.sf.jsqlparser.schema.Column) expression).getTable().getName()
                );
            }
            interceptor.accept(newOwner);
            if (newOwner.getName() == null) {
                ((net.sf.jsqlparser.schema.Column) expression).setTable(null);
            } else if (((net.sf.jsqlparser.schema.Column) expression).getTable() == null) {
                ((net.sf.jsqlparser.schema.Column) expression).setTable(new Table(newOwner.getName()));
            } else {
                ((net.sf.jsqlparser.schema.Column) expression).getTable().setName(newOwner.getName());
            }
            return expression;
        } else if (expression instanceof IsNullExpression) {
            Expression child = checkExpression(((IsNullExpression) expression).getLeftExpression(), interceptor);
            ((IsNullExpression) expression).setLeftExpression(child);
            return expression;
        } else {
            return expression;
        }
    }

    public static void checkFrom(FromItem fromItem, Consumer<Owner> interceptor) {
        if (fromItem instanceof SubSelect) {
            com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) fromItem).getSelectBody().toString());
            select.ownerInterceptor(interceptor);
            ((SubSelect) fromItem).setSelectBody(select.getSelect().getSelectBody());
        }
    }

    public static void checkJoins(List<Join> joins, Consumer<Owner> interceptor) {
        if (joins != null) {
            for (Join join : joins) {
                FromItem fromItem = join.getRightItem();
                checkFrom(fromItem, interceptor);
                Expression on = join.getOnExpression();
                Expression child = checkExpression(on, interceptor);
                join.setOnExpression(child);
            }
        }
    }

    public static void checkPlainSelect(PlainSelect plainSelect, Consumer<Owner> interceptor) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {
            if (selectItem instanceof SelectExpressionItem) {
                Expression expression = ((SelectExpressionItem) selectItem).getExpression();
                Expression child = checkExpression(expression, interceptor);
                ((SelectExpressionItem) selectItem).setExpression(child);
            }
        }
        List<Join> joins = plainSelect.getJoins();
        checkJoins(joins, interceptor);
        FromItem fromItem = plainSelect.getFromItem();
        checkFrom(fromItem, interceptor);
        Expression where = plainSelect.getWhere();
        Expression child = checkExpression(where, interceptor);
        plainSelect.setWhere(child);
    }
}
