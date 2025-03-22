package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.Expressions;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.nextentity.api.TypedExpression.PathExpression;

public interface AbstractPathExpression<T, U> extends PathExpression<T, U> {

    @Override
    default EntityRoot<T> root() {
        return Paths.root();
    }

    @Override
    default NumberExpression<T, Long> count() {
        return toTypedExpression(ExpressionImpls.operate(this, Operator.COUNT));
    }

    @Override
    default NumberExpression<T, Long> countDistinct() {
        Expression distinct = ExpressionImpls.operate(this, Operator.DISTINCT);
        return toTypedExpression(ExpressionImpls.operate(distinct, Operator.COUNT));
    }

    @Override
    default Predicate<T> eq(U value) {
        return eq(Expressions.of(value));
    }

    @Override
    default Predicate<T> eqIfNotNull(U value) {
        return value == null ? operateNull() : eq(value);
    }

    @Override
    default Predicate<T> eq(TypedExpression<T, U> value) {
        return operate(Operator.EQ, value);
    }

    @Override
    default Predicate<T> ne(U value) {
        return ne(Expressions.of(value));
    }

    @Override
    default Predicate<T> neIfNotNull(U value) {
        return value == null ? operateNull() : ne(value);
    }

    @Override
    default Predicate<T> ne(TypedExpression<T, U> value) {
        return operate(Operator.NE, value);
    }

    @Override
    default Predicate<T> in(@NotNull TypedExpression<T, List<U>> expressions) {
        return operate(Operator.IN, expressions);
    }

    @Override
    default Predicate<T> in(U[] values) {
        List<TypedExpression<T, U>> collect = Arrays.stream(values)
                .map(Expressions::<T, U>of)
                .collect(ImmutableList.collector(values.length));
        return in(collect);
    }

    @Override
    default Predicate<T> in(@NotNull Collection<? extends U> values) {
        List<TypedExpression<T, U>> collect = values.stream()
                .map(Expressions::<T, U>of)
                .collect(ImmutableList.collector(values.size()));
        return in(collect);
    }

    @Override
    default Predicate<T> notIn(U[] values) {
        return not(in(values));
    }

    @Override
    default Predicate<T> notIn(@NotNull Collection<? extends U> values) {
        return not(in(values));
    }

    @Override
    default Predicate<T> isNull() {
        return operate(Operator.IS_NULL);
    }

    @Override
    default Predicate<T> isNotNull() {
        return not(isNull());
    }

    @Override
    default Predicate<T> notIn(@NotNull List<? extends TypedExpression<T, U>> values) {
        return not(in(values));
    }

    @Override
    default Predicate<T> in(@NotNull List<? extends TypedExpression<T, U>> expressions) {
        return operate(Operator.IN, asTypeExpressions(expressions));
    }

    @Override
    default Predicate<T> ge(TypedExpression<T, U> expression) {
        return operate(Operator.GE, expression);
    }

    @Override
    default Predicate<T> gt(TypedExpression<T, U> expression) {
        return operate(Operator.GT, expression);
    }

    @Override
    default Predicate<T> le(TypedExpression<T, U> expression) {
        return operate(Operator.LE, expression);
    }

    @Override
    default Predicate<T> lt(TypedExpression<T, U> expression) {
        return operate(Operator.LT, expression);
    }

    @Override
    default Predicate<T> between(TypedExpression<T, U> l, TypedExpression<T, U> r) {
        return operate(Operator.BETWEEN, ImmutableList.of(l, r));
    }

    @Override
    default Predicate<T> notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r) {
        return not(between(l, r));
    }

    @Override
    default Order<T> sort(SortOrder order) {
        return new OrderImpl<>(this, order);
    }

    @Override
    default Predicate<T> geIfNotNull(U value) {
        return value == null ? operateNull() : ge(Expressions.of(value));
    }

    @Override
    default Predicate<T> gtIfNotNull(U value) {
        return value == null ? operateNull() : gt(Expressions.of(value));
    }

    @Override
    default Predicate<T> leIfNotNull(U value) {
        return value == null ? operateNull() : le(Expressions.of(value));
    }

    @Override
    default Predicate<T> ltIfNotNull(U value) {
        return value == null ? operateNull() : lt(Expressions.of(value));
    }

    default <X extends TypedExpression<?, ?>> X not(TypedExpression<?, ?> expression) {
        Expression operate = ExpressionImpls.operate(expression, Operator.NOT);
        return toTypedExpression(operate);
    }

    @NotNull
    default <X extends TypedExpression<?, ?>> X operate(Operator operator, Expression expression) {
        return toTypedExpression(ExpressionImpls.operate(this, operator, expression));
    }

    @NotNull
    default <X extends TypedExpression<?, ?>> X operate(Operator operator, List<? extends Expression> expressions) {
        return toTypedExpression(ExpressionImpls.operate(this, operator, expressions));
    }

    @NotNull
    default <X extends TypedExpression<?, ?>> X operate(Operator operator) {
        return toTypedExpression(ExpressionImpls.operate(this, operator));
    }

    @NotNull
    static <T> Predicate<T> operateNull() {
        //noinspection unchecked
        return EmptyExpression.EMPTY;
    }

    default List<? extends TypedExpression<?, ?>> asTypeExpressions(List<?> list) {
        return TypeCastUtil.cast(list);
    }

    default <X extends TypedExpression<?, ?>> X toTypedExpression(Expression expression) {
        return TypeCastUtil.unsafeCast(expression);
    }

}
