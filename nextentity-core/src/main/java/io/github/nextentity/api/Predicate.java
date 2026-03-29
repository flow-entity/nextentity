package io.github.nextentity.api;

import io.github.nextentity.core.expression.PredicateImpl;

/// Predicate interface, representing query conditions.
///
/// Provides logical operation methods, such as NOT, AND, OR, etc.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface Predicate<T> extends SimpleExpression<T, Boolean>, ExpressionBuilder.Conjunction<T>, ExpressionBuilder.Disjunction<T> {

    /// Creates a predicate that always evaluates to true.
    ///
    /// @param <T> Entity type
    /// @return True predicate
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> ofTrue() {
        return (Predicate<T>) PredicateImpl.TRUE;
    }

    /// Creates a predicate that always evaluates to false.
    ///
    /// @param <T> Entity type
    /// @return False predicate
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> ofFalse() {
        return (Predicate<T>) PredicateImpl.FALSE;
    }

    /// Logical NOT operation.
    ///
    /// @return Negated predicate
    Predicate<T> not();

    /// Logical AND operation, combined with another predicate.
    ///
    /// @param predicate Another predicate
    /// @return Combined predicate
    Predicate<T> and(Expression<T, Boolean> predicate);

    /// Logical OR operation, combined with another predicate.
    ///
    /// @param predicate Another predicate
    /// @return Combined predicate
    Predicate<T> or(Expression<T, Boolean> predicate);

    /// Logical AND operation, combined with multiple predicates.
    ///
    /// @param predicate Predicate array
    /// @return Combined predicate
    Predicate<T> and(Expression<T, Boolean>[] predicate);

    /// Logical OR operation, combined with multiple predicates.
    ///
    /// @param predicate Predicate array
    /// @return Combined predicate
    Predicate<T> or(Expression<T, Boolean>[] predicate);

    /// Logical AND operation, combined with multiple predicates.
    ///
    /// @param predicates Predicate iterator
    /// @return Combined predicate
    Predicate<T> and(Iterable<? extends Expression<T, Boolean>> predicates);

    /// Logical OR operation, combined with multiple predicates.
    ///
    /// @param predicates Predicate iterator
    /// @return Combined predicate
    Predicate<T> or(Iterable<? extends Expression<T, Boolean>> predicates);
}
