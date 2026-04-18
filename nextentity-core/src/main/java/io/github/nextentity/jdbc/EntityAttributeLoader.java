package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntitySchemaAttribute;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityAttributeLoader implements AttributeLoader {
    private final BatchLoaderContext context;
    private final Object foreignKey;

    public EntityAttributeLoader(BatchLoaderContext context, Object foreignKey) {
        this.context = context;
        this.foreignKey = foreignKey;
    }

    @Override
    public Object load() {
        Map<Object, Object> cache = context.getCache();

        // 如果缓存中已有结果，直接返回
        if (cache.containsKey(foreignKey)) {
            return cache.get(foreignKey);
        }

        // 如果尚未执行批量加载，执行一次
        if (!context.isLoaded()) {
            executeBatchLoad();
            context.setLoaded(true);
        }

        // 返回缓存结果（可能为 null 表示没有找到关联对象）
        return cache.get(foreignKey);
    }

    /// 执行批量加载
    ///
    /// 收集所有外键值，构建 WHERE IN 查询，一次性加载所有关联对象
    private void executeBatchLoad() {
        Set<Object> foreignKeys = context.getForeignKeys();
        ProjectionSchemaAttribute attribute = context.getAttribute();
        QueryContext queryContext = context.getQueryContext();
        // 收集所有外键值（去重，排除 null）

        if (foreignKeys.isEmpty()) {
            // 没有有效的外键值，无需查询
            return;
        }

        // 获取关联元数据
        EntitySchemaAttribute schemaAttribute = attribute.source();
        EntitySchema targetEntity = queryContext.getMetamodel().getEntity(attribute.type());
        EntityBasicAttribute targetAttribute = (EntityBasicAttribute) targetEntity.getAttribute(schemaAttribute.targetAttribute().name());

        // 构建批量查询 QueryStructure（投影类型而非实体类型）
        QueryStructure queryStructure = buildBatchQuery(targetEntity, foreignKeys);

        // 执行查询
        QueryExecutor queryExecutor = queryContext.getQueryExecutor();
        List<?> results = queryExecutor.getList(queryStructure);

        // 构建缓存映射：外键 -> 投影对象
        // 使用 targetAttribute 作为缓存 key 提取属性
        buildCacheMap(targetAttribute, results);
    }

    /// 构建批量查询的 QueryStructure
    ///
    /// SELECT * FROM target_entity WHERE targetAttribute IN (foreignKeys)
    /// 结果投影到 attribute.type() 类型
    private QueryStructure buildBatchQuery(EntitySchema targetEntity, Set<Object> foreignKeys) {
        ProjectionSchemaAttribute attribute = context.getAttribute();
        // 构建 WHERE IN 表达式
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        // 使用 targetAttribute 作为 WHERE 条件属性
        // 例如：id IN (1, 2, 3)
        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();
        PathNode targetPath = targetAttribute.path();
        ExpressionNode whereClause = targetPath.operate(Operator.IN, literals);

        // 构建投影查询 QueryStructure
        Selected selectProjection = new SelectEntity(ImmutableList.empty(), false);
        FromEntity fromEntity = new FromEntity(targetEntity.type());

        return QueryStructure.of(selectProjection, fromEntity).where(whereClause);
    }

    /// 构建缓存映射
    ///
    /// 将查询结果按 targetAttribute 值映射到缓存
    private void buildCacheMap(EntityBasicAttribute keyAttribute, List<?> results) {
        Map<Object, Object> cache = context.getCache();
        // 遍历结果，按 keyAttribute 值映射
        for (Object entity : results) {
            if (entity != null) {
                Object key = keyAttribute.get(entity);
                cache.put(key, entity);
            }
        }
        Set<Object> foreignKeys = context.getForeignKeys();
        // 对于没有匹配结果的外键值，缓存 null
        // 这样可以避免后续对同一外键值重复查询
        for (Object foreignKey : foreignKeys) {
            if (foreignKey != null && !cache.containsKey(foreignKey)) {
                cache.put(foreignKey, null);
            }
        }
    }
}
