package io.github.nextentity.core;

import io.github.nextentity.api.EntityContext;
import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.api.EntityPersistor;
import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

public record EntityTemplateFactory(
        @NonNull Metamodel metamodel,
        @NonNull QueryExecutor queryExecutor,
        @NonNull PersistExecutor persistExecutor,
        @NonNull PaginationConfig paginationConfig
) implements EntityContext {

    public <T> EntityTemplate<T> template(Class<T> entityType) {
        EntityTemplateDescriptor<T> descriptor = new EntityTemplateDescriptor<>(
                persistExecutor,
                queryExecutor,
                paginationConfig,
                metamodel,
                metamodel.getEntity(entityType),
                entityType
        );
        return new EntityTemplate<>(descriptor);
    }

    @Override
    public <T> EntityQuery<T> query(Class<T> entityType) {
        return template(entityType).query();
    }

    @Override
    public <T> EntityPersistor<T> persistor(Class<T> entityType) {
        return template(entityType);
    }

    @Override
    @Deprecated
    public <T> EntityOperations<T> operations(Class<T> entityType) {
        throw new UnsupportedOperationException();
    }
}
