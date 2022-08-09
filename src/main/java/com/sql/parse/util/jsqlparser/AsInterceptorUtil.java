package com.sql.parse.util.jsqlparser;

import com.sql.parse.model.Alias;
import com.sql.parse.model.Change;
import com.sql.parse.statement.impl.jsqlparser.Select;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2022/7/5 11:34
 * @desc TODO
 */
public class AsInterceptorUtil {
    public static void checkExpression(Expression expression, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        if (expression instanceof BinaryExpression) {
            checkExpression(((BinaryExpression) expression).getLeftExpression(), columnInterceptor, tableInterceptor, list);
            checkExpression(((BinaryExpression) expression).getRightExpression(), columnInterceptor, tableInterceptor, list);
        } else if (expression instanceof Parenthesis) {
            checkExpression(((Parenthesis) expression).getExpression(), columnInterceptor, tableInterceptor, list);
        } else if (expression instanceof InExpression) {
            ItemsList itemsList = ((InExpression) expression).getRightItemsList();
            if (itemsList instanceof SubSelect) {
                checkExpression((SubSelect) itemsList, columnInterceptor, tableInterceptor, list);
            }
        } else if (expression instanceof SubSelect) {
            Select select = Select.parse(((SubSelect) expression).getSelectBody().toString());
            select.asInterceptor(columnInterceptor, tableInterceptor);
            ((SubSelect) expression).setSelectBody(select.getSelect().getSelectBody());
        }
    }

    public static void checkFrom(FromItem fromItem, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        net.sf.jsqlparser.expression.Alias alias = fromItem.getAlias();
        if (alias != null) {
            fromItem.setAlias(null);
            Alias newAlias = new Alias(
                    fromItem.toString(),
                    alias.getName(),
                    alias.isUseAs()
            );
            tableInterceptor.accept(newAlias);
            if (newAlias.isDisable()) {
                list.add(new Change(alias.getName(), null));
                fromItem.setAlias(null);
            } else {
                list.add(new Change(alias.getName(), newAlias.getRightExpression()));
                alias.setName(newAlias.getRightExpression());
                fromItem.setAlias(alias);
            }
        } else {
            fromItem.setAlias(null);
            Alias newAlias = new Alias(
                    fromItem.toString()
            );
            tableInterceptor.accept(newAlias);
            if (newAlias.isDisable()) {
                fromItem.setAlias(null);
            } else {
                list.add(new Change(null, newAlias.getRightExpression()));
                alias = new net.sf.jsqlparser.expression.Alias(newAlias.getRightExpression(), newAlias.isUseAs());
                fromItem.setAlias(alias);
            }
        }
        if (fromItem instanceof SubSelect) {
            Select select = Select.parse(((SubSelect) fromItem).getSelectBody().toString());
            select.asInterceptor(columnInterceptor, tableInterceptor);
            ((SubSelect) fromItem).setSelectBody(select.getSelect().getSelectBody());
            fromItem.setAlias(alias);
        }
    }

    public static void checkJoins(List<Join> joins, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        if (joins != null) {
            for (Join join : joins) {
                FromItem fromItem = join.getRightItem();
                checkFrom(fromItem, columnInterceptor, tableInterceptor, list);
                Expression on = join.getOnExpression();
                checkExpression(on, columnInterceptor, tableInterceptor, list);
            }
        }
    }

    public static void checkPlainSelect(PlainSelect plainSelect, Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor, List<Change> list) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {
            if (selectItem instanceof SelectExpressionItem) {
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                net.sf.jsqlparser.expression.Alias alias = selectExpressionItem.getAlias();
                if (alias != null) {
                    Alias newAlias = new Alias(
                            selectExpressionItem.getExpression().toString(),
                            alias.getName(),
                            alias.isUseAs()
                    );
                    columnInterceptor.accept(newAlias);
                    if (newAlias.isDisable()) {
                        selectExpressionItem.setAlias(null);
                    } else {
                        alias.setName(newAlias.getRightExpression());
                        alias.setUseAs(newAlias.isUseAs());
                    }
                } else {
                    Alias newAlias = new Alias(
                            selectExpressionItem.getExpression().toString()
                    );
                    columnInterceptor.accept(newAlias);
                    if (newAlias.isDisable()) {
                        selectExpressionItem.setAlias(null);
                    } else {
                        alias = new net.sf.jsqlparser.expression.Alias(newAlias.getRightExpression(), newAlias.isUseAs());
                        selectExpressionItem.setAlias(alias);
                    }
                }
            }
        }
        List<Join> joins = plainSelect.getJoins();
        checkJoins(joins, columnInterceptor, tableInterceptor, list);
        FromItem fromItem = plainSelect.getFromItem();
        checkFrom(fromItem, columnInterceptor, tableInterceptor, list);
        Expression where = plainSelect.getWhere();
        checkExpression(where, columnInterceptor, tableInterceptor, list);
    }
}
