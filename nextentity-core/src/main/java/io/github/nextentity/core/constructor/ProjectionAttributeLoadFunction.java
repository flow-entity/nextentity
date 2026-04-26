package io.github.nextentity.core.constructor;

import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.exception.NextEntityException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ProjectionAttributeLoadFunction extends LazyLoaderFunction {

    @Override
    public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {

        JoinAttribute attribute = constructor.getAttribute();
        QueryConfig config = constructor.getQueryConfig();
        EntitySchema schemaAttribute = attribute.getTargetEntityType();

        EntityType targetEntity = config.metamodel().getEntity(schemaAttribute.type());
        EntityBasicAttribute targetAttribute = attribute.getTargetAttribute();
        ProjectionSchema projection = targetEntity.getProjection(attribute.type());

        ProjectionAttribute projectionAttribute = findProjectionAttribute(projection, targetAttribute);

        if (projectionAttribute == null) {
            return executeTupleQuery(projection, foreignKeys, constructor);
        } else {
            return executeProjectionQuery(constructor, projection, foreignKeys, config, projectionAttribute);
        }
    }

    private ProjectionAttribute findProjectionAttribute(ProjectionSchema projection, EntityBasicAttribute targetAttribute) {
        for (ProjectionAttribute attr : projection.getAttributes()) {
            if (attr.getEntityAttribute().path().equals(targetAttribute.path())) {
                return attr;
            }
        }
        return null;
    }

    private Map<Object, Object> executeProjectionQuery(LazyValueConstructor context,
                                                       ProjectionSchema projection,
                                                       Collection<Object> foreignKeys,
                                                       QueryConfig queryConfig,
                                                       ProjectionAttribute projectionAttribute) {
        QueryStructure queryStructure = buildBatchQuery(context, projection, foreignKeys);
        QueryContext newContext = QueryContext.create(queryConfig, queryStructure);
        List<?> results = queryConfig.queryExecutor().getList(newContext);
        return buildCacheMap(projectionAttribute, results);
    }

    private Map<Object, Object> executeTupleQuery(ProjectionSchema projection,
                                                  Collection<Object> foreignKeys,
                                                  LazyValueConstructor context) {
        QueryConfig config = context.getQueryConfig();
        QueryStructure queryStructure = buildTupleQuery(context, projection, foreignKeys);
        QueryContext newContext = QueryContext.create(config, queryStructure);
        List<?> results = config.queryExecutor().getList(newContext);
        return buildTupleCacheMap(results);
    }

    private QueryStructure buildBatchQuery(LazyValueConstructor context,
                                           ProjectionSchema projection,
                                           Collection<Object> foreignKeys) {

        ExpressionNode whereClause = buildWhereClause(foreignKeys, context.getAttribute());

        Selected selectProjection = new SelectProjection(projection.type(), false);
        FromEntity fromEntity = new FromEntity(projection.getEntitySchema().type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

    private QueryStructure buildTupleQuery(LazyValueConstructor context,
                                           ProjectionSchema projection,
                                           Collection<Object> foreignKeys) {
        JoinAttribute attribute = context.getAttribute();
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        EntityBasicAttribute targetAttribute = attribute.getTargetAttribute();
        PathNode targetPath = targetAttribute.path();
        ExpressionNode whereClause = targetPath.operate(Operator.IN, literals);

        SelectExpression keySelect = new SelectExpression(targetAttribute.path(), false);
        Selected projectionSelect = new SelectProjection(projection.type(), false);
        SelectNested selectNested = new SelectNested(ImmutableList.of(keySelect, projectionSelect), false);
        FromEntity fromEntity = new FromEntity(projection.getEntitySchema().type());

        return QueryStructure.of(selectNested, fromEntity).where(whereClause);
    }

    private Map<Object, Object> buildTupleCacheMap(List<?> results) {
        Map<Object, Object> cache = new HashMap<>();
        for (Object result : results) {
            if (result instanceof Object[] array && array.length >= 2) {
                cache.put(array[0], array[1]);
            } else if (result instanceof Tuple tuple) {
                cache.put(tuple.get(0), tuple.get(1));
            } else if (result != null) {
                throw new NextEntityException(
                        "Expected tuple result but got: " + result.getClass().getName());
            }
        }
        return cache;
    }

}