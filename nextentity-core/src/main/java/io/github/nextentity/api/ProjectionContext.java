package io.github.nextentity.api;

import io.github.nextentity.core.meta.EntityType;

import java.util.Collection;
import java.util.function.Supplier;

/// 投影上下文，提供投影处理过程中需要的服务和信息。
///
/// 在 ProjectionFieldHandler 处理字段时，提供：
/// - EntityFetcher 实体获取能力
/// - EntityType 实体元数据
/// - 延迟加载器创建能力
///
/// @author HuangChengwei
/// @since 2.2.0
public interface ProjectionContext {

    /// 获取实体获取器。
    ///
    /// 用于延迟加载时获取实体。
    ///
    /// @return EntityFetcher 实例
    EntityFetcher entityFetcher();

    /// 获取当前投影的源实体类型。
    ///
    /// @return 实体类型元数据
    EntityType entityType();

    /// 批量查询实体。
    ///
    /// 用于批量加载延迟引用，避免 N+1 查询问题。
    ///
    /// @param entityType 实体类型
    /// @param ids ID 集合
    /// @param <ID> ID 类型
    /// @return 实体列表
    <ID> Collection<?> fetchEntities(Class<?> entityType, Collection<ID> ids);

    /// 创建延迟加载器。
    ///
    /// @param entityType 实体类型
    /// @param id 实体 ID
    /// @param <T> 实体类型
    /// @param <ID> ID 类型
    /// @return 延迟加载 Supplier
    <T, ID> Supplier<T> createLoader(Class<T> entityType, ID id);
}