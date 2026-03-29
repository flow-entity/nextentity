package io.github.nextentity.api;

import java.util.List;

/// Subquery builder interface, used to build and execute subquery operations.
///
/// @param <T> Entity type
/// @param <U> Query result type
/// @author HuangChengwei
/// @since 1.0.0
public interface SubQueryBuilder<T, U> extends TypedExpression<T, List<U>> {
    /// Gets the total count of query results.
    ///
    /// @return Count expression
    TypedExpression<T, Long> count();

    /// Slices a part of the query results.
    ///
    /// @param offset Starting offset
    /// @param maxResult Maximum number of results
    /// @return Sliced results expression
    TypedExpression<T, List<U>> slice(int offset, int maxResult);

    /// Gets a single query result.
    ///
    /// @return Single result expression
    default TypedExpression<T, U> getSingle() {
        return getSingle(-1);
    }

    /// Gets a single query result from the specified offset.
    ///
    /// @param offset Starting offset
    /// @return Single result expression
    TypedExpression<T, U> getSingle(int offset);

    /// Gets the first query result.
    ///
    /// @return First result expression
    default TypedExpression<T, U> getFirst() {
        return getFirst(-1);
    }

    /**
     * Gets the first query result from the specified offset.
     *
     * @param offset Starting offset
     * @return First result expression
     */
    TypedExpression<T, U> getFirst(int offset);
}
