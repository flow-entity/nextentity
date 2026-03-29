package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.util.Paths;

/// Number expression interface, providing number-type expression operation methods.
///
/// Extends SimpleExpression, providing basic expression operation methods and adding number-specific operations.
///
/// @param <T> Entity type
/// @param <U> Number type
/// @author HuangChengwei
/// @since 1.0.0
public interface NumberExpression<T, U extends Number> extends SimpleExpression<T, U> {
    /// Addition operation, adds another expression.
    ///
    /// @param expression Another expression
    /// @return Addition result expression
    NumberExpression<T, U> add(TypedExpression<T, U> expression);

    /// Subtraction operation, subtracts another expression.
    ///
    /// @param expression Another expression
    /// @return Subtraction result expression
    NumberExpression<T, U> subtract(TypedExpression<T, U> expression);

    /// Multiplication operation, multiplies another expression.
    ///
    /// @param expression Another expression
    /// @return Multiplication result expression
    NumberExpression<T, U> multiply(TypedExpression<T, U> expression);

    /// Division operation, divides another expression.
    ///
    /// @param expression Another expression
    /// @return Division result expression
    NumberExpression<T, U> divide(TypedExpression<T, U> expression);

    /// Modulo operation, modulo another expression.
    ///
    /// @param expression Another expression
    /// @return Modulo result expression
    NumberExpression<T, U> mod(TypedExpression<T, U> expression);

    /// Sum operation.
    ///
    /// @return Sum result expression
    NumberExpression<T, U> sum();

    /// Average operation.
    ///
    /// @return Average result expression
    NumberExpression<T, Double> avg();

    /// Maximum operation.
    ///
    /// @return Maximum result expression
    NumberExpression<T, U> max();

    /// Minimum operation.
    ///
    /// @return Minimum result expression
    NumberExpression<T, U> min();

    /// Addition operation, adds the specified value.
    ///
    /// @param value Value to add
    /// @return Addition result expression
    default NumberExpression<T, U> add(U value) {
        return add(root().literal(value));
    }

    /// Subtraction operation, subtracts the specified value.
    ///
    /// @param value Value to subtract
    /// @return Subtraction result expression
    default NumberExpression<T, U> subtract(U value) {
        return subtract(root().literal(value));
    }

    /// Multiplication operation, multiplies the specified value.
    ///
    /// @param value Value to multiply
    /// @return Multiplication result expression
    default NumberExpression<T, U> multiply(U value) {
        return multiply(root().literal(value));
    }

    /// Division operation, divides the specified value.
    ///
    /// @param value Value to divide
    /// @return Division result expression
    default NumberExpression<T, U> divide(U value) {
        return divide(root().literal(value));
    }

    /// Modulo operation, modulo the specified value.
    ///
    /// @param value Value to modulo
    /// @return Modulo result expression
    default NumberExpression<T, U> mod(U value) {
        return mod(root().literal(value));
    }

    /// Conditional addition operation, adds the specified value if not null.
    ///
    /// @param value Value to add
    /// @return Addition result expression or current expression (if value is null)
    default NumberExpression<T, U> addIfNotNull(U value) {
        return value == null ? this : add(value);
    }

    /// Conditional subtraction operation, subtracts the specified value if not null.
    ///
    /// @param value Value to subtract
    /// @return Subtraction result expression or current expression (if value is null)
    default NumberExpression<T, U> subtractIfNotNull(U value) {
        return value == null ? this : subtract(value);
    }

    /// Conditional multiplication operation, multiplies the specified value if not null.
    ///
    /// @param value Value to multiply
    /// @return Multiplication result expression or current expression (if value is null)
    default NumberExpression<T, U> multiplyIfNotNull(U value) {
        return value == null ? this : multiply(value);
    }

    /// Conditional division operation, divides the specified value if not null.
    ///
    /// @param value Value to divide
    /// @return Division result expression or current expression (if value is null)
    default NumberExpression<T, U> divideIfNotNull(U value) {
        return value == null ? this : divide(value);
    }

    /// Conditional modulo operation, modulo the specified value if not null.
    ///
    /// @param value Value to modulo
    /// @return Modulo result expression or current expression (if value is null)
    default NumberExpression<T, U> modIfNotNull(U value) {
        return value == null ? this : mod(value);
    }

    private EntityRoot<T> root() {
        return Paths.root();
    }
}
