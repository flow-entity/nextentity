package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

@Deprecated
public record EntityTemplateFactory(
        @NonNull Metamodel metamodel,
        @NonNull QueryExecutor queryExecutor,
        @NonNull PersistExecutor persistExecutor,
        @NonNull PaginationConfig paginationConfig
) implements EntityOperationsFactory {

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
    public <T> EntityTemplate<T> operations(Class<T> entityType) {
        return template(entityType);
    }

}
