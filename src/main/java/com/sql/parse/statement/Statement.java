package com.sql.parse.statement;

import com.sql.parse.exception.BaseException;
import com.sql.parse.model.*;

import java.util.function.Consumer;

public abstract class Statement {
    static{
        BaseException.register("com/sql/parse/exception_messages");
    }

    public String getSql() {
        return getSql(true);
    }

    public abstract String getSql(boolean safe);

    public abstract void asInterceptor(Consumer<Alias> columnInterceptor, Consumer<Alias> tableInterceptor);

    public void columnAsInterceptor(Consumer<Alias> interceptor) {
        asInterceptor(interceptor, (result)-> {});
    }

    public void tableAsInterceptor(Consumer<Alias> interceptor) {
        asInterceptor((result)-> {}, interceptor);
    }

    public abstract void argInterceptor(Consumer<Arg> interceptor);

    public abstract void expressionInterceptor(Consumer<Expression> interceptor);

    public abstract void columnInterceptor(Consumer<Column> interceptor);

    public abstract void ownerInterceptor(Consumer<Owner> interceptor);

}
