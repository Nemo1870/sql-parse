package com.sql.parse.util;

import com.sql.parse.model.Column;
import com.sql.parse.statement.*;
import org.junit.Assert;
import org.junit.Test;

public class ColumnInterceptorTest {
    @Test
    public void testSelectWhere() {
        String sql = "select userId, userName from tb_user where userId = '1'";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select user_id, user_name from tb_user where user_id = '1'").getSql(false)
        );
        sql = "select user_id, user_name from tb_user where user_id = '1'";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
        sql = "select userId, userName from tb_user where userId = '2' or (userId = '1' and userName = '2')";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select user_id, user_name from tb_user where user_id = '2' or (user_id = '1' and user_name = '2')").getSql(false)
        );
    }

    @Test
    public void testSelectFrom() {
        String sql = "select userId, userName from (select userId, userName from tb_user where userId = '1') t";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select user_id, user_name from (select user_id, user_name from tb_user where user_id = '1') t").getSql(false)
        );
        sql = "select id, name from (select user_id, user_name from tb_user where user_id = '1') t";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoin() {
        String sql = "select userId, userName, addressInfo as info from address a, (select userId, userName from tb_user where userId = '1') t where a.addressId = t.addressId";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select user_id, user_name, address_info as info from address a, (select user_id, user_name from tb_user where user_id = '1') t where a.address_id = t.address_id").getSql(false)
        );
        sql = "select user_id, user_name, address_info as info from address a, (select user_id, user_name from tb_user where user_id = '1') t where a.address_id = t.address_id";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinOn() {
        String sql = "select a.userId, a.userName from user a left join company b on a.parent in (select c.userId as parent from user c where c.company = b.userId and b.userId != '1')";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.user_id, a.user_name from user a left join company b on a.parent in (select c.user_id as parent from user c where c.company = b.user_id and b.user_id != '1')").getSql(false)
        );
        sql = "select a.user_id, a.user_name from user a left join company b on a.parent in (select c.user_id as parent from user c where c.company = b.user_id and b.user_id != '1')";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsertSubSelect() {
        String sql = "insert into tb_user_tmp select userId, userName from tb_user where create_date < '2020-01-01' and userType = '1'";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_user_tmp select user_id, user_name from tb_user where create_date < '2020-01-01' and user_type = '1'").getSql(false)
        );
        sql = "insert into tb_user_tmp select user_id, user_name from tb_user where create_date < '2020-01-01' and user_type = '1'";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsertColumn() {
        String sql = "insert into tb_user_tmp(userId, userName) values ('1', 'test')";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_user_tmp(user_id, user_name) values ('1', 'test')").getSql(false)
        );
        sql = "insert into tb_user_tmp(user_id, user_name) values ('1', 'test')";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateWhere() {
        String sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'").getSql(false)
        );
        sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateColumn() {
        String sql = "update tb_user set userName = 'test' where create_date < '2020-01-01' and userType = '1'";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user set user_name = 'test' where create_date < '2020-01-01' and user_type = '1'").getSql(false)
        );
        sql = "update tb_user set user_name = 'test' where create_date < '2020-01-01' and user_type = '1'";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testDelete() {
        String sql = "delete from tb_user where userType = '1' and userFlag = true";
        String newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse("delete from tb_user where user_type = '1' and user_flag = true").getSql(false)
        );
        sql = "delete from tb_user where user_type = '1' and user_flag = true";
        newSql = columnInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse(sql).getSql(false)
        );
    }


    private String columnInterceptor(String sql) {
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.columnInterceptor((column) -> {
            check(column);
        });
        return statement.getSql(false);
    }

    private void check(Column column) {
        String tableName = column.getTableName();
        if (tableName != null) {
            column.setTableName(changeStr(tableName));
        }
        column.setColumnName(changeStr(column.getColumnName()));
    }

    private String changeStr(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                stringBuilder.append("_").append(Character.toLowerCase(c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
}
