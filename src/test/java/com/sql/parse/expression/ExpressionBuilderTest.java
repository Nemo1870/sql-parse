package com.sql.parse.expression;

import com.sql.parse.config.SqlParseConfig;
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
        String equalsTo = ExpressionBuilder.equalsTo("a", "1");
        Assert.assertEquals(
                equalsTo,
                "a = 1"
        );
    }


    @Test
    public void testNotEqualsTo() {
        String notEqualsTo = ExpressionBuilder.notEqualsTo("a", "1");
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                Assert.assertEquals(
                        notEqualsTo,
                        "a <> 1"
                );
                break;
            }
            case "com.alibaba.druid":{
                Assert.assertEquals(
                        notEqualsTo,
                        "a != 1"
                );
                break;
            }
            default:{
                Assert.fail();
            }
        }
    }

    @Test
    public void testGreaterThan() {
        String greaterThan = ExpressionBuilder.greaterThan("a", "1");
        Assert.assertEquals(
                greaterThan,
                "a > 1"
        );
    }

    @Test
    public void testGreaterThanEquals() {
        String greaterThanEquals = ExpressionBuilder.greaterThanEquals("a", "1");
        Assert.assertEquals(
                greaterThanEquals,
                "a >= 1"
        );
    }

    @Test
    public void testMinorThan() {
        String minorThan = ExpressionBuilder.minorThan("a", "1");
        Assert.assertEquals(
                minorThan,
                "a < 1"
        );
    }


    @Test
    public void testMinorThanEquals() {
        String minorThanEquals = ExpressionBuilder.minorThanEquals("a", "1");
        Assert.assertEquals(
                minorThanEquals,
                "a <= 1"
        );
    }

    @Test
    public void testIsNull() {
        String isNull = ExpressionBuilder.isNull("a");
        Assert.assertEquals(
                isNull,
                "a IS NULL"
        );
    }

    @Test
    public void testIsNotNull() {
        String isNotNull = ExpressionBuilder.isNotNull("a");
        Assert.assertEquals(
                isNotNull,
                "a IS NOT NULL"
        );
    }

    @Test
    public void testAnd() {
        String and = ExpressionBuilder.and("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(and),
                SqlFormatter.format("a IS NOT NULL AND b = 1")
        );
    }

    @Test
    public void testOr() {
        String or = ExpressionBuilder.or("a is not null", "b = 1");
        Assert.assertEquals(
                SqlFormatter.format(or),
                SqlFormatter.format("a IS NOT NULL OR b = 1")
        );
    }

    @Test
    public void testIn() {
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        String in = ExpressionBuilder.in("a", expr);
        Assert.assertEquals(
                in,
                "a IN (1, 2)"
        );
        in = ExpressionBuilder.in("a", "select * from A");
        Assert.assertEquals(
                SqlFormatter.format(in),
                SqlFormatter.format("a IN (SELECT * FROM A)")
        );
    }

    @Test
    public void testNotIn() {
        List<String> expr = new ArrayList<>();
        expr.add("1");
        expr.add("2");
        String notIn = ExpressionBuilder.notIn("a", expr);
        Assert.assertEquals(
                notIn,
                "a NOT IN (1, 2)"
        );
        notIn = ExpressionBuilder.notIn("a", "select * from A");
        Assert.assertEquals(
                SqlFormatter.format(notIn),
                SqlFormatter.format("a NOT IN (SELECT * FROM A)")
        );
    }

    @Test
    public void testBetween() {
        String between = ExpressionBuilder.between("a", "1", "3");
        Assert.assertEquals(
                between,
                "a BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testNotBetween() {
        String notBetween = ExpressionBuilder.notBetween("a","1", "3");
        Assert.assertEquals(
                notBetween,
                "a NOT BETWEEN 1 AND 3"
        );
    }

    @Test
    public void testGetColumns() {
        String and = ExpressionBuilder.and(
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
        String and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValues(and);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).get("column"), "b");
    }

    @Test
    public void testGetColumnsAndValuesAndOperators() {
        String and = ExpressionBuilder.and(
                ExpressionBuilder.isNotNull("a"),
                ExpressionBuilder.equalsTo("b", "1")
        );
        List<Map<String, Object>> list = ExpressionBuilder.getColumnsAndValuesAndOperators(and);
        Assert.assertEquals(list.size(), 2);
    }
}
