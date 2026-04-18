package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.Operator;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.schema.Attribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/// 抽象批量属性加载器，提供公共的缓存检查和批量加载触发逻辑。
///
/// 子类实现 {@link #executeBatchLoad()} 方法定义具体的批量加载策略。
///
/// @author HuangChengwei
/// @since 2.1.0
public abstract class AbstractAttributeLoader implements AttributeLoader {
    protected final BatchLoaderContext context;
    protected final Object foreignKey;

    public AbstractAttributeLoader(BatchLoaderContext context, Object foreignKey) {
        this.context = context;
        this.foreignKey = foreignKey;
    }

    @Override
    public Object load() {
        Map<Object, Object> cache = context.getCache();
        if (cache.containsKey(foreignKey)) {
            return cache.get(foreignKey);
        }
        if (!context.isLoaded()) {
            executeBatchLoad();
            context.setLoaded(true);
        }
        return cache.get(foreignKey);
    }

    /// 执行批量加载，将所有待加载的外键对应的结果缓存。
    ///
    /// 子类实现此方法，负责：
    /// 1. 构建批量查询
    /// 2. 执行查询
    /// 3. 构建缓存映射
    /// 4. 填充空值（调用 fillNullForMissingKeys）
    protected abstract void executeBatchLoad();

    /// 对于没有匹配结果的外键值，缓存 null。
    ///
    /// 这样可以避免后续对同一外键值重复查询。
    protected void fillNullForMissingKeys(Map<Object, Object> cache) {
        Set<Object> foreignKeys = context.getForeignKeys();
        for (Object key : foreignKeys) {
            if (key != null && !cache.containsKey(key)) {
                cache.put(key, null);
            }
        }
    }

    /// 从查询结果构建缓存映射。
    ///
    /// 使用 keyAttribute 从每个结果对象提取键值。
    protected void buildCacheMap(Attribute keyAttribute, List<?> results) {
        Map<Object, Object> cache = context.getCache();
        for (Object entity : results) {
            if (entity != null) {
                Object key = keyAttribute.get(entity);
                cache.put(key, entity);
            }
        }
        fillNullForMissingKeys(cache);
    }

    protected ExpressionNode buildWhereClause(Set<Object> foreignKeys, ProjectionSchemaAttribute attribute) {
        Collection<ExpressionNode> literals = foreignKeys.stream()
                .map(LiteralNode::new)
                .collect(Collectors.toList());

        EntityBasicAttribute targetAttribute = attribute.source().targetAttribute();
        PathNode targetPath = targetAttribute.path();
        return targetPath.operate(Operator.IN, literals);
    }
}