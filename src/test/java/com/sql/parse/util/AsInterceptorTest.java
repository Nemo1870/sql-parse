package com.sql.parse.util;

import com.sql.parse.model.Alias;
import com.sql.parse.statement.*;
import org.junit.Assert;
import org.junit.Test;

public class AsInterceptorTest {
    @Test
    public void testTableAlias() {
        String sql = "select id, name from tb_user tb_user where id = '1'";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id = '1'").getSql(false)
        );
        sql = "select id, name from tb_user a where id = '1'";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testSelectColumnAlias() {
        String sql = "select id as id, name from tb_user where id = '1'";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id = '1'").getSql(false)
        );
        sql = "select id as a, name from tb_user where id = '1'";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testFromAlias() {
        String sql = "select id, name from (select userid as userid, username as name from tb_user) t where id = '1'";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from (select userid, username as name from tb_user) t where id = '1'").getSql(false)
        );
        sql = "select id, name from (select userid as id, username as name from tb_user) t where id = '1'";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinAlias() {
        String sql = "select id, name, address from tb_address a, (select userid as userid, username as name from tb_user) t where t.id = '1' and a.id = t.address";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name, address from tb_address a, (select userid, username as name from tb_user) t where t.id = '1' and a.id = t.address").getSql(false)
        );
        sql = "select id, name, address from tb_address a, (select userid as id, username as name from tb_user) t where t.id = '1' and a.id = t.address";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testSelectInAlias() {
        String sql = "select info from tb_address where id in (select address as address from tb_user where id = '1')";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select info from tb_address where id in (select address from tb_user where id = '1')").getSql(false)
        );
        sql = "select info from tb_address where id in (select address as id from tb_user where id = '1')";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsertSelectAlias() {
        String sql = "insert into tb_address select address as address, address_info as name from tb_user";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_address select address, address_info as name from tb_user").getSql(false)
        );
        sql = "insert into tb_address select address as id, address_info as name from tb_user";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testDeleteInAlias() {
        String sql = "delete from tb_address where id in (select address as address from tb_user where id = '1')";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse("delete from tb_address where id in (select address from tb_user where id = '1')").getSql(false)
        );
        sql = "delete from tb_address where id in (select address as id from tb_user where id = '1')";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateInAlias() {
        String sql = "update tb_address set name = 'test' where id in (select address as address from tb_user where id = '1')";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_address set name = 'test' where id in (select address from tb_user where id = '1')").getSql(false)
        );
        sql = "update tb_address set name = 'test' where id in (select address as id from tb_user where id = '1')";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinOnAlias() {
        String sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as id from user c where c.company = b.id)";
        String newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.id, a.name from user a left join company b on a.parent in (select c.id from user c where c.company = b.id)").getSql(false)
        );
        sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id)";
        newSql = asInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testChangeColumnAlias() {
        String sql = "select a.id, a.name from user a where a.id is not null and a.name like '%test%'";
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.asInterceptor((alias) -> {

        }, (alias) -> {
            if ("user".equals(alias.getLeftExpression().toLowerCase())) {
                alias.setRightExpression("b");
            }
        });
        Assert.assertEquals(
                statement.getSql(false),
                Select.parse("select b.id, b.name from user b where b.id is not null and b.name like '%test%'").getSql(false)
        );

        sql = "select a.id, a.name from user a left join address b on a.address = b.id where a.id is not null and b.type in (1, 2)";
        sqlParser = new SqlParser(sql);
        statement = sqlParser.getStatement();
        statement.asInterceptor((alias) -> {

        }, (alias) -> {
            if ("address".equals(alias.getLeftExpression().toLowerCase())) {
                alias.setRightExpression("c");
            }
        });
        Assert.assertEquals(
                statement.getSql(false),
                Select.parse("select a.id, a.name from user a left join address c on a.address = c.id where a.id is not null and c.type in (1, 2)").getSql(false)
        );
    }

    private String asInterceptor(String sql) {
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.asInterceptor((alias) -> {
            check(alias);
        }, (alias) -> {
            check(alias);
        });
        return statement.getSql(false);
    }

    private void check(Alias alias) {
        String leftExpression = alias.getLeftExpression();
        if (leftExpression.contains(".")) {
            leftExpression = leftExpression.split("\\.")[1];
        }
        if (leftExpression.equals(alias.getRightExpression())) {
            alias.setDisable(true);
        }
    }

}
