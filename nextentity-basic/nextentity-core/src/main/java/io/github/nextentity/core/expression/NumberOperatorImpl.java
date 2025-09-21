package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.expression.Operator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NumberOperatorImpl<T, U extends Number, B> extends ExpressionBuilderImpl<T, U, B> implements ExpressionBuilder.NumberOperator<T, U, B> {
    public NumberOperatorImpl(ExpressionNode target, Function<? super ExpressionNode, ? extends B> operatedCallback) {
        super(target, operatedCallback);
    }

    @Override
    public NumberOperator<T, U, B> add(U value) {
        return operateToNumber(Operator.ADD, value);
    }

    @Override
    public NumberOperator<T, U, B> subtract(U value) {
        return operateToNumber(Operator.SUBTRACT, value);
    }

    @Override
    public NumberOperator<T, U, B> multiply(U value) {
        return operateToNumber(Operator.MULTIPLY, value);
    }

    @Override
    public NumberOperator<T, U, B> divide(U value) {
        return operateToNumber(Operator.DIVIDE, value);
    }

    @Override
    public NumberOperator<T, U, B> mod(U value) {
        return operateToNumber(Operator.MOD, value);
    }

    @Override
    public NumberOperator<T, U, B> add(TypedExpression<T, U> expression) {
        return operateToNumber(Operator.ADD, expression);
    }

    @Override
    public NumberOperator<T, U, B> subtract(TypedExpression<T, U> expression) {
        return operateToNumber(Operator.SUBTRACT, expression);
    }

    @Override
    public NumberOperator<T, U, B> multiply(TypedExpression<T, U> expression) {
        return operateToNumber(Operator.MULTIPLY, expression);
    }

    @Override
    public NumberOperator<T, U, B> divide(TypedExpression<T, U> expression) {
        return operateToNumber(Operator.DIVIDE, expression);
    }

    @Override
    public NumberOperator<T, U, B> mod(TypedExpression<T, U> expression) {
        return operateToNumber(Operator.MOD, expression);
    }

    private @NotNull NumberOperatorImpl<T, U, B> operateToNumber(Operator operator, TypedExpression<T, U> value) {
        return new NumberOperatorImpl<>(operate(operator, getNode(value)), operatedCallback);
    }

    private @NotNull NumberOperatorImpl<T, U, B> operateToNumber(Operator operator, U value) {
        return new NumberOperatorImpl<>(operate(operator, getNode(value)), operatedCallback);
    }
}
