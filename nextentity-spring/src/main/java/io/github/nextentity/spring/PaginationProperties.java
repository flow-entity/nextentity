package io.github.nextentity.spring;

import io.github.nextentity.core.PaginationConfig;

/// 分页配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   enabled: true
///   pagination:
///     auto-add-id-order: true
///     log-level: INFO
/// ```
///
/// 当配置加载时，会以 INFO 级别打印配置项及其作用说明。
///
/// @author HuangChengwei
/// @since 2.1.1
public class PaginationProperties {

    /// 当分页查询未指定排序时，是否自动添加主键排序（默认：true）
    private boolean autoAddIdOrder = true;

    /// 分页自动排序日志级别（默认：DEBUG）
    private PaginationConfig.LogLevel logLevel = PaginationConfig.LogLevel.DEBUG;

    /// 是否在分页查询时自动添加主键排序。
    ///
    /// 当查询包含 offset/limit 但未指定 ORDER BY 时，
    /// 如果此配置为 true，框架会自动添加主键排序以保证结果一致性。
    ///
    /// @return 是否启用自动排序
    public boolean isAutoAddIdOrder() {
        return autoAddIdOrder;
    }

    /// 设置是否在分页查询时自动添加主键排序。
    ///
    /// @param autoAddIdOrder 是否启用自动排序
    public void setAutoAddIdOrder(boolean autoAddIdOrder) {
        this.autoAddIdOrder = autoAddIdOrder;
    }

    /// 获取分页自动排序的日志级别。
    ///
    /// @return 日志级别
    public PaginationConfig.LogLevel getLogLevel() {
        return logLevel;
    }

    /// 设置分页自动排序的日志级别。
    ///
    /// @param logLevel 日志级别
    public void setLogLevel(PaginationConfig.LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /// 转换为 PaginationConfig 实例。
    ///
    /// @return 分页配置实例
    public PaginationConfig toConfig() {
        return PaginationConfig.builder()
                .autoAddIdOrder(autoAddIdOrder)
                .logLevel(logLevel)
                .build();
    }
}