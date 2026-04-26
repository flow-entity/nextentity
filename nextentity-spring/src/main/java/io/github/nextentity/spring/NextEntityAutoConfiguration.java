package io.github.nextentity.spring;

import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import io.github.nextentity.jdbc.SqlDialect;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/// NextEntity 自动配置类。
///
/// 当 Spring Boot 应用引入 nextentity-spring 依赖时，
/// 需要显式启用才会注册 EntityContext Bean：
///
/// ```yaml
/// nextentity:
///   enabled: true
/// ```
///
/// 该配置会根据运行环境自动选择模式：
/// - 如果存在 EntityManager Bean，使用 JPA 模式
/// - 否则使用纯 JDBC 模式
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   enabled: true
///   jdbc:
///     dialect: auto
///     query:
///       timeout: 30
///   jpa:
///     string-parameter-binding: true
/// ```
///
/// 用户可以通过定义自己的 EntityOperationsFactory Bean 来覆盖默认配置：
/// ```java
/// @Bean
/// public EntityOperationsFactory customEntityContext(JdbcTemplate jdbcTemplate) {
///     return EntityFactoryBuilder.jdbc(jdbcTemplate, template);
/// }
/// ```
///
/// @author HuangChengwei
/// @since 2.0.0
@AutoConfiguration
@ConditionalOnProperty(prefix = "nextentity", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(NextEntityProperties.class)
public class NextEntityAutoConfiguration {

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty(prefix = "nextentity", name = "generic-repository", havingValue = "true", matchIfMissing = true)
    protected <T, ID> Repository<T, ID> genericRepository(InjectionPoint injectionPoint, EntityOperationsFactory factory) {
        return new GenericRepository<>(factory, injectionPoint);
    }

    /// 创建 EntityOperationsFactory Bean。
    ///
    /// 自动检测是否存在 EntityManager 来决定使用 JPA 还是 JDBC 模式。
    /// 如果用户已定义 EntityOperationsFactory Bean，则跳过此配置。
    ///
    /// @param jdbcTemplate           Spring JDBC 模板（必需）
    /// @param entityManagerProvider  JPA 实体管理器提供者（可选）
    /// @param properties             NextEntity 配置属性
    /// @param constructInterceptorsProvider 构造拦截器提供者（可选）
    /// @return EntityOperationsFactory 实例
    @Bean
    @ConditionalOnMissingBean
    public EntityOperationsFactory entityContext(JdbcTemplate jdbcTemplate,
                                                   ObjectProvider<EntityManager> entityManagerProvider,
                                                   NextEntityProperties properties,
                                                   TransactionTemplate transactionTemplate,
                                                   ObjectProvider<ConstructInterceptor> constructInterceptorsProvider) {
        SqlDialect dialect = resolveDialect(jdbcTemplate, properties.getJdbc().getDialect());
        EntityManager entityManager = entityManagerProvider.getIfAvailable();
        List<ConstructInterceptor> constructInterceptors = constructInterceptorsProvider.stream().toList();

        if (entityManager != null) {
            return EntityFactoryBuilder.jpa(entityManager, dialect, properties, transactionTemplate,
                                            constructInterceptors);
        }
        return EntityFactoryBuilder.jdbc(jdbcTemplate, dialect, properties, transactionTemplate,
                                         constructInterceptors);
    }

    /// CGLIB 代理拦截器（处理普通类投影）
    @Bean
    @ConditionalOnMissingBean(ConstructInterceptor.class)
    public ConstructInterceptor cglibProxyInterceptor() {
        return new CglibProxyInterceptor();
    }

    /// JDK 代理拦截器（处理 interface 投影）
    @Bean
    @ConditionalOnMissingBean(name = "jdkProxyInterceptor")
    public ConstructInterceptor jdkProxyInterceptor() {
        return new JdkProxyInterceptor();
    }

    /// 解析 SQL 方言。
    ///
    /// @param jdbcTemplate  JDBC 模板
    /// @param dialectName   方言名称或实现类全名（null 表示自动检测）
    /// @return SqlDialect 实例
    private SqlDialect resolveDialect(JdbcTemplate jdbcTemplate, String dialectName) {
        if (dialectName == null || dialectName.isEmpty()) {
            return autoDetectDialect(jdbcTemplate);
        }
        // 使用全类名实例化
        return instantiateDialect(dialectName);
    }

    /// 自动检测 SQL 方言。
    private SqlDialect autoDetectDialect(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) {
            return SqlDialect.DEFAULT;
        }
        try {
            return SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            return SqlDialect.DEFAULT;
        }
    }

    /// 实例化方言实现类。
    private SqlDialect instantiateDialect(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (SqlDialect) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Failed to instantiate SqlDialect: " + className, e);
        }
    }

}