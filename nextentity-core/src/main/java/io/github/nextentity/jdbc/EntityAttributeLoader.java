package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;
import java.util.Set;

/// 实体类型批量属性加载器。
///
/// 用于加载 {@link ProjectionSchemaAttribute#type()} 指定的实体类型。
///
/// @author HuangChengwei
/// @since 2.1.0
public class EntityAttributeLoader extends AbstractAttributeLoader {

    public EntityAttributeLoader(BatchLoaderContext context, Object foreignKey) {
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

        EntitySchema targetEntity = queryContext.getMetamodel().getEntity(attribute.type());
        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();

        QueryStructure queryStructure = buildBatchQuery(targetEntity, foreignKeys);
        QueryExecutor queryExecutor = queryContext.getQueryExecutor();
        List<?> results = queryExecutor.getList(queryStructure);

        buildCacheMap(targetAttribute, results);
    }

    private QueryStructure buildBatchQuery(EntitySchema targetEntity, Set<Object> foreignKeys) {
        ExpressionNode whereClause = buildWhereClause(foreignKeys, context.getAttribute());

        Selected selectProjection = new SelectEntity(ImmutableList.empty(), false);
        FromEntity fromEntity = new FromEntity(targetEntity.type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

}