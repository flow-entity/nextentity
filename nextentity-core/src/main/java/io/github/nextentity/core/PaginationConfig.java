package io.github.nextentity.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// 分页配置。
///
/// 控制分页查询（包含 offset/limit）的行为。
///
/// @param autoAddIdOrder 当分页查询未指定排序时，是否自动添加主键排序
/// @param logLevel       分页自动排序日志的级别
/// @author HuangChengwei
/// @since 2.1.1
public record PaginationConfig(
        boolean autoAddIdOrder,
        LogLevel logLevel
) {

    private static final Logger log = LoggerFactory.getLogger(PaginationConfig.class);

    /// 默认配置实例：启用自动排序，日志级别为 DEBUG
    public static final PaginationConfig DEFAULT = new PaginationConfig(true, LogLevel.DEBUG);

    /// 创建默认配置。
    public PaginationConfig() {
        this(true, LogLevel.DEBUG);
    }

    /// 创建构建器。
    public static Builder builder() {
        return new Builder();
    }

    /// 应用配置并打印配置信息（INFO 级别）。
    public void apply() {
        log.info("NextEntity pagination configuration loaded:");
        log.info("  autoAddIdOrder = {} - Controls whether to automatically add primary key ordering when pagination (offset/limit) is used without explicit ORDER BY", autoAddIdOrder);
        log.info("  logLevel = {} - Log level for pagination auto-sort warnings. Determines how the framework logs when it auto-adds ORDER BY for pagination queries", logLevel);
    }

    /// 配置构建器。
    public static class Builder {
        private boolean autoAddIdOrder = true;
        private LogLevel logLevel = LogLevel.DEBUG;

        public Builder autoAddIdOrder(boolean autoAddIdOrder) {
            this.autoAddIdOrder = autoAddIdOrder;
            return this;
        }

        public Builder logLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public PaginationConfig build() {
            return new PaginationConfig(autoAddIdOrder, logLevel);
        }
    }

    /// 日志级别枚举。
    public enum LogLevel {
        DEBUG,
        INFO,
        WARN
    }
}