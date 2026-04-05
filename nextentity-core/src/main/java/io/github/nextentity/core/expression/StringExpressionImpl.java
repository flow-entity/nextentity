package io.github.nextentity.core.expression;

import io.github.nextentity.api.NumberExpression;
import io.github.nextentity.api.StringExpression;
import io.github.nextentity.api.StringPath;

public class StringExpressionImpl<T> extends SimpleExpressionImpl<T, String> implements StringPath<T> {
    public StringExpressionImpl(ExpressionNode root) {
        super(root);
    }

    @Override
    public StringExpression<T> lower() {
        return new StringExpressionImpl<>(operate(Operator.LOWER));
    }

    @Override
    public StringExpression<T> upper() {
        return new StringExpressionImpl<>(operate(Operator.UPPER));
    }

    @Override
    public StringExpression<T> substring(int offset, int length) {
        return new StringExpressionImpl<>(operate(Operator.SUBSTRING, getNode(offset), getNode(length)));
    }

    @Override
    public StringExpression<T> trim() {
        return new StringExpressionImpl<>(operate(Operator.TRIM));
    }

    @Override
    public NumberExpression<T, Integer> length() {
        return new NumberExpressionImpl<>(operate(Operator.LENGTH));
    }
}
