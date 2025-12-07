package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;

import java.util.function.Function;

public class StringOperatorImpl<T, B> extends ExpressionBuilderImpl<T, String, B> implements ExpressionBuilder.StringOperator<T, B> {

    public StringOperatorImpl(ExpressionNode target, Function<? super ExpressionNode, ? extends B> operatedCallback) {
        super(target, operatedCallback);
    }

    @Override
    public StringOperator<T, B> lower() {
        return new StringOperatorImpl<>(operate(Operator.LOWER), operatedCallback);
    }

    @Override
    public StringOperator<T, B> upper() {
        return new StringOperatorImpl<>(operate(Operator.UPPER), operatedCallback);
    }

    @Override
    public StringOperator<T, B> substring(int offset, int length) {
        return new StringOperatorImpl<>(operate(Operator.SUBSTRING, getNode(offset), getNode(length)), operatedCallback);
    }

    @Override
    public StringOperator<T, B> trim() {
        return new StringOperatorImpl<>(operate(Operator.TRIM), operatedCallback);
    }

    @Override
    public NumberOperator<T, Integer, B> length() {
        return new NumberOperatorImpl<>(operate(Operator.LENGTH), operatedCallback);
    }
}
