package io.github.nextentity.core.expression;

import io.github.nextentity.api.BooleanPath;
import io.github.nextentity.api.ExpressionBuilder;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.Predicate;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class PredicateImpl<T> extends SimpleExpressionImpl<T, Boolean> implements Predicate<T>, BooleanPath<T> {
    public static final PredicateImpl<?> EMPTY = new PredicateImpl<>(EmptyNode.INSTANCE);
    public static final PredicateImpl<?> TRUE = new PredicateImpl<>(LiteralNode.TRUE);
    public static final PredicateImpl<?> FALSE = new PredicateImpl<>(LiteralNode.FALSE);

    public PredicateImpl(ExpressionNode root) {
        super(root);
    }

    @Override
    protected Predicate<T> operateNull() {
        //noinspection unchecked
        return (PredicateImpl<T>) EMPTY;
    }

    @Override
    public <R> ExpressionBuilder.PathOperator<T, R, ExpressionBuilder.Conjunction<T>> and(PathRef<T, R> path) {
        return new PathOperatorImpl<>(PathNode.of(path), callback(Operator.AND));
    }

    @Override
    public <R extends Number> ExpressionBuilder.NumberOperator<T, R, ExpressionBuilder.Conjunction<T>> and(PathRef.NumberRef<T, R> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), callback(Operator.AND));
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ExpressionBuilder.Conjunction<T>> and(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), callback(Operator.AND));
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, ExpressionBuilder.Disjunction<T>> or(PathRef<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), callback(Operator.OR));
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, ExpressionBuilder.Disjunction<T>> or(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), callback(Operator.OR));
    }

    @Override
    public ExpressionBuilder.StringOperator<T, ? extends ExpressionBuilder.Disjunction<T>> or(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), callback(Operator.OR));
    }

    @Override
    public Predicate<T> toPredicate() {
        return this;
    }

    private @NonNull Function<ExpressionNode, PredicateImpl<T>> callback(Operator operator) {
        return expressionNode -> new PredicateImpl<>(operate(operator, expressionNode));
    }
}
