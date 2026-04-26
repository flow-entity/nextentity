package io.github.nextentity.core;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;

public record EntityTemplateFactoryConfig(
        Metamodel metamodel,
        PersistExecutor persistExecutor,
        QueryExecutor queryExecutor,
        InterceptorSelector<ConstructInterceptor> constructors,
        QueryProperties properties
) implements QueryConfig, PersistConfig {

    public static PersistConfig persistConfig(Metamodel metamodel, PersistExecutor executor) {
        return new EntityTemplateFactoryConfig(
                metamodel, executor, null, null, QueryProperties.DEFAULT
        );
    }

}
