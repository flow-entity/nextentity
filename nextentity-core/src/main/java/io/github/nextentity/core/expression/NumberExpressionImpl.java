package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.NumberExpression;
import io.github.nextentity.api.NumberPath;

public class NumberExpressionImpl<T, U extends Number> extends SimpleExpressionImpl<T, U> implements NumberPath<T, U> {

    public NumberExpressionImpl(ExpressionNode root) {
        super(root);
    }

    @Override
    public NumberExpression<T, U> add(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.ADD, getNode(expression)));
    }

    @Override
    public NumberExpression<T, U> subtract(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.SUBTRACT, getNode(expression)));
    }

    @Override
    public NumberExpression<T, U> multiply(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.MULTIPLY, getNode(expression)));
    }

    @Override
    public NumberExpression<T, U> divide(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.DIVIDE, getNode(expression)));
    }

    @Override
    public NumberExpression<T, U> mod(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.MOD, getNode(expression)));
    }

    @Override
    public NumberExpression<T, U> sum() {
        return new NumberExpressionImpl<>(operate(Operator.SUM));
    }

    @Override
    public NumberExpression<T, Double> avg() {
        return new NumberExpressionImpl<>(operate(Operator.AVG));
    }

    @Override
    public NumberExpression<T, U> max() {
        return new NumberExpressionImpl<>(operate(Operator.MAX));
    }

    @Override
    public NumberExpression<T, U> min() {
        return new NumberExpressionImpl<>(operate(Operator.MIN));
    }
}
