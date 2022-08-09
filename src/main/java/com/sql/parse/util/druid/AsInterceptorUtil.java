package com.sql.parse.util.druid;

import com.sql.parse.model.Alias;
import com.sql.parse.model.Change;
import com.sql.parse.statement.impl.druid.Select;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 11:34
 * @desc TODO
 */
public class AsInterceptorUtil {
    public static void checkExpression(SQLExpr expression, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        if (expression instanceof SQLBinaryOpExpr) {
            checkExpression(((SQLBinaryOpExpr) expression).getLeft(), columnInterceptor, tableInterceptor, list);
            checkExpression(((SQLBinaryOpExpr) expression).getRight(), columnInterceptor, tableInterceptor, list);
        } else if (expression instanceof SQLInSubQueryExpr) {
            SQLSelect sqlSelect = ((SQLInSubQueryExpr) expression).getSubQuery();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.asInterceptor(columnInterceptor, tableInterceptor);
            ((SQLInSubQueryExpr) expression).setSubQuery(select.getSelect().getSelect());
        }
    }

    public static void checkSQLTableSource(SQLTableSource sqlTableSource, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect sqlSelect = ((SQLSubqueryTableSource) sqlTableSource).getSelect();
            Select select = Select.parse(SQLUtils.toSQLString(sqlSelect));
            select.asInterceptor(columnInterceptor, tableInterceptor);
            ((SQLSubqueryTableSource) sqlTableSource).setSelect(select.getSelect().getSelect());
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getLeft(), columnInterceptor, tableInterceptor, list);
            checkSQLTableSource(((SQLJoinTableSource) sqlTableSource).getRight(), columnInterceptor, tableInterceptor, list);
            checkExpression(((SQLJoinTableSource) sqlTableSource).getCondition(), columnInterceptor, tableInterceptor, list);
        } else if (sqlTableSource instanceof SQLExprTableSource) {
            String alias = sqlTableSource.getAlias();
            if (alias != null) {
                Alias newAlias = new Alias(
                        SQLUtils.toSQLString(((SQLExprTableSource) sqlTableSource).getExpr()),
                        sqlTableSource.getAlias(),
                        false
                );
                tableInterceptor.accept(newAlias);
                if (newAlias.isDisable()) {
                    list.add(new Change(sqlTableSource.getAlias(), null));
                    sqlTableSource.setAlias(null);
                } else {
                    list.add(new Change(sqlTableSource.getAlias(), newAlias.getRightExpression()));
                    sqlTableSource.setAlias(newAlias.getRightExpression());
                }
            }
        }
    }

    public static void checkSQLSelectQueryBlock(SQLSelectQueryBlock queryBlock, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        List<SQLSelectItem> selectItems = queryBlock.getSelectList();
        for (SQLSelectItem selectItem : selectItems) {
            String alias = selectItem.getAlias();
            if (alias != null) {
                Alias newAlias = new Alias(
                        SQLUtils.toSQLString(selectItem.getExpr()),
                        selectItem.getAlias(),
                        true
                );
                columnInterceptor.accept(newAlias);
                if (newAlias.isDisable()) {
                    selectItem.setAlias(null);
                } else {
                    selectItem.setAlias(newAlias.getRightExpression());
                }
            }
        }
        SQLTableSource from = queryBlock.getFrom();
        checkSQLTableSource(from, columnInterceptor, tableInterceptor, list);
        SQLExpr where = queryBlock.getWhere();
        checkExpression(where, columnInterceptor, tableInterceptor, list);
    }
}
