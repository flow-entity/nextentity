package io.github.nextentity.api;

import java.util.List;
import java.util.Optional;

/// 扩展点注册中心，管理投影字段处理器和实体获取器。
///
/// 提供统一的扩展点注册和获取机制，
/// 使插件能够动态注册并按优先级排序。
///
/// ## 功能
/// - 注册 ProjectionFieldHandler
/// - 注册 EntityFetcher
/// - 按优先级获取处理器
/// - 获取字段支持的处理器
///
/// @author HuangChengwei
/// @since 2.2.0
public interface ExtensionRegistry {

    /// 注册投影字段处理器。
    ///
    /// 处理器按 order() 优先级排序，数值越小优先级越高。
    ///
    /// @param handler 处理器实例
    void registerHandler(ProjectionFieldHandler<?> handler);

    /// 注册实体获取器。
    ///
    /// @param fetcher 获取器实例
    void registerFetcher(EntityFetcher fetcher);

    /// 获取所有注册的处理器（按优先级排序）。
    ///
    /// @return 处理器列表
    List<ProjectionFieldHandler<?>> getHandlers();

    /// 获取支持给定字段的第一个处理器。
    ///
    /// 按优先级顺序查找，返回第一个 supports() 返回 true 的处理器。
    ///
    /// @param field 字段信息
    /// @param context 投影上下文
    /// @return 支持的处理器，如果没有返回 Optional.empty()
    Optional<ProjectionFieldHandler<?>> getHandler(FieldInfo field, ProjectionContext context);

    /// 获取注册的实体获取器。
    ///
    /// @return 实体获取器实例
    EntityFetcher getEntityFetcher();
}