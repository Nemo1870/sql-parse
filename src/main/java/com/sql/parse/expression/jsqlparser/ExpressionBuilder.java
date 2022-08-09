package com.sql.parse.expression.jsqlparser;

import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.OperatorTypeEnum;
import com.sql.parse.expression.ValueTypeEnum;
import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.util.SqlParseConstant;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

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
    public static EqualsTo equalsTo(Expression leftExpression, Expression rightExpression) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(leftExpression);
        equalsTo.setRightExpression(rightExpression);
        return equalsTo;
    }

    public static EqualsTo equalsTo(String leftExpression, String rightExpression) {
        return equalsTo(parse(leftExpression), parse(rightExpression));
    }

    public static EqualsTo equalsTo(com.sql.parse.expression.relational.EqualsTo equalsTo) {
        return equalsTo(equalsTo.getLeftExpression(), equalsTo.getRightExpression());
    }

    /**
     * @title: notEqualsTo
     * @desc "!="符号
     */
    public static NotEqualsTo notEqualsTo(Expression leftExpression, Expression rightExpression) {
        NotEqualsTo notEqualsTo = new NotEqualsTo();
        notEqualsTo.setLeftExpression(leftExpression);
        notEqualsTo.setRightExpression(rightExpression);
        return notEqualsTo;
    }

    public static NotEqualsTo notEqualsTo(String leftExpression, String rightExpression) {
        return notEqualsTo(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: greaterThan
     * @desc ">"符号
     */
    public static GreaterThan greaterThan(Expression leftExpression, Expression rightExpression) {
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.setLeftExpression(leftExpression);
        greaterThan.setRightExpression(rightExpression);
        return greaterThan;
    }

    public static GreaterThan greaterThan(String leftExpression, String rightExpression) {
        return greaterThan(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: greaterThanEquals
     * @desc ">="符号
     */
    public static GreaterThanEquals greaterThanEquals(Expression leftExpression, Expression rightExpression) {
        GreaterThanEquals greaterThanEquals = new GreaterThanEquals();
        greaterThanEquals.setLeftExpression(leftExpression);
        greaterThanEquals.setRightExpression(rightExpression);
        return greaterThanEquals;
    }

    public static GreaterThanEquals greaterThanEquals(String leftExpression, String rightExpression) {
        return greaterThanEquals(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: minorThan
     * @desc "<"符号
     */
    public static MinorThan minorThan(Expression leftExpression, Expression rightExpression) {
        MinorThan minorThan = new MinorThan();
        minorThan.setLeftExpression(leftExpression);
        minorThan.setRightExpression(rightExpression);
        return minorThan;
    }

    public static MinorThan minorThan(String leftExpression, String rightExpression) {
        return minorThan(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: minorThanEquals
     * @desc "<="符号
     */
    public static MinorThanEquals minorThanEquals(Expression leftExpression, Expression rightExpression) {
        MinorThanEquals minorThanEquals = new MinorThanEquals();
        minorThanEquals.setLeftExpression(leftExpression);
        minorThanEquals.setRightExpression(rightExpression);
        return minorThanEquals;
    }

    public static MinorThanEquals minorThanEquals(String leftExpression, String rightExpression) {
        return minorThanEquals(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: isNull
     * @desc "IS NULL"符号
     */
    public static IsNullExpression isNull(Expression expression) {
        IsNullExpression isNull = new IsNullExpression();
        isNull.setNot(false);
        isNull.setLeftExpression(expression);
        return isNull;
    }

    public static IsNullExpression isNull(String expression) {
        return isNull(parse(expression));
    }

    /**
     * @title: isNotNull
     * @desc "IS NOT NULL"符号
     */
    public static IsNullExpression isNotNull(Expression expression) {
        IsNullExpression isNotNull = new IsNullExpression();
        isNotNull.setNot(true);
        isNotNull.setLeftExpression(expression);
        return isNotNull;
    }

    public static IsNullExpression isNotNull(String expression) {
        return isNotNull(parse(expression));
    }

    /**
     * @title: and
     * @desc "AND"符号
     */
    public static Expression and(Expression leftExpression, Expression rightExpression) {
        if (rightExpression.getClass() == OrExpression.class) {
            rightExpression = new Parenthesis(rightExpression);
        }
        return new AndExpression(leftExpression, rightExpression);
    }

    public static Expression and(String leftExpression, String rightExpression) {
        return and(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public static Expression or(Expression leftExpression, Expression rightExpression) {
        if (rightExpression.getClass() == AndExpression.class) {
            rightExpression = new Parenthesis(rightExpression);
        }
        return new OrExpression(leftExpression, rightExpression);
    }

    public static Expression or(String leftExpression, String rightExpression) {
        return or(parse(leftExpression), parse(rightExpression));
    }

    /**
     * @title: in
     * @desc "IN"符号
     */
    public static InExpression in(Expression leftExpression, List<Expression> rightExpressions) {
        return new InExpression(leftExpression, new ExpressionList(rightExpressions));
    }

    public static InExpression in(String leftExpression, List<String> rightExpressions) {
        List<Expression> expressions = new ArrayList<Expression>();
        for (String expression : rightExpressions) {
            expressions.add(parse(expression));
        }
        return new InExpression(parse(leftExpression), new ExpressionList(expressions));
    }

    public static InExpression in(Expression leftExpression, Select select) {
        SubSelect subSelect = new SubSelect();
        subSelect.setSelectBody(select.getSelect().getSelectBody());
        subSelect.setUseBrackets(true);
        return new InExpression(leftExpression, subSelect);
    }

    public static InExpression in(String leftExpression, String select) {
        Select s = Select.parse(select);
        SubSelect subSelect = new SubSelect();
        subSelect.setSelectBody(s.getSelect().getSelectBody());
        subSelect.setUseBrackets(true);
        Expression expr = parse(leftExpression);
        return new InExpression(expr, subSelect);
    }

    /**
     * @title: in
     * @desc "NOT IN"符号
     */
    public static InExpression notIn(Expression leftExpression, List<Expression> rightExpressions) {
        InExpression notIn = in(leftExpression, rightExpressions);
        notIn.setNot(true);
        return notIn;
    }

    public static InExpression notIn(String leftExpression, List<String> rightExpressions) {
        InExpression notIn = in(leftExpression, rightExpressions);
        notIn.setNot(true);
        return notIn;
    }

    public static InExpression notIn(Expression leftExpression, Select select) {
        InExpression notIn = in(leftExpression, select);
        notIn.setNot(true);
        return notIn;
    }

    public static InExpression notIn(String leftExpression, String select) {
        InExpression notIn = in(leftExpression, select);
        notIn.setNot(true);
        return notIn;
    }

    /**
     * @title: between
     * @desc "BETWEEN"符号
     */
    public static Between between(Expression leftExpression, Expression beginExpression, Expression endExpression) {
        Between between = new Between();
        between.setLeftExpression(leftExpression);
        between.setBetweenExpressionStart(beginExpression);
        between.setBetweenExpressionEnd(endExpression);
        return between;
    }

    public static Between between(String leftExpression, String beginExpression, String endExpression) {
        return between(parse(leftExpression), parse(beginExpression), parse(endExpression));
    }

    /**
     * @title: not between
     * @desc "NOT BETWEEN"符号
     */
    public static Between notBetween(Expression leftExpression, Expression beginExpression, Expression endExpression) {
        Between between = between(leftExpression, beginExpression, endExpression);
        between.setNot(true);
        return between;
    }

    public static Between notBetween(String leftExpression, String beginExpression, String endExpression) {
        return notBetween(parse(leftExpression), parse(beginExpression), parse(endExpression));
    }

    /**
     * @title: parenthesis
     * @desc "()"符号
     */
    public static Parenthesis parenthesis(Expression expression) {
        return new Parenthesis(expression);
    }

    public static Parenthesis parenthesis(String expression) {
        return parenthesis(parse(expression));
    }

    public static Expression parse(String expr) {
        try {
            return CCJSqlParserUtil.parseCondExpression(expr);
        } catch (JSQLParserException e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_001);
        }
    }

    /**
     * @title: getColumns
     * @desc 获取所有列名
     */
    public static Set<String> getColumns(Expression expression) {
        Set<String> set = new HashSet<String>();
        getColumn(expression, set);
        return set;
    }

    public static Set<String> getColumns(String expression) {
        return getColumns(parse(expression));
    }

    private static void getColumn(Expression expression, Set<String> set) {
        if (expression instanceof Column) {
            set.add(((Column) expression).getColumnName());
        }
        else if (expression instanceof BinaryExpression) {
            getColumn(((BinaryExpression) expression).getLeftExpression(), set);
            getColumn(((BinaryExpression) expression).getRightExpression(), set);
        }
        else if (expression instanceof Between) {
            getColumn(((Between) expression).getLeftExpression(), set);
        }
        else if (expression instanceof InExpression) {
            getColumn(((InExpression) expression).getLeftExpression(), set);
        }
        else if (expression instanceof IsNullExpression) {
            getColumn((((IsNullExpression) expression).getLeftExpression()), set);
        }
        else if (expression instanceof Parenthesis) {
            getColumn(((Parenthesis) expression).getExpression(), set);
        }
    }

    /**
     * @title: getColumnsAndValues
     * @desc 获取所有等式的列名和值
     */
    public static List<Map<String, Object>> getColumnsAndValues(Expression expression) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        getColumnAndValue(expression, list);
        return list;
    }

    public static List<Map<String, Object>> getColumnsAndValues(String expression) {
        return getColumnsAndValues(parse(expression));
    }

    private static void getColumnAndValue(Expression expression, List<Map<String, Object>> list) {
        if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            if (equalsTo.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(equalsTo.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) equalsTo.getLeftExpression()).getColumnName());
                    list.add(map);
                }
            } else if (equalsTo.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(equalsTo.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) equalsTo.getRightExpression()).getColumnName());
                    list.add(map);
                }
            }
        } else if (expression instanceof AndExpression) {
            getColumnAndValue(((AndExpression) expression).getLeftExpression(), list);
            getColumnAndValue(((AndExpression) expression).getRightExpression(), list);
        } else if (expression instanceof OrExpression) {
            getColumnAndValue(((OrExpression) expression).getLeftExpression(), list);
            getColumnAndValue(((OrExpression) expression).getRightExpression(), list);
        } else if (expression instanceof Parenthesis) {
            getColumnAndValue(((Parenthesis) expression).getExpression(), list);
        }
    }

    /**
     * @title: getColumnsAndValues
     * @desc 获取所有等式的列名和值
     */
    public static List<Map<String, Object>> getColumnsAndValuesAndOperators(Expression expression) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        getColumnAndValueAndOperator(expression, list);
        return list;
    }

    public static List<Map<String, Object>> getColumnsAndValuesAndOperators(String expression) {
        return getColumnsAndValuesAndOperators(parse(expression));
    }

    private static void getColumnAndValueAndOperator(Expression expression, List<Map<String, Object>> list) {
        if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            if (equalsTo.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(equalsTo.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) equalsTo.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.EQUALS);
                    list.add(map);
                }
            } else if (equalsTo.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(equalsTo.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) equalsTo.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.EQUALS);
                    list.add(map);
                }
            }
        } else if (expression instanceof NotEqualsTo) {
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            if (notEqualsTo.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(notEqualsTo.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) notEqualsTo.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.NOT_EQUALS);
                    list.add(map);
                }
            } else if (notEqualsTo.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(notEqualsTo.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) notEqualsTo.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.NOT_EQUALS);
                    list.add(map);
                }
            }
        } else if (expression instanceof GreaterThan) {
            GreaterThan greaterThan = (GreaterThan) expression;
            if (greaterThan.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(greaterThan.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) greaterThan.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.GREATER);
                    list.add(map);
                }
            } else if (greaterThan.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(greaterThan.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) greaterThan.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.GREATER);
                    list.add(map);
                }
            }
        } else if (expression instanceof GreaterThanEquals) {
            GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
            if (greaterThanEquals.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(greaterThanEquals.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) greaterThanEquals.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.GREATER_EQUALS);
                    list.add(map);
                }
            } else if (greaterThanEquals.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(greaterThanEquals.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) greaterThanEquals.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.GREATER_EQUALS);
                    list.add(map);
                }
            }
        } else if (expression instanceof MinorThan) {
            MinorThan minorThan = (MinorThan) expression;
            if (minorThan.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(minorThan.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) minorThan.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.MINOR);
                    list.add(map);
                }
            } else if (minorThan.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(minorThan.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) minorThan.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.MINOR);
                    list.add(map);
                }
            }
        } else if (expression instanceof MinorThanEquals) {
            MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
            if (minorThanEquals.getLeftExpression() instanceof Column) {
                Map<String, Object> map = checkValue(minorThanEquals.getRightExpression());
                if (map != null) {
                    map.put("column", ((Column) minorThanEquals.getLeftExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.MINOR_EQUALS);
                    list.add(map);
                }
            } else if (minorThanEquals.getRightExpression() instanceof Column) {
                Map<String, Object> map = checkValue(minorThanEquals.getLeftExpression());
                if (map != null) {
                    map.put("column", ((Column) minorThanEquals.getRightExpression()).getColumnName());
                    map.put("operator", OperatorTypeEnum.MINOR_EQUALS);
                    list.add(map);
                }
            }
        } else if (expression instanceof IsNullExpression) {
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("column", ((Column) isNullExpression.getLeftExpression()).getColumnName());
            map.put("operator", isNullExpression.isNot() ? OperatorTypeEnum.IS_NOT_NULL : OperatorTypeEnum.IS_NULL);
            list.add(map);
        } else if (expression instanceof Between) {
            Between between = (Between) expression;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("column", ((Column) between.getLeftExpression()).getColumnName());
            map.put("operator", OperatorTypeEnum.BETWEEN);
            List<Expression> itemsList = new ArrayList<>();
            itemsList.add(between.getBetweenExpressionStart());
            itemsList.add(between.getBetweenExpressionEnd());
            map.put("itemsList", itemsList);
            list.add(map);
        } else if(expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            ItemsList itemsList = inExpression.getRightItemsList();
            if (itemsList instanceof ExpressionList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("column", ((Column) inExpression.getLeftExpression()).getColumnName());
                map.put("operator", OperatorTypeEnum.IN);
                map.put("itemsList", ((ExpressionList) itemsList).getExpressions());
                list.add(map);
            }
        } else if (expression instanceof AndExpression) {
            getColumnAndValueAndOperator(((AndExpression) expression).getLeftExpression(), list);
            getColumnAndValueAndOperator(((AndExpression) expression).getRightExpression(), list);
        } else if (expression instanceof OrExpression) {
            getColumnAndValueAndOperator(((OrExpression) expression).getLeftExpression(), list);
            getColumnAndValueAndOperator(((OrExpression) expression).getRightExpression(), list);
        } else if (expression instanceof Parenthesis) {
            getColumnAndValueAndOperator(((Parenthesis) expression).getExpression(), list);
        }
    }

    private static Map<String, Object> checkValue(Expression expression) {
        Map <String, Object> map = new HashMap<String, Object>();
        if (expression instanceof DateTimeLiteralExpression) {
            map.put("value", ((DateTimeLiteralExpression) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATETIME);
        } else if (expression instanceof DateValue) {
            map.put("value", ((DateValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATE);
        } else if (expression instanceof DoubleValue) {
            map.put("value", ((DoubleValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof Function) {
            map.put("value", expression);
            map.put("valueType", ValueTypeEnum.FUNCTION);
        } else if (expression instanceof HexValue) {
            map.put("value", ((HexValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof LongValue) {
            map.put("value", ((LongValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.NUMBER);
        } else if (expression instanceof StringValue) {
            map.put("value", ((StringValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.STRING);
        } else if (expression instanceof TimestampValue) {
            map.put("value", ((TimestampValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.DATETIME);
        } else if (expression instanceof TimeValue) {
            map.put("value", ((TimeValue) expression).getValue());
            map.put("valueType", ValueTypeEnum.TIME);
        } else {
            map.put("value", expression);
            map.put("valueType", ValueTypeEnum.OTHER);
        }
        return map;
    }
}
