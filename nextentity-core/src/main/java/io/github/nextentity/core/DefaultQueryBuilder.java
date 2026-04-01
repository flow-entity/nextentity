package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.*;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableList;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.stream.Collectors;

public class DefaultQueryBuilder<T> extends WhereImpl<T, T> implements QueryBuilder<T>, FetchStep<T> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DefaultQueryBuilder.class);

    public DefaultQueryBuilder(Metamodel metamodel, QueryExecutor executor, Class<T> entityType) {
        this(QueryStructure.of(entityType), metamodel, executor);
    }

    protected DefaultQueryBuilder(QueryStructure queryStructure, Metamodel metamodel, QueryExecutor executor) {
        super(queryStructure, metamodel, executor);
    }

    public WhereStep<T, T> fetch(Collection<? extends PathRef<T, ?>> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return this;
        }
        SelectEntity select = (SelectEntity) queryStructure.select();
        ImmutableList.Builder<PathNode> builder = new ImmutableList.Builder<>(select.fetch().size() + expressions.size());
        builder.addAll(select.fetch().asList());
        EntityType entityType = metamodel.getEntity(fromType());
        for (PathRef<T, ?> expression : expressions) {
            PathNode entityPath = (PathNode) ExpressionNodes.getNode(expression);
            Attribute attribute = entityPath.getAttribute(entityType);
            if (!attribute.isObject()) {
                log.warn("ignoring fetch a non-entity attribute `{}` of {}",
                        entityPath.stream().collect(Collectors.joining(".")),
                        entityType.type().getName());
            } else {
                builder.add(entityPath);
            }
        }
        if (builder.isEmpty()) {
            return this;
        }
        SelectEntity selected = new SelectEntity(builder.build(), select.distinct());
        return updateSelected(selected);
    }

    @Override
    public <R> WhereStep<T, R> selectDistinct(Class<R> projectionType) {
        return select(true, projectionType);
    }

    @Override
    public <R> WhereStep<T, R> select(Class<R> projectionType) {
        return select(false, projectionType);
    }

    protected <R> WhereStep<T, R> select(boolean distinct, Class<R> projectionType) {
        Class<?> entityType = fromType();
        if (projectionType == entityType) {
            return update(queryStructure);
        }
        SelectProjection select = new SelectProjection(projectionType, distinct);
        return updateSelected(select);
    }

    @Override
    public <R> WhereStep<T, R> selectDistinct(PathRef<T, R> path) {
        return select(true, path);
    }

    @Override
    public <R> WhereStep<T, R> select(PathRef<T, R> path) {
        return select(false, path);
    }

    protected <R> WhereStep<T, R> select(boolean distinct, PathRef<T, ? extends R> path) {
        SelectExpression select = new SelectExpression(PathNode.of(path), distinct);
        return updateSelected(select);
    }

    private WhereImpl<T, Tuple> select(boolean distinct, ImmutableList<ExpressionNode> expressionNodes) {
        SelectExpressions selected = new SelectExpressions(expressionNodes, distinct);
        return updateSelected(selected);
    }

    @Override
    public <A, B> WhereStep<T, Tuple2<A, B>> select(PathRef<T, A> a, PathRef<T, B> b) {
        return selectTuple(false, a, b);
    }

    @Override
    public <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c) {
        return selectTuple(false, a, b, c);
    }

    @Override
    public <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d) {
        return selectTuple(false, a, b, c, d);
    }

    @Override
    public <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e) {
        return selectTuple(false, a, b, c, d, e);
    }

    @Override
    public <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f) {
        return selectTuple(false, a, b, c, d, e, f);
    }

    @Override
    public <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g) {
        return selectTuple(false, a, b, c, d, e, f, g);
    }

    @Override
    public <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h) {
        return selectTuple(false, a, b, c, d, e, f, g, h);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i) {
        return selectTuple(false, a, b, c, d, e, f, g, h, i);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i, PathRef<T, J> j) {
        return selectTuple(false, a, b, c, d, e, f, g, h, i, j);
    }

    @Override
    public WhereStep<T, Tuple> selectDistinct(Collection<? extends Expression<T, ?>> expressions) {
        return select(true, ExpressionNodes.mapping(expressions));
    }

    @Override
    public <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b) {
        return selectTuple(true, a, b);
    }

    @Override
    public <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c) {
        return selectTuple(true, a, b, c);
    }

    @Override
    public <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d) {
        return selectTuple(true, a, b, c, d);
    }

    @Override
    public <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e) {
        return selectTuple(true, a, b, c, d, e);
    }

    @Override
    public <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f) {
        return selectTuple(true, a, b, c, d, e, f);
    }

    @Override
    public <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g) {
        return selectTuple(true, a, b, c, d, e, f, g);
    }

    @Override
    public <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h) {
        return selectTuple(true, a, b, c, d, e, f, g, h);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i) {
        return selectTuple(true, a, b, c, d, e, f, g, h, i);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(PathRef<T, A> a, PathRef<T, B> b, PathRef<T, C> c, PathRef<T, D> d, PathRef<T, E> e, PathRef<T, F> f, PathRef<T, G> g, PathRef<T, H> h, PathRef<T, I> i, PathRef<T, J> j) {
        return selectTuple(true, a, b, c, d, e, f, g, h, i, j);
    }

    @Override
    public WhereStep<T, Tuple> select(Collection<? extends Expression<T, ?>> expressions) {
        ImmutableList<ExpressionNode> nodes = ExpressionNodes.mapping(expressions);
        return select(false, nodes);
    }

    protected <R extends Tuple> WhereStep<T, R> selectTuple(boolean distinct, PathRef<?, ?>... paths) {
        ImmutableList<ExpressionNode> nodes = PathNode.mapping(paths);
        SelectExpressions select = new SelectExpressions(nodes, distinct);
        return updateSelected(select);
    }

    @Override
    public <A, B> WhereStep<T, Tuple2<A, B>> select(Expression<T, A> a, Expression<T, B> b) {
        return selectTupleByExpr(false, a, b);
    }

    @Override
    public <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c) {
        return selectTupleByExpr(false, a, b, c);
    }

    @Override
    public <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d) {
        return selectTupleByExpr(false, a, b, c, d);
    }

    @Override
    public <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e) {
        return selectTupleByExpr(false, a, b, c, d, e);
    }

    @Override
    public <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f) {
        return selectTupleByExpr(false, a, b, c, d, e, f);
    }

    @Override
    public <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g) {
        return selectTupleByExpr(false, a, b, c, d, e, f, g);
    }

    @Override
    public <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h) {
        return selectTupleByExpr(false, a, b, c, d, e, f, g, h);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i) {
        return selectTupleByExpr(false, a, b, c, d, e, f, g, h, i);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i, Expression<T, J> j) {
        return selectTupleByExpr(false, a, b, c, d, e, f, g, h, i, j);
    }

    @Override
    public <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(Expression<T, A> a, Expression<T, B> b) {
        return selectTupleByExpr(true, a, b);
    }

    @Override
    public <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c) {
        return selectTupleByExpr(true, a, b, c);
    }

    @Override
    public <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d) {
        return selectTupleByExpr(true, a, b, c, d);
    }

    @Override
    public <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e) {
        return selectTupleByExpr(true, a, b, c, d, e);
    }

    @Override
    public <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f) {
        return selectTupleByExpr(true, a, b, c, d, e, f);
    }

    @Override
    public <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g) {
        return selectTupleByExpr(true, a, b, c, d, e, f, g);
    }

    @Override
    public <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h) {
        return selectTupleByExpr(true, a, b, c, d, e, f, g, h);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i) {
        return selectTupleByExpr(true, a, b, c, d, e, f, g, h, i);
    }

    @Override
    public <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Expression<T, A> a, Expression<T, B> b, Expression<T, C> c, Expression<T, D> d, Expression<T, E> e, Expression<T, F> f, Expression<T, G> g, Expression<T, H> h, Expression<T, I> i, Expression<T, J> j) {
        return selectTupleByExpr(true, a, b, c, d, e, f, g, h, i, j);
    }

    @SafeVarargs
    protected final <R extends Tuple> WhereStep<T, R> selectTupleByExpr(boolean distinct, Expression<T, ?>... expressions) {
        ImmutableList<ExpressionNode> nodes = ExpressionNodes.mapping(expressions);
        SelectExpressions select = new SelectExpressions(nodes, distinct);
        return updateSelected(select);
    }

    @Override
    public <R> WhereStep<T, R> selectDistinct(Expression<T, R> expression) {
        return selectExpr(true, expression);
    }

    @Override
    public <R> WhereStep<T, R> select(Expression<T, R> expression) {
        return selectExpr(false, expression);
    }

    protected <R> WhereStep<T, R> selectExpr(boolean distinct, Expression<T, R> expression) {
        SelectExpression select = new SelectExpression(ExpressionNodes.getNode(expression), distinct);
        return updateSelected(select);
    }

    private <X, Y> WhereImpl<X, Y> updateSelected(Selected select) {
        QueryStructure structure = new QueryStructure(
                select,
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                queryStructure.having(),
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
        return update(structure);
    }

    private Class<?> fromType() {
        From from = queryStructure.from();
        if (from instanceof FromEntity(Class<?> type)) {
            return type;
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported From type: " + from.getClass().getName() +
                    ". Only FromEntity is supported for this operation.");
        }
    }

}
