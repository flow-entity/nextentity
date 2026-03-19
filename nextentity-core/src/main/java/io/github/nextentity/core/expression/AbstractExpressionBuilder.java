package io.github.nextentity.core.expression;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.SimpleExpression;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.PathReference;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public abstract class AbstractExpressionBuilder<T, U, B> implements ExpressionTree {

    private volatile ExpressionNode root;

    public AbstractExpressionBuilder(ExpressionNode root) {
        this.root = root;
    }

    public ExpressionNode getRoot() {
        return root;
    }

    public B eq(U value) {
        ExpressionNode operate = operate(Operator.EQ, getNode(value));
        return next(operate);
    }

    public B eqIfNotNull(U value) {
        return value == null ? operateNull() : eq(value);
    }

    public B eq(TypedExpression<T, U> value) {
        return next(operate(Operator.EQ, getNode(value)));
    }

    public B ne(U value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    public B neIfNotNull(U value) {
        return value == null ? operateNull() : ne(value);
    }

    public B ne(TypedExpression<T, U> value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    public B in(@NonNull TypedExpression<T, List<U>> expressions) {
        return next(operate(Operator.IN, getNode(expressions)));
    }

    @SafeVarargs
    public final B in(U... values) {
        ExpressionNode[] nodes = Arrays.stream(values).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    public B in(@NonNull List<? extends TypedExpression<T, U>> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    public B in(@NonNull Collection<? extends U> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    @SafeVarargs
    public final B notIn(U... values) {
        ExpressionNode[] nodes = Arrays.stream(values).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    public B notIn(@NonNull List<? extends TypedExpression<T, U>> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    public B notIn(@NonNull Collection<? extends U> values) {
        ExpressionNode[] nodes = values.stream().map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes).operate(Operator.NOT));
    }

    public B isNull() {
        return next(operate(Operator.IS_NULL));
    }

    public B isNotNull() {
        return next(operate(Operator.IS_NOT_NULL));
    }

    public B ge(TypedExpression<T, U> expression) {
        return next(operate(Operator.GE, getNode(expression)));
    }

    public B gt(TypedExpression<T, U> expression) {
        return next(operate(Operator.GT, getNode(expression)));
    }

    public B le(TypedExpression<T, U> expression) {
        return next(operate(Operator.LE, getNode(expression)));
    }

    public B lt(TypedExpression<T, U> expression) {
        return next(operate(Operator.LT, getNode(expression)));
    }

    public B between(TypedExpression<T, U> l, TypedExpression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    public B gt(U value) {
        return next(operate(Operator.GT, getNode(value)));
    }

    public B ge(U value) {
        return next(operate(Operator.GE, getNode(value)));
    }

    public B le(U value) {
        return next(operate(Operator.LE, getNode(value)));
    }

    public B lt(U value) {
        return next(operate(Operator.LT, getNode(value)));
    }

    public B geIfNotNull(U value) {
        return value == null ? operateNull() : ge(value);
    }

    public B gtIfNotNull(U value) {
        return value == null ? operateNull() : gt(value);
    }

    public B leIfNotNull(U value) {
        return value == null ? operateNull() : le(value);
    }

    public B ltIfNotNull(U value) {
        return value == null ? operateNull() : lt(value);
    }

    public B between(U l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B notBetween(U l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    public B between(TypedExpression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B between(U l, TypedExpression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B notBetween(TypedExpression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    public B notBetween(U l, TypedExpression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    public B like(String value) {
        return next(operate(Operator.LIKE, getNode(value)));
    }

    public B notLike(String value) {
        return next(operate(Operator.LIKE, getNode(value)).operate(Operator.NOT));
    }

    public B likeIfNotNull(String value) {
        return value == null ? operateNull() : like(value);
    }

    public B notLikeIfNotNull(String value) {
        return value == null ? operateNull() : notLike(value);
    }

    public B likeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : like(value);
    }

    public B notLikeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : notLike(value);
    }


    public B eqIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : next(operate(Operator.EQ, getNode(value)));
    }

    public B not() {
        return next(operate(Operator.NOT));
    }

    public B and(TypedExpression<T, Boolean> predicate) {
        return next(operate(Operator.AND, getNode(predicate)));
    }

    public B or(TypedExpression<T, Boolean> predicate) {
        return next(operate(Operator.OR, getNode(predicate)));
    }

    public B and(TypedExpression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    public B or(TypedExpression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    public B and(Iterable<? extends TypedExpression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode).filter(it -> !(it instanceof EmptyNode))
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    public B or(Iterable<? extends TypedExpression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode)
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    protected PathNode appendPath(Path<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        String fieldName = PathReference.of(path).getFieldName();
        return target.get(fieldName);
    }

    protected PathNode appendPath(TypedExpression<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        PathNode next = (PathNode) getNode(path);
        return target.get(next);
    }

    protected B operateNull() {
        return next(EmptyNode.INSTANCE);
    }

    abstract protected B next(ExpressionNode operate);

    protected ExpressionNode getNode(TypedExpression<?, ?> typedExpression) {
        return ((ExpressionTree) typedExpression).getRoot();
    }

    protected ExpressionNode getNode(Object value) {
        return new LiteralNode(value);
    }

    protected final ExpressionNode getRootWithInitial(Supplier<ExpressionNode> supplier) {
        if (root == null) {
            synchronized (this) {
                if (root == null) {
                    root = supplier.get();
                }
            }
        }
        return root;
    }

    protected ExpressionNode operate(Operator operator) {
        return getRoot().operate(operator);
    }

    protected ExpressionNode operate(Operator operator, ExpressionNode node) {
        return getRoot().operate(operator, node);
    }

    protected ExpressionNode operate(Operator operator, ExpressionNode node0, ExpressionNode node1) {
        return getRoot().operate(operator, node0, node1);
    }

    protected ExpressionNode operate(Operator operator, ExpressionNode[] nodes) {
        return getRoot().operate(operator, nodes);
    }

    protected ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        return getRoot().operate(operator, nodes);
    }
}
