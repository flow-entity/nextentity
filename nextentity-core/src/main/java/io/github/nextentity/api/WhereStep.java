package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

/// Rows select where step interface, providing condition construction methods for row selection.
///
/// Extends SelectGroupByStep and WhereStep, providing grouping and condition construction functions.
///
/// @param <T> Entity type
/// @param <U> Result type
/// @author HuangChengwei
/// @since 1.0.0
public interface WhereStep<T, U> extends GroupByStep<T, U>, BaseWhereStep<T, U> {

    /// Add the specified condition predicate.
    ///
    /// @param predicate Condition predicate
    /// @return WhereStep instance
    WhereStep<T, U> where(TypedExpression<T, Boolean> predicate);

    /// Build conditions based on the specified path.
    ///
    /// @param path Path
    /// @param <N> Path type
    /// @return PathOperator instance
    <N> PathOperator<T, N, WhereStep<T, U>> where(PathRef<T, N> path);

    /// Build conditions based on the specified number path.
    ///
    /// @param path Number path
    /// @param <N> Number type
    /// @return NumberOperator instance
    <N extends Number> NumberOperator<T, N, WhereStep<T, U>> where(NumberRef<T, N> path);

    /// Build conditions based on the specified string path.
    ///
    /// @param path String path
    /// @return StringOperator instance
    StringOperator<T, WhereStep<T, U>> where(StringRef<T> path);

    /// Build conditions based on the specified path expression.
    ///
    /// @param path Path expression
    /// @param <N> Path type
    /// @return PathOperator instance
    <N> PathOperator<T, N, WhereStep<T, U>> where(Path<T, N> path);

    /// Build conditions based on the specified number path expression.
    ///
    /// @param path Number path expression
    /// @param <N> Number type
    /// @return NumberOperator instance
    <N extends Number> NumberOperator<T, N, WhereStep<T, U>> where(NumberPath<T, N> path);

    /// Build conditions based on the specified string path expression.
    ///
    /// @param path String path expression
    /// @return StringOperator instance
    StringOperator<T, WhereStep<T, U>> where(StringPath<T> path);

}
