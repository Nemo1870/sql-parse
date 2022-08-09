package com.sql.parse.statement.impl.jsqlparser;

import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import com.sql.parse.expression.relational.EqualsTo;
import com.sql.parse.model.Arg;
import com.sql.parse.model.Change;
import com.sql.parse.model.Owner;
import com.sql.parse.schema.OrderByItem;
import com.sql.parse.schema.SelectItem;
import com.sql.parse.schema.Table;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月29日上午11:32:02
 * @desc Select语法操作类
 */
public class Select extends com.sql.parse.statement.Select {
    private net.sf.jsqlparser.statement.select.Select select;

    public Select(net.sf.jsqlparser.statement.select.Select select) {
        this.select = select;
    }

    public Select(Table table) {
        this.select = SelectUtils.buildSelectFromTable(transformTable(table));
        isChangeColumn = true;
    }

    public Select(String tableName) {
        this(new Table(tableName));
    }

    /*public Select(Owner table, SelectItem... columns) {
        if(columns.length==0){
            this.select = SelectUtils.buildSelectFromTable(table.getTable());
        }else{
            SelectExpressionItem[] array = new SelectExpressionItem[columns.length];
            for (int i = 0; i < columns.length; i++) {
                array[i] = columns[i].getColumn();
            }
            this.select = SelectUtils.buildSelectFromTableAndSelectItems(table.getTable(), array);
        }
    }*/

    private net.sf.jsqlparser.schema.Table transformTable(Table table) {
        net.sf.jsqlparser.schema.Table t = new net.sf.jsqlparser.schema.Table(table.getTableName());
        if (table.getAlias() != null) {
            Alias alias = new Alias(table.getAlias(), table.isUseAs());
            t.setAlias(alias);
        }
        return t;
    }

    @Override
    public String getSql(boolean safe) {
        /*where条件为空时，添加[rownum<500]或[limit 500]的条件表达式*/
        Select mySelect = this.clone();
        PlainSelect plainSelect = (PlainSelect) mySelect.getSelect().getSelectBody();
        if (plainSelect.getLimit() == null) {
            boolean flag = true;
            List<net.sf.jsqlparser.statement.select.SelectItem> selectItems = plainSelect.getSelectItems();
            if (selectItems.size() == 1) {
                net.sf.jsqlparser.statement.select.SelectItem selectItem = selectItems.get(0);
                if (!(selectItem instanceof AllColumns)) {
                    Expression expression = ((SelectExpressionItem) selectItem).getExpression();
                    if (expression instanceof Function && "count".equals(((Function) expression).getName())) {
                        flag = false;
                    }
                }
            }
            if (plainSelect.getWhere() != null) {
                flag = false;
            }
            if (safe && flag) {
                mySelect.limit(500);
            }
        }
        return mySelect.getSelect().toString();
    }

    public net.sf.jsqlparser.statement.select.Select getSelect() {
        return this.select;
    }

    /**
     * @title: addColumn
     * @desc: 增加查询字段，支持别名
     */
    public Select addColumn(String expr, String alias) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        if(isChangeColumn){
            plainSelect.getSelectItems().clear();
            isChangeColumn = false;
        }
        SelectItem item = new SelectItem(expr, alias);
        /*支持别名使用，代替*/
        plainSelect.getSelectItems().add(transformSelectItem(item));
        return this;
    }

    public Select addColumn(SelectItem item) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        if(isChangeColumn){
            plainSelect.getSelectItems().clear();
            isChangeColumn = false;
        }
        plainSelect.getSelectItems().add(transformSelectItem(item));
        return this;
    }

    /**
     * @title: addColumn
     * @desc: 增加查询字段，不支持别名
     */
    public Select addColumn(String expr) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        if(isChangeColumn){
            plainSelect.getSelectItems().clear();
            isChangeColumn = false;
        }
        try {
            Expression expression;
            if ("*".equals(expr)) {
                List<net.sf.jsqlparser.statement.select.SelectItem> list =  plainSelect.getSelectItems();
                list.add(new AllColumns());
            } else {
                expression = ExpressionBuilder.parse(expr);
                /*观察者模式，不支持别名的使用*/
                SelectUtils.addExpression(select, expression);
            }
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_008);
        }
        return this;
    }

    private SelectExpressionItem transformSelectItem(SelectItem selectItem) {
        try{
            SelectExpressionItem selectExpressionItem = new SelectExpressionItem(ExpressionBuilder.parse(selectItem.getColumnName()));
            selectExpressionItem.setAlias(new Alias(selectItem.getAlias(), selectItem.isUseAs()));
            return selectExpressionItem;
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_002);
        }
    }

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public Select where(Expression where) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.setWhere(where);
        return this;
    }

    public Select where(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        where(expression);
        return this;
    }

    /**
     * @title: removeAllWhere
     * @desc 移除where条件
     */
    public Select removeAllWhere() {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.setWhere(null);
        return this;
    }

    /**
     * @title: and
     * @desc "AND"符号
     */
    public Select and(Expression where) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression oldWhere = plainSelect.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_005);
        } else {
            plainSelect.setWhere(ExpressionBuilder.and(oldWhere, where));
        }
        return this;
    }

    public Select and(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        and(expression);
        return this;
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public Select or(Expression where) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression oldWhere = plainSelect.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_006);
        } else {
            plainSelect.setWhere(ExpressionBuilder.or(oldWhere, where));
        }
        return this;
    }

    public Select or(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        or(expression);
        return this;
    }

    /**
     * @title: simpleJoin
     * @desc "select * form a,b"的JOIN方式
     */
    public Select simpleJoin(String tableName) {
        return simpleJoin(new Table(tableName));
    }

    public Select simpleJoin(Table table) {
        net.sf.jsqlparser.schema.Table t = transformTable(table);
        Join join = new Join();
        join.setRightItem(t);
        join.setSimple(true);
        return createJoin(join);
    }

    /**
     * @title: join
     * @desc "JOIN"符号
     */
    public Select join(String tableName, EqualsTo on) {
        return join(new Table(tableName), on);
    }

    public Select join(Table table, EqualsTo on) {
        net.sf.jsqlparser.schema.Table t = transformTable(table);
        Join join = new Join();
        join.setRightItem(t);
        join.setOnExpression(ExpressionBuilder.equalsTo(on));
        return createJoin(join);
    }

    /**
     * @title: leftJoin
     * @desc "LEFT JOIN"符号
     */
    public Select leftJoin(String tableName, EqualsTo on) {
        return leftJoin(new Table(tableName), on);
    }

    public Select leftJoin(Table table, EqualsTo on) {
        net.sf.jsqlparser.schema.Table t = transformTable(table);
        Join join = new Join();
        join.setRightItem(t);
        join.setLeft(true);
        join.setOnExpression(ExpressionBuilder.equalsTo(on));
        return createJoin(join);
    }

    /**
     * @title: rightJoin
     * @desc "RIGHT JOIN"符号
     */
    public Select rightJoin(String tableName, EqualsTo on) {
        return rightJoin(new Table(tableName), on);
    }

    public Select rightJoin(Table table, EqualsTo on) {
        net.sf.jsqlparser.schema.Table t = transformTable(table);
        Join join = new Join();
        join.setRightItem(t);
        join.setRight(true);
        join.setOnExpression(ExpressionBuilder.equalsTo(on));
        return createJoin(join);
    }

    /**
     * @title: innerJoin
     * @desc "INNER JOIN"符号
     */
    public Select innerJoin(String tableName, EqualsTo on) {
        return innerJoin(new Table(tableName), on);
    }

    public Select innerJoin(Table table, EqualsTo on) {
        net.sf.jsqlparser.schema.Table t = transformTable(table);
        Join join = new Join();
        join.setRightItem(t);
        join.setInner(true);
        join.setOnExpression(ExpressionBuilder.equalsTo(on));
        return createJoin(join);
    }

    private Select createJoin(Join join) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<Join> joins = plainSelect.getJoins();
        List<Join> newJoins = new ArrayList<Join>();
        if (joins != null) {
            newJoins = joins;
        }
        newJoins.add(join);
        plainSelect.setJoins(newJoins);
        return this;
    }

    /**
     * @title: orderBy
     * @desc "ORDER BY"符号
     */
    public Select orderBy(String... columnNames) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<OrderByElement> orderBys = plainSelect.getOrderByElements();
        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (String columnName : columnNames) {
            OrderByElement orderBy = new OrderByElement();
            orderBy.setExpression(new Column(columnName));
            newOrderBys.add(orderBy);
        }
        plainSelect.setOrderByElements(newOrderBys);
        return this;
    }

    public Select orderBy(OrderByItem... orderByItems) {
        List<String> columnNames = new ArrayList<String>();
        for (OrderByItem orderByElement : orderByItems) {
            Expression expression = transformOrderByItem(orderByElement).getExpression();
            if (expression instanceof Column) {
                columnNames.add(((Column) expression).getColumnName());
            }
        }
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<OrderByElement> orderBys = plainSelect.getOrderByElements();
        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
        if (orderBys != null ) {
            newOrderBys = orderBys;
        }
        for (OrderByItem orderByItem : orderByItems) {
            newOrderBys.add(transformOrderByItem(orderByItem));
        }
        plainSelect.setOrderByElements(newOrderBys);
        return this;
    }

    private OrderByElement transformOrderByItem(OrderByItem orderByItem) {
        OrderByElement orderBy = new OrderByElement();
        orderBy.setExpression(new Column(orderByItem.getColumnName()));
        orderBy.setAsc(orderByItem.isAsc());
        orderBy.setAscDescPresent(!orderByItem.isAsc());
        return orderBy;
    }


    /**
     * @title: groupBy
     * @desc "GROUP BY"符号
     */
    public Select groupBy(String... columnNames) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        GroupByElement groupBys = plainSelect.getGroupBy();
        GroupByElement newGroupBys = new GroupByElement();
        if (groupBys != null ) {
            newGroupBys = groupBys;
        }
        for (String columnName : columnNames) {
            newGroupBys.addGroupingSet(new Column(columnName));
        }
        plainSelect.setGroupByElement(newGroupBys);
        return this;
    }

    /**
     * @title: limit
     * @desc "LIMIT"符号
     */
    public Select limit(int offset, int rows) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Limit limit = new Limit();
        limit.setRowCount(new LongValue(rows));
        limit.setOffset(new LongValue(offset));
        plainSelect.setLimit(limit);
        return this;
    }

    public Select limit(int rows) {
        PlainSelect plainSelect = (PlainSelect)select.getSelectBody();
        Limit limit = new Limit();
        limit.setRowCount(new LongValue(rows));
        plainSelect.setLimit(limit);
        return this;
    }

    /**
     * @title: changeAllColumns
     * @desc 替换所有查询字段
     */
    public Select changeAllColumns(SelectItem... columns) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.getSelectItems().clear();
        for (SelectItem column : columns) {
            addColumn(column);
        }
        return this;
    }

    public Select changeAllColumns(String... exprs) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.getSelectItems().clear();
        for (String expr : exprs) {
            addColumn(expr);
        }
        return this;
    }

    /**
     * @title: changeColumnByColumnName
     * @desc 替换指定的查询字段
     */
    public Select changeColumnByColumnName(String columnName, String expr) {
        try {
            Expression expression = ExpressionBuilder.parse(expr);
            return changeColumnByColumnName(columnName, expression);
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_008);
        }
    }

    public Select changeColumnByColumnName(String columnName, Expression expr) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<net.sf.jsqlparser.statement.select.SelectItem> selectItems = plainSelect.getSelectItems();
        List<net.sf.jsqlparser.statement.select.SelectItem> list = new ArrayList<>();
        for (net.sf.jsqlparser.statement.select.SelectItem selectItem : selectItems) {
            Expression expression = ((SelectExpressionItem) selectItem).getExpression();
            if (expression instanceof Column && (columnName.equals(((Column) expression).getColumnName()))) {
                SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
                selectExpressionItem.setExpression(expr);
                list.add(selectExpressionItem);
            } else {
                list.add(selectItem);
            }
        }
        plainSelect.setSelectItems(list);
        return this;
    }

    public Select changeColumnByColumnName(String columnName, String expr, String alias) {
        try {
            Expression expression = ExpressionBuilder.parse(expr);
            return changeColumnByColumnName(columnName, expression, alias);
        } catch (Exception e) {
            throw BaseException.errorCode(SqlParseConstant.CODE_008);
        }
    }

    public Select changeColumnByColumnName(String columnName, Expression expr, String alias) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<net.sf.jsqlparser.statement.select.SelectItem> selectItems = plainSelect.getSelectItems();
        List<net.sf.jsqlparser.statement.select.SelectItem> list = new ArrayList<>();
        for (net.sf.jsqlparser.statement.select.SelectItem selectItem : selectItems) {
            Expression expression = ((SelectExpressionItem) selectItem).getExpression();
            if (expression instanceof Column && (columnName.equals(expression.toString()))) {
                SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
                selectExpressionItem.setExpression(expr);
                selectExpressionItem.setAlias(new Alias(alias, true));
                list.add(selectExpressionItem);
            } else {
                list.add(selectItem);
            }
        }
        plainSelect.setSelectItems(list);
        return this;
    }

    /**
     * @title: parse
     * @desc Select解析器
     */
    public static Select parse(String sql) {
        try {
            net.sf.jsqlparser.statement.select.Select select = (net.sf.jsqlparser.statement.select.Select) CCJSqlParserUtil.parse(sql);
            return new Select(select);
        } catch (JSQLParserException e) {
            throw BaseException.error(e);
        }
    }

    public Select clone() {
        return Select.parse(this.select.toString());
    }

    public void asInterceptor(Consumer<com.sql.parse.model.Alias> columnInterceptor, Consumer<com.sql.parse.model.Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        AsInterceptorUtil.checkPlainSelect(plainSelect, columnInterceptor, tableInterceptor, list);
        for (Change change : list) {
            ownerInterceptor((owner) -> {
                if ((owner.isDisable() && change.getOldExpr() == null) || (!owner.isDisable() && owner.getName().equals(change.getOldExpr()))) {
                    if (change.getNewExpr() == null) {
                        owner.setDisable(true);
                    } else {
                        owner.setName(change.getNewExpr());
                    }
                }
            });
        }
    }

    public void argInterceptor(Consumer<Arg> interceptor) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        ArgInterceptorUtil.checkPlainSelect(plainSelect, interceptor);
    }

    public void expressionInterceptor(Consumer<com.sql.parse.model.Expression> interceptor) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        ExpressionInterceptorUtil.checkPlainSelect(plainSelect, interceptor);
    }

    public void columnInterceptor(Consumer<com.sql.parse.model.Column> interceptor) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        ColumnInterceptorUtil.checkPlainSelect(plainSelect, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        OwnerInterceptorUtil.checkPlainSelect(plainSelect, interceptor);
    }
}
