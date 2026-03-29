package io.github.nextentity.api;

/// Select having step interface, providing condition construction methods after grouping.
///
/// Extends OrderByStep, used to add filter conditions after grouping.
///
/// @param <T> Entity type
/// @param <U> Result type
/// @author HuangChengwei
/// @since 1.0.0
public interface HavingStep<T, U> extends OrderByStep<T, U> {

    /// Add the specified grouping condition predicate.
    ///
    /// @param predicate Condition predicate
    /// @return OrderByStep instance
    OrderByStep<T, U> having(TypedExpression<T, Boolean> predicate);

}
