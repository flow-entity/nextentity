package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/// 实体类型批量属性加载器。
///
/// 用于加载 {@link ProjectionSchemaAttribute#type()} 指定的实体类型。
///
/// @author HuangChengwei
/// @since 2.1.0
public class EntityAttributeLoadFunction extends AttributeLoadFunction {

    @Override
    public Map<Object, Object> apply(BatchAttributeLoader context, Collection<Object> foreignKeys) {

        ProjectionSchemaAttribute attribute = context.getAttribute();
        QueryContext queryContext = context.getQueryContext();

        EntitySchema targetEntity = queryContext.getMetamodel().getEntity(attribute.type());
        EntityBasicAttribute targetAttribute = attribute.getEntityAttribute().getTargetAttribute();

        QueryStructure queryStructure = buildBatchQuery(context, targetEntity, foreignKeys);
        QueryContext newContext = queryContext.newContext(queryStructure);
        List<?> results = queryContext.getQueryExecutor().getList(newContext);

        return buildCacheMap(targetAttribute, results);
    }

    private QueryStructure buildBatchQuery(BatchAttributeLoader context, EntitySchema targetEntity, Collection<Object> foreignKeys) {
        ExpressionNode whereClause = buildWhereClause(foreignKeys, context.getAttribute());

        Selected selectProjection = new SelectEntity(ImmutableList.empty(), false);
        FromEntity fromEntity = new FromEntity(targetEntity.type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

}