package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface QueryConfig {

    Metamodel metamodel();

    QueryExecutor queryExecutor();

}
