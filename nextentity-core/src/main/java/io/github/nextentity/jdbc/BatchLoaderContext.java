package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/// 批量加载上下文
///
/// 管理跨投影对象的批量加载器，确保同一类型的目标实体只创建一个批量加载器。
/// 支持 WHERE IN 批量查询，避免 N+1 问题。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchLoaderContext {

    private final Map<String, BatchLazyLoader> loaders = new ConcurrentHashMap<>();
    private final QueryExecutor queryExecutor;
    private final FetchConfig fetchConfig;
    private final Map<Class<?>, EntitySchema> entitySchemaCache = new ConcurrentHashMap<>();

    /// 构造批量加载上下文
    ///
    /// @param queryExecutor 查询执行器
    /// @param fetchConfig   批量加载配置（包括 batchMaxSize）
    public BatchLoaderContext(QueryExecutor queryExecutor, FetchConfig fetchConfig) {
        this.queryExecutor = queryExecutor;
        this.fetchConfig = fetchConfig;
    }

    /// 获取或创建批量加载器
    ///
    /// @param targetType         目标实体类型
    /// @param targetIdAttribute  目标实体主键属性
    /// @param foreignKeyCollector 外键收集器（遍历 results 提取外键）
    /// @return 批量加载器实例
    public BatchLazyLoader getBatchLoader(Class<?> targetType,
                                          EntityBasicAttribute targetIdAttribute,
                                          Supplier<Set<Object>> foreignKeyCollector) {
        String key = buildKey(targetType, targetIdAttribute);
        return loaders.computeIfAbsent(key, _ ->
                new BatchLazyLoader(targetType, targetIdAttribute, this, foreignKeyCollector));
    }

    /// 执行批量查询
    ///
    /// 构建 WHERE targetId IN (foreignKeys) 查询并执行，
    /// 返回 Map<id, entity> 结果映射。
    ///
    /// @param targetType         目标实体类型
    /// @param targetIdAttribute  目标实体主键属性
    /// @param foreignKeys        外键值集合
    /// @return Map<id, entity> 结果映射
    public Map<Object, Object> batchLoad(Class<?> targetType,
                                          EntityBasicAttribute targetIdAttribute,
                                          Collection<Object> foreignKeys) {
        if (foreignKeys.isEmpty()) {
            return Map.of();
        }

        // 如果数量小于批次大小，直接查询
        int batchSize = fetchConfig.batchMaxSize();
        if (foreignKeys.size() <= batchSize) {
            return executeBatchQuery(targetType, targetIdAttribute, foreignKeys);
        }

        // 分批查询：转换为 List 后分批
        List<Object> keyList = new ArrayList<>(foreignKeys);
        Map<Object, Object> results = new HashMap<>();

        for (int i = 0; i < keyList.size(); i += batchSize) {
            List<Object> batch = keyList.subList(i,
                    Math.min(i + batchSize, keyList.size()));
            Map<Object, Object> batchResults = executeBatchQuery(targetType, targetIdAttribute, batch);
            results.putAll(batchResults);
        }

        return results;
    }

    /// 执行单批查询
    ///
    /// @param targetType         目标实体类型
    /// @param targetIdAttribute  目标实体主键属性
    /// @param batch              当前批次的外键值集合
    /// @return Map<id, entity> 结果映射
    private Map<Object, Object> executeBatchQuery(Class<?> targetType,
                                                   EntityBasicAttribute targetIdAttribute,
                                                   Collection<Object> batch) {
        // 使用属性名构建 PathNode
        PathNode idPath = new PathNode(targetIdAttribute.name());

        // 构建 IN 表达式
        List<ExpressionNode> literals = batch.stream()
                .filter(Objects::nonNull)
                .map(LiteralNode::new)
                .map(ln -> (ExpressionNode) ln)
                .toList();

        if (literals.isEmpty()) {
            return Map.of();
        }

        ExpressionNode where = idPath.operate(Operator.IN, literals);

        // 构建查询结构
        QueryStructure query = QueryStructure.of(targetType).where(where);

        // 执行查询
        List<?> batchResults = queryExecutor.getList(query);

        // 构建结果映射 (id -> entity)
        Map<Object, Object> cache = new HashMap<>();
        for (Object entity : batchResults) {
            Object id = targetIdAttribute.get(entity);
            cache.put(id, entity);
        }

        return cache;
    }

    /// 构建加载器键
    ///
    /// @param targetType         目标实体类型
    /// @param targetIdAttribute  目标实体主键属性
    /// @return 唯一键字符串
    private String buildKey(Class<?> targetType, EntityBasicAttribute targetIdAttribute) {
        return targetType.getName() + "#" + targetIdAttribute.name();
    }
}