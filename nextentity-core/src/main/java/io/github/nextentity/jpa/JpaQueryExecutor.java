package io.github.nextentity.jpa;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.constructor.SelectItem;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.From;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///
/// JPA 查询执行器，使用 JPA Criteria API 执行查询操作。
/// 该执行器支持实体查询、投影查询以及复杂的条件查询、排序、分组等功能。
///
/// @author HuangChengwei
/// @since 2.0.0
public class JpaQueryExecutor implements QueryExecutor {

    private final EntityManager entityManager;
    private final QueryExecutor nativeQueryExecutor;
    private final JpaConfig config;

    public JpaQueryExecutor(EntityManager entityManager,
                            QueryExecutor nativeQueryExecutor,
                            JpaConfig config) {
        this.entityManager = entityManager;
        this.nativeQueryExecutor = nativeQueryExecutor;
        this.config = config;
    }

    @Override
    public <T> List<T> getList(@NonNull QueryContext context) {
        QueryStructure queryStructure = context.getStructure();
        // 应用 nativeSubqueries 配置
        if (config.nativeSubqueries() && requiredNativeQuery(context, queryStructure)) {
            return nativeQueryExecutor.getList(context);
        }
        Selected selected = queryStructure.select();
        if (selected instanceof SelectEntity) {
            List<?> resultList = getEntityResultList(queryStructure);
            return TypeCastUtil.cast(resultList);
        }
        ValueConstructor constructor = context.newConstructor();
        List<Object[]> objectsList = getObjectsList(queryStructure, constructor.columns());
        List<Object> result = objectsList.stream()
                .map(objects -> {
                    JpaArguments arguments = new JpaArguments(
                            objects);
                    return constructor.construct(arguments);
                })
                .collect(ImmutableList.collector(objectsList.size()));
        return TypeCastUtil.cast(result);
    }

    private boolean requiredNativeQuery(@NonNull QueryContext context, @NonNull QueryStructure queryStructure) {
        var from = queryStructure.from();
        return from instanceof FromSubQuery
               || context.getMetamodel().getEntity(((FromEntity) from).type()) instanceof SubQueryEntityType
               || hasSubQuery(queryStructure);
    }

    private boolean hasSubQuery(QueryStructure queryStructure) {
        return hasSubQuery(queryStructure.where())
               || hasSubQuery(queryStructure.groupBy())
               || hasSubQuery(queryStructure.orderBy())
               || hasSubQuery(queryStructure.having());
    }

    private boolean hasSubQuery(List<? extends SortExpression> orders) {
        for (SortExpression order : orders) {
            if (hasSubQuery(order.expression())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSubQuery(Collection<? extends ExpressionNode> expressions) {
        for (ExpressionNode operand : expressions) {
            if (hasSubQuery(operand)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSubQuery(ExpressionNode expression) {
        if (expression instanceof QueryStructure) {
            return true;
        }
        if (expression instanceof OperatorNode operator) {
            List<? extends ExpressionNode> expressions = operator.operands();
            return hasSubQuery(expressions);
        }
        return false;
    }


    private List<?> getEntityResultList(@NonNull QueryStructure structure) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        FromEntity from = (FromEntity) structure.from();
        Class<?> type = from.type();
        return getResultList(structure, cb, type);
    }

    private <T> List<?> getResultList(@NonNull QueryStructure structure, CriteriaBuilder cb, Class<T> type) {
        CriteriaQuery<T> query = cb.createQuery(type);
        Root<T> root = query.from(type);
        return new EntityBuilder<>(root, cb, query, structure, config).getResultList();
    }

    private List<Object[]> getObjectsList(@NonNull QueryStructure structure, Collection<? extends SelectItem> columns) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        FromEntity from = (FromEntity) structure.from();
        Root<?> root = query.from(from.type());
        return new ObjectArrayBuilder(root, cb, query, structure, columns, config).getResultList();
    }

    class ObjectArrayBuilder extends Builder<Object[]> {

        private final Collection<? extends SelectItem> selects;
        private final Map<JoinAttribute, From<?, ?>> joins = new HashMap<>();

        public ObjectArrayBuilder(Root<?> root,
                                  CriteriaBuilder cb,
                                  CriteriaQuery<Object[]> query,
                                  QueryStructure structure,
                                  Collection<? extends SelectItem> selects,
                                  JpaConfig config) {
            super(root, cb, query, structure, config);
            this.selects = selects;
        }

        public List<Object[]> getResultList() {
            List<?> resultList = super.getResultList();
            return resultList
                    .stream()
                    .map(it -> {
                        if (it instanceof Object[] objects) {
                            return objects;
                        }
                        return new Object[]{it};
                    })
                    .collect(ImmutableList.collector(resultList.size()));
        }

        @Override
        protected TypedQuery<Object[]> getTypedQuery() {
            List<Selection<?>> collect = selects.stream()
                    .map(this::getExpression)
                    .collect(ImmutableList.collector(selects.size()));
            Selection<Object[]> tuple = cb.array(collect);

            CriteriaQuery<Object[]> select = query.select(tuple);

            return entityManager.createQuery(select);
        }

        private Expression<?> getExpression(SelectItem column) {
            switch (column) {
                case SelectItem.Joined(var attribute, var join) -> {
                    return getJoinedExpression(attribute, join);
                }
                case SelectItem.Expr(var source, var _) -> {
                    return this.toExpression(source);
                }
                case null -> throw new IllegalArgumentException("Column must not be null");
                default -> throw new IllegalArgumentException("Unsupported Column type: " + column.getClass().getName());
            }
        }

        private Expression<?> getJoinedExpression(EntityBasicAttribute attribute,
                                                  JoinAttribute join) {
            return getJoin(join).get(attribute.name());
        }

        private From<?, ?> getJoin(JoinAttribute join) {
            return joins.computeIfAbsent(join, this::createJoin);
        }

        private From<?, ?> createJoin(JoinAttribute join) {
            return switch (join) {
                case ProjectionJoinAttribute projectionJoin -> createProjectionJoin(projectionJoin);
                case ProjectionSchemaAttribute projectionSchema -> createSchemaJoin(
                        projectionSchema,
                        projectionSchema.getEntityAttribute().name()
                );
                case EntitySchemaAttribute entitySchema -> createSchemaJoin(entitySchema, entitySchema.name());
            };
        }

        private From<?, ?> createProjectionJoin(ProjectionJoinAttribute projectionJoin) {
            From<?, ?> left = resolveParentFrom(projectionJoin.declareBy());
            Join<?, ?> join = left.join(projectionJoin.getTargetEntityType().type(), JoinType.LEFT);
            join.on(cb.equal(
                    left.get(projectionJoin.getSourceAttribute().name()),
                    join.get(projectionJoin.getTargetAttribute().name())
            ));
            return join;
        }

        private From<?, ?> createSchemaJoin(JoinAttribute join, String attributeName) {
            From<?, ?> left = resolveParentFrom(join.declareBy());
            return left.join(attributeName, JoinType.LEFT);
        }

        private From<?, ?> resolveParentFrom(Schema schema) {
            if (schema instanceof JoinAttribute join) {
                return getJoin(join);
            }
            return root;
        }

    }

    class EntityBuilder<T> extends Builder<T> {
        public EntityBuilder(Root<T> root, CriteriaBuilder cb, CriteriaQuery<T> query, QueryStructure structure, JpaConfig config) {
            super(root, cb, query, structure, config);
        }

        @Override
        protected TypedQuery<?> getTypedQuery() {
            return entityManager.createQuery(query);
        }

    }

    protected static abstract class Builder<T> extends JpaExpressionBuilder {
        protected final QueryStructure structure;
        protected final CriteriaQuery<T> query;

        public Builder(Root<?> root, CriteriaBuilder cb, CriteriaQuery<T> query, QueryStructure structure, JpaConfig config) {
            super(root, cb, config);
            this.structure = structure;
            this.query = query;
        }

        protected void setOrderBy(List<? extends SortExpression> orderBy) {
            if (orderBy != null && !orderBy.isEmpty()) {
                List<Order> orders = orderBy.stream()
                        .map(o -> o.order() == SortOrder.DESC
                                ? cb.desc(toExpression(o.expression()))
                                : cb.asc(toExpression(o.expression())))
                        .collect(ImmutableList.collector(orderBy.size()));
                query.orderBy(orders);
            }
        }

        protected void setWhere(ExpressionNode where) {
            if (!ExpressionNodes.isNullOrTrue(where)) {
                query.where(toPredicate(where));
            }
        }

        protected void setGroupBy(List<? extends ExpressionNode> groupBy) {
            if (groupBy != null && !groupBy.isEmpty()) {
                List<Expression<?>> grouping = groupBy.stream()
                        .map(this::toExpression)
                        .collect(ImmutableList.collector(groupBy.size()));
                query.groupBy(grouping);
            }
        }

        protected void setHaving(ExpressionNode having) {
            if (!ExpressionNodes.isNullOrTrue(having)) {
                query.having(toPredicate(having));
            }
        }

        protected void setFetch(Collection<? extends PathNode> fetchPaths) {
            if (fetchPaths != null) {
                for (PathNode path : fetchPaths) {
                    Fetch<?, ?> fetch = null;
                    for (int i = 0; i < path.deep(); i++) {
                        Fetch<?, ?> cur = fetch;
                        String stringPath = path.get(i);
                        PathNode sub = path.subLength(i + 1);
                        fetch = (Fetch<?, ?>) fetched.computeIfAbsent(sub, _ -> {
                            if (cur == null) {
                                return root.fetch(stringPath, JoinType.LEFT);
                            } else {
                                return cur.fetch(stringPath, JoinType.LEFT);
                            }
                        });
                    }
                }
            }
        }

        protected List<?> getResultList() {
            Selected select = structure.select();
            setDistinct(select);
            if (select instanceof SelectEntity selectEntity) {
                Collection<? extends PathNode> attributes = selectEntity.fetch();
                setFetch(attributes);
            }
            setWhere(structure.where());
            setGroupBy(structure.groupBy());
            setHaving(structure.having());
            setOrderBy(structure.orderBy());
            TypedQuery<?> objectsQuery = createTypedQuery();
            return objectsQuery.getResultList();
        }

        private TypedQuery<?> createTypedQuery() {
            TypedQuery<?> objectsQuery = getTypedQuery();
            // Set string literal parameter values
            for (int i = 0; i < stringParameters.size(); i++) {
                objectsQuery.setParameter("p" + i, stringParameters.get(i));
            }
            Integer offset = structure.offset();
            if (offset != null && offset > 0) {
                objectsQuery = objectsQuery.setFirstResult(offset);
            }
            Integer maxResult = structure.limit();
            if (maxResult != null && maxResult > 0) {
                objectsQuery = objectsQuery.setMaxResults(maxResult);
            }
            LockModeType lockModeType = structure.lockType();
            if (lockModeType != null) {
                objectsQuery.setLockMode(lockModeType);
            }
            return objectsQuery;
        }

        private void setDistinct(Selected select) {
            query.distinct(select.distinct());
        }

        protected abstract TypedQuery<?> getTypedQuery();

    }

}
