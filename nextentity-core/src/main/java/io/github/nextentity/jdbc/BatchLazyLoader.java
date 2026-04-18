package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityBasicAttribute;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/// 批量懒加载器
///
/// 管理单个 LAZY 属性的批量加载。
/// 支持延迟批量加载：首次 load() 调用时触发 WHERE IN 查询。
///
/// 流程：
/// 1. construct 构建代理对象（外键存储在 data 中）
/// 2. setResults 存储结果列表
/// 3. load(foreignKey) 首次调用时：
///    - 通过 foreignKeyCollector 遍历 results 提取所有外键
///    - 执行 WHERE IN 批量查询
///    - 缓存结果，从 cache 返回当前值
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchLazyLoader {

    private final Class<?> targetType;
    private final EntityBasicAttribute targetIdAttribute;
    private final BatchLoaderContext context;

    /// 外键收集器：遍历 results 提取所有外键值
    private final Supplier<Set<Object>> foreignKeyCollector;

    /// 批量加载结果缓存
    private volatile Map<Object, Object> cache;
    private volatile boolean loaded;

    /// 构造批量懒加载器
    ///
    /// @param targetType         目标实体类型
    /// @param targetIdAttribute  目标实体主键属性
    /// @param context            批量加载上下文
    /// @param foreignKeyCollector 外键收集器（遍历 results 提取外键）
    public BatchLazyLoader(Class<?> targetType,
                           EntityBasicAttribute targetIdAttribute,
                           BatchLoaderContext context,
                           Supplier<Set<Object>> foreignKeyCollector) {
        this.targetType = targetType;
        this.targetIdAttribute = targetIdAttribute;
        this.context = context;
        this.foreignKeyCollector = foreignKeyCollector;
    }

    /// 加载目标实体（首次调用时触发批量查询）
    ///
    /// 首次调用时通过 foreignKeyCollector 收集所有外键，
    /// 执行 WHERE IN 查询并缓存结果。
    ///
    /// @param foreignKey 外键值
    /// @return 目标实体实例，如果外键为 null 或未找到则返回 null
    public Object load(Object foreignKey) {
        if (foreignKey == null) {
            return null;
        }

        // 已加载：从缓存获取
        if (loaded) {
            return cache.get(foreignKey);
        }

        // 未加载：触发批量查询
        synchronized (this) {
            if (!loaded) {
                // 收集所有外键（遍历 results）
                Set<Object> allForeignKeys = foreignKeyCollector.get();

                // 执行批量查询
                cache = context.batchLoad(targetType, targetIdAttribute, allForeignKeys);
                loaded = true;
            }
        }

        return cache.get(foreignKey);
    }

    /// 检查是否已加载
    ///
    /// @return 如果已执行批量查询则返回 true
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchLazyLoader that = (BatchLazyLoader) o;
        return Objects.equals(targetType, that.targetType)
                && Objects.equals(targetIdAttribute, that.targetIdAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType, targetIdAttribute);
    }
}