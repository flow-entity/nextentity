package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.expression.OrderOperatorImpl;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class WhereImpl<T, U> implements RowsSelectWhereStep<T, U>, SelectHavingStep<T, U>, AbstractCollector<U> {

    public static final SelectExpression SELECT_COUNT_ANY = new SelectExpression(new OperatorNode(ImmutableList.of(LiteralNode.TRUE), Operator.COUNT), false);
    public static final SelectExpression SELECT_ANY = new SelectExpression(LiteralNode.TRUE, false);
    protected QueryStructure queryStructure;
    protected QueryConfig config;

    public WhereImpl() {
        this(null, null);
    }

    public WhereImpl(QueryConfig config, QueryStructure queryStructure) {
        this.queryStructure = queryStructure;
        this.config = config;
    }

    protected void init(RepositoryFactory entitiesFactory, Class<T> entityType) {
        this.config = entitiesFactory;
        this.queryStructure = QueryStructure.of(entityType);
    }

    @Override
    public RowsSelectWhereStep<T, U> where(TypedExpression<T, Boolean> predicate) {
        if (ExpressionNodes.isNullOrTrue(predicate)) {
            return this;
        }
        ExpressionNode node = ExpressionNodes.getNode(predicate);
        return andWhere(node);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, RowsSelectWhereStep<T, U>> where(Path<T, N> path) {
        return new PathOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, RowsSelectWhereStep<T, U>> where(Path.NumberRef<T, N> path) {
        return new NumberOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, RowsSelectWhereStep<T, U>> where(Path.StringRef<T> path) {
        return new StringOperatorImpl<>(PathNode.of(path), this::andWhere);
    }

    @Override
    public <N> ExpressionBuilder.PathOperator<T, N, RowsSelectWhereStep<T, U>> where(PathExpression<T, N> path) {
        return new PathOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    @Override
    public <N extends Number> ExpressionBuilder.NumberOperator<T, N, RowsSelectWhereStep<T, U>> where(NumberPath<T, N> path) {
        return new NumberOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    @Override
    public ExpressionBuilder.StringOperator<T, RowsSelectWhereStep<T, U>> where(StringPath<T> path) {
        return new StringOperatorImpl<>(ExpressionNodes.getNode(path), this::andWhere);
    }

    public final SelectHavingStep<T, U> groupBy(TypedExpression<T, ?> expressions) {
        return groupBy(Collections.singletonList(expressions));
    }

    @Override
    public SelectHavingStep<T, U> groupBy(List<? extends TypedExpression<T, ?>> typedExpressions) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), typedExpressions);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public SelectHavingStep<T, U> groupBy(Path<T, ?> path) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), path);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public SelectHavingStep<T, U> groupBy(Collection<Path<T, ?>> paths) {
        ImmutableList<ExpressionNode> newList = ExpressionNodes.join(queryStructure.groupBy(), paths);
        return update(queryStructure.groupBy(newList));
    }

    @Override
    public SelectOrderByStep<T, U> having(TypedExpression<T, Boolean> predicate) {
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
    public Collector<U> orderBy(Function<EntityRoot<T>, List<? extends Order<T>>> ordersBuilder) {
        ImmutableList<SortExpression> add = SortExpression.mapping(ordersBuilder.apply(root()));
        ImmutableList<SortExpression> newOrders = ImmutableList.concat(queryStructure.orderBy().asList(), add);
        return update(queryStructure.orderBy(newOrders));
    }

    @Override
    public OrderOperator<T, U> orderBy(Collection<Path<T, Comparable<?>>> paths) {
        ImmutableList<ExpressionNode> add = PathNode.mapping(paths);
        return new OrderOperatorImpl<>(this, add);
    }

    @Override
    public long count() {
        return config.queryExecutor().<Number>getList(buildCountData()).get(0).longValue();
    }

    @Override
    public List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        QueryStructure structure = getQueryListStructure(offset, maxResult, lockModeType);
        return config.queryExecutor().getList(structure);
    }

    private @NotNull QueryStructure getQueryListStructure(int offset, int maxResult, LockModeType lockModeType) {
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
        return !config.queryExecutor().getList(structure).isEmpty();
    }

    @Override
    public <X> SubQueryBuilder<X, U> asSubQuery() {
        return new SubQueryBuilderImpl<>();
    }

    @Override
    public EntityRoot<T> root() {
        return Paths.root();
    }

    public QueryStructure getQueryStructure() {
        return queryStructure;
    }

    public WhereImpl<T, U> andWhere(ExpressionNode node) {
        QueryStructure structure = queryStructure.where(queryStructure.where().operate(Operator.AND, node));
        return update(structure);
    }

    public <X, Y> WhereImpl<X, Y> update(QueryStructure structure) {
        return new WhereImpl<>(config, structure);
    }

    @NotNull
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

    public SelectOrderByStep<T, U> addOrderBy(@NotNull List<SortExpression> orderList) {
        ImmutableList<SortExpression> sortExpressions = queryStructure.orderBy();
        ImmutableList<SortExpression> newExpressions = ImmutableList.concat(sortExpressions, orderList);
        return update(queryStructure.orderBy(newExpressions));
    }


    public class SubQueryBuilderImpl<X> implements SubQueryBuilder<X, U>, ExpressionTree {

        @Override
        public TypedExpression<X, Long> count() {
            return new NumberExpressionImpl<>(buildCountData());
        }

        @Override
        public TypedExpression<X, List<U>> slice(int offset, int maxResult) {
            QueryStructure structure = getQueryListStructure(offset, maxResult, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public TypedExpression<X, U> getSingle(int offset) {
            QueryStructure structure = getQueryListStructure(offset, 2, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public TypedExpression<X, U> getFirst(int offset) {
            QueryStructure structure = getQueryListStructure(offset, 1, null);
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public ExpressionNode getRoot() {
            return queryStructure;
        }
    }
}
