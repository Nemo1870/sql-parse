package com.sql.parse.util;

import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @date 2019/4/30 10:48
 * @desc sql解析器测试类
 */
public class SqlParserTest {
    private static final Logger logger = LogManager.getLogger(SqlParserTest.class);

    /**
     * @desc 获取所有表名
     */
    @Test
    public void testGetTableNames(){
        SqlParser sqlParser = new SqlParser("DELETE FROM A");
        List<String> tableNames = sqlParser.getTableNames();
        logger.debug(tableNames.toString());
        Assert.assertEquals(tableNames.size(), 1);
        Assert.assertTrue(tableNames.contains("A"));

        sqlParser = new SqlParser("insert into A (select * from B)");
        tableNames = sqlParser.getTableNames();
        logger.debug(tableNames.toString());
        Assert.assertEquals(tableNames.size(), 2);
        Assert.assertTrue(tableNames.contains("A"));
        Assert.assertTrue(tableNames.contains("B"));

        sqlParser = new SqlParser("UPDATE job SET status = 1, finish = 1, last_update_time = 1 WHERE status = 0 ORDER BY create_time DESC LIMIT 1");
        tableNames = sqlParser.getTableNames();
        logger.debug(tableNames.toString());
        Assert.assertEquals(tableNames.size(), 1);
        Assert.assertTrue(tableNames.contains("job"));

        sqlParser = new SqlParser("select * from (select id, name from B) A");
        tableNames = sqlParser.getTableNames();
        logger.debug(tableNames.toString());
        Assert.assertEquals(tableNames.size(), 1);
        Assert.assertFalse(tableNames.contains("A"));
        Assert.assertTrue(tableNames.contains("B"));

        sqlParser = new SqlParser("SELECT A.name AS name, A.age AS age, A.sex AS sex, B.name AS subject, B.value AS score FROM Student A LEFT JOIN Score B ON A.id = B.student WHERE B.name = 'math' AND B.value > 90 OR (B.name = 'chinese' AND B.value > 85) AND A.class_id IN (SELECT * FROM Class WHERE grade IN (2, 3))");
        tableNames = sqlParser.getTableNames();
        logger.debug(tableNames.toString());
        Assert.assertEquals(tableNames.size(), 3);
        Assert.assertTrue(tableNames.contains("Student"));
        Assert.assertTrue(tableNames.contains("Score"));
        Assert.assertTrue(tableNames.contains("Class"));
    }

    /**
     * @desc 获取列名
     */
    @Test
    public void testGetColumnNames(){
        SqlParser sqlParser = new SqlParser("insert into A (select * from B)");
        List<String> columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 0);

        sqlParser = new SqlParser("insert into A(a, b) (select * from B)");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 2);
        Assert.assertTrue(columnNames.contains("a"));
        Assert.assertTrue(columnNames.contains("b"));

        sqlParser = new SqlParser("insert into A values (1, 2)");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 0);

        sqlParser = new SqlParser("insert into A(a, b) values (1, 2)");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 2);
        Assert.assertTrue(columnNames.contains("a"));
        Assert.assertTrue(columnNames.contains("b"));

        sqlParser = new SqlParser("UPDATE job SET status = 1, finish = 1, last_update_time = 1 WHERE status = 0 ORDER BY create_time DESC LIMIT 1");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 3);
        Assert.assertTrue(columnNames.contains("status"));
        Assert.assertTrue(columnNames.contains("finish"));
        Assert.assertTrue(columnNames.contains("last_update_time"));

        sqlParser = new SqlParser("SELECT id, name from A");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 2);
        Assert.assertTrue(columnNames.contains("id"));
        Assert.assertTrue(columnNames.contains("name"));

        sqlParser = new SqlParser("SELECT A.name AS name, A.age AS age, A.sex AS sex, B.name AS subject, B.value AS score FROM Student A LEFT JOIN Score B ON A.id = B.student WHERE B.name = 'math' AND B.value > 90 OR (B.name = 'chinese' AND B.value > 85) AND A.class_id IN (SELECT * FROM Class WHERE grade IN (2, 3))");
        columnNames = sqlParser.getColumnNames();
        logger.debug(columnNames.toString());
        Assert.assertEquals(columnNames.size(), 5);
        Assert.assertTrue(columnNames.contains("name"));
        Assert.assertTrue(columnNames.contains("age"));
        Assert.assertTrue(columnNames.contains("sex"));
    }

    /**
     * @desc 获取查询条件的列名
     */
    @Test
    public void testGetColumnNamesInWhere(){
        SqlParser sqlParser = new SqlParser("UPDATE job SET status = 1, finish = 1, last_update_time = 1 WHERE status = 0 ORDER BY create_time DESC LIMIT 1");
        String where = sqlParser.getWhere();
        logger.debug(ExpressionBuilder.getColumns(ExpressionBuilder.parse(where)).toString());

        sqlParser = new SqlParser("SELECT A.name AS name, A.age AS age, A.sex AS sex, B.name AS subject, B.value AS score FROM Student A LEFT JOIN Score B ON A.id = B.student WHERE B.name = 'math' AND B.value > 90 OR (B.name = 'chinese' AND B.value > 85) AND A.class_id IN (SELECT * FROM Class WHERE grade IN (2, 3))");
        where = sqlParser.getWhere();
        logger.debug(ExpressionBuilder.getColumns(ExpressionBuilder.parse(where)).toString());
    }

    @Test
    public void testGetAliasOrColumnNames() {
        SqlParser sqlParser = new SqlParser("SELECT USER_ID as a, USER_NAME, USER_AGE, USER_SEX, USER_TAG FROM CB_USER WHERE USER_NAME = getParam(USER_NAME) AND USER_SEX = getParam(USER_SEX) AND USER_ID = getParam(USER_ID) AND USER_TAG = getParam(USER_TAG)");
        List<String> list = sqlParser.getAliasOrColumnNames();
        logger.debug(list.toString());
        Assert.assertEquals(list.size(), 5);
        Assert.assertTrue(list.contains("a"));
        Assert.assertFalse(list.contains("USER_ID"));
    }
}
