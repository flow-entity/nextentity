package io.github.nextentity.spring;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/// NextEntity 自动配置类。
///
/// 当 Spring Boot 应用引入 nextentity-spring 依赖时，
/// 自动注册 NextEntityFactory Bean，无需手动配置。
///
/// 该配置会根据运行环境自动选择模式：
/// - 如果存在 EntityManager Bean，使用 JPA 模式
/// - 否则使用纯 JDBC 模式
///
/// 用户可以通过定义自己的 NextEntityFactory Bean 来覆盖默认配置：
/// ```java
/// @Bean
/// public NextEntityFactory customNextEntityFactory(JdbcTemplate jdbcTemplate) {
///     return DefaultNextEntityFactory.jdbc(jdbcTemplate);
/// }
/// ```
///
/// @author HuangChengwei
/// @since 2.0.0
@AutoConfiguration
public class NextEntityAutoConfiguration {

    /// 创建 NextEntityFactory Bean。
    ///
    /// 自动检测是否存在 EntityManager 来决定使用 JPA 还是 JDBC 模式。
    /// 如果用户已定义 NextEntityFactory Bean，则跳过此配置。
    ///
    /// @param jdbcTemplate           Spring JDBC 模板（必需）
    /// @param entityManagerProvider  JPA 实体管理器提供者（可选）
    /// @return NextEntityFactory 实例
    @Bean
    @ConditionalOnMissingBean
    public NextEntityFactory nextEntityFactory(JdbcTemplate jdbcTemplate,
                                               ObjectProvider<EntityManager> entityManagerProvider) {
        EntityManager entityManager = entityManagerProvider.getIfAvailable();
        if (entityManager != null) {
            return DefaultNextEntityFactory.jpa(entityManager, jdbcTemplate);
        }
        return DefaultNextEntityFactory.jdbc(jdbcTemplate);
    }
}
