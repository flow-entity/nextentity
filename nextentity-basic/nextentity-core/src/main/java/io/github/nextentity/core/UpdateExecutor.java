package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author HuangChengwei
 * @since 2024-05-06 13:55
 */
public interface UpdateExecutor {

    default <T> T insert(@NotNull T entity, @NotNull Class<T> entityType) {
        return insertAll(ImmutableList.of(entity), entityType).get(0);
    }

    <T> List<T> insertAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType);

    <T> List<T> updateAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType);

    default <T> T update(@NotNull T entity, Class<T> entityType) {
        return updateAll(ImmutableList.of(entity), entityType).get(0);
    }

    <T> void deleteAll(@NotNull Iterable<T> entities, @NotNull Class<T> entityType);

    default <T> void delete(@NotNull T entity, @NotNull Class<T> entityType) {
        deleteAll(ImmutableList.of(entity), entityType);
    }

    <T> T updateExcludeNullColumn(@NotNull T entity, @NotNull Class<T> entityType);

}
