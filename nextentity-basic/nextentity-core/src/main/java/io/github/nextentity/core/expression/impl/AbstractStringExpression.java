package io.github.nextentity.core.expression.impl;

import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.util.ImmutableList;

import static io.github.nextentity.api.TypedExpression.StringPathExpression;

public interface AbstractStringExpression<T> extends StringPathExpression<T>, AbstractPathExpression<T, String> {

    @Override
    default Predicate<T> like(String value) {
        return operate(Operator.LIKE, ExpressionImpls.of(value));
    }

    @Override
    default Predicate<T> notLike(String value) {
        return not(like(value));
    }

    @Override
    default Predicate<T> likeIfNotNull(String value) {
        return value == null ? AbstractPathExpression.operateNull() : like(value);
    }

    @Override
    default Predicate<T> notLikeIfNotNull(String value) {
        return value == null ? AbstractPathExpression.operateNull() : notLike(value);
    }

    @Override
    default StringExpression<T> lower() {
        return operate(Operator.LOWER);
    }

    @Override
    default StringExpression<T> upper() {
        return operate(Operator.UPPER);
    }

    @Override
    default StringExpression<T> substring(int offset, int length) {
        return operate(Operator.SUBSTRING, ImmutableList.of(ExpressionImpls.of(offset), ExpressionImpls.of(length)));
    }

    @Override
    default StringExpression<T> trim() {
        return operate(Operator.TRIM);
    }

    @Override
    default NumberExpression<T, Integer> length() {
        return operate(Operator.LENGTH);
    }

    @Override
    default Predicate<T> likeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? AbstractPathExpression.operateNull() : like(value);
    }

    @Override
    default Predicate<T> notLikeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? AbstractPathExpression.operateNull() : notLike(value);
    }

    @Override
    default Predicate<T> eqIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? AbstractPathExpression.operateNull() : eq(value);
    }

}
