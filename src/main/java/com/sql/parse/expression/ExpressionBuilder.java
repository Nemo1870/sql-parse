package com.sql.parse.expression;

import com.alibaba.druid.sql.SQLUtils;
import com.sql.parse.config.SqlParseConfig;
import com.sql.parse.exception.BaseException;
import com.sql.parse.util.SqlParseConstant;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @date 2022/7/1 11:06
 * @desc TODO
 */
public class ExpressionBuilder {
    public static String equalsTo(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.equalsTo(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.equalsTo(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String notEqualsTo(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.notEqualsTo(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.notEqualsTo(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String greaterThan(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.greaterThan(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.greaterThan(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String greaterThanEquals(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.greaterThanEquals(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.greaterThanEquals(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String minorThan(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.minorThan(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.minorThan(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String minorThanEquals(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.minorThanEquals(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.minorThanEquals(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String isNull(String expression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.isNull(expression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.isNull(expression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String isNotNull(String expression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.isNotNull(expression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.isNotNull(expression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String and(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.and(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.and(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String or(String leftExpression, String rightExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.or(leftExpression, rightExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.or(leftExpression, rightExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String in(String leftExpression, List<String> rightExpressions) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.in(leftExpression, rightExpressions).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.in(leftExpression, rightExpressions));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String in(String leftExpression, String select) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.in(leftExpression, select).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.in(leftExpression, select));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String notIn(String leftExpression, List<String> rightExpressions) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.notIn(leftExpression, rightExpressions).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.notIn(leftExpression, rightExpressions));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String notIn(String leftExpression, String select) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.notIn(leftExpression, select).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.notIn(leftExpression, select));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String between(String leftExpression, String beginExpression, String endExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.between(leftExpression, beginExpression, endExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.between(leftExpression, beginExpression, endExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static String notBetween(String leftExpression, String beginExpression, String endExpression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.notBetween(leftExpression, beginExpression, endExpression).toString();
            }
            case "com.alibaba.druid":{
                return SQLUtils.toSQLString(com.sql.parse.expression.druid.ExpressionBuilder.notBetween(leftExpression, beginExpression, endExpression));
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static Set<String> getColumns(String expression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.getColumns(expression);
            }
            case "com.alibaba.druid":{
                return com.sql.parse.expression.druid.ExpressionBuilder.getColumns(expression);
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static List<Map<String, Object>> getColumnsAndValues(String expression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.getColumnsAndValues(expression);
            }
            case "com.alibaba.druid":{
                return com.sql.parse.expression.druid.ExpressionBuilder.getColumnsAndValues(expression);
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }

    public static List<Map<String, Object>> getColumnsAndValuesAndOperators(String expression) {
        switch (SqlParseConfig.getHandle()) {
            case "com.github.jsqlparser":{
                return com.sql.parse.expression.jsqlparser.ExpressionBuilder.getColumnsAndValuesAndOperators(expression);
            }
            case "com.alibaba.druid":{
                return com.sql.parse.expression.druid.ExpressionBuilder.getColumnsAndValuesAndOperators(expression);
            }
            default:{
                throw BaseException.errorCode(SqlParseConstant.CODE_018);
            }
        }
    }
}
