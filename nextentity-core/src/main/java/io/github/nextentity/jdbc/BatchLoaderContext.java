package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntitySchemaAttribute;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.BatchAttributeLoader;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// 批量加载上下文
///
/// 管理跨投影对象的批量加载器，确保同一类型的目标实体只创建一个批量加载器。
/// 支持 WHERE IN 批量查询，避免 N+1 问题。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchLoaderContext implements BatchAttributeLoader {

    private final QueryContext queryContext;
    private final ProjectionSchemaAttribute attribute;

    private final List<AttributeLoader> attributeLoaders = new ArrayList<>();
    private final Map<Object, Object> cache = new HashMap<>();
    private boolean loaded = false;

    public BatchLoaderContext(ProjectionSchemaAttribute attribute, QueryContext queryContext) {
        this.attribute = attribute;
        this.queryContext = queryContext;
    }

    @Override
    public Supplier<Object> addForeignKey(AttributeLoader loader) {
        attributeLoaders.add(loader);
        return () -> this.load(loader);
    }

    private Object load(AttributeLoader loader) {
        Object foreignKey = loader.getForeignKey();

        // 如果缓存中已有结果，直接返回
        if (cache.containsKey(foreignKey)) {
            return cache.get(foreignKey);
        }

        // 如果尚未执行批量加载，执行一次
        if (!loaded) {
            executeBatchLoad();
            loaded = true;
        }

        // 返回缓存结果（可能为 null 表示没有找到关联对象）
        return cache.get(foreignKey);
    }

    /// 执行批量加载
    ///
    /// 收集所有外键值，构建 WHERE IN 查询，一次性加载所有关联对象
    private void executeBatchLoad() {
        // 收集所有外键值（去重，排除 null）
        Set<Object> foreignKeys = attributeLoaders.stream()
                .map(AttributeLoader::getForeignKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (foreignKeys.isEmpty()) {
            // 没有有效的外键值，无需查询
            return;
        }

        // 获取关联元数据
        EntitySchemaAttribute schemaAttribute = attribute.source();
        EntitySchema targetEntity = schemaAttribute.target();
        EntityBasicAttribute targetAttribute = schemaAttribute.targetAttribute();

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
        // 投影类型（如 DepartmentInfoLazy.class）
        Class<?> projectionType = attribute.type();

        // 构建 WHERE IN 表达式
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        // 使用 targetAttribute 作为 WHERE 条件属性
        // 例如：id IN (1, 2, 3)
        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();
        PathNode targetPath = new PathNode(targetAttribute.name());
        ExpressionNode whereClause = targetPath.operate(Operator.IN, literals);

        // 构建投影查询 QueryStructure
        SelectProjection selectProjection = new SelectProjection(projectionType, false);
        FromEntity fromEntity = new FromEntity(targetEntity.type());

        return QueryStructure.of(selectProjection, fromEntity)
                .where(whereClause);
    }

    /// 构建缓存映射
    ///
    /// 将查询结果按 targetAttribute 值映射到缓存
    private void buildCacheMap(EntityBasicAttribute keyAttribute, List<?> results) {
        // 遍历结果，按 keyAttribute 值映射
        for (Object entity : results) {
            if (entity != null) {
                Object key = keyAttribute.get(entity);
                cache.put(key, entity);
            }
        }

        // 对于没有匹配结果的外键值，缓存 null
        // 这样可以避免后续对同一外键值重复查询
        for (AttributeLoader loader : attributeLoaders) {
            Object foreignKey = loader.getForeignKey();
            if (foreignKey != null && !cache.containsKey(foreignKey)) {
                cache.put(foreignKey, null);
            }
        }
    }

}