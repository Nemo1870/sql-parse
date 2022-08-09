package com.sql.parse.util;

import com.sql.parse.expression.ExpressionBuilder;
import com.sql.parse.model.Expression;
import com.sql.parse.model.InListExpression;
import com.sql.parse.statement.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExpressionInterceptorTest {
    @Test
    public void testSelectWhere() {
        String sql = "select id, name from tb_user where id in (1, 2, 3, 4, 5, 6)";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id between 1 and 6").getSql(false)
        );
        sql = "select id, name from tb_user where id in (1, 2, 3, 3, 5, 6)";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
        sql = "select id, name from tb_user where id in ('1', '2', '3', '4', '5', '6')";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
        sql = "select id, name from tb_user where id in (-1, 0, 1, 2, 3, -2)";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where id between -2 and 3").getSql(false)
        );
        sql = "select id, name from tb_user where type = '1' and id in (1, 2, 3, 4, 5, 6)";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from tb_user where type = '1' and id between 1 and 6").getSql(false)
        );
    }

    @Test
    public void testSelectFrom() {
        String sql = "select id, name from (select id, name from tb_user where id in (1, 2, 3, 4, 5, 6)) t";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name from (select id, name from tb_user where id between 1 and 6) t").getSql(false)
        );
        sql = "select id, name from (select id, name from tb_user where id in (1, 3, 5)) t";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoin() {
        String sql = "select id, name, address_info as info from address a, (select id, name from tb_user where id in (1, 2, 3, 4, 5, 6)) t where a.address_id = t.address_id";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select id, name, address_info as info from address a, (select id, name from tb_user where id between 1 and 6) t where a.address_id = t.address_id").getSql(false)
        );
        sql = "select id, name, address_info as info from address a, (select id, name from tb_user where id in ('1', '2', '3')) t where a.address_id = t.address_id";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testJoinOn() {
        String sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id in (1, 2, 3, 4, 5, 6))";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse("select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id between 1 and 6)").getSql(false)
        );
        sql = "select a.id, a.name from user a left join company b on a.parent in (select c.id as parent from user c where c.company = b.id and b.id in ('1', '2', '3'))";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Select.parse(sql).getSql(false)
        );
    }

    @Test
    public void testInsert() {
        String sql = "insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type not in (1, 2, 3, 4, 5, 6)";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse("insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type not between 1 and 6").getSql(false)
        );
        sql = "insert into tb_user_tmp select id, name from tb_user where create_date < '2020-01-01' and type not in ('1', '2', '3')";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Insert.parse(sql).getSql(false)
        );
    }

    @Test
    public void testUpdateWhere() {
        String sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type not in (1, 2, 3, 4, 5, 6)";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse("update tb_user set name = 'test' where create_date < '2020-01-01' and type not between 1 and 6").getSql(false)
        );
        sql = "update tb_user set name = 'test' where create_date < '2020-01-01' and type not in ('1', '2', '3')";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Update.parse(sql).getSql(false)
        );
    }

    @Test
    public void testDelete() {
        String sql = "delete from tb_user where type not in (1, 2, 3, 4, 5, 6) and flag = true";
        String newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse("delete from tb_user where type not between 1 and 6 and flag = true").getSql(false)
        );
        sql = "delete from tb_user where type not in ('1', '2', '3') and flag = true";
        newSql = expressionInterceptor(sql);
        Assert.assertEquals(
                newSql,
                Delete.parse(sql).getSql(false)
        );
    }

    private String expressionInterceptor(String sql) {
        SqlParser sqlParser = new SqlParser(sql);
        Statement statement = sqlParser.getStatement();
        statement.expressionInterceptor((expression) -> {
            check(expression);
        });
        return statement.getSql(false);
    }

    private void check(Expression expression) {
        if (expression instanceof InListExpression) {
            List<String> rightExpressions = ((InListExpression) expression).getItemList();
            boolean canChange = true;
            List<Long> list = new ArrayList<>();
            for (String rightExpression : rightExpressions) {
                try {
                    list.add(Long.valueOf(rightExpression));
                } catch (NumberFormatException e) {
                    canChange = false;
                    break;
                }
            }
            if (canChange && list.size() > 1) {
                boolean isSeries = isSeries(list);
                if (isSeries) {
                    String expr;
                    String left = ((InListExpression) expression).getLeftExpression();
                    String start = String.valueOf(list.get(0));
                    String end = String.valueOf(list.get(list.size() - 1));
                    if (((InListExpression) expression).isNot()) {
                        expr = ExpressionBuilder.notBetween(left, start, end);
                    } else {
                        expr = ExpressionBuilder.between(left, start, end);
                    }
                    expression.setExpression(expr);
                }
            }
        }
    }

    private boolean isSeries(List<Long> list) {
        list.sort(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (o1 > o2) {
                    return 1;
                } else if (o1 == o2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        Long v = null;
        for (Long value : list) {
            if (v != null) {
                if (value - v != 1 && value != v) {
                    return false;
                }
            }
            v = value;
        }
        return true;
    }
}
