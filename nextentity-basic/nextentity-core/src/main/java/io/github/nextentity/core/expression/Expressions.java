package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.Paths;

public class Expressions {

    public static <T, U> TypedExpression<T, U> of(U value) {
        return Paths.<T>root().literal(value);
    }

    public static <T> Predicate<T> ofTrue() {
        return new PredicateImpl<>(LiteralNode.TRUE);
    }

    public static <T> Predicate<T> ofFalse() {
        return new PredicateImpl<>(LiteralNode.FALSE);
    }

    public static <T> Predicate<T> ofPredicate(Expression expression) {
        return toTypedExpression(expression);
    }

    static <T extends TypedExpression<?, ?>> T toTypedExpression(Expression expression) {
        return TypeCastUtil.unsafeCast(expression);
    }

}
