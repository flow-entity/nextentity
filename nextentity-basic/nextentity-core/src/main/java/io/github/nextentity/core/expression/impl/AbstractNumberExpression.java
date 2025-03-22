package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.expression.Operator;

import static io.github.nextentity.api.TypedExpression.NumberPathExpression;

public interface AbstractNumberExpression<T, U extends Number> extends NumberPathExpression<T, U>, AbstractPathExpression<T, U> {

    @Override
    default NumberExpression<T, U> add(TypedExpression<T, U> expression) {
        return operate(Operator.ADD, expression);
    }

    @Override
    default NumberExpression<T, U> subtract(TypedExpression<T, U> expression) {
        return operate(Operator.SUBTRACT, expression);
    }

    @Override
    default NumberExpression<T, U> multiply(TypedExpression<T, U> expression) {
        return operate(Operator.MULTIPLY, expression);
    }

    @Override
    default NumberExpression<T, U> divide(TypedExpression<T, U> expression) {
        return operate(Operator.DIVIDE, expression);
    }

    @Override
    default NumberExpression<T, U> mod(TypedExpression<T, U> expression) {
        return operate(Operator.MOD, expression);
    }

    @Override
    default NumberExpression<T, U> sum() {
        return operate(Operator.SUM);
    }

    @Override
    default NumberExpression<T, Double> avg() {
        return operate(Operator.AVG);
    }

    @Override
    default NumberExpression<T, U> max() {
        return operate(Operator.MAX);
    }

    @Override
    default NumberExpression<T, U> min() {
        return operate(Operator.MIN);
    }


}
