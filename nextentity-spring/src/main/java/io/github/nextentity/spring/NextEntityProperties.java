package io.github.nextentity.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/// NextEntity 配置属性。
///
/// 通过 Spring Boot 配置（application.yml 或 application.properties）
/// 为 NextEntity 行为提供外部配置。
///
/// NextEntity 默认不启用自动配置，需要显式设置：
/// ```yaml
/// nextentity:
///   enabled: true
/// ```
///
/// 完整配置示例：
/// ```yaml
/// nextentity:
///   enabled: true
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

    /// 是否启用 NextEntity 自动配置。
    ///
    /// 默认为 false，需要用户显式启用：
    /// ```yaml
    /// nextentity:
    ///   enabled: true
    /// ```
    private boolean enabled = false;

    /// 是否启用泛型 Repository 自动注入。
    ///
    /// 启用后可通过注入 `Repository<T, ID>` 自动创建 Repository 实例：
    /// ```java
    /// @Autowired
    /// private Repository<User, Long> userRepository;
    /// ```
    ///
    /// 默认为 true，可通过配置关闭：
    /// ```yaml
    /// nextentity:
    ///   enabled: true
    ///   generic-repository: false
    /// ```
    private boolean genericRepository = true;

    /// JDBC 配置
    @NestedConfigurationProperty
    private final JdbcProperties jdbc = new JdbcProperties();

    /// JPA 配置
    @NestedConfigurationProperty
    private final JpaProperties jpa = new JpaProperties();

    /// 日志配置
    @NestedConfigurationProperty
    private final LoggingProperties logging = new LoggingProperties();

    /// 分页配置
    @NestedConfigurationProperty
    private final PaginationProperties pagination = new PaginationProperties();

    /// 懒加载配置
    @NestedConfigurationProperty
    private final FetchProperties fetch = new FetchProperties();

    public JdbcProperties getJdbc() {
        return jdbc;
    }

    public JpaProperties getJpa() {
        return jpa;
    }

    public LoggingProperties getLogging() {
        return logging;
    }

    public PaginationProperties getPagination() {
        return pagination;
    }

    public FetchProperties getFetch() {
        return fetch;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isGenericRepository() {
        return genericRepository;
    }

    public void setGenericRepository(boolean genericRepository) {
        this.genericRepository = genericRepository;
    }
}