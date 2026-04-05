package io.github.nextentity.core.util;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.PredicateImpl;

import static io.github.nextentity.core.expression.Operator.NOT;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Predicates {

    static <T> Predicate<T> of(Expression<T, Boolean> predicate) {
        return new PredicateImpl<>(ExpressionNodes.getNode(predicate));
    }

    @SafeVarargs
    static <T> Predicate<T> and(Expression<T, Boolean> predicate,
                                Expression<T, Boolean>... predicates) {
        return of(predicate).and(predicates);
    }

    @SafeVarargs
    static <T> Predicate<T> or(Expression<T, Boolean> predicate,
                               Expression<T, Boolean>... predicates) {
        return of(predicate).or(predicates);
    }

    static <T> Predicate<T> not(Expression<T, Boolean> predicate) {
        ExpressionNode operand = ExpressionNodes.getNode(predicate);
        return new PredicateImpl<>(operand.operate(NOT));
    }

}
