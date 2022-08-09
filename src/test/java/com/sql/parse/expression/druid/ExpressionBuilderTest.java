package com.sql.parse.expression.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.statement.impl.druid.Select;
import com.sql.parse.util.SqlFormatter;
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
        SQLBinaryOpExpr equalsTo = ExpressionBuilder.equalsTo(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(equalsTo),
                "a = 1"
        );
        equalsTo = ExpressionBuilder.equalsTo("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(equalsTo),
                "a = 1"
        );
        equalsTo = ExpressionBuilder.equalsTo(new EqualsTo("a", "1"));
        Assert.assertEquals(
                SQLUtils.toSQLString(equalsTo),
                "a = 1"
        );
    }


    @Test
    public void testNotEqualsTo() {
        SQLBinaryOpExpr notEqualsTo = ExpressionBuilder.notEqualsTo(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(notEqualsTo),
                "a != 1"
        );
        notEqualsTo = ExpressionBuilder.notEqualsTo("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(notEqualsTo),
                "a != 1"
        );
    }

    @Test
    public void testGreaterThan() {
        SQLBinaryOpExpr greaterThan = ExpressionBuilder.greaterThan(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(greaterThan),
                "a > 1"
        );
        greaterThan = ExpressionBuilder.greaterThan("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(greaterThan),
                "a > 1"
        );
    }

    @Test
    public void testGreaterThanEquals() {
        SQLBinaryOpExpr greaterThanEquals = ExpressionBuilder.greaterThanEquals(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(greaterThanEquals),
                "a >= 1"
        );
        greaterThanEquals = ExpressionBuilder.greaterThanEquals("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(greaterThanEquals),
                "a >= 1"
        );
    }

    @Test
    public void testMinorThan() {
        SQLBinaryOpExpr minorThan = ExpressionBuilder.minorThan(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(minorThan),
                "a < 1"
        );
        minorThan = ExpressionBuilder.minorThan("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(minorThan),
                "a < 1"
        );
    }


    @Test
    public void testMinorThanEquals() {
        SQLBinaryOpExpr minorThanEquals = ExpressionBuilder.minorThanEquals(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1));
        Assert.assertEquals(
                SQLUtils.toSQLString(minorThanEquals),
                "a <= 1"
        );
        minorThanEquals = ExpressionBuilder.minorThanEquals("a", "1");
        Assert.assertEquals(
                SQLUtils.toSQLString(minorThanEquals),
                "a <= 1"
        );
    }

    @Test
    public void testIsNull() {
        SQLBinaryOpExpr isNull = ExpressionBuilder.isNull(new SQLIdentifierExpr("a"));
        Assert.assertEquals(
                SQLUtils.toSQLString(isNull),
                "a IS NULL"
        );
        isNull = ExpressionBuilder.isNull("a");
        Assert.assertEquals(
                SQLUtils.toSQLString(isNull),
                "a IS NULL"
        );
    }

    @Test
    public void testIsNotNull() {
        SQLBinaryOpExpr isNotNull = ExpressionBuilder.isNotNull(new SQLIdentifierExpr("a"));
        Assert.assertEquals(
                SQLUtils.toSQLString(isNotNull),
                "a IS NOT NULL"
        );
        isNotNull = ExpressionBuilder.isNotNull("a");
        Assert.assertEquals(
                SQLUtils.toSQLString(isNotNull),
                "a IS NOT NULL"
        );
    }

    @Test
    public void testAnd() {
        SQLBinaryOpExpr and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(and)),
                SqlFormatter.format("a IS NOT NULL AND b = 1")
        );
        and = ExpressionBuilder.and("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(and)),
                SqlFormatter.format("a IS NOT NULL AND b = 1")
        );
    }

    @Test
    public void testOr() {
        SQLBinaryOpExpr or = ExpressionBuilder.or(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(or)),
                SqlFormatter.format("a IS NOT NULL OR b = 1")
        );
        or = ExpressionBuilder.or("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(or)),
                SqlFormatter.format("a IS NOT NULL OR b = 1")
        );
    }

    @Test
    public void testIn() {
        List<SQLExpr> list = new ArrayList<>();
        list.add(new SQLIntegerExpr(1));
        list.add(new SQLIntegerExpr(2));
        SQLInListExpr in = ExpressionBuilder.in(new SQLIdentifierExpr("a"), list);
        Assert.assertEquals(
                SQLUtils.toSQLString(in),
                "a IN (1, 2)"
        );
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        in = ExpressionBuilder.in("a", expr);
        Assert.assertEquals(
                SQLUtils.toSQLString(in),
                "a IN (1, 2)"
        );
        Select select = Select.parse("select * from A");
        SQLInSubQueryExpr in2 = ExpressionBuilder.in(new SQLIdentifierExpr("a"), select);
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(in2)),
                SqlFormatter.format("a IN (SELECT *  FROM A)")
        );
        in2 = ExpressionBuilder.in("a", "select * from A");
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(in2)),
                SqlFormatter.format("a IN (SELECT *  FROM A)")
        );
    }

    @Test
    public void testNotIn() {
        List<SQLExpr> list = new ArrayList<>();
        list.add(new SQLIntegerExpr(1));
        list.add(new SQLIntegerExpr(2));
        SQLInListExpr notIn = ExpressionBuilder.notIn(new SQLIdentifierExpr("a"), list);
        Assert.assertEquals(
                SQLUtils.toSQLString(notIn),
                "a NOT IN (1, 2)"
        );
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        notIn = ExpressionBuilder.notIn("a", expr);
        Assert.assertEquals(
                SQLUtils.toSQLString(notIn),
                "a NOT IN (1, 2)"
        );
        Select select = Select.parse("select * from A");
        SQLInSubQueryExpr notIn2 = ExpressionBuilder.notIn(new SQLIdentifierExpr("a"), select);
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(notIn2)),
                SqlFormatter.format("a NOT IN ( SELECT *  FROM A )")
        );
        notIn2 = ExpressionBuilder.notIn("a", "select * from A");
        Assert.assertEquals(
                SqlFormatter.format(SQLUtils.toSQLString(notIn2)),
                SqlFormatter.format("a NOT IN ( SELECT *  FROM A )")
        );
    }

    @Test
    public void testBetween() {
        SQLBetweenExpr between = ExpressionBuilder.between("a", "1", "3");
        Assert.assertEquals(
                SQLUtils.toSQLString(between),
                "a BETWEEN 1 AND 3"
        );
        between = ExpressionBuilder.between(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1), new SQLIntegerExpr(3));
        Assert.assertEquals(
                SQLUtils.toSQLString(between),
                "a BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testNotBetween() {
        SQLBetweenExpr notBetween = ExpressionBuilder.notBetween("a","1", "3");
        Assert.assertEquals(
                SQLUtils.toSQLString(notBetween),
                "a NOT BETWEEN 1 AND 3"
        );
        notBetween = ExpressionBuilder.notBetween(new SQLIdentifierExpr("a"), new SQLIntegerExpr(1), new SQLIntegerExpr(3));
        Assert.assertEquals(
                SQLUtils.toSQLString(notBetween),
                "a NOT BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testGetColumns() {
        SQLBinaryOpExpr and = ExpressionBuilder.and(
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
        SQLBinaryOpExpr and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValues(and);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).get("column"), "b");
    }

    @Test
    public void testGetColumnsAndValuesAndOperators() {
        SQLBinaryOpExpr and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValuesAndOperators(and);
        Assert.assertEquals(list.size(), 2);
    }
}
