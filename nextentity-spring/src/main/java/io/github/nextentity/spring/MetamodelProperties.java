package io.github.nextentity.spring;

import io.github.nextentity.core.configuration.MetamodelConfiguration;

/// 元模型配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   metamodel:
///     interface-projection-lazy-load: true
///     dto-projection-lazy-load: false
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
public class MetamodelProperties {

    /// Interface 投影懒加载开关
    ///
    /// Interface 投影通过动态代理实现，原生支持懒加载。
    /// 默认启用（true）。
    private boolean interfaceProjectionLazyLoad = true;

    /// Dto 投影懒加载开关
    private boolean dtoProjectionLazyLoad = true;

    public boolean isInterfaceProjectionLazyLoad() {
        return interfaceProjectionLazyLoad;
    }

    public void setInterfaceProjectionLazyLoad(boolean interfaceProjectionLazyLoad) {
        this.interfaceProjectionLazyLoad = interfaceProjectionLazyLoad;
    }

    public boolean isDtoProjectionLazyLoad() {
        return dtoProjectionLazyLoad;
    }

    public void setDtoProjectionLazyLoad(boolean dtoProjectionLazyLoad) {
        this.dtoProjectionLazyLoad = dtoProjectionLazyLoad;
    }

    /// 转换为 MetamodelConfiguration。
    ///
    /// @return MetamodelConfiguration 实例
    public MetamodelConfiguration toMetamodelConfiguration() {
        return MetamodelConfiguration.of(isInterfaceProjectionLazyLoad(), isDtoProjectionLazyLoad());
    }
}