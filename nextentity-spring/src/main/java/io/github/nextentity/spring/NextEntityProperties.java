package io.github.nextentity.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/// NextEntity 配置属性。
///
/// 通过 Spring Boot 配置（application.yml 或 application.properties）
/// 为 NextEntity 行为提供外部配置。
///
/// 使用示例：
/// ```yaml
/// nextentity:
///   jdbc:
///     dialect: auto
///     query:
///       timeout: 30
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
@ConfigurationProperties(prefix = "nextentity")
public class NextEntityProperties {

    /// JDBC 配置
    @NestedConfigurationProperty
    private final JdbcProperties jdbc = new JdbcProperties();

    /// JPA 配置
    @NestedConfigurationProperty
    private final JpaProperties jpa = new JpaProperties();

    /// 日志配置
    @NestedConfigurationProperty
    private final LoggingProperties logging = new LoggingProperties();

    public JdbcProperties getJdbc() {
        return jdbc;
    }

    public JpaProperties getJpa() {
        return jpa;
    }

    public LoggingProperties getLogging() {
        return logging;
    }
}