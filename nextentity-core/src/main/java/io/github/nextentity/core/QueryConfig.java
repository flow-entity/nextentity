package io.github.nextentity.core;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.FetchConfig;

public interface QueryConfig {

    QueryExecutor queryExecutor();

    FetchConfig fetch();

    Metamodel metamodel();

    PaginationConfig pagination();

    InterceptorSelector<ConstructInterceptor> constructors();

}
