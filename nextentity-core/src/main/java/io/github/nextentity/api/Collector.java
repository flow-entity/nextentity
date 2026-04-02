package io.github.nextentity.api;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.core.expression.SliceImpl;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

/// Query result collector interface.
///
/// Provides terminal operations for retrieving results, plus windowed access
/// helpers for offset/limit style queries.
///
/// Usage examples:
/// <pre>{@code
/// // Basic query
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .list();
///
/// // Top N results
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .limit(20);
///
/// // Windowed results
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .window(10, 20);
///
/// // With lock
/// User user = repository.query()
///     .where(User::getId).eq(1L)
///     .lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
///     .first();
/// }</pre>
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface Collector<T> {

    /// Gets the total record count.
    ///
    /// @return Total record count
    long count();

    /// Checks if records exist.
    ///
    /// @return Whether records exist
    boolean exists();

    /// Checks if records exist starting from the specified offset.
    ///
    /// @param offset Number of records to skip before checking existence
    /// @return Whether records exist at or after the given offset
    boolean exists(int offset);

    /// Gets all results as a list.
    ///
    /// @return List of all results
    List<T> list();

    /// Gets the first {@code limit} results.
    ///
    /// @param limit Maximum result count
    /// @return Windowed result list starting at offset 0
    default List<T> limit(int limit) {
        return window(0, limit);
    }

    /// Gets results using the specified offset and limit window.
    ///
    /// @param offset Number of records to skip
    /// @param limit Maximum result count
    /// @return Windowed result list
    List<T> window(int offset, int limit);

    /// Gets the first result.
    ///
    /// @return First result, null if not exists
    default T first() {
        List<T> list = limit(1);
        return list.isEmpty() ? null : list.getFirst();
    }

    /// Gets a single result.
    ///
    /// @return Single result, null if not exists
    /// @throws IllegalStateException If multiple results found
    default T single() {
        List<T> list = window(0, 2);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.getFirst();
    }

    /// Slices results with the specified offset and limit.
    ///
    /// @param offset Offset
    /// @param limit Maximum result count
    /// @return Slice result
    default Slice<T> slice(int offset, int limit) {
        long count = count();
        if (count <= offset) {
            return new SliceImpl<>(ImmutableList.of(), count, offset, limit);
        }
        return new SliceImpl<>(window(offset, limit), count, offset, limit);
    }

    /// Converts the query to a subquery builder.
    ///
    /// @param <X> Subquery type
    /// @return Subquery builder
    <X> SubQueryBuilder<X, T> toSubQuery();
}
