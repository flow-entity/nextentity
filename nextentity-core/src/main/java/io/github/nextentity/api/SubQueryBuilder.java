package io.github.nextentity.api;

import java.util.List;

/// Subquery builder interface, used to build and execute subquery operations.
///
/// @param <T> Entity type
/// @param <U> Query result type
/// @author HuangChengwei
/// @since 1.0.0
public interface SubQueryBuilder<T, U> extends Expression<T, List<U>> {
    /// Gets the total count of query results.
    ///
    /// @return Count expression
    Expression<T, Long> count();

    /// Slices a part of the query results.
    ///
    /// @param offset Starting offset
    /// @param maxResult Maximum number of results
    /// @return Sliced results expression
    Expression<T, List<U>> slice(int offset, int maxResult);

    /// Gets a single query result.
    ///
    /// @return Single result expression
    default Expression<T, U> getSingle() {
        return getSingle(-1);
    }

    /// Gets a single query result from the specified offset.
    ///
    /// @param offset Starting offset
    /// @return Single result expression
    Expression<T, U> getSingle(int offset);

    /// Gets the first query result.
    ///
    /// @return First result expression
    default Expression<T, U> getFirst() {
        return getFirst(-1);
    }

    /// Gets the first query result from the specified offset.
    ///
    /// @param offset Starting offset
    /// @return First result expression
    Expression<T, U> getFirst(int offset);
}
