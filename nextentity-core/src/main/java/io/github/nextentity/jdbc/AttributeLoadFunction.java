package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// 批量属性加载函数基类。
///
/// 负责根据外键集合构建查询并返回 {@code Map<foreignKey, result>}。
/// 具体的查询策略由子类实现。
///
/// @see EntityAttributeLoadFunction
/// @see ProjectionAttributeLoadFunction
/// @see BatchAttributeLoader
public abstract class AttributeLoadFunction {

    /// 执行批量加载查询。
    ///
    /// @param batchAttributeLoader 批量加载器，提供查询上下文和属性元数据
    /// @param foreignKeys 待加载的外键集合
    /// @return 外键 → 查询结果的映射
    abstract public Map<Object, Object> apply(BatchAttributeLoader batchAttributeLoader, Collection<Object> foreignKeys);

    /// 从查询结果构建缓存映射。
    ///
    /// 使用 keyAttribute 从每个结果对象提取键值。
    ///
    /// @param keyAttribute 用于提取键值的属性
    /// @param results 查询结果列表
    /// @return 键值 → 结果对象的映射
    protected Map<Object, Object> buildCacheMap(Attribute keyAttribute, List<?> results) {
        Map<Object, Object> cache = new HashMap<>();
        for (Object entity : results) {
            if (entity != null) {
                Object key = keyAttribute.get(entity);
                cache.put(key, entity);
            }
        }
        return cache;
    }

    /// 构建 WHERE IN 条件子句。
    ///
    /// @param foreignKeys 外键集合
    /// @param attribute 投影属性元数据
    /// @return IN 表达式节点
    protected ExpressionNode buildWhereClause(Collection<Object> foreignKeys, ProjectionSchemaAttribute attribute) {
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();
        PathNode targetPath = targetAttribute.path();
        return targetPath.operate(Operator.IN, literals);
    }
}