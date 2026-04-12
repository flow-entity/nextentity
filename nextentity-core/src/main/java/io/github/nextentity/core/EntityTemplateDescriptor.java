package io.github.nextentity.core;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

public record EntityTemplateDescriptor<T>(

        PersistExecutor persistExecutor,

        QueryExecutor queryExecutor,

        PaginationConfig paginationConfig,

        Metamodel metamodel,

        EntityType entityType,

        Class<T> entityClass
) implements PersistDescriptor<T>, QueryDescriptor<T> {
}
