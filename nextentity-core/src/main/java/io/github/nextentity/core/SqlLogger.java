package io.github.nextentity.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// SQL 日志记录工具类。
///
/// 提供统一的 SQL 日志记录功能，支持配置日志名称和开关。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SqlLogger {

    private static volatile LoggingConfig config = LoggingConfig.DEFAULT;
    private static volatile Logger log = LoggerFactory.getLogger(config.loggerName());

    /// 更新日志配置。
    ///
    /// @param newConfig 新的日志配置
    public static void setConfig(LoggingConfig newConfig) {
        config = newConfig;
        log = LoggerFactory.getLogger(newConfig.loggerName());
    }

    /// 获取当前日志配置。
    ///
    /// @return 当前日志配置
    public static LoggingConfig getConfig() {
        return config;
    }

    /// 记录 DEBUG 级别日志。
    ///
    /// @param s 日志消息
    public static void debug(String s) {
        if (config.enabled() && log.isDebugEnabled()) {
            log.debug(s);
        }
    }

    /// 记录 DEBUG 级别日志（带参数）。
    ///
    /// @param s 日志消息模板
    /// @param o 参数
    public static void debug(String s, Object o) {
        if (config.enabled() && log.isDebugEnabled()) {
            log.debug(s, o);
        }
    }

    /// 记录 SQL 语句。
    ///
    /// @param sql SQL 语句
    public static void logSql(String sql) {
        if (config.enabled() && log.isDebugEnabled()) {
            log.debug(sql);
        }
    }

    /// 记录 SQL 参数。
    ///
    /// @param sql       SQL 语句
    /// @param parameters 参数列表
    public static void logParameters(String sql, Iterable<?> parameters) {
        if (config.enabled() && config.parameters() && log.isDebugEnabled()) {
            log.debug("{} | params: {}", sql, parameters);
        }
    }

    /// 获取底层 Logger。
    ///
    /// @return SLF4J Logger 实例
    public static Logger getLogger() {
        return log;
    }
}