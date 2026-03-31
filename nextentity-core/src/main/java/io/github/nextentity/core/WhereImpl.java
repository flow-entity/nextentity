package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WhereImpl<T, U> implements WhereStep<T, U>, HavingStep<T, U>, AbstractCollector<U> {

    public static final SelectExpression SELECT_COUNT_ANY = new SelectExpression(new OperatorNode(ImmutableList.of(LiteralNode.TRUE), Operator.COUNT), false);
    public static final SelectExpression SELECT_ANY = new SelectExpression(LiteralNode.TRUE, false);
    protected final QueryStructure queryStructure;
    protected final Metamodel metamodel;
    protected final QueryExecutor queryExecutor;

    public WhereImpl(QueryStructure queryStructure, Metamodel metamodel, QueryExecutor queryExecutor) {
        this.queryStructure = queryStructure;
        this.metamodel = metamodel;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public WhereStep<T, U> where(Expression<T, Boolean> predicate) {
        if (ExpressionNodes.isNullOrTrue(predicate)) {
            return this;
        }
        ExpressionNode node = ExpressionNodes.getNode(predicate);
        return andWhere(node);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, WhereStep<T, U>> where(PathRef<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, WhereStep<T, U>> where(PathRef.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, WhereStep<T, U>> where(PathRef.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, WhereStep<T, U>> where(Path<T, N> path) {
        return new PathOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, WhereStep<T, U>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, WhereStep<T, U>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    public final HavingStep<T, U> groupByExpr(Expression<T, ?> expressions) {
        return groupByExpr(Collections.singletonList(expressions));
    }

    @Override
    public HavingStep<T, U> groupByExpr(List<? extends Expression<T, ?>> typedExpressions) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), typedExpressions);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public HavingStep<T, U> groupBy(PathRef<T, ?> path) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), path);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public HavingStep<T, U> groupBy(Collection<PathRef<T, ?>> paths) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), paths);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public OrderByStep<T, U> having(Expression<T, Boolean> predicate) {
        ExpressionNode newHaving = queryStructure.having().operate(Operator.AND, ExpressionNodes.getNode(predicate));
        return update(queryStructure.having(newHaving));
    }

    @Override
    public Collector<U> orderBy(List<? extends Order<T>> orders) {
        ImmutableList<SortExpression> add = SortExpression.mapping(orders);
        ImmutableList<SortExpression> newOrders = ImmutableList.concat(queryStructure.orderBy().asList(), add);
        return update(queryStructure.orderBy(newOrders));
    }

    @Override
    public OrderOperator<T, U> orderBy(Collection<PathRef<T, ? extends Comparable<?>>> paths) {
        ImmutableList<ExpressionNode> add = PathNode.mapping(paths);
        return new OrderOperatorImpl<>(this, add);
    }

    @Override
    public long count() {
        return queryExecutor.<Number>getList(buildCountData()).get(0).longValue();
    }

    @Override
    public List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        QueryStructure structure = getQueryListStructure(offset, maxResult, lockModeType);
        return queryExecutor.getList(structure);
    }

    private @NonNull QueryStructure getQueryListStructure(int offset, int maxResult, LockModeType lockModeType) {
        return new QueryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                queryStructure.having(),
                offset,
                maxResult,
                lockModeType
        );
    }

    @Override
    public boolean exist(int offset) {
        QueryStructure structure = new QueryStructure(
                SELECT_ANY,
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                ImmutableList.of(),
                queryStructure.having(),
                offset,
                1,
                queryStructure.lockType()
        );
        return !queryExecutor.getList(structure).isEmpty();
    }

    @Override
    public <X> SubQueryBuilder<X, U> asSubQuery() {
        return new SubQueryBuilderImpl<>();
    }

    public QueryStructure getQueryStructure() {
        return queryStructure;
    }

    public WhereImpl<T, U> andWhere(ExpressionNode node) {
        QueryStructure structure = queryStructure.where(queryStructure.where().operate(Operator.AND, node));
        return update(structure);
    }

    public <X, Y> WhereImpl<X, Y> update(QueryStructure structure) {
        return new WhereImpl<>(structure, metamodel, queryExecutor);
    }

    @NonNull
    QueryStructure buildCountData() {
        if (queryStructure.select().distinct()) {
            return QueryStructure.of(queryStructure.select(), new FromSubQuery(queryStructure));
        } else if (requiredCountSubQuery(queryStructure.select())) {
            return QueryStructure.of(SELECT_COUNT_ANY, new FromSubQuery(queryStructure));
        } else if (queryStructure.groupBy() != null && !queryStructure.groupBy().isEmpty()) {
            return QueryStructure.of(SELECT_COUNT_ANY, new FromSubQuery(queryStructure));
        } else {
            return new QueryStructure(
                    SELECT_COUNT_ANY,
                    queryStructure.from(),
                    queryStructure.where(),
                    queryStructure.groupBy(),
                    ImmutableList.of(),
                    queryStructure.having(),
                    null,
                    null,
                    LockModeType.NONE);
        }

    }

    boolean requiredCountSubQuery(Selected select) {
        if (select instanceof SelectExpression) {
            return requiredCountSubQuery(((SelectExpression) select).expression());
        }
        if (select instanceof SelectExpressions) {
            for (ExpressionNode expression : ((SelectExpressions) select).items()) {
                if (requiredCountSubQuery(expression)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean requiredCountSubQuery(ExpressionNode expression) {
        if (expression instanceof OperatorNode operation) {
            if (operation.operator().isAgg()) {
                return true;
            }
            ImmutableArray<ExpressionNode> args = operation.operands();
            if (args != null) {
                for (ExpressionNode arg : args) {
                    if (requiredCountSubQuery(arg)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public class SubQueryBuilderImpl<X> implements SubQueryBuilder<X, U>, ExpressionTree {

        @Override
        public Expression<X, Long> count() {
            return new NumberExpressionImpl<>(buildCountData());
        }

        @Override
        public Expression<X, List<U>> slice(int offset, int maxResult) {
            QueryStructure structure = getQueryListStructure(offset, maxResult, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public Expression<X, U> getSingle(int offset) {
            QueryStructure structure = getQueryListStructure(offset, 2, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public Expression<X, U> getFirst(int offset) {
            QueryStructure structure = getQueryListStructure(offset, 1, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public ExpressionNode getRoot() {
            return queryStructure;
        }
    }
}
