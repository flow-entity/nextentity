package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

///
/// Update executor interface for executing INSERT, UPDATE, and DELETE operations.
///
/// This interface provides methods for bulk and single entity persistence operations,
/// as well as transaction management capabilities.
///
/// Implementations typically use JDBC batch operations or JPA entity manager
/// to interact with the database.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface UpdateExecutor {

    ///
    /// Inserts a single entity into the database.
    ///
    /// This is a convenience method that wraps `insertAll(Iterable, Class)`.
    ///
    /// @param <T> the entity type
    /// @param entity the entity to insert
    /// @param entityType the entity class
    /// @throws NullPointerException if entity or entityType is null
    ///
    default <T> void insert(@NonNull T entity, @NonNull Class<T> entityType) {
        insertAll(ImmutableList.of(entity), entityType);
    }

    ///
    /// Inserts multiple entities into the database in a batch operation.
    ///
    /// This method is optimized for bulk insertions and typically uses
    /// JDBC batch updates or JPA batch processing.
    ///
    /// @param <T> the entity type
    /// @param entities the entities to insert
    /// @param entityType the entity class
    /// @throws NullPointerException if entities or entityType is null
    ///
    <T> void insertAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    ///
    /// Updates multiple entities in the database and returns the updated instances.
    ///
    /// The returned entities may contain updated values such as generated IDs
    /// or version numbers after the update operation.
    ///
    /// @param <T> the entity type
    /// @param entities the entities to update
    /// @param entityType the entity class
    /// @return a list of updated entities
    /// @throws NullPointerException if entities or entityType is null
    ///
    <T> List<T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    ///
    /// Updates a single entity in the database.
    ///
    /// This is a convenience method that wraps `updateAll(Iterable, Class)`.
    ///
    /// @param <T> the entity type
    /// @param entity the entity to update
    /// @param entityType the entity class
    /// @return the updated entity
    /// @throws NullPointerException if entity or entityType is null
    ///
    default <T> T update(@NonNull T entity, Class<T> entityType) {
        return updateAll(ImmutableList.of(entity), entityType).getFirst();
    }

    ///
    /// Deletes multiple entities from the database.
    ///
    /// @param <T> the entity type
    /// @param entities the entities to delete
    /// @param entityType the entity class
    /// @throws NullPointerException if entities or entityType is null
    ///
    <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType);

    ///
    /// Deletes a single entity from the database.
    ///
    /// This is a convenience method that wraps `deleteAll(Iterable, Class)`.
    ///
    /// @param <T> the entity type
    /// @param entity the entity to delete
    /// @param entityType the entity class
    /// @throws NullPointerException if entity or entityType is null
    ///
    default <T> void delete(@NonNull T entity, @NonNull Class<T> entityType) {
        deleteAll(ImmutableList.of(entity), entityType);
    }

    ///
    /// Executes a command within a transaction.
    ///
    /// This is a convenience method for operations that don't return a value.
    ///
    /// @param command the command to execute
    ///
    default void doInTransaction(Runnable command) {
        doInTransaction(() -> {
            command.run();
            return null;
        });
    }

    ///
    /// Executes a command within a transaction and returns its result.
    ///
    /// The transaction is automatically committed if the command succeeds,
    /// or rolled back if an exception is thrown.
    ///
    /// @param <T> the return type of the command
    /// @param command the command to execute
    /// @return the result of the command
    /// @throws RuntimeException if the transaction fails
    ///
    <T> T doInTransaction(Supplier<T> command);
}
