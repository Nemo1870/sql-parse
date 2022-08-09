package com.sql.parse.statement.impl.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.model.*;
import com.sql.parse.util.SqlParseConstant;
import com.sql.parse.util.druid.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Delete语法操作类
 */
public class Delete extends com.sql.parse.statement.Delete {
    private SQLDeleteStatement delete;

    public Delete(SQLDeleteStatement delete) {
        this.delete = delete;
    }

    public Delete(String tableName) {
        SQLDeleteStatement delete = new SQLDeleteStatement(SqlParseConfig.getDbType());
        delete.setTableName(tableName);
        this.delete = delete;
    }

    @Override
    public String getSql(boolean safe) {
        Delete myDelete = this.clone();
        if (safe && myDelete.getDelete().getWhere() == null) {
            myDelete.where("1 = 2");
        }
        return SQLUtils.toSQLString(myDelete.getDelete());
    }

    public SQLDeleteStatement getDelete() {
        return this.delete;
    }

    /**
     * @title: where
     * @desc "WHERE"符号
     */
    public Delete where(String expr) {
        SQLExpr expression = new SQLExprParser(expr).expr();
        where(expression);
        return this;
    }

    public Delete where(SQLExpr where) {
        SQLExpr oldWhere = delete.getWhere();
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
        SQLExpr expression = new SQLExprParser(expr).expr();
        and(expression);
        return this;
    }

    public Delete and(SQLExpr where) {
        SQLExpr oldWhere = delete.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_005);
        } else {
            delete.setWhere(SQLBinaryOpExpr.and(oldWhere, where));
        }
        return this;
    }

    /**
     * @title: or
     * @desc "OR"符号
     */
    public Delete or(String expr) {
        SQLExpr expression = new SQLExprParser(expr).expr();
        or(expression);
        return this;
    }

    public Delete or(SQLExpr where) {
       SQLExpr oldWhere = delete.getWhere();
        if (oldWhere == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_006);
        } else {
            delete.setWhere(SQLBinaryOpExpr.or(oldWhere, where));
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
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, SqlParseConfig.getDbType());
        SQLDeleteStatement delete = (SQLDeleteStatement) sqlStatementList.get(0);
        return new Delete(delete);
    }

    public Delete clone() {
        return new Delete(this.delete.clone());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        List<Change> list = new ArrayList<>();
        SQLExpr where = delete.getWhere();
        AsInterceptorUtil.checkExpression(where, columnInterceptor, tableInterceptor, list);
        SQLTableSource from = delete.getFrom();
        AsInterceptorUtil.checkSQLTableSource(from, columnInterceptor, tableInterceptor, list);
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
        SQLExpr where = delete.getWhere();
        if (where != null) {
            SQLExpr child = ArgInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        SQLTableSource from = delete.getFrom();
        ArgInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void expressionInterceptor(Consumer<Expression> interceptor) {
        SQLExpr where = delete.getWhere();
        if (where != null) {
            SQLExpr child = ExpressionInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        SQLTableSource from = delete.getFrom();
        ExpressionInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void columnInterceptor(Consumer<Column> interceptor) {
        SQLExpr where = delete.getWhere();
        if (where != null) {
            SQLExpr child = ColumnInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        SQLTableSource from = delete.getFrom();
        ColumnInterceptorUtil.checkSQLTableSource(from, interceptor);
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        SQLExpr where = delete.getWhere();
        if (where != null) {
            SQLExpr child = OwnerInterceptorUtil.checkExpression(where, interceptor);
            delete.setWhere(child);
        }
        SQLTableSource from = delete.getFrom();
        OwnerInterceptorUtil.checkSQLTableSource(from, interceptor);
    }
}
