package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/// 实体获取器接口，提供实体加载能力。
///
/// 用于 EntityReference 延迟加载时获取实体实例。
/// 支持单个和批量查询，批量查询优化避免 N+1 问题。
///
/// ## 功能
/// - 单个实体查询
/// - 批量实体查询
/// - ID 提取
/// - ID -> 实体映射构建
///
/// @author HuangChengwei
/// @since 2.2.0
public interface EntityFetcher {

    /// 根据 ID 获取单个实体。
    ///
    /// @param entityType 实体类型
    /// @param id 实体 ID
    /// @param <T> 实体类型
    /// @param <ID> ID 类型
    /// @return 可选的实体实例（如果不存在返回 Optional.empty()）
    <T, ID> Optional<T> fetch(Class<T> entityType, ID id);

    /// 批量获取实体。
    ///
    /// 返回 ID -> 实体的映射，便于快速查找。
    ///
    /// @param entityType 实体类型
    /// @param ids ID 集合
    /// @param <T> 实体类型
    /// @param <ID> ID 类型
    /// @return ID 到实体的映射
    <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, Collection<ID> ids);

    /// 判断是否支持获取给定类型的实体。
    ///
    /// @param entityType 实体类型
    /// @return 如果支持返回 true
    <T> boolean supports(Class<T> entityType);
}