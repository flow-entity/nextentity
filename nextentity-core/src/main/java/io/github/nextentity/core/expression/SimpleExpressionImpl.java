package io.github.nextentity.core.expression;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.Order;

public class SimpleExpressionImpl<T, U>
        extends AbstractExpressionBuilder<T, U, Predicate<T>>
        implements EntityPath<T, U>, ExpressionTree {

    public SimpleExpressionImpl(ExpressionNode root) {
        super(root);
    }

    @Override
    public NumberExpression<T, Long> count() {
        return new NumberExpressionImpl<>(operate(Operator.COUNT));
    }

    @Override
    public NumberExpression<T, Long> countDistinct() {
        return new NumberExpressionImpl<>(operate(Operator.DISTINCT).operate(Operator.COUNT));
    }

    @Override
    public Order<T> sort(SortOrder order) {
        return new OrderImpl<>(this, order);
    }

    @Override
    public <R> EntityPath<T, R> get(PathRef<U, R> path) {
        return new SimpleExpressionImpl<>(appendPathRef(path));
    }

    @Override
    public StringPath<T> get(PathRef.StringRef<U> path) {
        return new StringExpressionImpl<>(appendPathRef(path));
    }

    @Override
    public <R extends Number> NumberPath<T, R> get(PathRef.NumberRef<U, R> path) {
        return new NumberExpressionImpl<>(appendPathRef(path));
    }

    @Override
    public <R> Path<T, R> get(Path<U, R> path) {
        return new SimpleExpressionImpl<>(appendPath(path));
    }

    @Override
    public StringPath<T> get(StringPath<U> path) {
        return new StringExpressionImpl<>(appendPath(path));
    }

    @Override
    public BooleanPath<T> get(PathRef.BooleanRef<T> path) {
        return new PredicateImpl<>(appendPathRef(path));
    }

    @Override
    public <R extends Number> NumberPath<T, R> get(NumberPath<U, R> path) {
        return new NumberExpressionImpl<>(appendPath(path));
    }

    @Override
    public SimpleExpression<T, U> max() {
        return new SimpleExpressionImpl<>(operate(Operator.MAX));
    }

    @Override
    public SimpleExpression<T, U> min() {
        return new SimpleExpressionImpl<>(operate(Operator.MIN));
    }

    @Override
    protected Predicate<T> next(ExpressionNode operate) {
        return new PredicateImpl<>(operate);
    }
}
