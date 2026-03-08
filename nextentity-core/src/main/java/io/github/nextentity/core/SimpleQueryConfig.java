package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class SimpleQueryConfig implements QueryConfig {
    private QueryExecutor queryExecutor;
    private Metamodel metamodel;

    public QueryExecutor queryExecutor() {
        return this.queryExecutor;
    }

    public Metamodel metamodel() {
        return this.metamodel;
    }

    public SimpleQueryConfig queryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
        return this;
    }

    public SimpleQueryConfig metamodel(Metamodel metamodel) {
        this.metamodel = metamodel;
        return this;
    }
}
