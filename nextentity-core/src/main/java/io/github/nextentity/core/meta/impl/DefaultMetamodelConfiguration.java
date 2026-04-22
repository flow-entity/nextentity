package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.MetamodelConfiguration;

/// 默认元模型配置实现
///
/// 提供元模型配置的具体实现，使用 Builder 模式构造。
public class DefaultMetamodelConfiguration implements MetamodelConfiguration {

    private final boolean interfaceProjectionLazyLoadEnabled;
    private final boolean dtoProjectionLazyLoadEnabled;

    public DefaultMetamodelConfiguration(boolean interfaceProjectionLazyLoadEnabled,
                                         boolean dtoProjectionLazyLoadEnabled) {
        this.interfaceProjectionLazyLoadEnabled = interfaceProjectionLazyLoadEnabled;
        this.dtoProjectionLazyLoadEnabled = dtoProjectionLazyLoadEnabled;
    }

    @Override
    public boolean interfaceProjectionLazyLoadEnabled() {
        return interfaceProjectionLazyLoadEnabled;
    }

    @Override
    public boolean dtoProjectionLazyLoadEnabled() {
        return dtoProjectionLazyLoadEnabled;
    }

    /// 默认配置实例
    public static final DefaultMetamodelConfiguration DEFAULT =
            new DefaultMetamodelConfiguration(true, false);

    /// 创建构建器
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器
    public static class Builder {
        private boolean interfaceProjectionLazyLoadEnabled = true;
        private boolean dtoProjectionLazyLoadEnabled = false;

        /// 设置Interface投影懒加载开关
        public Builder interfaceProjectionLazyLoadEnabled(boolean enabled) {
            this.interfaceProjectionLazyLoadEnabled = enabled;
            return this;
        }

        /// 设置Dto投影懒加载开关
        public Builder dtoProjectionLazyLoadEnabled(boolean enabled) {
            this.dtoProjectionLazyLoadEnabled = enabled;
            return this;
        }

        /// 构建配置实例
        public DefaultMetamodelConfiguration build() {
            return new DefaultMetamodelConfiguration(interfaceProjectionLazyLoadEnabled, dtoProjectionLazyLoadEnabled);
        }
    }
}
