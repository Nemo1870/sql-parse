package com.sql.parse.util.jsqlparser;

import com.sql.parse.expression.OperatorTypeEnum;
import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.model.InListExpression;
import com.sql.parse.model.RegexpExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 17:22
 * @desc TODO
 */
public class ExpressionInterceptorUtil {
    public static Expression checkExpression(Expression expression, Consumer<com.sql.parse.model.Expression> interceptor) {
        if (expression instanceof BinaryExpression) {
            if (expression instanceof EqualsTo) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof NotEqualsTo) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.NOT_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof GreaterThan) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.GREATER
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof GreaterThanEquals) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.GREATER_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof MinorThan) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.MINOR
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof MinorThanEquals) {
                com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                        expression.toString(),
                        OperatorTypeEnum.MINOR_EQUALS
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof LikeExpression) {
                Expression leftExpression = ((LikeExpression) expression).getLeftExpression();
                Expression child = checkExpression(leftExpression, interceptor);
                ((LikeExpression) expression).setLeftExpression(child);
                com.sql.parse.model.LikeExpression expr = new com.sql.parse.model.LikeExpression(
                        expression.toString(),
                        ((LikeExpression) expression).isNot(),
                        ((LikeExpression) expression).getLeftExpression().toString(),
                        ((LikeExpression) expression).getRightExpression().toString()
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else if (expression instanceof AndExpression || expression instanceof OrExpression) {
                Expression child = checkExpression(((BinaryExpression) expression).getLeftExpression(), interceptor);
                if (child instanceof AndExpression || child instanceof  OrExpression) {
                    child = ExpressionBuilder.parenthesis(child);
                }
                ((BinaryExpression) expression).setLeftExpression(child);
                child = checkExpression(((BinaryExpression) expression).getRightExpression(), interceptor);
                if (child instanceof AndExpression || child instanceof  OrExpression) {
                    child = ExpressionBuilder.parenthesis(child);
                }
                ((BinaryExpression) expression).setRightExpression(child);
                return expression;
            } else if (expression instanceof RegExpMySQLOperator) {
                Expression leftExpression = ((RegExpMySQLOperator) expression).getLeftExpression();
                Expression child = checkExpression(leftExpression, interceptor);
                ((RegExpMySQLOperator) expression).setLeftExpression(child);
                RegexpExpression expr = new RegexpExpression(
                        expression.toString(),
                        ((RegExpMySQLOperator) expression).isUseRLike(),
                        ((RegExpMySQLOperator) expression).getLeftExpression().toString(),
                        ((RegExpMySQLOperator) expression).getRightExpression().toString()
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            } else {
                Expression child = checkExpression(((BinaryExpression) expression).getLeftExpression(), interceptor);
                ((BinaryExpression) expression).setLeftExpression(child);
                child = checkExpression(((BinaryExpression) expression).getRightExpression(), interceptor);
                ((BinaryExpression) expression).setRightExpression(child);
                return expression;
            }
        } else if(expression instanceof InExpression) {
            Expression leftExpression = ((InExpression) expression).getLeftExpression();
            Expression child = checkExpression(leftExpression, interceptor);
            ((InExpression) expression).setLeftExpression(child);
            ItemsList itemsList = ((InExpression) expression).getRightItemsList();
            if (itemsList == null) {
                child = checkExpression(((InExpression) expression).getRightExpression(), interceptor);
                ((InExpression) expression).setRightExpression(child);
                return expression;
            } else if (itemsList instanceof SubSelect) {
                com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) itemsList).getSelectBody().toString());
                select.expressionInterceptor(interceptor);
                ((SubSelect) itemsList).setSelectBody(select.getSelect().getSelectBody());
                com.sql.parse.model.InExpression expr = new com.sql.parse.model.InExpression(
                        expression.toString(),
                        ((InExpression) expression).isNot()
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            }  else {
                List<String> list = new ArrayList<>();
                ExpressionList rightExpressionList = ((ExpressionList) ((InExpression) expression).getRightItemsList());
                List<Expression> rightExpressions = rightExpressionList.getExpressions();
                for (Expression rightExpression : rightExpressions) {
                    list.add(rightExpression.toString());
                }
                InListExpression expr = new InListExpression(
                        expression.toString(),
                        ((InExpression) expression).isNot(),
                        leftExpression.toString(),
                        list
                );
                interceptor.accept(expr);
                return ExpressionBuilder.parse(expr.getExpression());
            }
        } else if (expression instanceof Between) {
            Expression child = checkExpression(((Between) expression).getLeftExpression(), interceptor);
            ((Between) expression).setLeftExpression(child);
            com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                    expression.toString(),
                    OperatorTypeEnum.BETWEEN
            );
            interceptor.accept(expr);
            return ExpressionBuilder.parse(expr.getExpression());
        } else if (expression instanceof Parenthesis) {
            Expression child = checkExpression(((Parenthesis) expression).getExpression(), interceptor);
            ((Parenthesis) expression).setExpression(child);
            return expression;
        } else if (expression instanceof IsNullExpression) {
            OperatorTypeEnum type = ((IsNullExpression) expression).isNot() ? OperatorTypeEnum.IS_NOT_NULL : OperatorTypeEnum.IS_NULL;
            com.sql.parse.model.Expression expr = new com.sql.parse.model.Expression(
                    expression.toString(),
                    type
            );
            interceptor.accept(expr);
            return ExpressionBuilder.parse(expr.getExpression());
        } else {
            return expression;
        }
    }

    public static void checkFrom(FromItem fromItem, Consumer<com.sql.parse.model.Expression> interceptor) {
        if (fromItem instanceof SubSelect) {
            com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) fromItem).getSelectBody().toString());
            select.expressionInterceptor(interceptor);
            ((SubSelect) fromItem).setSelectBody(select.getSelect().getSelectBody());
        }
    }

    public static void checkJoins(List<Join> joins, Consumer<com.sql.parse.model.Expression> interceptor) {
        if (joins != null) {
            for (Join join : joins) {
                FromItem fromItem = join.getRightItem();
                checkFrom(fromItem, interceptor);
                Expression on = join.getOnExpression();
                on = checkExpression(on, interceptor);
                join.setOnExpression(on);
            }
        }
    }

    public static void checkPlainSelect(PlainSelect plainSelect, Consumer<com.sql.parse.model.Expression> interceptor) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {
            if (selectItem instanceof SelectExpressionItem) {
                Expression child = checkExpression(((SelectExpressionItem) selectItem).getExpression(), interceptor);
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
