package com.sql.parse.util.jsqlparser;

import com.sql.parse.expression.ValueTypeEnum;
import com.sql.parse.model.Arg;
import com.sql.parse.statement.impl.jsqlparser.Select;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 17:22
 * @desc TODO
 */
public class ArgInterceptorUtil {
    public static Expression checkExpression(Expression expression, Consumer<Arg> interceptor) {
        if (expression instanceof BinaryExpression) {
            Expression child = checkExpression(((BinaryExpression) expression).getLeftExpression(), interceptor);
            ((BinaryExpression) expression).setLeftExpression(child);
            child = checkExpression(((BinaryExpression) expression).getRightExpression(), interceptor);
            ((BinaryExpression) expression).setRightExpression(child);
            return expression;
        } else if (expression instanceof Between) {
            Expression child = checkExpression(((Between) expression).getLeftExpression(), interceptor);
            ((Between) expression).setLeftExpression(child);
            child = checkExpression(((Between) expression).getBetweenExpressionStart(), interceptor);
            ((Between) expression).setBetweenExpressionStart(child);
            child = checkExpression(((Between) expression).getBetweenExpressionEnd(), interceptor);
            ((Between) expression).setBetweenExpressionEnd(child);
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
        } else if (expression instanceof Parenthesis) {
            Expression child = checkExpression(((Parenthesis) expression).getExpression(), interceptor);
            ((Parenthesis) expression).setExpression(child);
            return expression;
        } else if (expression instanceof SubSelect) {
            com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) expression).getSelectBody().toString());
            select.argInterceptor(interceptor);
            ((SubSelect) expression).setSelectBody(select.getSelect().getSelectBody());
            return expression;
        } else if (expression instanceof DateTimeLiteralExpression) {
            Arg arg = new Arg(((DateTimeLiteralExpression) expression).getValue(), ValueTypeEnum.DATETIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof DateValue) {
            Arg arg = new Arg(String.valueOf(((DateValue) expression).getValue()), ValueTypeEnum.DATE);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof DoubleValue) {
            Arg arg = new Arg(String.valueOf(((DoubleValue) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof Function) {
            ExpressionList params = ((Function) expression).getParameters();
            List<Expression> expressions = params.getExpressions();
            List<Expression> newExpr = new ArrayList<>();
            for (Expression expr : expressions) {
                Expression child = checkExpression(expr, interceptor);
                if (child != null) {
                    newExpr.add(child);
                }
            }
            params.setExpressions(newExpr);
            return expression;
        } else if (expression instanceof HexValue) {
            Arg arg = new Arg(String.valueOf(((HexValue) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof LongValue) {
            Arg arg = new Arg(String.valueOf(((LongValue) expression).getValue()), ValueTypeEnum.NUMBER);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof StringValue) {
            Arg arg = new Arg(((StringValue) expression).getValue(), ValueTypeEnum.STRING);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof TimestampValue) {
            Arg arg = new Arg(String.valueOf(((TimestampValue) expression).getValue()), ValueTypeEnum.DATETIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof TimeValue) {
            Arg arg = new Arg(String.valueOf(((TimeValue) expression).getValue()), ValueTypeEnum.TIME);
            interceptor.accept(arg);
            return changeArg(arg);
        } else if (expression instanceof Column) {
            String columnName = ((Column) expression).getColumnName().toLowerCase();
            if ("true".equals(columnName) || "false".equals(columnName)) {
                Arg arg = new Arg(columnName, ValueTypeEnum.BOOLEAN);
                interceptor.accept(arg);
                return changeArg(arg);
            } else {
                return expression;
            }
        } else if (expression instanceof NullValue) {
            Arg arg = new Arg(null, ValueTypeEnum.NULL);
            interceptor.accept(arg);
            return changeArg(arg);
        } else {
            return expression;
        }
    }

    private static Expression changeArg(Arg arg) {
        if (arg.isDisable()) {
            return null;
        } else if (arg.getType().resolve() == ValueTypeEnum.NULL.resolve()) {
            return new NullValue();
        } else if (arg.getType().resolve() == ValueTypeEnum.STRING.resolve()) {
            return new StringValue(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.NUMBER.resolve()) {
            return new DoubleValue(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.DATE.resolve()) {
            return new DateValue(arg.getExpression());
        }  else if (arg.getType().resolve() == ValueTypeEnum.TIME.resolve()) {
            return new TimeValue(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.DATETIME.resolve()) {
            return new TimestampValue(arg.getExpression());
        } else if (arg.getType().resolve() == ValueTypeEnum.BOOLEAN.resolve()) {
            return new Column(arg.getExpression());
        } else {
            return null;
        }
    }

    public static void checkFrom(FromItem fromItem, Consumer<Arg> interceptor) {
        if (fromItem instanceof SubSelect) {
            com.sql.parse.statement.impl.jsqlparser.Select select = Select.parse(((SubSelect) fromItem).getSelectBody().toString());
            select.argInterceptor(interceptor);
            ((SubSelect) fromItem).setSelectBody(select.getSelect().getSelectBody());
        }
    }

    public static void checkJoins(List<Join> joins, Consumer<Arg> interceptor) {
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

    public static void checkPlainSelect(PlainSelect plainSelect, Consumer<Arg> interceptor) {
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
