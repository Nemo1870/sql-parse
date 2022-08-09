package com.sql.parse.statement.jsqlparser;

import com.sql.parse.statement.impl.jsqlparser.Insert;
import com.sql.parse.statement.impl.jsqlparser.Select;
import com.sql.parse.schema.InsertItem;
import com.sql.parse.util.SqlFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @date 2019年4月30日上午09:58:02
 * @desc Insert测试类
 */
public class InsertTest {
    private static final Logger logger = LogManager.getLogger(InsertTest.class);

    private String table;
    private String column1;
    private String column2;
    private String column3;
    private String value1;
    private String value2;
    private String value3;
    private String simpleSql;
    private String simpleSql2;
    private String simpleSql3;

    @Before
    public void before() {
        // TODO Auto-generated method stub
        table = "table1";
        column1 = "column1";
        column2 = "column2";
        column3 = "column3";
        value1 = "1";
        value2 = "'2'";
        value3 = "3";
        simpleSql = "insert into " + table + " values (1)";
        simpleSql2 = "insert into " + table + "(pk) values (1)";
        simpleSql3 = "insert into " + table + " select * from table2";
    }

    @Test
    public void testParse() {
        try {
            Insert insert = Insert.parse(simpleSql);
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1)")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1)")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 简单的新增：INSERT INTO table VALUES (1, '2', 3)
     */
    @Test
    public void testInsertWithValueColumn() {
        try {
            Insert insert = Insert.parse(simpleSql);
            insert.addValueColumn(value1).addValueColumn(value2).addValueColumn(value3);
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1, 1, '2', 3)")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1, 1, '2', 3)")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInsertWithValue() {
        try {
            Insert insert = Insert.parse(simpleSql);
            insert.addValue(value1).addValue(value2).addValue(value3);
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1), (1), ('2'), (3)")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1 VALUES (1), (1), ('2'), (3)")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 简单的新增：INSERT INTO table (column1, column2, column3) VALUES (1, '2', 3)
     */
    @Test
    public void testInsertAddColumn() {
        try {
            Insert insert = Insert.parse(simpleSql2);
            insert.addColumn(new InsertItem(column1, value1));
            insert.addColumn(new InsertItem(column2, value2));
            insert.addColumn(new InsertItem(column3, value3));
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1(pk, column1, column2, column3) VALUES (1, 1, '2', 3)")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1(pk, column1, column2, column3) VALUES (1, 1, '2', 3)")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 新增新增数据:INSERT INTO table (column1, column2, column3) VALUES (1, '2', now())
     */
    @Test
    public void testInsertAddColumnWithValue() {
        try {
            Insert insert = Insert.parse(simpleSql2);
            insert.addColumn(column1, value1);
            insert.addColumn(column2, value2);
            insert.addColumn(column3, "now()");
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1(pk, column1, column2, column3) VALUES (1, 1, '2', now())")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1(pk, column1, column2, column3) VALUES (1, 1, '2', now())")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带select的新增：INSERT INTO table (SELECT * FROM table)
     */
    @Test
    public void testInsertWithSelect() {
        try {
            Insert insert = Insert.parse(simpleSql3);
            insert.setSelect("select * from table2");
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1 SELECT * FROM table2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1 SELECT * FROM table2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带select的新增：INSERT INTO table (column1, column2, column3) (SELECT * FROM table)
     */
    @Test
    public void testInsertWithColumnAndSelect() {
        try {
            Insert insert = Insert.parse(simpleSql3);
            insert.setSelect("select * from table2");
            insert.addColumn(column1).addColumn(column2).addColumn(column3);
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql()),
                    SqlFormatter.format("INSERT INTO table1(column1, column2, column3) SELECT * FROM table2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(insert.getSql(false)),
                    SqlFormatter.format("INSERT INTO table1(column1, column2, column3) SELECT * FROM table2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
