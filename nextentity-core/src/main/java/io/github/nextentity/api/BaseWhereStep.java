package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

/// Query condition building step interface, providing methods to add query conditions.
///
/// @param <T> Entity type
/// @param <U> Query result type
/// @author HuangChengwei
/// @since 1.0.0
public interface BaseWhereStep<T, U> extends OrderByStep<T, U> {

    /// Adds boolean expression as query condition.
    ///
    /// @param predicate Boolean expression
    /// @return Query condition building step
    BaseWhereStep<T, U> where(Expression<T, Boolean> predicate);

    /// Adds path as query condition.
    ///
    /// @param path Path
    /// @param <N> Path type
    /// @return Path operator
    <N> PathOperator<T, N, ? extends BaseWhereStep<T, U>> where(PathRef<T, N> path);

    /// Adds number path as query condition.
    ///
    /// @param path Number path
    /// @param <N> Number type
    /// @return Number operator
    <N extends Number> NumberOperator<T, N, ? extends BaseWhereStep<T, U>> where(NumberRef<T, N> path);

    /// Adds string path as query condition.
    ///
    /// @param path String path
    /// @return String operator
    StringOperator<T, ? extends BaseWhereStep<T, U>> where(StringRef<T> path);

    /// Adds path expression as query condition.
    ///
    /// @param path Path expression
    /// @param <N> Path type
    /// @return Path operator
    <N> PathOperator<T, N, ? extends BaseWhereStep<T, U>> where(Path<T, N> path);

    /// Adds number path as query condition.
    ///
    /// @param path Number path
    /// @param <N> Number type
    /// @return Number operator
    <N extends Number> NumberOperator<T, N, ? extends BaseWhereStep<T, U>> where(NumberPath<T, N> path);

    /// Adds string path as query condition.
    ///
    /// @param path String path
    /// @return String operator
    StringOperator<T, ? extends BaseWhereStep<T, U>> where(StringPath<T> path);


}
