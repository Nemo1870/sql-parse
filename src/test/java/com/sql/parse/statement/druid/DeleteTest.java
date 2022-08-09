package com.sql.parse.statement.druid;

import com.sql.parse.statement.impl.druid.Delete;
import com.sql.parse.util.SqlFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @team IPU
 * @date 2019年4月30日上午09:58:02
 * @desc Delete测试类
 */
public class DeleteTest {
    private static final Logger logger = LogManager.getLogger(DeleteTest.class);

    private String table;
    private String column1;
    private String column2;
    private String simpleSql;

    @Before
    public void before() {
        // TODO Auto-generated method stub
        table = "table";
        column1 = "column1";
        column2 = "column2";
        simpleSql = "delete from " + table;
    }

    /**
     * @desc 最简单的删除
     */
    @Test
    public void testParse() {
        try {
            Delete delete = Delete.parse(simpleSql);
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE 1 = 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 带where的删除
     */
    @Test
    public void testDeleteWithWhere() {
        try {
            Delete delete = Delete.parse(simpleSql);
            //delete.where(ExpressionBuilder.isNull("a"));
            delete.where("column1 IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 删除添加and条件
     */
    @Test
    public void testDeleteWithAnd() {
        try {
            Delete delete = Delete.parse(simpleSql);
            delete.where("column1 IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
            delete = delete.and("column2 >= 2");
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL AND column2 >= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL AND column2 >= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * @desc 删除添加or条件
     */
    @Test
    public void testDeleteWithOr() {
        try {
            Delete delete = Delete.parse(simpleSql);
            delete.where("column1 IS NULL");
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL")
            );
            delete = delete.or("column2 >= 2");
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql()),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL OR column2 >= 2")
            );
            Assert.assertEquals(
                    SqlFormatter.format(delete.getSql(false)),
                    SqlFormatter.format("DELETE FROM table WHERE column1 IS NULL OR column2 >= 2")
            );
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
