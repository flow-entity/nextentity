package io.github.nextentity.core;

import org.jspecify.annotations.NonNull;

public record EntityTemplateFactory(
        @NonNull QueryExecutor queryExecutor,
        @NonNull PersistExecutor persistExecutor,
        @NonNull EntityTemplateFactoryConfig config
) implements EntityOperationsFactory {

    public <T> EntityTemplate<T> template(Class<T> entityType) {
        EntityTemplateDescriptor<T> descriptor = new EntityTemplateDescriptor<>(config, entityType);
        return new EntityTemplate<>(descriptor);
    }

    @Override
    public <T> EntityTemplate<T> operations(Class<T> entityType) {
        return template(entityType);
    }

}
