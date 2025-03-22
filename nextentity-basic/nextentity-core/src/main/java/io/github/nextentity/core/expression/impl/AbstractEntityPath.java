package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import static io.github.nextentity.api.TypedExpression.EntityPathExpression;

public interface AbstractEntityPath<T, U> extends AbstractPathExpression<T, U>, EntityPathExpression<T, U> {

    @Override
    default StringPathExpression<T> get(Path.StringPath<U> path) {
        return get0(path);
    }

    @Override
    default StringPathExpression<T> get(StringPathExpression<U> path) {
        return get0(path);
    }

    @Override
    default BooleanPathExpression<T> get(Path.BooleanPath<T> path) {
        return get0(path);
    }

    @Override
    default <R extends Number> NumberPathExpression<T, R> get(NumberPathExpression<U, R> path) {
        return get0(path);
    }

    @Override
    default <R> EntityPathExpression<T, R> get(Path<U, R> path) {
        return get0(path);
    }


    @Override
    default <R> PathExpression<T, R> get(PathExpression<U, R> path) {
        return get0(path);
    }

    @Override
    default <R extends Number> NumberPathExpression<T, R> get(Path.NumberPath<U, R> path) {
        return get0(path);
    }

    default <X extends TypedExpression<?, ?>> X get0(Path<?, ?> path) {
        PathExpression<?, ?> pathExpression = Paths.get((Path<?, ?>) path);
        return get0(pathExpression);
    }

    @NotNull
    default <X extends TypedExpression<?, ?>> X get0(PathExpression<?, ?> pathExpression) {
        InternalPathExpression expression = (InternalPathExpression) pathExpression;
        return toTypedExpression(((InternalPathExpression) this).get(expression));
    }


}
