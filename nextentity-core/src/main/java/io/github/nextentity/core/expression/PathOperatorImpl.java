package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;
import io.github.nextentity.api.PathRef;

import java.util.function.Function;

public class PathOperatorImpl<T, R, B> extends ExpressionBuilderImpl<T, R, B> implements ExpressionBuilder.PathOperator<T, R, B> {

    public PathOperatorImpl(ExpressionNode target, Function<? super ExpressionNode, ? extends B> operatedCallback) {
        super(target, operatedCallback);
    }

    @Override
    public <V> PathOperator<T, V, B> get(PathRef<R, V> path) {
        return new PathOperatorImpl<>(appendPath(path), operatedCallback);
    }

    @Override
    public StringOperator<T, B> get(PathRef.StringRef<R> path) {
        return new StringOperatorImpl<>(appendPath(path), operatedCallback);
    }

    @Override
    public <V extends Number> NumberOperator<T, V, B> get(PathRef.NumberRef<R, V> path) {
        return new NumberOperatorImpl<>(appendPath(path), operatedCallback);
    }

}
