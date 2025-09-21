package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface SimpleExpression<T, U> extends TypedExpression<T, U> {

    EntityRoot<T> root();

    NumberExpression<T, Long> count();

    NumberExpression<T, Long> countDistinct();

    Predicate<T> eq(U value);

    Predicate<T> eqIfNotNull(U value);

    Predicate<T> eq(TypedExpression<T, U> value);

    Predicate<T> ne(U value);

    Predicate<T> neIfNotNull(U value);

    Predicate<T> ne(TypedExpression<T, U> value);

    Predicate<T> in(@NotNull TypedExpression<T, List<U>> expressions);

    @SuppressWarnings("unchecked")
    Predicate<T> in(U... values);

    Predicate<T> in(@NotNull List<? extends TypedExpression<T, U>> values);

    Predicate<T> in(@NotNull Collection<? extends U> values);

    @SuppressWarnings("unchecked")
    Predicate<T> notIn(U... values);

    Predicate<T> notIn(@NotNull List<? extends TypedExpression<T, U>> values);

    Predicate<T> notIn(@NotNull Collection<? extends U> values);

    Predicate<T> isNull();

    Predicate<T> isNotNull();

    Predicate<T> ge(TypedExpression<T, U> expression);

    Predicate<T> gt(TypedExpression<T, U> expression);

    Predicate<T> le(TypedExpression<T, U> expression);

    Predicate<T> lt(TypedExpression<T, U> expression);

    Predicate<T> between(TypedExpression<T, U> l, TypedExpression<T, U> r);

    Predicate<T> notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r);

    default Order<T> asc() {
        return sort(SortOrder.ASC);
    }

    default Order<T> desc() {
        return sort(SortOrder.DESC);
    }

    Order<T> sort(SortOrder order);

    default Predicate<T> ge(U value) {
        return ge(root().literal(value));
    }

    default Predicate<T> gt(U value) {
        return gt(root().literal(value));
    }

    default Predicate<T> le(U value) {
        return le(root().literal(value));
    }

    default Predicate<T> lt(U value) {
        return lt(root().literal(value));
    }

    Predicate<T> geIfNotNull(U value);

    Predicate<T> gtIfNotNull(U value);

    Predicate<T> leIfNotNull(U value);

    Predicate<T> ltIfNotNull(U value);

    default Predicate<T> between(U l, U r) {
        EntityRoot<T> eb = root();
        return between(eb.literal(l), eb.literal(r));
    }

    default Predicate<T> notBetween(U l, U r) {
        EntityRoot<T> eb = root();
        return notBetween(eb.literal(l), eb.literal(r));
    }

    default Predicate<T> between(TypedExpression<T, U> l, U r) {
        return between(l, root().literal(r));
    }

    default Predicate<T> between(U l, TypedExpression<T, U> r) {
        return between(root().literal(l), r);
    }

    default Predicate<T> notBetween(TypedExpression<T, U> l, U r) {
        return notBetween(l, root().literal(r));
    }

    default Predicate<T> notBetween(U l, TypedExpression<T, U> r) {
        return notBetween(root().literal(l), r);
    }

}
