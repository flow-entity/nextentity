package io.github.nextentity.core;

import io.github.nextentity.api.Update;
import org.jspecify.annotations.NonNull;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public class Updaters {

    public static <T> Update<T> create(UpdateExecutor updateExecutor, Class<T> type) {
        return new UpdateImpl<>(updateExecutor, type);
    }

    public static class UpdateImpl<T> implements Update<T> {
        private final UpdateExecutor updateExecutor;
        private final Class<T> entityType;

        public UpdateImpl(UpdateExecutor updateExecutor, Class<T> entityType) {
            this.entityType = entityType;
            this.updateExecutor = updateExecutor;
        }

        @Override
        public void insert(@NonNull T entity) {
            updateExecutor.insert(entity, entityType);
        }

        @Override
        public void insert(@NonNull Iterable<T> entities) {
            updateExecutor.insertAll(entities, entityType);
        }

        @Override
        public void update(@NonNull Iterable<T> entities) {
            updateExecutor.updateAll(entities, entityType);
        }

        @Override
        public void update(@NonNull T entity) {
            updateExecutor.update(entity, entityType);
        }

        @Override
        public void delete(@NonNull Iterable<T> entities) {
            updateExecutor.deleteAll(entities, entityType);
        }

        @Override
        public void delete(@NonNull T entity) {
            updateExecutor.delete(entity, entityType);
        }

        @Override
        public String toString() {
            return "Updater(" + entityType.getSimpleName() + ')';
        }
    }

}
