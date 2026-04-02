package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// Conditional delete builder interface for batch delete operations with WHERE conditions.
///
/// This interface provides a fluent API for building DELETE statements
/// with conditional WHERE clauses, allowing batch deletes without
/// retrieving entities first.
///
/// Usage example:
/// <pre>{@code
/// // Delete all inactive users
/// int deleted = repository.deleteWhere()
///     .where(User::getStatus).eq("INACTIVE")
///     .execute();
///
/// // Delete with multiple conditions
/// int deleted = repository.deleteWhere()
///     .where(User::getStatus).eq("ARCHIVED")
///     .and(User::getCreatedAt).lt(oneYearAgo)
///     .execute();
///
/// // Delete using a predicate expression
/// int deleted = repository.deleteWhere()
///     .where(root().get(User::status).eq("INACTIVE")
///         .and(root().get(User::lastLoginAt).lt(threshold)))
///     .execute();
/// }</pre>
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
public interface DeleteWhereStep<T> {

    /// Starts a WHERE condition for the specified path.
    ///
    /// @param path Path reference to start the condition
    /// @param <N> Path value type
    /// @return Path operator for building the condition
    <N> ExpressionBuilder.PathOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef<T, N> path);

    /// Starts a WHERE condition for the specified numeric path.
    ///
    /// @param path Numeric path reference
    /// @param <N> Number type
    /// @return Number operator for building the condition
    <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends DeleteWhereStep<T>> where(PathRef.NumberRef<T, N> path);

    /// Starts a WHERE condition for the specified string path.
    ///
    /// @param path String path reference
    /// @return String operator for building the condition
    ExpressionBuilder.StringOperator<T, ? extends DeleteWhereStep<T>> where(PathRef.StringRef<T> path);

    /// Adds a WHERE condition using a predicate expression.
    ///
    /// @param predicate Predicate expression
    /// @return This builder for method chaining
    DeleteWhereStep<T> where(@NonNull Expression<T, Boolean> predicate);

    /// Executes the delete statement.
    ///
    /// @return Number of rows affected
    int execute();
}