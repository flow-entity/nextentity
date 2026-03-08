package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;

import java.util.function.Function;

public class ExpressionBuilderImpl<T, U, B> extends AbstractExpressionBuilder<T, U, B> implements ExpressionBuilder<T, U, B> {

    protected final Function<? super ExpressionNode, ? extends B> operatedCallback;


    public ExpressionBuilderImpl(ExpressionNode target, Function<? super ExpressionNode, ? extends B> operatedCallback) {
        super(target);
        this.operatedCallback = operatedCallback;
    }

    protected B next(ExpressionNode operate) {
        return operatedCallback.apply(operate);
    }

    @Override
    public B eqIfNotNull(U value) {
        return value == null ? operateNull() : eq(value);
    }

}
