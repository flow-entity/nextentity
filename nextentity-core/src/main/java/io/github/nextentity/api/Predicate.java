package io.github.nextentity.api;

/// Predicate interface, representing query conditions.
///
/// Provides logical operation methods, such as NOT, AND, OR, etc.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface Predicate<T> extends SimpleExpression<T, Boolean>, ExpressionBuilder.Conjunction<T>, ExpressionBuilder.Disjunction<T> {
    /// Logical NOT operation.
    ///
    /// @return Negated predicate
    Predicate<T> not();

    /// Logical AND operation, combined with another predicate.
    ///
    /// @param predicate Another predicate
    /// @return Combined predicate
    Predicate<T> and(TypedExpression<T, Boolean> predicate);

    /// Logical OR operation, combined with another predicate.
    ///
    /// @param predicate Another predicate
    /// @return Combined predicate
    Predicate<T> or(TypedExpression<T, Boolean> predicate);

    /// Logical AND operation, combined with multiple predicates.
    ///
    /// @param predicate Predicate array
    /// @return Combined predicate
    Predicate<T> and(TypedExpression<T, Boolean>[] predicate);

    /// Logical OR operation, combined with multiple predicates.
    ///
    /// @param predicate Predicate array
    /// @return Combined predicate
    Predicate<T> or(TypedExpression<T, Boolean>[] predicate);

    /// Logical AND operation, combined with multiple predicates.
    ///
    /// @param predicates Predicate iterator
    /// @return Combined predicate
    Predicate<T> and(Iterable<? extends TypedExpression<T, Boolean>> predicates);

    /// Logical OR operation, combined with multiple predicates.
    ///
    /// @param predicates Predicate iterator
    /// @return Combined predicate
    Predicate<T> or(Iterable<? extends TypedExpression<T, Boolean>> predicates);
}
