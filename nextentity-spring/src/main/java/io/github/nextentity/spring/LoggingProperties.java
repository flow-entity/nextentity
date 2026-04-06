package io.github.nextentity.spring;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/// 日志配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   logging:
///     sql:
///       enabled: true
///       parameters: true
///       logger-name: io.github.nextentity.sql
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
public class LoggingProperties {

    /// SQL 日志配置
    @NestedConfigurationProperty
    private final SqlProperties sql = new SqlProperties();

    public SqlProperties getSql() {
        return sql;
    }

    /// SQL 日志配置
    public static class SqlProperties {

        /// 是否记录 SQL 语句
        private boolean enabled = true;

        /// 是否记录 SQL 参数
        private boolean parameters = true;

        /// 日志类别名
        private String loggerName = "io.github.nextentity.sql";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isParameters() {
            return parameters;
        }

        public void setParameters(boolean parameters) {
            this.parameters = parameters;
        }

        public String getLoggerName() {
            return loggerName;
        }

        public void setLoggerName(String loggerName) {
            this.loggerName = loggerName;
        }
    }
}