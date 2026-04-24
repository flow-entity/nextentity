package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.jdbc.AttributeLoadFunction;
import io.github.nextentity.jdbc.BatchAttributeLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/// 实体类型批量属性加载器。
///
/// 用于加载 {@link ProjectionSchemaAttribute#type()} 指定的实体类型。
///
/// @author HuangChengwei
/// @since 2.1.0
public class EntityAttributeLoadFunction extends LazyLoaderFunction {

    @Override
    public Map<Object, Object> apply(LazyValueConstructor context, Collection<Object> foreignKeys) {

        ProjectionSchemaAttribute attribute = context.getAttribute();
        QueryConfig config = context.getQueryConfig();

        EntitySchema targetEntity = config.metamodel().getEntity(attribute.type());
        EntityBasicAttribute targetAttribute = attribute.getEntityAttribute().getTargetAttribute();

        QueryStructure queryStructure = buildBatchQuery(context, targetEntity, foreignKeys);
        QueryContext newContext = QueryContext.create(config, queryStructure);
        List<?> results = config.queryExecutor().getList(newContext);

        return buildCacheMap(targetAttribute, results);
    }

    private QueryStructure buildBatchQuery(LazyValueConstructor context, EntitySchema targetEntity, Collection<Object> foreignKeys) {
        ExpressionNode whereClause = buildWhereClause(foreignKeys, context.getAttribute());

        Selected selectProjection = new SelectEntity(ImmutableList.empty(), false);
        FromEntity fromEntity = new FromEntity(targetEntity.type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

}