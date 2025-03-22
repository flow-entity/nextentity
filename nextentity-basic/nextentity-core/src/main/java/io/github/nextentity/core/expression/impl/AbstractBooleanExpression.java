package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.ExpressionBuilder;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.Expressions;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.util.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static io.github.nextentity.api.TypedExpression.BooleanPathExpression;

public interface AbstractBooleanExpression<T> extends BooleanPathExpression<T>, AbstractPathExpression<T, Boolean> {
    @Override
    default <R> ExpressionBuilder.PathOperator<T, R, ExpressionBuilder.Conjunction<T>> and(Path<T, R> path) {
        return ExpressionBuilders.ofPath(of(path), this::and);
    }

    @NotNull
    default AbstractBooleanExpression<T> and(OperatableExpression<?, ?> basicExpression) {
        return basicExpression == null ? this : operate(Operator.AND, basicExpression);
    }

    @NotNull
    default AbstractBooleanExpression<T> or(OperatableExpression<?, ?> basicExpression) {
        return basicExpression == null ? this : operate(Operator.OR, basicExpression);
    }

    @NotNull
    default <X extends TypedExpression<?, ?>> X of(Path<?, ?> path) {
        return toTypedExpression(ExpressionImpls.of(path));
    }

    @Override
    default <R extends Number> ExpressionBuilder.NumberOperator<T, R, ExpressionBuilder.Conjunction<T>> and(Path.NumberPath<T, R> path) {
        return ExpressionBuilders.ofNumber(of(path), this::and);
    }

    @Override
    default ExpressionBuilder.StringOperator<T, ExpressionBuilder.Conjunction<T>> and(Path.StringPath<T> path) {
        return ExpressionBuilders.ofString(of(path), this::and);
    }

    @Override
    default <N> ExpressionBuilder.PathOperator<T, N, ExpressionBuilder.Disjunction<T>> or(Path<T, N> path) {
        return ExpressionBuilders.ofPath(of(path), this::or);
    }

    @Override
    default <N extends Number> ExpressionBuilder.NumberOperator<T, N, ExpressionBuilder.Disjunction<T>> or(Path.NumberPath<T, N> path) {
        return ExpressionBuilders.ofNumber(of(path), this::or);
    }


    @Override
    default ExpressionBuilder.StringOperator<T, ? extends ExpressionBuilder.Disjunction<T>> or(Path.StringPath<T> path) {
        return ExpressionBuilders.ofString(of(path), this::or);
    }

    @Override
    default Predicate<T> or(TypedExpression<T, Boolean> predicate) {
        return operate(Operator.OR, predicate);
    }

    @Override
    default Predicate<T> and(TypedExpression<T, Boolean> predicate) {
        return operate(Operator.AND, predicate);
    }


    @Override
    default Predicate<T> not() {
        return operate(Operator.NOT);
    }

    @Override
    default Predicate<T> and(TypedExpression<T, Boolean>[] predicate) {
        return operate(Operator.AND, Arrays.asList(predicate));
    }

    @Override
    default Predicate<T> or(TypedExpression<T, Boolean>[] predicate) {
        return operate(Operator.OR, Arrays.asList(predicate));
    }

    @Override
    default Predicate<T> and(Iterable<? extends TypedExpression<T, Boolean>> predicates) {
        return operate(Operator.AND, TypeCastUtil.cast(Iterators.toList((Iterable<?>) predicates)));
    }

    @Override
    default Predicate<T> toPredicate() {
        return Expressions.ofPredicate(this);
    }

    @Override
    default Predicate<T> or(Iterable<? extends TypedExpression<T, Boolean>> predicates) {
        return operate(Operator.OR, TypeCastUtil.cast(Iterators.toList((Iterable<?>) predicates)));
    }


}
