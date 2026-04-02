package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// Conditional update builder interface for batch update operations with WHERE conditions.
///
/// This interface provides a fluent API for building UPDATE statements
/// with conditional WHERE clauses, allowing batch updates without
/// retrieving entities first.
///
/// Usage example:
/// <pre>{@code
/// // Update all inactive users to archived status
/// int updated = repository.updateWhere()
///     .set(User::getStatus, "ARCHIVED")
///     .where(User::getLastLoginAt).lt(threshold)
///     .execute();
///
/// // Update with multiple conditions
/// int updated = repository.updateWhere()
///     .set(User::getStatus, "INACTIVE")
///     .set(User::updatedAt, LocalDateTime.now())
///     .where(User::getStatus).eq("ACTIVE")
///     .and(User::getLastLoginAt).lt(oneYearAgo)
///     .execute();
/// }</pre>
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 2.1
public interface UpdateWhereStep<T> {

    /// Sets the value for a specific field.
    ///
    /// @param path Path reference to the field
    /// @param value Value to set
    /// @param <U> Field value type
    /// @return This builder for method chaining
    <U> UpdateWhereStep<T> set(PathRef<T, U> path, U value);

    /// Sets the value for a specific field by name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @param value Value to set
    /// @return This builder for method chaining
    UpdateWhereStep<T> set(String fieldName, Object value);

    /// Starts a WHERE condition for the specified path.
    ///
    /// @param path Path reference to start the condition
    /// @param <N> Path value type
    /// @return Path operator for building the condition
    <N> ExpressionBuilder.PathOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef<T, N> path);

    /// Starts a WHERE condition for the specified numeric path.
    ///
    /// @param path Numeric path reference
    /// @param <N> Number type
    /// @return Number operator for building the condition
    <N extends Number> ExpressionBuilder.NumberOperator<T, N, ? extends UpdateWhereStep<T>> where(PathRef.NumberRef<T, N> path);

    /// Starts a WHERE condition for the specified string path.
    ///
    /// @param path String path reference
    /// @return String operator for building the condition
    ExpressionBuilder.StringOperator<T, ? extends UpdateWhereStep<T>> where(PathRef.StringRef<T> path);

    /// Adds a WHERE condition using a predicate expression.
    ///
    /// @param predicate Predicate expression
    /// @return This builder for method chaining
    UpdateWhereStep<T> where(@NonNull Expression<T, Boolean> predicate);

    /// Executes the update statement.
    ///
    /// @return Number of rows affected
    int execute();
}