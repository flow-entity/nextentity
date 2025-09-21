package io.github.nextentity.api;

public interface Predicate<T> extends SimpleExpression<T, Boolean>, ExpressionBuilder.Conjunction<T>, ExpressionBuilder.Disjunction<T> {
    io.github.nextentity.api.Predicate<T> not();

    io.github.nextentity.api.Predicate<T> and(TypedExpression<T, Boolean> predicate);

    io.github.nextentity.api.Predicate<T> or(TypedExpression<T, Boolean> predicate);

    io.github.nextentity.api.Predicate<T> and(TypedExpression<T, Boolean>[] predicate);

    io.github.nextentity.api.Predicate<T> or(TypedExpression<T, Boolean>[] predicate);

    io.github.nextentity.api.Predicate<T> and(Iterable<? extends TypedExpression<T, Boolean>> predicates);

    io.github.nextentity.api.Predicate<T> or(Iterable<? extends TypedExpression<T, Boolean>> predicates);
}
