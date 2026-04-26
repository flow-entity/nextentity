package io.github.nextentity.spring;

import io.github.nextentity.core.PaginationConfig;
import jakarta.persistence.FetchType;
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
///   default-fetch-type: LAZY
///   fetch-batch-max-size: 1000
///   lazy-load-enabled: true
///   auto-add-id-order: true
///   pagination-log-level: DEBUG
///   interface-lazy-enabled: true
///   dto-lazy-enabled: false
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

    // ===================== Fetch 配置 =====================

    /// 默认加载策略，未标注 @Fetch 注解时使用的默认策略
    private FetchType defaultFetchType = FetchType.LAZY;

    /// 批量加载的最大批次大小（默认 1000）
    private int fetchBatchMaxSize = 1000;

    /// 是否启用懒加载功能（默认 true）
    private boolean lazyLoadEnabled = true;

    // ===================== Pagination 配置 =====================

    /// 当分页查询未指定排序时，是否自动添加主键排序（默认 true）
    private boolean autoAddIdOrder = true;

    /// 分页自动排序日志级别（默认 DEBUG）
    private PaginationConfig.LogLevel paginationLogLevel = PaginationConfig.LogLevel.DEBUG;

    // ===================== Metamodel 配置 =====================

    /// Interface 投影懒加载开关（默认 true）
    private boolean interfaceLazyEnabled = true;

    /// Dto 投影懒加载开关（默认 false）
    private boolean classLazyEnabled = false;

    // ===================== 执行器配置 =====================

    /// JDBC 配置
    @NestedConfigurationProperty
    private final JdbcProperties jdbc = new JdbcProperties();

    /// JPA 配置
    @NestedConfigurationProperty
    private final JpaProperties jpa = new JpaProperties();

    /// 日志配置
    @NestedConfigurationProperty
    private final LoggingProperties logging = new LoggingProperties();

    // ===================== Getters & Setters =====================

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

    public FetchType getDefaultFetchType() {
        return defaultFetchType;
    }

    public void setDefaultFetchType(FetchType defaultFetchType) {
        this.defaultFetchType = defaultFetchType;
    }

    public int getFetchBatchMaxSize() {
        return fetchBatchMaxSize;
    }

    public void setFetchBatchMaxSize(int fetchBatchMaxSize) {
        this.fetchBatchMaxSize = fetchBatchMaxSize;
    }

    public boolean isLazyLoadEnabled() {
        return lazyLoadEnabled;
    }

    public void setLazyLoadEnabled(boolean lazyLoadEnabled) {
        this.lazyLoadEnabled = lazyLoadEnabled;
    }

    public boolean isAutoAddIdOrder() {
        return autoAddIdOrder;
    }

    public void setAutoAddIdOrder(boolean autoAddIdOrder) {
        this.autoAddIdOrder = autoAddIdOrder;
    }

    public PaginationConfig.LogLevel getPaginationLogLevel() {
        return paginationLogLevel;
    }

    public void setPaginationLogLevel(PaginationConfig.LogLevel paginationLogLevel) {
        this.paginationLogLevel = paginationLogLevel;
    }

    public boolean isInterfaceLazyEnabled() {
        return interfaceLazyEnabled;
    }

    public void setInterfaceLazyEnabled(boolean interfaceLazyEnabled) {
        this.interfaceLazyEnabled = interfaceLazyEnabled;
    }

    public boolean isClassLazyEnabled() {
        return classLazyEnabled;
    }

    public void setClassLazyEnabled(boolean classLazyEnabled) {
        this.classLazyEnabled = classLazyEnabled;
    }

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
