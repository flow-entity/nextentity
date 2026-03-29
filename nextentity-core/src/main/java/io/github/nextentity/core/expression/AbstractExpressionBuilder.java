package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.core.PathReference;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

///
/// Abstract base class for expression builders.
/// <p>
/// Provides common implementation for building expression trees with
/// support for comparison operators, logical operators, and null-safe
/// conditional operations.
/// <p>
/// Type parameters:
/// <ul>
///   <li>T - the entity type</li>
///   <li>U - the value type being compared</li>
///   <li>B - the builder return type for method chaining</li>
/// </ul>
///
/// @param <T> the entity type
/// @param <U> the value type
/// @param <B> the builder return type
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractExpressionBuilder<T, U, B> implements ExpressionTree {

    private volatile ExpressionNode root;

    ///
    /// Creates a new expression builder with the specified root node.
    ///
    /// @param root the root expression node
    ///
    public AbstractExpressionBuilder(ExpressionNode root) {
        this.root = root;
    }

    ///
    /// Gets the root expression node.
    ///
    /// @return the root node
    ///
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

    public B eq(Expression<T, U> value) {
        return next(operate(Operator.EQ, getNode(value)));
    }

    public B ne(U value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    public B neIfNotNull(U value) {
        return value == null ? operateNull() : ne(value);
    }

    public B ne(Expression<T, U> value) {
        return next(operate(Operator.NE, getNode(value)));
    }

    public B in(@NonNull Expression<T, List<U>> expressions) {
        return next(operate(Operator.IN, getNode(expressions)));
    }

    @SafeVarargs
    public final B in(U... values) {
        ExpressionNode[] nodes = Arrays.stream(values).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.IN, nodes));
    }

    public B in(@NonNull List<? extends Expression<T, U>> values) {
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

    public B notIn(@NonNull List<? extends Expression<T, U>> values) {
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

    public B ge(Expression<T, U> expression) {
        return next(operate(Operator.GE, getNode(expression)));
    }

    public B gt(Expression<T, U> expression) {
        return next(operate(Operator.GT, getNode(expression)));
    }

    public B le(Expression<T, U> expression) {
        return next(operate(Operator.LE, getNode(expression)));
    }

    public B lt(Expression<T, U> expression) {
        return next(operate(Operator.LT, getNode(expression)));
    }

    public B between(Expression<T, U> l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B notBetween(Expression<T, U> l, Expression<T, U> r) {
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

    public B between(Expression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B between(U l, Expression<T, U> r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)));
    }

    public B notBetween(Expression<T, U> l, U r) {
        return next(operate(Operator.BETWEEN, getNode(l), getNode(r)).operate(Operator.NOT));
    }

    public B notBetween(U l, Expression<T, U> r) {
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

    public B and(Expression<T, Boolean> predicate) {
        return next(operate(Operator.AND, getNode(predicate)));
    }

    public B or(Expression<T, Boolean> predicate) {
        return next(operate(Operator.OR, getNode(predicate)));
    }

    public B and(Expression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    public B or(Expression<T, Boolean>[] predicate) {
        ExpressionNode[] nodes = Arrays.stream(predicate).map(this::getNode).toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    public B and(Iterable<? extends Expression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode).filter(it -> !(it instanceof EmptyNode))
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.AND, nodes));
    }

    public B or(Iterable<? extends Expression<T, Boolean>> predicates) {
        ExpressionNode[] nodes = StreamSupport.stream(predicates.spliterator(), false)
                .map(this::getNode)
                .toArray(ExpressionNode[]::new);
        return next(operate(Operator.OR, nodes));
    }

    protected PathNode appendPath(PathRef<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        String fieldName = PathReference.of(path).getFieldName();
        return target.get(fieldName);
    }

    protected PathNode appendPath(Expression<?, ?> path) {
        PathNode target = (PathNode) getRoot();
        PathNode next = (PathNode) getNode(path);
        return target.get(next);
    }

    protected B operateNull() {
        return next(EmptyNode.INSTANCE);
    }

    abstract protected B next(ExpressionNode operate);

    protected ExpressionNode getNode(Expression<?, ?> expression) {
        return ((ExpressionTree) expression).getRoot();
    }

    protected ExpressionNode getNode(Object value) {
        return new LiteralNode(value);
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
