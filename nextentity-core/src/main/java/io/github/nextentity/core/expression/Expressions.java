package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.util.Paths;

@Deprecated
public class Expressions {

    @Deprecated
    public static <T, U> TypedExpression<T, U> of(U value) {
        return Paths.<T>root().literal(value);
    }

    @Deprecated
    public static <T> Predicate<T> ofTrue() {
        return new PredicateImpl<>(LiteralNode.TRUE);
    }

    @Deprecated
    public static <T> Predicate<T> ofFalse() {
        return new PredicateImpl<>(LiteralNode.FALSE);
    }

    @Deprecated
    public static <T> Predicate<T> ofPredicate(Expression expression) {
        return toTypedExpression(expression);
    }

    @Deprecated
    static <T extends TypedExpression<?, ?>> T toTypedExpression(Expression expression) {
        return TypeCastUtil.unsafeCast(expression);
    }

}
