package com.sql.parse.util;

import com.sql.parse.statement.Select;
import com.sql.parse.statement.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * @date 2019/4/30 15:09
 * @desc sql格式化测试类
 */
public class SqlFormatterTest {
    private static final Logger logger = LogManager.getLogger(SqlFormatterTest.class);

    @Test
    public void testFormat() {
        String sql = "SELECT A.name AS name, A.age AS age, A.sex AS sex, B.name AS subject, B.value AS score FROM Student A LEFT JOIN Score B ON A.id = B.student WHERE B.name = 'math' AND B.value > 90 OR (B.name = 'chinese' AND B.value > 85) AND A.class_id IN (SELECT * FROM Class WHERE grade IN (2, 3))";
        logger.debug(sql);
        logger.debug(SqlFormatter.format(sql));
        Assert.assertEquals(
                Select.parse(sql).getSql(false),
                Select.parse(SqlFormatter.format(sql)).getSql(false)
        );
        sql = "UPDATE job SET status = 1, finish = 1, last_update_time = 1 WHERE status = 0 ORDER BY create_time DESC LIMIT 1";
        logger.debug(sql);
        logger.debug(SqlFormatter.format(sql));
        Assert.assertEquals(
                Update.parse(sql).getSql(false),
                Update.parse(SqlFormatter.format(sql)).getSql(false)
        );
    }
}
