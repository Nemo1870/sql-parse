package com.sql.parse.statement.impl.jsqlparser;

import com.sql.parse.exception.BaseException;
import com.sql.parse.expression.jsqlparser.ExpressionBuilder;
import com.sql.parse.model.Alias;
import com.sql.parse.model.Arg;
import com.sql.parse.model.Owner;
import com.sql.parse.schema.InsertItem;
import com.sql.parse.util.SqlParseConstant;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @date 2019年4月30日上午09:58:02
 * @desc Insert语法操作类
 */
public class Insert extends com.sql.parse.statement.Insert {
    private net.sf.jsqlparser.statement.insert.Insert insert;

    public Insert(net.sf.jsqlparser.statement.insert.Insert insert) {
        this.insert = insert;
    }

    public Insert(String tableName) {
        net.sf.jsqlparser.statement.insert.Insert insert = new net.sf.jsqlparser.statement.insert.Insert();
        insert.setTable(new Table(tableName));
        this.insert = insert;
    }

    @Override
    public String getSql(boolean safe) {
        if (this.insert.getSelect() == null && this.insert.getItemsList() == null) {
            throw BaseException.errorCode(SqlParseConstant.CODE_014);
        } else {
            return this.insert.toString();
        }
    }

    public net.sf.jsqlparser.statement.insert.Insert getInsert() {
        return this.insert;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(String column) {
        if(insert.getColumns()==null){
            insert.setColumns(new ArrayList<Column>());
        }
        insert.getColumns().add(new Column(column));
        return this;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(String column, String value) {
        return addColumn(new InsertItem(column, value));
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据列
     */
    public Insert addColumn(InsertItem insertItem) {
        if (insert.getSelect() == null) {
            if (insert.getItemsList() == null) {
                insert.setItemsList(new ExpressionList(new ArrayList<Expression>()));
                insert.setUseValues(true);
            }
            if (insert.getColumns() == null) {
                insert.setColumns(new ArrayList<Column>());
            }
            insert.getColumns().add(new Column(insertItem.getColumnName()));
            addItemList(insert.getItemsList(), ExpressionBuilder.parse(insertItem.getExpression()));
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    /**
     * @title: addValue
     * @desc 新增insert数据列
     */
    public Insert addValueColumn(String value) {
        if (insert.getSelect() == null) {
            if (insert.getItemsList() == null) {
                insert.setItemsList(new ExpressionList(new ArrayList<Expression>()));
                insert.setUseValues(true);
            }
            try {
                Expression expression = ExpressionBuilder.parse(value);
                addItemList(insert.getItemsList(), expression);
            } catch (Exception e) {
                throw BaseException.errorCode(SqlParseConstant.CODE_002);
            }
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    private void addItemList(ItemsList itemsList, Expression expression) {
        itemsList.accept(new ItemsListVisitorAdapter() {
            public void visit(SubSelect subSelect) {
            }

            public void visit(ExpressionList expressionList) {
                expressionList.getExpressions().add(expression);
            }

            public void visit(MultiExpressionList multiExpressionList) {
                multiExpressionList.accept(new ItemsListVisitor() {
                    @Override
                    public void visit(SubSelect subSelect) {}

                    @Override
                    public void visit(ExpressionList expressionList) {
                        expressionList.getExpressions().add(expression);
                    }

                    @Override
                    public void visit(NamedExpressionList namedExpressionList) {}

                    @Override
                    public void visit(MultiExpressionList multiExpressionList) {}
                });
            }
        });
    }

    /**
     * @title: addValue
     * @desc 新增insert数据条数
     */
    public Insert addValue(String... valueColumns) {
        return addValue(Arrays.asList(valueColumns));
    }

    public Insert addValue(List<String> value) {
        if (insert.getSelect() == null) {
            try {
                List<Expression> list = new ArrayList<>();
                for (String valueColumn : value) {
                    Expression expression = ExpressionBuilder.parse(valueColumn);
                    list.add(expression);
                }
                if (insert.getItemsList() == null) {
                    MultiExpressionList multiExpressionList = new MultiExpressionList();
                    insert.setItemsList(multiExpressionList);
                    insert.setUseValues(true);
                } else if (insert.getItemsList() instanceof ExpressionList) {
                    ExpressionList expressionList = (ExpressionList) insert.getItemsList();
                    MultiExpressionList multiExpressionList = new MultiExpressionList();
                    multiExpressionList.addExpressionList(expressionList);
                    insert.setItemsList(multiExpressionList);
                }
                insert.getItemsList().accept(new ItemsListVisitorAdapter() {
                    public void visit(SubSelect subSelect) {
                    }

                    public void visit(ExpressionList expressionList) {
                    }

                    public void visit(MultiExpressionList multiExpressionList) {
                        multiExpressionList.addExpressionList(list);
                    }
                });
            } catch (Exception e) {
                throw BaseException.errorCode(SqlParseConstant.CODE_002);
            }
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_007);
        }
        return this;
    }

    /**
     * @title: addColumn
     * @desc 新增insert数据子查询
     */
    public Insert setSelect(com.sql.parse.statement.Select select) {
        if (insert.getItemsList() == null) {
            Select s = (Select) select;
            if (insert.getSelect() == null) {
                insert.setUseValues(false);
            }
            insert.setSelect(s.getSelect());
        } else {
            throw BaseException.errorCode(SqlParseConstant.CODE_019);
        }
        return this;
    }

    /**
     * @title: parse
     * @desc Insert解析器
     */
    public static Insert parse(String sql) {
        try {
            net.sf.jsqlparser.statement.insert.Insert insert = (net.sf.jsqlparser.statement.insert.Insert) CCJSqlParserUtil.parse(sql);
            return new Insert(insert);
        } catch (JSQLParserException e) {
            throw BaseException.error(e);
        }
    }

    public Insert clone() {
        return Insert.parse(this.insert.toString());
    }

    public void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor) {
        net.sf.jsqlparser.statement.select.Select sqlSelect = insert.getSelect();
        if (!insert.isUseValues()) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(sqlSelect.toString());
            select.asInterceptor(columnInterceptor, tableInterceptor);
            insert.setSelect(((Select) select).getSelect());
        }
    }

    public void argInterceptor(Consumer<Arg> interceptor) {
        net.sf.jsqlparser.statement.select.Select sqlSelect = insert.getSelect();
        if (!insert.isUseValues()) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(sqlSelect.toString());
            select.argInterceptor(interceptor);
            insert.setSelect(((Select) select).getSelect());
        }
    }

    public void expressionInterceptor(Consumer<com.sql.parse.model.Expression> interceptor) {
        net.sf.jsqlparser.statement.select.Select sqlSelect = insert.getSelect();
        if (!insert.isUseValues()) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(sqlSelect.toString());
            select.expressionInterceptor(interceptor);
            insert.setSelect(((Select) select).getSelect());
        }
    }

    public void columnInterceptor(Consumer<com.sql.parse.model.Column> interceptor) {
        List<Column> columns = insert.getColumns();
        if (columns != null) {
            for (Column column : columns) {
                com.sql.parse.model.Column newColumn = new com.sql.parse.model.Column(
                        column.getTable() == null ? null : column.getTable().getName(),
                        column.getColumnName()
                );
                interceptor.accept(newColumn);
                column.setColumnName(newColumn.getColumnName());
                if (newColumn.getTableName() == null) {
                    column.setTable(null);
                } else if (column.getTable() == null) {
                    column.setTable(new Table(newColumn.getTableName()));
                } else {
                    column.getTable().setName(newColumn.getTableName());
                }
            }
        }
        net.sf.jsqlparser.statement.select.Select sqlSelect = insert.getSelect();
        if (!insert.isUseValues()) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(sqlSelect.toString());
            select.columnInterceptor(interceptor);
            insert.setSelect(((Select) select).getSelect());
        }
    }

    public void ownerInterceptor(Consumer<Owner> interceptor) {
        List<Column> columns = insert.getColumns();
        if (columns != null) {
            for (Column column : columns) {
                Table table = column.getTable();
                Owner newOwner;
                if (table == null) {
                    newOwner = new Owner(null);
                    newOwner.setDisable(true);
                } else {
                    newOwner = new Owner(table.getName());
                }
                interceptor.accept(newOwner);
                if (newOwner.isDisable()) {
                    column.setTable(null);
                } else if (column.getTable() == null) {
                    column.setTable(new Table(newOwner.getName()));
                } else {
                    column.getTable().setName(newOwner.getName());
                }
            }
        }
        net.sf.jsqlparser.statement.select.Select sqlSelect = insert.getSelect();
        if (!insert.isUseValues()) {
            com.sql.parse.statement.Select select = com.sql.parse.statement.Select.parse(sqlSelect.toString());
            select.ownerInterceptor(interceptor);
            insert.setSelect(((Select) select).getSelect());
        }
    }
}
