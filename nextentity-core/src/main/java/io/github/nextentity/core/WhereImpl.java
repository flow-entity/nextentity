package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/// WHERE 子句构建器的抽象基类实现。
///
/// 提供 WHERE、GROUP BY、HAVING、ORDER BY 等查询操作的实现，
/// 支持分页、锁定模式和子查询构建。
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public class WhereImpl<T, U> implements WhereStep<T, U>, HavingStep<T, U>, Collector<U> {

    private static final Logger log = LoggerFactory.getLogger(WhereImpl.class);

    public static final SelectExpression SELECT_COUNT_ANY = new SelectExpression(new OperatorNode(ImmutableList.of(LiteralNode.TRUE), Operator.COUNT), false);
    public static final SelectExpression SELECT_ANY = new SelectExpression(LiteralNode.TRUE, false);

    protected final QueryStructure queryStructure;
    protected final QueryDescriptor<T> descriptor;

    protected WhereImpl(QueryStructure queryStructure, QueryDescriptor<T> descriptor) {
        this.queryStructure = queryStructure;
        this.descriptor = descriptor;
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
    public LockStep<U> orderBy(List<? extends Order<T>> orders) {
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
        return descriptor.queryExecutor().<Number>getList(buildCountData()).getFirst().longValue();
    }

    @Override
    public boolean exists() {
        QueryStructure structure = new QueryStructure(
                SELECT_ANY,
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                ImmutableList.of(),
                queryStructure.having(),
                queryStructure.offset(),
                1,
                queryStructure.lockType()
        );
        return !descriptor.queryExecutor().getList(structure).isEmpty();
    }

    @Override
    public boolean exists(int offset) {
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
        return !descriptor.queryExecutor().getList(structure).isEmpty();
    }

    @Override
    public Collector<U> lock(LockModeType lockModeType) {
        return update(queryStructure.lockType(lockModeType));
    }

    @Override
    public List<U> list() {
        return descriptor.queryExecutor().getList(queryStructure);
    }

    @Override
    public List<U> list(int offset, int limit) {
        return descriptor.queryExecutor().getList(buildPaginatedQueryStructure(offset, limit, true));
    }

    @Override
    public U single() {
        List<U> list = descriptor.queryExecutor().getList(buildPaginatedQueryStructure(0, 2, false));
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.getFirst();
    }

    @Override
    public <X> SubQueryBuilder<X, U> toSubQuery() {
        return new SubQueryBuilderImpl<>();
    }

    public QueryStructure getQueryStructure() {
        return queryStructure;
    }

    public WhereImpl<T, U> andWhere(ExpressionNode node) {
        QueryStructure structure = queryStructure.where(queryStructure.where().operate(Operator.AND, node));
        return update(structure);
    }

    /// 创建新的 WhereImpl 实例，保持实体类型不变。
    ///
    /// @param structure 新的查询结构
    /// @param <Y>       新的结果类型
    /// @return 新的 WhereImpl 实例
    public <Y> WhereImpl<T, Y> update(QueryStructure structure) {
        return new WhereImpl<>(structure, descriptor);
    }

    /// 构建分页查询结构，根据配置决定是否自动添加主键排序。
    ///
    /// 当分页查询（包含 offset/limit）未指定 ORDER BY 时，
    /// 如果 paginationConfig.autoAddIdOrder() 为 true，
    /// 框架会自动添加主键排序以保证结果一致性。
    ///
    /// @param offset 偏移量
    /// @param limit  限制数
    /// @return 查询结构
    private QueryStructure buildPaginatedQueryStructure(int offset, int limit, boolean logAutoSortWarning) {
        ImmutableList<SortExpression> orderBy = queryStructure.orderBy();

        // 只有在配置启用、非聚合查询且未指定排序时才自动添加主键排序
        if (descriptor.paginationConfig().autoAddIdOrder() && orderBy.isEmpty() && !isAggregateQuery()) {
            EntityType entity = descriptor.entityType();
            if (entity != null) {
                var idAttr = entity.id();
                if (logAutoSortWarning) {
                    logAutoSort(descriptor.entityClass());
                }
                PathNode idPath = new PathNode(idAttr.name());
                SortExpression sortExpr = new SortExpression(idPath, SortOrder.ASC);
                orderBy = ImmutableList.of(sortExpr);
            }
        }

        return new QueryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                orderBy,
                queryStructure.having(),
                offset,
                limit,
                queryStructure.lockType()
        );
    }

    /// 根据配置的日志级别记录自动排序日志。
    ///
    /// @param entityType 实体类型
    private void logAutoSort(Class<?> entityType) {
        String message = "Pagination without ORDER BY detected. " +
                "Automatically adding primary key ordering for entity {}. " +
                "Consider adding explicit orderBy() for deterministic results.";
        switch (descriptor.paginationConfig().logLevel()) {
            case INFO -> log.info(message, entityType.getSimpleName());
            case WARN -> log.warn(message, entityType.getSimpleName());
            default -> log.debug(message, entityType.getSimpleName());
        }
    }

    /// 检查是否是聚合查询。
    ///
    /// @return 如果是聚合查询返回 true
    private boolean isAggregateQuery() {
        // 有 GROUP BY 子句
        if (queryStructure.groupBy() != null && !queryStructure.groupBy().isEmpty()) {
            return true;
        }
        // SELECT 子句包含聚合函数
        return requiredCountSubQuery(queryStructure.select());
    }

    @NonNull
    QueryStructure buildCountData() {
        if (queryStructure.select().distinct()) {
            return QueryStructure.of(queryStructure.select(), newCountSubQuery(queryStructure));
        } else if (requiredCountSubQuery(queryStructure.select())) {
            return QueryStructure.of(SELECT_COUNT_ANY, newCountSubQuery(queryStructure));
        } else if (queryStructure.groupBy() != null && !queryStructure.groupBy().isEmpty()) {
            return QueryStructure.of(SELECT_COUNT_ANY, newCountSubQuery(queryStructure));
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

    /// 返回移除了 ORDER BY、偏移量和限制的新 QueryStructure。
    ///
    /// 用于构建 COUNT 查询的子查询，因为：
    /// - COUNT 不需要排序
    /// - SQL Server 等数据库不允许子查询中有 ORDER BY（除非配合 TOP/OFFSET）
    ///
    /// @return 没有 orderBy/offset/limit 的新 QueryStructure 实例
    public FromSubQuery newCountSubQuery(QueryStructure structure) {
        if (structure.orderBy().isEmpty() && structure.offset() == null && structure.limit() == null) {
            return new FromSubQuery(structure);
        }
        QueryStructure queryStructure1 = new QueryStructure(
                structure.select(),
                structure.from(),
                structure.where(),
                structure.groupBy(),
                ImmutableList.of(),
                structure.having(),
                null,
                null,
                LockModeType.NONE
        );
        return new FromSubQuery(queryStructure1);
    }

    boolean requiredCountSubQuery(Selected select) {
        if (select instanceof SelectExpression selectExpression) {
            return requiredCountSubQuery(selectExpression.expression());
        }
        if (select instanceof SelectExpressions selectExpressions) {
            for (ExpressionNode expression : selectExpressions.items()) {
                if (requiredCountSubQuery(expression)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean requiredCountSubQuery(ExpressionNode expression) {
        if (expression instanceof OperatorNode(ImmutableList<ExpressionNode> args, Operator operator)) {
            if (operator.isAgg()) {
                return true;
            }
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
            QueryStructure structure = new QueryStructure(
                    queryStructure.select(),
                    queryStructure.from(),
                    queryStructure.where(),
                    queryStructure.groupBy(),
                    queryStructure.orderBy(),
                    queryStructure.having(),
                    offset,
                    maxResult,
                    null
            );
            return new SimpleExpressionImpl<>(structure);
        }

        @Override
        public ExpressionNode getRoot() {
            return queryStructure;
        }
    }
}
