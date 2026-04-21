package io.github.nextentity.core;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.FetchConfig;

public record EntityTemplateFactoryConfig(
        Metamodel metamodel,
        PersistExecutor persistExecutor,
        QueryExecutor queryExecutor,
        FetchConfig fetch,
        PaginationConfig pagination,
        InterceptorSelector<ConstructInterceptor> constructors
) implements QueryConfig, PersistConfig {

    public static QueryConfig queryConfig(Metamodel metamodel,
                                          QueryExecutor executor,
                                          FetchConfig fetch,
                                          PaginationConfig pagination,
                                          InterceptorSelector<ConstructInterceptor> constructor
    ) {
        return new EntityTemplateFactoryConfig(metamodel, null, executor, fetch, pagination, constructor);
    }

    public static PersistConfig persistConfig(Metamodel metamodel, PersistExecutor executor) {
        return new EntityTemplateFactoryConfig(metamodel, executor, null, null, null, null);
    }

}
