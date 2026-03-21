package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.EntityTransaction;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface UpdateExecutor {

    default <T> void insert(@NonNull T entity, @NonNull Class<T> entityType) {
        insertAll(ImmutableList.of(entity), entityType);
    }

    <T> void insertAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    <T> List<T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    default <T> T update(@NonNull T entity, Class<T> entityType) {
        return updateAll(ImmutableList.of(entity), entityType).get(0);
    }

    <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    default <T> void delete(@NonNull T entity, @NonNull Class<T> entityType) {
        deleteAll(ImmutableList.of(entity), entityType);
    }

    <T> T patch(@NonNull T entity, @NonNull Class<T> entityType);

    default void doInTransaction(Runnable command) {
        doInTransaction(() -> {
            command.run();
            return null;
        });
    }

    <T> T doInTransaction(Supplier<T> command);
}
