package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.expression.Literal;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.util.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("rawtypes")
final class EmptyExpression implements Literal, AbstractExpression {
    public static final EmptyExpression EMPTY = new EmptyExpression();

    private EmptyExpression() {
    }

    @Override
    public @NotNull TypedExpression operate(Operator operator, Expression expression) {
        return toTypedExpression(expression);
    }

    @Override
    public @NotNull TypedExpression operate(Operator operator, List expressions) {
        @SuppressWarnings("unchecked")
        List<? extends Expression> es = expressions;
        if (operator.isMultivalued()) {
            int count = (int) es.stream()
                    .filter(EmptyExpression::notEmpty)
                    .count();
            if (count == 0) {
                return this;
            }
            if (count != es.size()) {
                es = es.stream()
                        .filter(EmptyExpression::notEmpty)
                        .collect(ImmutableList.collector(count));
            }
            Expression baseExpression = ExpressionImpls.newOperation(es, operator);
            return toTypedExpression(baseExpression);
        }
        throw new UnsupportedOperationException();
    }

    private static boolean notEmpty(Object e) {
        return e != ExpressionImpls.EMPTY;
    }

    @Override
    public @NotNull TypedExpression<?, ?> operate(Operator operator) {
        return this;
    }

    @Override
    public Object value() {
        throw new UnsupportedOperationException();
    }
}
