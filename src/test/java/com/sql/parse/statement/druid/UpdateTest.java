package com.sql.parse.statement.druid;

import com.sql.parse.schema.UpdateItem;
import com.sql.parse.statement.impl.druid.Update;
import com.sql.parse.util.SqlFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @team IPU
 * @date 2019年4月30日上午09:58:02
 * @desc Update测试类
 */
public class UpdateTest {
    private static final Logger logger = LogManager.getLogger(UpdateTest.class);

    private String table;
    private String column1;
    private String column2;
    private String column3;
    private String value1;
    private String value2;
    private String simpleSql;

    @Before
    public void before() {
        // TODO Auto-generated method stub
        table = "table";
        column1 = "column1";
        column2 = "column2";
        column3 = "column3";
        value1 = "1";
        value2 = "2";
        simpleSql = "update " + table + " set pk = 1";
    }

    @Test
    public void testParse() {
        try {
            Update update = Update.parse(simpleSql);
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1 WHERE 1 = 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 最简单的更新
     */
    @Test
    public void testSimpleUpdate() {
        try {
            Update update = Update.parse(simpleSql);
            update.addColumn(new UpdateItem(column1, value1));
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE 1 = 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 新增更新数据
     */
    @Test
    public void testUpdateAddColumn() {
        try {
            Update update = Update.parse(simpleSql);
            update.addColumn(column1, value1);
            update.addColumn(new UpdateItem(column2, value2));
            update.addColumn(column3, "now()");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1, column2 = 2, column3 = now() WHERE 1 = 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1, column2 = 2, column3 = now()")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带WHERE的更新
     */
    @Test
    public void testUpdateWithWhere() {
        try {
            Update update = Update.parse(simpleSql);
            update.addColumn(column1, value1);
            update.where(column3 + " IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 更新新增and条件
     */
    @Test
    public void testUpdateWithAnd() {
        try {
            Update update = Update.parse(simpleSql);
            update.addColumn(column1, value1);
            update.where(column3 + " IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
            update = update.and(column2 + ">=" + value2);
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL AND column2 >= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL AND column2 >= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 更新新增or条件
     */
    @Test
    public void testUpdateWithOr() {
        try {
            Update update = Update.parse(simpleSql);
            update.addColumn(column1, value1);
            update.where(column3 + " IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL")
            );
            update = update.or("column2<=2");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL OR column2 <= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE table SET pk = 1, column1 = 1 WHERE column3 IS NULL OR column2 <= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateWithConnName() {
        try {
            Update update = Update.parse("update ipu_db_demo set pk = 1");
//        update.addColumn(new UpdateItem("pk", "1"));
            //update.addColumn(new UpdateItem("a", "1"));
            update.where("pk = 1");
            update.and("int_type > 1");
            update.or("null_type is null");
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql()),
                    SqlFormatter.format("UPDATE ipu_db_demo SET pk = 1 WHERE pk = 1 AND int_type > 1 OR null_type IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(update.getSql(false)),
                    SqlFormatter.format("UPDATE ipu_db_demo SET pk = 1 WHERE pk = 1 AND int_type > 1 OR null_type IS NULL")            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
