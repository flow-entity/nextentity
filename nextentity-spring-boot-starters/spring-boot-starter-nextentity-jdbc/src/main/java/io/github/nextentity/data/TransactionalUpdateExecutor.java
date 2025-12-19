package io.github.nextentity.data;

import io.github.nextentity.core.UpdateExecutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TransactionalUpdateExecutor implements UpdateExecutor {

    private final UpdateExecutor target;

    public TransactionalUpdateExecutor(UpdateExecutor target) {
        this.target = target;
    }

    @Override
    @Transactional
    public <T> T insert(@NotNull T entity, @NotNull Class<T> entityType) {
        return target.insert(entity, entityType);
    }

    @Override
    @Transactional
    public <T> List<T> insertAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType) {
        return target.insertAll(entities, entityType);
    }

    @Override
    @Transactional
    public <T> List<T> updateAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType) {
        return target.updateAll(entities, entityType);
    }

    @Override
    @Transactional
    public <T> T update(@NotNull T entity, Class<T> entityType) {
        return target.update(entity, entityType);
    }

    @Override
    @Transactional
    public <T> void deleteAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType) {
        target.deleteAll(entities, entityType);
    }

    @Override
    @Transactional
    public <T> void delete(@NotNull T entity, @NotNull Class<T> entityType) {
        target.delete(entity, entityType);
    }

    @Override
    @Transactional
    public <T> T updateExcludeNullColumn(@NotNull T entity, @NotNull Class<T> entityType) {
        return target.updateExcludeNullColumn(entity, entityType);
    }

}
