package io.github.nextentity.core;

import io.github.nextentity.api.EntityContext;
import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.api.EntityPersistor;
import io.github.nextentity.api.EntityQuery;

public interface EntityOperationsFactory extends EntityContext {

    <T> EntityOperations<T> operations(Class<T> entityType);

    @Override
    default <T> EntityQuery<T> query(Class<T> entityType) {
        return operations(entityType).query();
    }

    @Override
    default <T> EntityPersistor<T> persistor(Class<T> entityType) {
        return operations(entityType);
    }
}
