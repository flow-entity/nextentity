package io.github.nextentity.core;

/// 日志配置。
///
/// 封装 SQL 日志的可配置参数。
///
/// @param enabled      是否启用 SQL 日志
/// @param parameters   是否记录 SQL 参数
/// @param loggerName   日志类别名
///
/// @author HuangChengwei
/// @since 2.1.0
public record LoggingConfig(
        boolean enabled,
        boolean parameters,
        String loggerName
) {

    /// 默认配置实例
    public static final LoggingConfig DEFAULT = new LoggingConfig(true, true, "io.github.nextentity.sql");

    /// 创建默认配置。
    public LoggingConfig() {
        this(true, true, "io.github.nextentity.sql");
    }

    /// 创建构建器。
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器。
    public static class Builder {
        private boolean enabled = true;
        private boolean parameters = true;
        private String loggerName = "io.github.nextentity.sql";

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder parameters(boolean parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder loggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public LoggingConfig build() {
            return new LoggingConfig(enabled, parameters, loggerName);
        }
    }
}