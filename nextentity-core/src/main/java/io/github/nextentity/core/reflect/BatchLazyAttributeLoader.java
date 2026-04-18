package io.github.nextentity.core.reflect;

import io.github.nextentity.jdbc.BatchLoaderContext;
import io.github.nextentity.jdbc.BatchLazyLoader;
import io.github.nextentity.jdbc.SelectProjectionContext.LazyAttributeInfo;

import java.util.Set;
import java.util.function.Supplier;

/// 批量懒加载属性加载器。
///
/// 实现 LazyLoader 接口，封装投影对象 LAZY 属性的批量加载逻辑。
/// 首次访问时触发 WHERE IN 批量查询，避免 N+1 问题。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class BatchLazyAttributeLoader implements LazyLoader {

    private final LazyAttributeInfo info;
    private final Supplier<BatchLoaderContext> batchLoaderContextSupplier;
    private final Supplier<Set<Object>> foreignKeyCollector;

    /// 构造批量懒加载属性加载器。
    ///
    /// @param info                   懒加载属性元数据
    /// @param batchLoaderContextSupplier 批量加载上下文提供者（延迟初始化）
    /// @param foreignKeyCollector    外键收集器（遍历 results 提取外键）
    public BatchLazyAttributeLoader(LazyAttributeInfo info,
                                    Supplier<BatchLoaderContext> batchLoaderContextSupplier,
                                    Supplier<Set<Object>> foreignKeyCollector) {
        this.info = info;
        this.batchLoaderContextSupplier = batchLoaderContextSupplier;
        this.foreignKeyCollector = foreignKeyCollector;
    }

    @Override
    public Object load(Object data) {
        // 从 EAGER 属性数据获取外键值
        Object foreignKey = info.sourceAttribute().get(data);

        // 获取批量加载上下文
        BatchLoaderContext ctx = batchLoaderContextSupplier.get();

        // 目标实体类型和主键属性
        Class<?> targetType = info.attribute().source().target().type();

        // 获取批量加载器（首次 load 时触发批量查询）
        BatchLazyLoader loader = ctx.getBatchLoader(targetType, info.targetIdAttribute(), foreignKeyCollector);

        // 执行加载
        return loader.load(foreignKey);
    }
}