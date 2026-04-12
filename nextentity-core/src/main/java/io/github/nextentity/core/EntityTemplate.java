package io.github.nextentity.core;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityPersistor;
import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.api.UpdateSetStep;

import java.util.function.Supplier;

public class EntityTemplate<T> implements EntityPersistor<T> {

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

    public EntityQuery<T> query() {
        return query;
    }

    @Override
    public void insert(T entity) {
        descriptor.persistExecutor().insert(entity, descriptor);
    }

    @Override
    public void insertAll(Iterable<T> entities) {
        descriptor.persistExecutor().insertAll(entities, descriptor);
    }

    @Override
    public void update(T entity) {
        descriptor.persistExecutor().update(entity, descriptor);
    }

    @Override
    public void updateAll(Iterable<T> entities) {
        descriptor.persistExecutor().updateAll(entities, descriptor);
    }

    @Override
    public void delete(T entity) {
        descriptor.persistExecutor().delete(entity, descriptor);
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        descriptor.persistExecutor().deleteAll(entities, descriptor);
    }

    @Override
    public UpdateSetStep<T> update() {
        return new UpdateSetStepImpl<>(descriptor);
    }

    @Override
    public DeleteWhereStep<T> delete() {
        return new DeleteWhereStepImpl<>(descriptor);
    }

    @Override
    @Deprecated
    public void doInTransaction(Runnable command) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public <X> X doInTransaction(Supplier<X> command) {
        throw new UnsupportedOperationException();
    }
}
