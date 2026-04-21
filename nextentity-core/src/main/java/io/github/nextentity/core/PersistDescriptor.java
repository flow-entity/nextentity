package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

public interface PersistDescriptor<T> {

    EntityDescriptor<T> entityDescriptor();

    PersistConfig persistConfig();

    default PersistExecutor persistExecutor() {
        return persistConfig().persistExecutor();
    }

    default Class<T> entityClass() {
        return entityDescriptor().entityClass();
    }

    default Metamodel metamodel() {
        return persistConfig().metamodel();
    }

    default EntityType entityType() {
        return entityDescriptor().entityType();
    }
}
