package io.github.nextentity.core.meta;

import io.github.nextentity.core.meta.impl.DefaultMetamodelConfiguration;

/// 元模型配置接口
///
/// 定义投影查询的懒加载行为配置。
/// Interface投影和Dto投影的懒加载行为可以独立配置。
public interface MetamodelConfiguration {

    /// Interface投影是否支持懒加载
    ///
    /// Interface投影通过动态代理实现，原生支持懒加载。
    /// 默认启用（true）。
    ///
    /// @return 是否启用Interface投影懒加载
    boolean interfaceProjectionLazyLoadEnabled();

    /// Dto投影是否支持字段懒加载
    ///
    /// Dto投影通过反射构造，不支持真正的懒加载。
    /// 默认禁用（false）。启用后需要特殊处理。
    ///
    /// @return 是否启用Dto投影懒加载
    boolean dtoProjectionLazyLoadEnabled();

    /// 创建元模型配置
    ///
    /// @param interfaceProjectionLazyLoadEnabled Interface投影懒加载开关
    /// @param dtoProjectionLazyLoadEnabled       Dto投影懒加载开关
    /// @return 元模型配置实例
    static MetamodelConfiguration of(boolean interfaceProjectionLazyLoadEnabled,
                                     boolean dtoProjectionLazyLoadEnabled) {
        return new DefaultMetamodelConfiguration(interfaceProjectionLazyLoadEnabled, dtoProjectionLazyLoadEnabled);
    }

    /// 默认配置
    ///
    /// Interface投影懒加载启用，Dto投影懒加载禁用。
    MetamodelConfiguration DEFAULT = of(true, false);
}
