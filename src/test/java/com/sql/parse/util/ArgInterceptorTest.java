package com.sql.parse.util;

import com.sql.parse.expression.ValueTypeEnum;
import com.sql.parse.model.Arg;
import com.sql.parse.statement.*;
import org.junit.Assert;
import org.junit.Test;

public class ArgInterceptorTest {
    @Test
    public void testSelectWhere() {
        String sql = "select id, name from tb_user where id = ' 1'";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id = '1'").getSql(false)
        );
        sql = "select id, name from tb_user where id = '1'";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
        sql = "select id, name from tb_user where id = '2' or (id = '1 ' and nam = '2')";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id = '2' or (id = '1' and nam = '2')").getSql(false)
        );
    }

    @Test
    public void testSelectFrom() {
        String sql = "select id, name from (select id, name from tb_user where id = ' 1') t";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from (select id, name from tb_user where id = '1') t").getSql(false)
        );
        sql = "select id, name from (select id, name from tb_user where id = '1') t";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoin() {
        String sql = "select id, name, address_info as info from address a, (select id, name from tb_user where id = ' 1') t where a.address_id = t.address_id";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name, address_info as info from address a, (select id, name from tb_user where id = '1') t where a.address_id = t.address_id").getSql(false)
        );
        sql = "select id, name, address_info as info from address a, (select id, name from tb_user where id = '1') t where a.address_id = t.address_id";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinOn() {
        String sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id != '1 ')";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id != '1')").getSql(false)
        );
        sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id != '1')";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testFunction() {
        String sql = "select id, name from tb_user where id = myfun(' 1')";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id = myfun('1')").getSql(false)
        );
        sql = "select id, name from tb_user where id = myfun('1')";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testIn() {
        String sql = "select id, name from tb_user where id in ('1', ' 2', '3')";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id in ('1', '2', '3')").getSql(false)
        );
        sql = "select id, name from tb_user where id in ('1', '2', '3')";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testSelectColumn() {
        String sql = "select id, myfun(' 1') as value from tb_user where id = 1";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, myfun('1') as value from tb_user where id = 1").getSql(false)
        );
        sql = "select id, myfun('1') as value from tb_user where id = 1";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsert() {
        String sql = "insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type = ' 1'";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type = '1'").getSql(false)
        );
        sql = "insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type = '1'";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateWhere() {
        String sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type = ' 1'";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'").getSql(false)
        );
        sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateColumn() {
        String sql = "update tb_user set name = ' test' where create_date < '2020-01-01' and type = '1'";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'").getSql(false)
        );
        sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type = '1'";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testDelete() {
        String sql = "delete from tb_user where type = '1 ' and flag = true";
        String newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse("delete from tb_user where type = '1' and flag = true").getSql(false)
        );
        sql = "delete from tb_user where type = '1' and flag = true";
        newSql = argInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse(sql).getSql(false)
        );
    }

    private String argInterceptor(String sql) {
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.argInterceptor((arg) -> {
            check(arg);
        });
        return statement.getSql(false);
    }

    private void check(Arg arg) {
        if (arg.getType().resolve() == ValueTypeEnum.STRING.resolve()) {
            String value = arg.getExpression();
            if (value != null) {
                String trim = value.trim();
                if (!value.equals(trim)) {
                    arg.setExpression(trim);
                }
            }
        }
    }
}
