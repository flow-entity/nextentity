package io.github.nextentity.spring;

import io.github.nextentity.api.EntityFetcher;
import io.github.nextentity.api.ExtensionRegistry;
import io.github.nextentity.core.EntityTemplate;
import io.github.nextentity.core.EntityTemplateFactory;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.jdbc.SqlDialect;
import io.github.nextentity.plugin.DefaultExtensionRegistry;
import io.github.nextentity.plugin.EntityReferencePlugin;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

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
/// 用户可以通过定义自己的 EntityContext Bean 来覆盖默认配置：
/// ```java
/// @Bean
/// public EntityContext customEntityContext(JdbcTemplate jdbcTemplate) {
///     return DefaultEntityContext.jdbc(jdbcTemplate);
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
    protected <T, ID> Repository<T, ID> genericRepository(InjectionPoint injectionPoint, EntityTemplateFactory factory) {
        return new GenericRepository<>(factory, injectionPoint);
    }

    /// 创建 EntityContext Bean。
    ///
    /// 自动检测是否存在 EntityManager 来决定使用 JPA 还是 JDBC 模式。
    /// 如果用户已定义 EntityContext Bean，则跳过此配置。
    ///
    /// @param jdbcTemplate           Spring JDBC 模板（必需）
    /// @param entityManagerProvider  JPA 实体管理器提供者（可选）
    /// @param properties             NextEntity 配置属性
    /// @return EntityContext 实例
    @Bean
    @ConditionalOnMissingBean
    public EntityTemplateFactory entityContext(JdbcTemplate jdbcTemplate,
                                               ObjectProvider<EntityManager> entityManagerProvider,
                                               NextEntityProperties properties,
                                               TransactionTemplate transactionTemplate,
                                               ExtensionRegistry extensionRegistry) {
        SqlDialect dialect = resolveDialect(jdbcTemplate, properties.getJdbc().getDialect());
        EntityManager entityManager = entityManagerProvider.getIfAvailable();

        if (entityManager != null) {
            return EntityFactoryBuilder.jpa(entityManager, jdbcTemplate, dialect, properties, transactionTemplate, extensionRegistry);
        }
        return EntityFactoryBuilder.jdbc(jdbcTemplate, dialect, properties, transactionTemplate, extensionRegistry);
    }

    /// 创建 ExtensionRegistry Bean。
    ///
    /// 扩展点注册中心，管理投影字段处理器和实体获取器。
    /// 自动注册 EntityReferencePlugin。
    ///
    /// @return ExtensionRegistry 实例
    @Bean
    @ConditionalOnMissingBean
    public ExtensionRegistry extensionRegistry() {
        DefaultExtensionRegistry registry = new DefaultExtensionRegistry();
        // 自动注册 EntityReference 延迟加载插件
        registry.registerHandler(new EntityReferencePlugin());
        return registry;
    }

    /// 创建 EntityFetcher Bean。
    ///
    /// 实体获取器，用于 EntityReference 延迟加载。
    /// 基于 EntityTemplateFactory 创建。
    ///
    /// @param factory EntityTemplateFactory
    /// @return EntityFetcher 实例
    @Bean
    @ConditionalOnMissingBean
    public EntityFetcher entityFetcher(EntityTemplateFactory factory) {
        return new RepositoryEntityFetcher(factory);
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
            throw new IllegalArgumentException(
                    "Failed to instantiate SqlDialect: " + className, e);
        }
    }

}