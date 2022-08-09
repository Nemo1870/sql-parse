package com.sql.parse.expression.druid;

import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.expression.OperatorTypeEnum;
import com.sql.parse.expression.ValueTypeEnum;
import com.sql.parse.statement.impl.druid.Select;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;

import java.util.*;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc 表达式构造器
 */
public class ExpressionBuilder {
    /**
     * @title: equalsTo
     * @desc "="符号
     */
    public static SQLBinaryOpExpr equalsTo(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.Equality, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr equalsTo(String leftExpression, String rightExpression) {
        return equalsTo(parse(leftExpression), parse(rightExpression));
    }

    public static SQLBinaryOpExpr equalsTo(EqualsTo equalsTo) {
        return equalsTo(equalsTo.getLeftExpression(), equalsTo.getRightExpression());
    }

    /**
     * @title: notEqualsTo
     * @desc "!="符号
     */
    public static SQLBinaryOpExpr notEqualsTo(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.NotEqual, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr notEqualsTo(String leftExpression, String rightExpression) {
        return notEqualsTo(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: greaterThan
     * @desc ">"符号
     */
    public static SQLBinaryOpExpr greaterThan(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.GreaterThan, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr greaterThan(String leftExpression, String rightExpression) {
        return greaterThan(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: greaterThanEquals
     * @desc ">="符号
     */
    public static SQLBinaryOpExpr greaterThanEquals(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.GreaterThanOrEqual, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr greaterThanEquals(String leftExpression, String rightExpression) {
        return greaterThanEquals(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: minorThan
     * @desc "<"符号
     */
    public static SQLBinaryOpExpr minorThan(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.LessThan, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr minorThan(String leftExpression, String rightExpression) {
        return minorThan(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: minorThanEquals
     * @desc "<="符号
     */
    public static SQLBinaryOpExpr minorThanEquals(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.LessThanOrEqual, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr minorThanEquals(String leftExpression, String rightExpression) {
        return minorThanEquals(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: isNull
     * @desc "IS NULL"符号
     */
    public static SQLBinaryOpExpr isNull(SQLExpr expression) {
        return new SQLBinaryOpExpr(expression, SQLBinaryOperator.Is, new SQLNullExpr(), SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr isNull(String expression) {
        return isNull(parse(expression));
    }

    /**
     * @title: isNotNull
     * @desc "IS NOT NULL"符号
     */
    public static SQLBinaryOpExpr isNotNull(SQLExpr expression) {
        return new SQLBinaryOpExpr(expression, SQLBinaryOperator.IsNot, new SQLNullExpr(), SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr isNotNull(String expression) {
        return isNotNull(parse(expression));
    }

    /**
     * @title: and
     * @desc "AND"符号
     */
    public static SQLBinaryOpExpr and(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.BooleanAnd, rightExpression, SqlParseConfig.getDbType());
    }

    public static SQLBinaryOpExpr and(String leftExpression, String rightExpression) {
        return and(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public static SQLBinaryOpExpr or(SQLExpr leftExpression, SQLExpr rightExpression) {
        return new SQLBinaryOpExpr(leftExpression, SQLBinaryOperator.BooleanOr, rightExpression, SqlParseConfig.getDbType());

    }

    public static SQLBinaryOpExpr or(String leftExpression, String rightExpression) {
        return or(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: in
     * @desc "IN"符号
     */
    public static SQLInListExpr in(SQLExpr leftExpression, List<SQLExpr> rightExpressions) {
        SQLInListExpr in = new SQLInListExpr(leftExpression);
        for (SQLExpr rightExpression : rightExpressions) {
            in.addTarget(rightExpression);
        }
        return in;
    }

    public static SQLInListExpr in(String leftExpression, List<String> rightExpressions) {
        List<SQLExpr> sqlExprs = new ArrayList<>();
        for (String rightExpression : rightExpressions) {
            sqlExprs.add(parse(rightExpression));
        }
        return in(parse(leftExpression), sqlExprs);
    }

    public static SQLInSubQueryExpr in(SQLExpr leftExpression, Select select) {
        return new SQLInSubQueryExpr(leftExpression, select.getSelect().getSelect().getQueryBlock());
    }

    public static SQLInSubQueryExpr in(String leftExpression, String select) {
        Select s = Select.parse(select);
        return in(parse(leftExpression), s);
    }

    /**
     * @title: in
     * @desc "NOT IN"符号
     */
    public static SQLInListExpr notIn(SQLExpr leftExpression, List<SQLExpr> rightExpressions) {
        SQLInListExpr notIn = in(leftExpression, rightExpressions);
        notIn.setNot(true);
        return notIn;
    }

    public static SQLInListExpr notIn(String leftExpression, List<String> rightExpressions) {
        SQLInListExpr notIn = in(leftExpression, rightExpressions);
        notIn.setNot(true);
        return notIn;
    }

    public static SQLInSubQueryExpr notIn(SQLExpr leftExpression, Select select) {
        SQLInSubQueryExpr notIn = in(leftExpression, select);
        notIn.setNot(true);
        return notIn;
    }

    public static SQLInSubQueryExpr notIn(String leftExpression, String select) {
        SQLInSubQueryExpr notIn = in(leftExpression, select);
        notIn.setNot(true);
        return notIn;
    }

    /**
     * @title: between
     * @desc "BETWEEN"符号
     */
    public static SQLBetweenExpr between(SQLExpr leftExpression, SQLExpr beginExpression, SQLExpr endExpression) {
        return new SQLBetweenExpr(leftExpression, false, beginExpression, endExpression);
    }

    public static SQLBetweenExpr between(String leftExpression, String beginExpression, String endExpression) {
        return between(parse(leftExpression), parse(beginExpression), parse(endExpression));
    }

    /**
     * @title: not between
     * @desc "NOT BETWEEN"符号
     */
    public static SQLBetweenExpr notBetween(SQLExpr leftExpression, SQLExpr beginExpression, SQLExpr endExpression) {
        return new SQLBetweenExpr(leftExpression, true, beginExpression, endExpression);
    }

    public static SQLBetweenExpr notBetween(String leftExpression, String beginExpression, String endExpression) {
        return notBetween(parse(leftExpression), parse(beginExpression), parse(endExpression));
    }

    public static SQLExpr parse(String expr) {
        return SQLUtils.toSQLExpr(expr);
    }

    /**
     * @title: getColumns
     * @desc 获取所有列名
     */
    public static Set<String> getColumns(SQLExpr expression) {
        Set<String> set = new HashSet<String>();
        getColumn(expression, set);
        return set;
    }

    public static Set<String> getColumns(String expression) {
        return getColumns(parse(expression));
    }

    private static void getColumn(SQLExpr expression, Set<String> set) {
        if (expression instanceof SQLIdentifierExpr) {
            set.add(((SQLIdentifierExpr) expression).getName());
        }
        else if (expression instanceof SQLPropertyExpr) {
            set.add(((SQLPropertyExpr) expression).getName());
        }
        else if (expression instanceof SQLBinaryOpExpr) {
            getColumn(((SQLBinaryOpExpr) expression).getLeft(), set);
            getColumn(((SQLBinaryOpExpr) expression).getRight(), set);
        }
        else if (expression instanceof SQLBetweenExpr) {
            getColumn(((SQLBetweenExpr) expression).getTestExpr(), set);
        }
        else if (expression instanceof SQLInListExpr) {
            getColumn(((SQLInListExpr) expression).getExpr(), set);
        }
        else if (expression instanceof SQLInSubQueryExpr) {
            getColumn((((SQLInSubQueryExpr) expression).getExpr()), set);
        }
    }

    /**
     * @title: getColumnsAndValues
     * @desc 获取所有等式的列名和值
     */
    public static List<Map<String, Object>> getColumnsAndValues(SQLExpr expression) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        getColumnAndValue(expression, list);
        return list;
    }

    public static List<Map<String, Object>> getColumnsAndValues(String expression) {
        return getColumnsAndValues(parse(expression));
    }

    private static void getColumnAndValue(SQLExpr expression, List<Map<String, Object>> list) {
        if (expression instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr expr = (SQLBinaryOpExpr) expression;
            SQLBinaryOperator operator = expr.getOperator();
            if (operator == SQLBinaryOperator.Equality) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        list.add(map);
                    }
                }
            } else if(operator == SQLBinaryOperator.BooleanAnd) {
                getColumnAndValue(expr.getLeft(), list);
                getColumnAndValue(expr.getRight(), list);
            } else if (operator == SQLBinaryOperator.BooleanOr) {
                getColumnAndValue(expr.getLeft(), list);
                getColumnAndValue(expr.getRight(), list);
            }
        }
    }

    /**
     * @title: getColumnsAndValues
     * @desc 获取所有等式的列名和值
     */
    public static List<Map<String, Object>> getColumnsAndValuesAndOperators(SQLExpr expression) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        getColumnAndValueAndOperator(expression, list);
        return list;
    }

    public static List<Map<String, Object>> getColumnsAndValuesAndOperators(String expression) {
        return getColumnsAndValuesAndOperators(parse(expression));
    }

    private static void getColumnAndValueAndOperator(SQLExpr expression, List<Map<String, Object>> list) {
        if (expression instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr expr = (SQLBinaryOpExpr) expression;
            SQLBinaryOperator operator = expr.getOperator();
            if (operator == SQLBinaryOperator.Equality) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.EQUALS);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.EQUALS);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.NotEqual) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.NOT_EQUALS);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.NOT_EQUALS);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.GreaterThan) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.GREATER);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.GREATER);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.GreaterThanOrEqual) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.GREATER_EQUALS);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.GREATER_EQUALS);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.LessThan) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.MINOR);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.MINOR);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.LessThanOrEqual) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = checkValue(right);
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.MINOR_EQUALS);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = checkValue(left);
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.MINOR_EQUALS);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.Is) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = new HashMap<>();
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.IS_NULL);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = new HashMap<>();
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.IS_NULL);
                        list.add(map);
                    }
                }
            } else if (operator == SQLBinaryOperator.IsNot) {
                SQLExpr left = expr.getLeft();
                SQLExpr right = expr.getRight();
                if (getColumnName(left) != null) {
                    Map<String, Object> map = new HashMap<>();
                    if (map != null) {
                        map.put("column", getColumnName(left));
                        map.put("operator", OperatorTypeEnum.IS_NOT_NULL);
                        list.add(map);
                    }
                } else if (getColumnName(right) != null) {
                    Map<String, Object> map = new HashMap<>();
                    if (map != null) {
                        map.put("column", getColumnName(right));
                        map.put("operator", OperatorTypeEnum.IS_NOT_NULL);
                        list.add(map);
                    }
                }
            } else if(operator == SQLBinaryOperator.BooleanAnd) {
                getColumnAndValueAndOperator(expr.getLeft(), list);
                getColumnAndValueAndOperator(expr.getRight(), list);
            } else if (operator == SQLBinaryOperator.BooleanOr) {
                getColumnAndValueAndOperator(expr.getLeft(), list);
                getColumnAndValueAndOperator(expr.getRight(), list);
            }
        } else if (expression instanceof SQLBetweenExpr) {
            Map<String, Object> map = new HashMap<String, Object>();
            String columnName = getColumnName(((SQLBetweenExpr) expression).getTestExpr());
            if (columnName != null) {
                map.put("column", columnName);
                map.put("operator", OperatorTypeEnum.BETWEEN);
                List<SQLExpr> itemsList = new ArrayList<>();
                itemsList.add(((SQLBetweenExpr) expression).getBeginExpr());
                itemsList.add(((SQLBetweenExpr) expression).getEndExpr());
                map.put("itemsList", itemsList);
            }
        } else if (expression instanceof SQLInListExpr) {
            Map<String, Object> map = new HashMap<String, Object>();
            String columnName = getColumnName(((SQLBetweenExpr) expression).getTestExpr());
            if (columnName != null) {
                map.put("column", columnName);
                map.put("operator", OperatorTypeEnum.IN);
                map.put("itemsList", ((SQLInListExpr) expression).getTargetList());
            }
        }
    }

    private static String getColumnName(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
           return((SQLIdentifierExpr) expr).getName();
        } else if (expr instanceof SQLPropertyExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        } else {
            return null;
        }
    }

    private static Map<String, Object> checkValue(SQLExpr expression) {
        Map <String, Object> map = new HashMap<String, Object>();
        if (expression instanceof SQLBigIntExpr) {
            map.put("value", ((SQLBigIntExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLBinaryExpr) {
            map.put("value", ((SQLBinaryExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } if (expression instanceof SQLBooleanExpr) {
            map.put("value", ((SQLBooleanExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.BOOLEAN);
        } else if (expression instanceof SQLCharExpr) {
            map.put("value", ((SQLCharExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.STRING);
        } else if (expression instanceof SQLDateExpr) {
            map.put("value", ((SQLDateExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATE);
        } else if (expression instanceof SQLDateTimeExpr) {
            map.put("value", ((SQLDateTimeExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATETIME);
        } else if (expression instanceof SQLDecimalExpr) {
            map.put("value", ((SQLDecimalExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLDoubleExpr) {
            map.put("value", ((SQLDoubleExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLFloatExpr) {
            map.put("value", ((SQLFloatExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLHexExpr) {
            map.put("value", ((SQLHexExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLIntegerExpr) {
            map.put("value", ((SQLIntegerExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLJSONExpr) {
            map.put("value", ((SQLJSONExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.STRING);
        } else if (expression instanceof SQLNumberExpr) {
            map.put("value", ((SQLNumberExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLRealExpr) {
            map.put("value", ((SQLRealExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLSmallIntExpr) {
            map.put("value", ((SQLSmallIntExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLTimeExpr) {
            map.put("value", ((SQLTimeExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.TIME);
        } else if (expression instanceof SQLTimestampExpr) {
            map.put("value", ((SQLTimestampExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATETIME);
        } else if (expression instanceof SQLTinyIntExpr) {
            map.put("value", ((SQLTinyIntExpr) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof SQLMethodInvokeExpr) {
            map.put("value", expression);
            map.put("valueType", ValueTypeEnum.FUNCTION);
        } else {
            map.put("value", expression);
            map.put("valueType", ValueTypeEnum.OTHER);
        }
        return map;
    }
}
