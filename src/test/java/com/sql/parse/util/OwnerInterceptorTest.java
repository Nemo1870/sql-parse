package com.sql.parse.util;

import com.sql.parse.model.Owner;
import com.sql.parse.statement.*;
import org.junit.Assert;
import org.junit.Test;

public class OwnerInterceptorTest {
    @Test
    public void testSelectWhere() {
        String sql = "select A.id, A.name from tb_user A where A.id = '1'";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.id, a.name from tb_user A where a.id = '1'").getSql(false)
        );
        sql = "select a.id, a.name from tb_user a where a.id = '1'";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
        sql = "select A.id, A.name from tb_user A where A.id = '2' or (A.id = '1' and A.name = '2')";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.id, a.name from tb_user A where a.id = '2' or (a.id = '1' and a.name = '2')").getSql(false)
        );
    }

    @Test
    public void testSelectFrom() {
        String sql = "select id, name from (select A.id, A.name from tb_user A where A.id = '1') t";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from (select a.id, a.name from tb_user A where a.id = '1') t").getSql(false)
        );
        sql = "select id, name from (select a.id, a.name from tb_user a where a.id = '1') t";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoin() {
        String sql = "select id, name, address_info as info from address a, (select B.id, B.name from tb_user B where b.id = '1') t where a.address_id = t.address_id";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name, address_info as info from address a, (select b.id, b.name from tb_user B where b.id = '1') t where a.address_id = t.address_id").getSql(false)
        );
        sql = "select id, name, address_info as info from address a, (select b.id, b.name from tb_user b where b.id = '1') t where a.address_id = t.address_id";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinOn() {
        String sql = "select A.user_id, A.user_name from user A left join company B on A.parent in (select C.user_id as parent from user C where C.company = B.user_id and B.user_id != '1')";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.user_id, a.user_name from user A left join company B on a.parent in (select c.user_id as parent from user C where c.company = b.user_id and b.user_id != '1')").getSql(false)
        );
        sql = "select a.user_id, a.user_name from user a left join company b on a.parent in (select c.user_id as parent from user c where c.company = b.user_id and b.user_id != '1')";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsert() {
        String sql = "insert into tb_user_tmp select A.id, A.name from tb_user A where A.create_date < '2020-01-01' and A.type = '1'";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_user_tmp select a.id, a.name from tb_user A where a.create_date < '2020-01-01' and a.type = '1'").getSql(false)
        );
        sql = "insert into tb_user_tmp select a.id, a.name from tb_user a where a.create_date < '2020-01-01' and a.type = '1'";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdate() {
        String sql = "update tb_user A set A.name = 'test' where A.create_date < '2020-01-01' and A.type = '1'";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user A set a.name = 'test' where a.create_date < '2020-01-01' and a.type = '1'").getSql(false)
        );
        sql = "update tb_user a set a.name = 'test' where a.create_date < '2020-01-01' and a.type = '1'";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testDelete() {
        String sql = "delete from tb_user A where A.type = '1' and A.flag = true";
        String newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse("delete from tb_user A where a.type = '1' and a.flag = true").getSql(false)
        );
        sql = "delete from tb_user a where a.type = '1' and a.flag = true";
        newSql = ownerIntercetor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse(sql).getSql(false)
        );
    }

    private String ownerIntercetor(String sql) {
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.ownerInterceptor((owner) -> {
            check(owner);
        });
        return statement.getSql(false);
    }

    private void check(Owner owner) {
        if (owner.getName() != null) {
            owner.setName(owner.getName().toLowerCase());
        }
    }
}
