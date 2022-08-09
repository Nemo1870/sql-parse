package com.sql.parse.statement.druid;

import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.schema.OrderByItem;
import com.sql.parse.schema.SelectItem;
import com.sql.parse.schema.Table;
import com.sql.parse.statement.impl.druid.Select;
import com.sql.parse.util.SqlFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @team IPU
 * @date 2019年4月29日上午11:20:58··
 * @desc Select语法单元测试类
 */
public class SelectTest {
    private static final Logger logger = LogManager.getLogger(SelectTest.class);

    private String table1;
    private String table2;
    private String column1;
    private String column2;
    private String column3;
    private String simpleSql;
    private String simpleSql2;

    @Before
    public void before() {
        // TODO Auto-generated method stub
        table1 = "table1";
        table2 = "table2";
        column1 = "column1";
        column2 = "column2";
        column3 = "column3";
        simpleSql = "select " + column1 +" from " + table1;
        simpleSql2 = "select t." + column1 + " from " + table1 + " t";
    }

    @Test
    public void testParse() {
        try {
            Select select = Select.parse(simpleSql);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 新增查询列
     */
    @Test
    public void testSelectWithAddColumns() {
        try {
            Select select = Select.parse(simpleSql);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1")
            );
            select.addColumn(column2).addColumn(column3);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2, column3 FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2, column3 FROM table1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSelectWithColumnAlias1() {
        try {
            Select select = Select.parse(simpleSql2);
//            select.addColumn(new SelectItem("t."+column1, "c1"));
            select.addColumn(new SelectItem("t."+column2, "c2"));
            select.addColumn(new SelectItem("t."+column3, "c3"));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT t.column1, t.column2 AS c2, t.column3 AS c3 FROM table1 t LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT t.column1, t.column2 AS c2, t.column3 AS c3 FROM table1 t")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSelectWithColumnAlias2() {
        try {
            Select select = Select.parse(simpleSql);
//        select.addColumn(column1, "c1");
            select.addColumn(column2, "c2");
            select.addColumn(column3, "c3");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 AS c2, column3 AS c3 FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 AS c2, column3 AS c3 FROM table1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @title: testSelectWithColumnAliasOther
     * @desc: 达不到预期效果：SELECT t.column1, t.column2, t.column3 FROM table1 t LIMIT 500
     * @throws
     */
    @Test
    public void testSelectWithColumnAlias3() {
        try {
            Select select = Select.parse(simpleSql2);
//            select.addColumn("t."+column1+" c1");
            select.addColumn("t."+column2+" c2");
            select.addColumn("t."+column3+" c3");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT t.column1, t.column2 c2, t.column3 c3 FROM table1 t LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT t.column1, t.column2 c2, t.column3 c3 FROM table1 t")
            );
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * @desc 带where的查询
     */
    @Test
    public void testSelectWithWhere() {
        try {
            Select select = Select.parse(simpleSql);
            select.where("a<>1");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE a <> 1")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE a <> 1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 新增and查询条件
     */
    @Test
    public void testSelectWithAnd() {
        try {
            Select select = Select.parse(simpleSql);
            select.where(column1 + " > 'x'");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 > 'x'")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 > 'x'")
            );
            select.and(column2 + " >= 2");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 > 'x' AND column2 >= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 > 'x' AND column2 >= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 新增or查询条件
     */
    @Test
    public void testSelectWithOr() {
        try {
            Select select = Select.parse(simpleSql);
            select.where(column1 + " < 'x'");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 < 'x'")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 < 'x'")
            );
            select.or(column2 + " <= 2");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 < 'x' OR column2 <= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1 FROM table1 WHERE column1 < 'x' OR column2 <= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 简单join的查询：SELECT t1.column1, t2.column2 FROM table1 t1, table2 t2
     * WHERE t1.column1 = 'x' AND t1.column1 = t2.column2
     */
    @Test
    public void testSelectWithSimpleJoin() {
        try {
            Select select = Select.parse("select t1.column1 from table1 t1");
            select.addColumn("t2."+column2);
            select.simpleJoin(new Table(table2, "t2"));
            select.where("t1."+column1 + " = 'x'")
                    .and("t1."+column1 + " = t2."+column2);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT t1.column1, t2.column2 FROM table1 t1, table2 t2 WHERE t1.column1 = 'x' AND t1.column1 = t2.column2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT t1.column1, t2.column2 FROM table1 t1, table2 t2 WHERE t1.column1 = 'x' AND t1.column1 = t2.column2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带join的查询
     */
    @Test
    public void testSelectWithJoin() {
        try {
            Select select = Select.parse("select column1 from table1 t1");
            select.addColumn(column2);
            select.join(table2+" t2", new EqualsTo("t1." + column1, "t2."+ column3));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 t1 JOIN table2 t2 ON t1.column1 = t2.column3 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 t1 JOIN table2 t2 ON t1.column1 = t2.column3")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带left join的查询
     */
    @Test
    public void testSelectWithLeftJoin() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column2).addColumn(column3);
            select.leftJoin(table2, new EqualsTo(column1, column3));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2, column3 FROM table1 LEFT JOIN table2 ON column1 = column3 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2, column3 FROM table1 LEFT JOIN table2 ON column1 = column3")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带right join的查询
     */
    @Test
    public void testSelectWithRightJoin() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column2);
            select.rightJoin(table2, new EqualsTo(column1, column3));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 RIGHT JOIN table2 ON column1 = column3 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 RIGHT JOIN table2 ON column1 = column3")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带inner join的查询
     */
    @Test
    public void testSelectWithInnerJoin() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column2);
            select.innerJoin(table2, new EqualsTo(column1, column3));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 INNER JOIN table2 ON column1 = column3 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 INNER JOIN table2 ON column1 = column3")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带order by的查询
     */
    @Test
    public void testSelectWithOrderBy() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column3);
            select.orderBy(column3);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column3 FROM table1 ORDER BY column3 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column3 FROM table1 ORDER BY column3")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带order by desc的查询
     */
    @Test
    public void testSelectWithOrderByDesc() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column3);
            select.orderBy(new OrderByItem(column3, false));
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column3 FROM table1 ORDER BY column3 DESC LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column3 FROM table1 ORDER BY column3 DESC")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带group by的查询
     */
    @Test
    public void testSelectWithGroupBy() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn("count(*)");
            select.groupBy(column1);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, count(*) FROM table1 GROUP BY column1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, count(*) FROM table1 GROUP BY column1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带limit的查询
     */
    @Test
    public void testSelectWithLimit() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column2);
            select.limit(10);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 LIMIT 10")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 LIMIT 10")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带limit的查询
     */
    @Test
    public void testSelectWithLimitWithOffset() {
        try {
            Select select = Select.parse(simpleSql);
            select.addColumn(column2);
            select.limit(1,500);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 LIMIT 1, 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT column1, column2 FROM table1 LIMIT 1, 500")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 复杂查询
     */
    @Test
    public void testComplexSelect() {
        try {
            Select select = Select.parse("select A.name name from Student A");
            select.addColumn("A.age", "age").addColumn("A.sex", "sex")
                    .addColumn("B.name", "subject").addColumn("B.value", "score")
                    .leftJoin(new Table("Score", "B"), new EqualsTo("A.id", "B.student"))
                    .where("B.name = 'math' \n" +
                            "    AND B.value >= 90 \n" +
                            "    OR (\n" +
                            "        B.name = 'chinese' \n" +
                            "        AND B.value >= '85'\n" +
                            "    ) \n" +
                            "    AND A.class_id IN (\n" +
                            "        SELECT\n" +
                            "            c.class_id \n" +
                            "        FROM\n" +
                            "            Class c \n" +
                            "        WHERE\n" +
                            "            c.grade IN (\n" +
                            "                2, 3\n" +
                            "            )\n" +
                            "    ) ");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT\n" +
                            "    A.name AS name,\n" +
                            "    A.age AS age,\n" +
                            "    A.sex AS sex,\n" +
                            "    B.name AS subject,\n" +
                            "    B.value AS score\n" +
                            "FROM\n" +
                            "    Student A\n" +
                            "LEFT JOIN\n" +
                            "    Score B\n" +
                            "        ON A.id = B.student\n" +
                            "WHERE\n" +
                            "    B.name = 'math'\n" +
                            "    AND B.value >= 90\n" +
                            "    OR B.name = 'chinese'\n" +
                            "    AND B.value >= '85'\n" +
                            "    AND A.class_id IN (\n" +
                            "        SELECT\n" +
                            "            c.class_id\n" +
                            "        FROM\n" +
                            "            Class c\n" +
                            "        WHERE\n" +
                            "            c.grade IN (\n" +
                            "                2, 3\n" +
                            "            )\n" +
                            "    )")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT\n" +
                            "    A.name AS name,\n" +
                            "    A.age AS age,\n" +
                            "    A.sex AS sex,\n" +
                            "    B.name AS subject,\n" +
                            "    B.value AS score\n" +
                            "FROM\n" +
                            "    Student A\n" +
                            "LEFT JOIN\n" +
                            "    Score B\n" +
                            "        ON A.id = B.student\n" +
                            "WHERE\n" +
                            "    B.name = 'math'\n" +
                            "    AND B.value >= 90\n" +
                            "    OR B.name = 'chinese'\n" +
                            "    AND B.value >= '85'\n" +
                            "    AND A.class_id IN (\n" +
                            "        SELECT\n" +
                            "            c.class_id\n" +
                            "        FROM\n" +
                            "            Class c\n" +
                            "        WHERE\n" +
                            "            c.grade IN (\n" +
                            "                2, 3\n" +
                            "            )\n" +
                            "    )")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 更换查询列
     */
    @Test
    public void testChangeColumn() {
        try {
            String sql = "select * from "+table1;
            Select select = Select.parse(sql);
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT * FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT * FROM table1")
            );
            select = select.changeAllColumns("count(*)");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT count(*) FROM table1")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT count(*) FROM table1")
            );
            select = select.changeAllColumns("*");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT * FROM table1 LIMIT 500")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT * FROM table1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSelectWithConnNameAndJoin() {
        try {
            Select select = Select.parse("select * from ipu_db_demo");
            select.join("ipu_sql_config", new EqualsTo("pk", "id"));
            select.where("sql='abc'");
//        select.and("a='abc'");
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql()),
                    SqlFormatter.format("SELECT * FROM ipu_db_demo JOIN ipu_sql_config ON pk = id WHERE sql = 'abc'")
            );
            Assert.assertEquals(
                    SqlFormatter.format(select.getSql(false)),
                    SqlFormatter.format("SELECT * FROM ipu_db_demo JOIN ipu_sql_config ON pk = id WHERE sql = 'abc'")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
