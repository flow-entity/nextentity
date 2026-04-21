package io.github.nextentity.core;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.api.UpdateSetStep;

public class EntityTemplate<T> implements EntityOperations<T> {

    private final PersistDescriptor<T> descriptor;
    private final EntityQuery<T> query;

    public EntityTemplate(EntityTemplateDescriptor<T> descriptor) {
        this.descriptor = descriptor;
        this.query = new EntityQueryImpl<>(descriptor);
    }

    public EntityTemplate(PersistDescriptor<T> descriptor, EntityQuery<T> query) {
        this.descriptor = descriptor;
        this.query = query;
    }

    public PersistDescriptor<T> descriptor() {
        return descriptor;
    }

    @Override
    public EntityQuery<T> query() {
        return query;
    }

    @Override
    public void insert(T entity) {
        persistExecutor().insert(entity, descriptor);
    }

    private PersistExecutor persistExecutor() {
        return descriptor.persistConfig().persistExecutor();
    }

    @Override
    public void insertAll(Iterable<T> entities) {
        persistExecutor().insertAll(entities, descriptor);
    }

    @Override
    public void update(T entity) {
        persistExecutor().update(entity, descriptor);
    }

    @Override
    public void updateAll(Iterable<T> entities) {
        persistExecutor().updateAll(entities, descriptor);
    }

    @Override
    public void delete(T entity) {
        persistExecutor().delete(entity, descriptor);
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        persistExecutor().deleteAll(entities, descriptor);
    }

    @Override
    public UpdateSetStep<T> update() {
        return new UpdateSetStepImpl<>(descriptor);
    }

    @Override
    public DeleteWhereStep<T> delete() {
        return new DeleteWhereStepImpl<>(descriptor);
    }
}
