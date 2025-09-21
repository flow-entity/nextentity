package io.github.nextentity.api;

public interface NumberExpression<T, U extends Number> extends SimpleExpression<T, U> {
    io.github.nextentity.api.NumberExpression<T, U> add(TypedExpression<T, U> expression);

    io.github.nextentity.api.NumberExpression<T, U> subtract(TypedExpression<T, U> expression);

    io.github.nextentity.api.NumberExpression<T, U> multiply(TypedExpression<T, U> expression);

    io.github.nextentity.api.NumberExpression<T, U> divide(TypedExpression<T, U> expression);

    io.github.nextentity.api.NumberExpression<T, U> mod(TypedExpression<T, U> expression);

    io.github.nextentity.api.NumberExpression<T, U> sum();

    io.github.nextentity.api.NumberExpression<T, Double> avg();

    io.github.nextentity.api.NumberExpression<T, U> max();

    io.github.nextentity.api.NumberExpression<T, U> min();

    default io.github.nextentity.api.NumberExpression<T, U> add(U value) {
        return add(root().literal(value));
    }

    default io.github.nextentity.api.NumberExpression<T, U> subtract(U value) {
        return subtract(root().literal(value));
    }

    default io.github.nextentity.api.NumberExpression<T, U> multiply(U value) {
        return multiply(root().literal(value));
    }

    default io.github.nextentity.api.NumberExpression<T, U> divide(U value) {
        return divide(root().literal(value));
    }

    default io.github.nextentity.api.NumberExpression<T, U> mod(U value) {
        return mod(root().literal(value));
    }

    default io.github.nextentity.api.NumberExpression<T, U> addIfNotNull(U value) {
        return value == null ? this : add(value);
    }

    default io.github.nextentity.api.NumberExpression<T, U> subtractIfNotNull(U value) {
        return value == null ? this : subtract(value);
    }

    default io.github.nextentity.api.NumberExpression<T, U> multiplyIfNotNull(U value) {
        return value == null ? this : multiply(value);
    }

    default io.github.nextentity.api.NumberExpression<T, U> divideIfNotNull(U value) {
        return value == null ? this : divide(value);
    }

    default io.github.nextentity.api.NumberExpression<T, U> modIfNotNull(U value) {
        return value == null ? this : mod(value);
    }

}
