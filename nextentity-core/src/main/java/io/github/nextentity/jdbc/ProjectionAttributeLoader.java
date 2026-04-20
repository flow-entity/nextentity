package io.github.nextentity.jdbc;

import io.github.nextentity.core.exception.NextEntityException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/// 投影类型批量属性加载器。
///
/// 用于加载 {@link EntitySchemaAttribute#type()} 指定的源实体类型，
/// 并投影到 {@link ProjectionSchemaAttribute#type()} 类型。
///
/// 支持两种缓存构建模式：
/// 1. 投影属性模式：找到匹配的 ProjectionAttribute，直接提取 key
/// 2. Tuple 模式：使用嵌套查询返回 (key, projection) 元组
///
/// @author HuangChengwei
/// @since 2.1.0
public class ProjectionAttributeLoader extends AbstractAttributeLoader {

    public ProjectionAttributeLoader(BatchLoaderContext context, Object foreignKey) {
        super(context, foreignKey);
    }

    @Override
    protected void executeBatchLoad() {
        Set<Object> foreignKeys = context.getForeignKeys();
        if (foreignKeys.isEmpty()) {
            return;
        }

        ProjectionSchemaAttribute attribute = context.getAttribute();
        QueryContext queryContext = context.getQueryContext();
        EntitySchemaAttribute schemaAttribute = attribute.source();

        EntityType targetEntity = queryContext.getMetamodel().getEntity(schemaAttribute.type());
        EntityBasicAttribute targetAttribute = (EntityBasicAttribute) targetEntity.getAttribute(schemaAttribute.targetAttribute().name());
        ProjectionSchema projection = targetEntity.getProjection(attribute.type());

        ProjectionAttribute projectionAttribute = findProjectionAttribute(projection, targetAttribute);

        if (projectionAttribute == null) {
            executeTupleQuery(projection, foreignKeys, queryContext);
        } else {
            executeProjectionQuery(projection, foreignKeys, queryContext, projectionAttribute);
        }
    }

    private ProjectionAttribute findProjectionAttribute(ProjectionSchema projection, EntityBasicAttribute targetAttribute) {
        for (ProjectionAttribute attr : projection.getAttributes()) {
            if (attr.source().path().equals(targetAttribute.path())) {
                return attr;
            }
        }
        return null;
    }

    private void executeProjectionQuery(ProjectionSchema projection,
                                        Set<Object> foreignKeys,
                                        QueryContext queryContext,
                                        ProjectionAttribute projectionAttribute) {
        QueryStructure queryStructure = buildBatchQuery(projection, foreignKeys);
        QueryContext newContext = queryContext.newContext(queryStructure);
        List<?> results = queryContext.getQueryExecutor().getList(newContext);
        buildCacheMap(projectionAttribute, results);
    }

    private void executeTupleQuery(ProjectionSchema projection,
                                   Set<Object> foreignKeys,
                                   QueryContext queryContext) {
        QueryStructure queryStructure = buildTupleQuery(projection, foreignKeys);
        QueryContext newContext = queryContext.newContext(queryStructure);
        List<?> results = queryContext.getQueryExecutor().getList(newContext);
        buildTupleCacheMap(results);
    }

    private QueryStructure buildBatchQuery(ProjectionSchema projection, Set<Object> foreignKeys) {
        ExpressionNode whereClause = buildWhereClause(foreignKeys, context.getAttribute());

        Selected selectProjection = new SelectProjection(projection.type(), false);
        FromEntity fromEntity = new FromEntity(projection.getEntitySchema().type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

    private QueryStructure buildTupleQuery(ProjectionSchema projection, Set<Object> foreignKeys) {
        ProjectionSchemaAttribute attribute = context.getAttribute();
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();
        PathNode targetPath = targetAttribute.path();
        ExpressionNode whereClause = targetPath.operate(Operator.IN, literals);

        SelectExpression keySelect = new SelectExpression(targetAttribute.path(), false);
        Selected projectionSelect = new SelectProjection(projection.type(), false);
        SelectNested selectNested = new SelectNested(ImmutableList.of(keySelect, projectionSelect), false);
        FromEntity fromEntity = new FromEntity(projection.getEntitySchema().type());

        return QueryStructure.of(selectNested, fromEntity).where(whereClause);
    }

    private void buildTupleCacheMap(List<?> results) {
        Map<Object, Object> cache = context.getCache();
        for (Object result : results) {
            if (result instanceof Object[] array && array.length >= 2) {
                cache.put(array[0], array[1]);
            } else if (result != null) {
                throw new NextEntityException(
                        "Expected Object[] tuple result but got: " + result.getClass().getName());
            }
        }
        fillNullForMissingKeys(cache);
    }
}