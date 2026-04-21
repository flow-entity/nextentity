package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

public record EntityTemplateDescriptor<T>(

        EntityTemplateFactoryConfig config,

        EntityDescriptor<T> descriptor

) implements QueryDescriptor<T>, PersistDescriptor<T> {

    public EntityTemplateDescriptor(EntityTemplateFactoryConfig config, Class<T> clazz) {
        this(
                config,
                new SimpleEntityDescriptor<>(config.metamodel().getEntity(clazz), clazz)
        );
    }

    @Override
    public PersistConfig persistConfig() {
        return config;
    }

    @Override
    public EntityDescriptor<T> entityDescriptor() {
        return descriptor;
    }

    @Override
    public QueryConfig queryConfig() {
        return config;
    }

    public Class<T> entityClass() {
        return entityDescriptor().entityClass();
    }

    public Metamodel metamodel() {
        return queryConfig().metamodel();
    }

    public EntityType entityType() {
        return entityDescriptor().entityType();
    }
}
