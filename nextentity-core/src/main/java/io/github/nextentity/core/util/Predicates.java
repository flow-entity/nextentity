package io.github.nextentity.core.util;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.OperatorNode;
import io.github.nextentity.core.expression.PredicateImpl;

import static io.github.nextentity.core.expression.Operator.NOT;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Predicates {

    static <T> Predicate<T> of(TypedExpression<T, Boolean> predicate) {
        return new PredicateImpl<>(ExpressionNodes.getNode(predicate));
    }

    @SafeVarargs
    static <T> Predicate<T> and(TypedExpression<T, Boolean> predicate,
                                TypedExpression<T, Boolean>... predicates) {
        return of(predicate).and(predicates);
    }

    @SafeVarargs
    static <T> Predicate<T> or(TypedExpression<T, Boolean> predicate,
                               TypedExpression<T, Boolean>... predicates) {
        return of(predicate).or(predicates);
    }

    static <T> Predicate<T> not(TypedExpression<T, Boolean> predicate) {
        ExpressionNode operand = ExpressionNodes.getNode(predicate);
        return new PredicateImpl<>(operand.operate(NOT));
    }

}
