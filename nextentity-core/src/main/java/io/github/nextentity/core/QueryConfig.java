package io.github.nextentity.core;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;

public interface QueryConfig {

    QueryExecutor queryExecutor();

    Metamodel metamodel();

    InterceptorSelector<ConstructInterceptor> constructors();

    QueryProperties properties();

}
