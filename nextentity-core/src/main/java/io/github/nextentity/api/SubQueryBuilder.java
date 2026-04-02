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
    /// @param limit Maximum number of results
    /// @return Sliced results expression
    Expression<T, List<U>> window(int offset, int limit);

}
