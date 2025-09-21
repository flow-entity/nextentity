package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class EntityMeta<Entity, T> implements ExpressionTree, PathExpression<T, Entity> {
    private static final PathNode EMPTY_NODE = new PathNode(new String[0]);

    private static final ThreadLocal<PathNode> NODE = new ThreadLocal<>();

    private final SimpleExpressionImpl<T, Entity> root;

    public EntityMeta() {
        PathNode node = NODE.get();
        if (node == null) {
            root = new SimpleExpressionImpl<>(EMPTY_NODE);
        } else {
            NODE.remove();
            root = new SimpleExpressionImpl<>(node);
        }
    }

    @Override
    public PathNode getRoot() {
        return (PathNode) root.getRoot();
    }

    @Override
    public EntityRoot<T> root() {
        return root.root();
    }

    @Override
    public NumberExpression<T, Long> count() {
        return root.count();
    }

    @Override
    public NumberExpression<T, Long> countDistinct() {
        return root.countDistinct();
    }

    @Override
    public Order<T> sort(SortOrder order) {
        return root.sort(order);
    }

    @Override
    public Predicate<T> eq(Entity value) {
        return root.eq(value);
    }

    @Override
    public Predicate<T> eqIfNotNull(Entity value) {
        return root.eqIfNotNull(value);
    }

    @Override
    public Predicate<T> eq(TypedExpression<T, Entity> value) {
        return root.eq(value);
    }

    @Override
    public Predicate<T> ne(Entity value) {
        return root.ne(value);
    }

    @Override
    public Predicate<T> neIfNotNull(Entity value) {
        return root.neIfNotNull(value);
    }

    @Override
    public Predicate<T> ne(TypedExpression<T, Entity> value) {
        return root.ne(value);
    }

    @Override
    public Predicate<T> in(@NotNull TypedExpression<T, List<Entity>> expressions) {
        return root.in(expressions);
    }

    @Override
    @SafeVarargs
    public final Predicate<T> in(Entity... values) {
        return root.in(values);
    }

    @Override
    public Predicate<T> in(@NotNull List<? extends TypedExpression<T, Entity>> values) {
        return root.in(values);
    }

    @Override
    public Predicate<T> in(@NotNull Collection<? extends Entity> values) {
        return root.in(values);
    }

    @Override
    @SafeVarargs
    public final Predicate<T> notIn(Entity... values) {
        return root.notIn(values);
    }

    @Override
    public Predicate<T> notIn(@NotNull List<? extends TypedExpression<T, Entity>> values) {
        return root.notIn(values);
    }

    @Override
    public Predicate<T> notIn(@NotNull Collection<? extends Entity> values) {
        return root.notIn(values);
    }

    @Override
    public Predicate<T> isNull() {
        return root.isNull();
    }

    @Override
    public Predicate<T> isNotNull() {
        return root.isNotNull();
    }

    @Override
    public Predicate<T> ge(TypedExpression<T, Entity> expression) {
        return root.ge(expression);
    }

    @Override
    public Predicate<T> gt(TypedExpression<T, Entity> expression) {
        return root.gt(expression);
    }

    @Override
    public Predicate<T> le(TypedExpression<T, Entity> expression) {
        return root.le(expression);
    }

    @Override
    public Predicate<T> lt(TypedExpression<T, Entity> expression) {
        return root.lt(expression);
    }

    @Override
    public Predicate<T> between(TypedExpression<T, Entity> l, TypedExpression<T, Entity> r) {
        return root.between(l, r);
    }

    @Override
    public Predicate<T> notBetween(TypedExpression<T, Entity> l, TypedExpression<T, Entity> r) {
        return root.notBetween(l, r);
    }

    @Override
    public Predicate<T> geIfNotNull(Entity value) {
        return root.geIfNotNull(value);
    }

    @Override
    public Predicate<T> gtIfNotNull(Entity value) {
        return root.gtIfNotNull(value);
    }

    @Override
    public Predicate<T> leIfNotNull(Entity value) {
        return root.leIfNotNull(value);
    }

    @Override
    public Predicate<T> ltIfNotNull(Entity value) {
        return root.ltIfNotNull(value);
    }

    @Override
    public Order<T> asc() {
        return root.asc();
    }

    @Override
    public Order<T> desc() {
        return root.desc();
    }

    protected PathNode getPathNode(Path<?, ?> path) {
        String[] strings = toPath(path);
        return new PathNode(strings);
    }

    private String @NotNull [] toPath(Path<?, ?> path) {
        String fieldName = PathReference.of(path).getFieldName();
        return getRoot().join(fieldName);
    }

    protected BooleanPath<T> of(Path.BooleanRef<Entity> path) {
        return new PredicateImpl<>(getPathNode(path));
    }


    protected StringPath<T> of(Path.StringRef<Entity> path) {
        return new StringExpressionImpl<>(getPathNode(path));
    }

    protected <U extends Number> NumberPath<T, U> of(Path.NumberRef<Entity, U> path) {
        return getNumberExpression(path);
    }

    private <U extends Number> @NotNull NumberExpressionImpl<T, U> getNumberExpression(Path.NumberRef<Entity, U> path) {
        return new NumberExpressionImpl<>(getPathNode(path));
    }

    protected NumberPath<T, Long> of(Path.LongRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Integer> of(Path.IntegerRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Short> of(Path.ShortRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Byte> of(Path.ByteRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Double> of(Path.DoubleRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Float> of(Path.FloatRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, BigDecimal> of(Path.BigDecimalRef<Entity> path) {
        return getNumberExpression(path);
    }

    protected <U> PathExpression<T, U> of(Path<Entity, U> path) {
        return new SimpleExpressionImpl<>(getPathNode(path));
    }

    protected <X extends EntityMeta<U, T>, U> X of(Path<Entity, U> path, Supplier<X> supplier) {
        PathNode root = getRoot();
        if (root.size() > 10) {
            return null;
        }
        NODE.set(root.append(path));
        return supplier.get();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

}
