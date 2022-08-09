package com.sql.parse.expression.jsqlparser;

import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.util.SqlFormatter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc 表达式构造器
 */
public class ExpressionBuilderTest {
    private static final Logger logger = LogManager.getLogger(ExpressionBuilderTest.class);

    @Test
    public void testEqualsTo() {
        EqualsTo equalsTo = ExpressionBuilder.equalsTo(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                equalsTo.toString(),
                "a = 1"
        );
        equalsTo = ExpressionBuilder.equalsTo("a", "1");
        Assert.assertEquals(
                equalsTo.toString(),
                "a = 1"
        );
        equalsTo = ExpressionBuilder.equalsTo(new com.sql.parse.expression.relational.EqualsTo("a", "1"));
        Assert.assertEquals(
                equalsTo.toString(),
                "a = 1"
        );
    }


    @Test
    public void testNotEqualsTo() {
        NotEqualsTo notEqualsTo = ExpressionBuilder.notEqualsTo(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                notEqualsTo.toString(),
                "a <> 1"
        );
        notEqualsTo = ExpressionBuilder.notEqualsTo("a", "1");
        Assert.assertEquals(
                notEqualsTo.toString(),
                "a <> 1"
        );
    }

    @Test
    public void testGreaterThan() {
        GreaterThan greaterThan = ExpressionBuilder.greaterThan(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                greaterThan.toString(),
                "a > 1"
        );
        greaterThan = ExpressionBuilder.greaterThan("a", "1");
        Assert.assertEquals(
                greaterThan.toString(),
                "a > 1"
        );
    }

    @Test
    public void testGreaterThanEquals() {
        GreaterThanEquals greaterThanEquals = ExpressionBuilder.greaterThanEquals(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                greaterThanEquals.toString(),
                "a >= 1"
        );
        greaterThanEquals = ExpressionBuilder.greaterThanEquals("a", "1");
        Assert.assertEquals(
                greaterThanEquals.toString(),
                "a >= 1"
        );
    }

    @Test
    public void testMinorThan() {
        MinorThan minorThan = ExpressionBuilder.minorThan(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                minorThan.toString(),
                "a < 1"
        );
        minorThan = ExpressionBuilder.minorThan("a", "1");
        Assert.assertEquals(
                minorThan.toString(),
                "a < 1"
        );
    }


    @Test
    public void testMinorThanEquals() {
        MinorThanEquals minorThanEquals = ExpressionBuilder.minorThanEquals(new Column("a"), new LongValue("1"));
        Assert.assertEquals(
                minorThanEquals.toString(),
                "a <= 1"
        );
        minorThanEquals = ExpressionBuilder.minorThanEquals("a", "1");
        Assert.assertEquals(
                minorThanEquals.toString(),
                "a <= 1"
        );
    }

    @Test
    public void testIsNull() {
        IsNullExpression isNull = ExpressionBuilder.isNull(new Column("a"));
        Assert.assertEquals(
                isNull.toString(),
                "a IS NULL"
        );
        isNull = ExpressionBuilder.isNull("a");
        Assert.assertEquals(
                isNull.toString(),
                "a IS NULL"
        );
    }

    @Test
    public void testIsNotNull() {
        IsNullExpression isNotNull = ExpressionBuilder.isNotNull(new Column("a"));
        Assert.assertEquals(
                isNotNull.toString(),
                "a IS NOT NULL"
        );
        isNotNull = ExpressionBuilder.isNotNull("a");
        Assert.assertEquals(
                isNotNull.toString(),
                "a IS NOT NULL"
        );
    }

    @Test
    public void testAnd() {
        Expression and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        Assert.assertEquals(
                SqlFormatter.format(and.toString()),
                SqlFormatter.format("a IS NOT NULL AND b = 1")
        );
        and = ExpressionBuilder.and("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(and.toString()),
                SqlFormatter.format("a IS NOT NULL AND b = 1")
        );
    }

    @Test
    public void testOr() {
        Expression or = ExpressionBuilder.or(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        Assert.assertEquals(
                SqlFormatter.format(or.toString()),
                SqlFormatter.format("a IS NOT NULL OR b = 1")
        );
        or = ExpressionBuilder.or("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(or.toString()),
                SqlFormatter.format("a IS NOT NULL OR b = 1")
        );
    }

    @Test
    public void testIn() {
        List<Expression> list = new ArrayList<>();
        list.add(new LongValue(1));
        list.add(new LongValue(2));
        InExpression in = ExpressionBuilder.in(new Column("a"), list);
        Assert.assertEquals(
                in.toString(),
                "a IN (1, 2)"
        );
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        in = ExpressionBuilder.in("a", expr);
        Assert.assertEquals(
                in.toString(),
                "a IN (1, 2)"
        );
        in = ExpressionBuilder.in(new Column("a"), new Select("A"));
        Assert.assertEquals(
                in.toString(),
                "a IN (SELECT * FROM A)"
        );
        in = ExpressionBuilder.in("a", "select * from A");
        Assert.assertEquals(
                in.toString(),
                "a IN (SELECT * FROM A)"
        );
    }

    @Test
    public void testNotIn() {
        List<Expression> list = new ArrayList<>();
        list.add(new LongValue(1));
        list.add(new LongValue(2));
        InExpression notIn = ExpressionBuilder.notIn(new Column("a"), list);
        Assert.assertEquals(
                notIn.toString(),
                "a NOT IN (1, 2)"
        );
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        notIn = ExpressionBuilder.notIn("a", expr);
        Assert.assertEquals(
                notIn.toString(),
                "a NOT IN (1, 2)"
        );
        notIn = ExpressionBuilder.notIn(new Column("a"), new Select("A"));
        Assert.assertEquals(
                notIn.toString(),
                "a NOT IN (SELECT * FROM A)"
        );
        notIn = ExpressionBuilder.notIn("a", "select * from A");
        Assert.assertEquals(
                notIn.toString(),
                "a NOT IN (SELECT * FROM A)"
        );
    }

    @Test
    public void testBetween() {
        Between between = ExpressionBuilder.between("a", "1", "3");
        Assert.assertEquals(
                between.toString(),
                "a BETWEEN 1 AND 3"
        );
        between = ExpressionBuilder.between(new Column("a"), new LongValue(1), new LongValue(3));
        Assert.assertEquals(
                between.toString(),
                "a BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testNotBetween() {
        Between notBetween = ExpressionBuilder.notBetween("a","1", "3");
        Assert.assertEquals(
                notBetween.toString(),
                "a NOT BETWEEN 1 AND 3"
        );
        notBetween = ExpressionBuilder.notBetween(new Column("a"), new LongValue(1), new LongValue(3));
        Assert.assertEquals(
                notBetween.toString(),
                "a NOT BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testParenthesis() {
        Parenthesis parenthesis = ExpressionBuilder.parenthesis(ExpressionBuilder.equalsTo("a", "1"));
        Assert.assertEquals(
                parenthesis.toString(),
                "(a = 1)"
        );
        parenthesis = ExpressionBuilder.parenthesis("a = 1");
        Assert.assertEquals(
                parenthesis.toString(),
                "(a = 1)"
        );
    }

    @Test
    public void testGetColumns() {
        Expression and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        Set<String> colums = ExpressionBuilder.getColumns(and);
        Assert.assertEquals(colums.size(), 2);
        Assert.assertTrue(colums.contains("a"));
        Assert.assertTrue(colums.contains("b"));
    }

    @Test
    public void testGetColumnsAndValues() {
        Expression and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValues(and);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).get("column"), "b");
    }

    @Test
    public void testGetColumnsAndValuesAndOperators() {
        Expression and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValuesAndOperators(and);
        Assert.assertEquals(list.size(), 2);
    }
}
