package com.sql.parse.statement.impl.jsqlparser;

import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import com.sql.parse.model.*;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.jsqlparser.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Delete语法操作类
 */
public class Delete extends com.sql.parse.statement.Delete {
    private net.sf.jsqlparser.statement.delete.Delete delete;

    public Delete(net.sf.jsqlparser.statement.delete.Delete delete) {
        this.delete = delete;
    }

    public Delete(String tableName) {
        net.sf.jsqlparser.statement.delete.Delete delete = new net.sf.jsqlparser.statement.delete.Delete();
        delete.setTable(new Table(tableName));
        this.delete = delete;
    }

    @Override
    public String getSql(boolean safe) {
        Delete myDelete = this.clone();
        if (safe && myDelete.getDelete().getWhere() == null) {
            myDelete.where("1 = 2");
        }
        return myDelete.getDelete().toString();
    }

    public net.sf.jsqlparser.statement.delete.Delete getDelete() {
        return this.delete;
    }

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public Delete where(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        where(expression);
        return this;
    }

    public Delete where(Expression where) {
        Expression oldWhere = delete.getWhere();
        if (oldWhere == null) {
            delete.setWhere(where);
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_004);
        }
        return this;
    }

    /**
     * @title: and
     * @desc 移除where条件
     */
    public Delete removeAllWhere() {
        delete.setWhere(null);
        return this;
    }

    /**
     * @title: and
     * @desc "AND"符号
     */
    public Delete and(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        and(expression);
        return this;
    }

    public Delete and(Expression where) {
        Expression oldWhere = delete.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_005);
        } else {
            delete.setWhere(ExpressionBuilder.and(oldWhere, where));
        }
        return this;
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public Delete or(String expr) {
        Expression expression = ExpressionBuilder.parse(expr);
        or(expression);
        return this;
    }

    public Delete or(Expression where) {
        Expression oldWhere = delete.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_006);
        } else {
            delete.setWhere(ExpressionBuilder.or(oldWhere, where));
        }
        return this;
    }

//    /**
//     * @title: orderBy
//     * @desc "ORDER BY"符号
//     */
//    public Delete orderBy(String... columnNames) {
//        checkColumnNames(Arrays.asList(columnNames));
//        List<OrderByElement> orderBys = delete.getOrderByElements();
//        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
//        if (orderBys != null ) {
//            newOrderBys = orderBys;
//        }
//        for (String columnName : columnNames) {
//            OrderByElement orderBy = new OrderByElement();
//            orderBy.setExpression(new Column(columnName));
//            newOrderBys.add(orderBy);
//        }
//        delete.setOrderByElements(newOrderBys);
//        return this;
//    }
//
//    public Delete orderBy(OrderByItem... orderByItems) {
//        List<String> columnNames = new ArrayList<String>();
//        for (OrderByItem orderByElement : orderByItems) {
//            Expression expression = orderByElement.getOrderBy().getExpression();
//            if (expression instanceof Column) {
//                columnNames.add(((Column) expression).getColumnName());
//            }
//        }
//        checkColumnNames(columnNames);
//        List<OrderByElement> orderBys = delete.getOrderByElements();
//        List<OrderByElement> newOrderBys = new ArrayList<OrderByElement>();
//        if (orderBys != null ) {
//            newOrderBys = orderBys;
//        }
//        for (OrderByItem orderByItem : orderByItems) {
//            newOrderBys.add(orderByItem.getOrderBy());
//        }
//        delete.setOrderByElements(newOrderBys);
//        return this;
//    }
//
//    /**
//     * @title: limit
//     * @desc "LIMIT"符号
//     */
//    public Delete limit(int rows) {
//        Limit limit = new Limit();
//        limit.setRowCount(new LongValue(rows));
//        delete.setLimit(limit);
//        return this;
//    }

    /**
     * @title: parse
     * @desc Delete解析器
     */
    public static Delete parse(String sql) {
        try {
            net.sf.jsqlparser.statement.delete.Delete delete = (net.sf.jsqlparser.statement.delete.Delete) CCJSqlParserUtil.parse(sql);
            return new Delete(delete);
        } catch (JSQLParserException e) {
            throw BaseException.error(e);
        }
    }


    public Delete clone() {
        return Delete.parse(this.delete.toString());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        Expression where = delete.getWhere();
        AsInterceptorUtil.checkExpression(where, columnInterceptor, tableInterceptor, list);
        List<Join> joins = delete.getJoins();
        AsInterceptorUtil.checkJoins(joins, columnInterceptor, tableInterceptor, list);
        Table table = delete.getTable();
        AsInterceptorUtil.checkFrom(table, columnInterceptor, tableInterceptor, list);
        for (Change change : list) {
            ownerInterceptor((owner) -> {
                if ((owner.getName() == null && change.getOldExpr() == null) || (owner.getName().equals(change.getOldExpr()))) {
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
        Expression where = delete.getWhere();
        if (where != null) {
            Expression child = ArgInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        List<Join> joins = delete.getJoins();
        ArgInterceptorUtil.checkJoins(joins, interceptor);
        Table table = delete.getTable();
        ArgInterceptorUtil.checkFrom(table, interceptor);
    }

    public void expressionInterceptor(Consumer<com.sql.parse.model.Expression> interceptor) {
        Expression where = delete.getWhere();
        if (where != null) {
            Expression child = ExpressionInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        List<Join> joins = delete.getJoins();
        ExpressionInterceptorUtil.checkJoins(joins, interceptor);
        Table table = delete.getTable();
        ExpressionInterceptorUtil.checkFrom(table, interceptor);
    }

    public void columnInterceptor(Consumer<Column> interceptor) {
        Expression where = delete.getWhere();
        if (where != null) {
            Expression child = ColumnInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        List<Join> joins = delete.getJoins();
        ColumnInterceptorUtil.checkJoins(joins, interceptor);
        Table table = delete.getTable();
        ColumnInterceptorUtil.checkFrom(table, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        Expression where = delete.getWhere();
        if (where != null) {
            Expression child = OwnerInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        List<Join> joins = delete.getJoins();
        OwnerInterceptorUtil.checkJoins(joins, interceptor);
        Table table = delete.getTable();
        OwnerInterceptorUtil.checkFrom(table, interceptor);
    }
}
