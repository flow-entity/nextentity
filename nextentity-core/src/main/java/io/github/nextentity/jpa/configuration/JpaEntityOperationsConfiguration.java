package io.github.nextentity.jpa.configuration;

import io.github.nextentity.core.configuration.EntityOperationsConfiguration;
import io.github.nextentity.core.configuration.MetamodelConfiguration;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.configuration.QueryConfiguration;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcConfig;
import io.github.nextentity.jdbc.SqlDialect;
import io.github.nextentity.jpa.JpaConfig;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;

/// JPA 实体操作配置类
///
/// 定义 JPA 实体操作所需的所有配置项。
/// 继承 EntityOperationsConfiguration 基类，添加 JPA 特定配置。
/// 工厂内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public abstract class JpaEntityOperationsConfiguration extends EntityOperationsConfiguration {

    protected final EntityManager entityManager;
    protected final JpaConfig jpaConfig;
    protected final ConnectionProvider connectionProvider;  // 可选，用于原生 JDBC 查询
    protected final JdbcConfig jdbcConfig;  // 可选，用于 JDBC 查询配置

    /// 构造 JPA 实体操作配置
    ///
    /// @param sqlDialect          SQL 方言（必填，用于原生查询）
    /// @param entityManager       Entity Manager 实例（必填）
    /// @param jpaConfig           JPA 配置（可为 null，使用默认值）
    /// @param connectionProvider  Connection Provider（可选，用于原生 JDBC 查询）
    /// @param jdbcConfig          JDBC 配置（可选，用于原生 JDBC 查询）
    /// @param persistConfiguration 持久化配置（可为 null）
    /// @param queryConfiguration  查询配置（可为 null，使用默认值）
    /// @param metamodelConfiguration 元模型配置（可为 null，使用默认值）
    protected JpaEntityOperationsConfiguration(
            @NonNull SqlDialect sqlDialect,
            @NonNull EntityManager entityManager,
            JpaConfig jpaConfig,
            ConnectionProvider connectionProvider,
            JdbcConfig jdbcConfig,
            PersistConfiguration persistConfiguration,
            QueryConfiguration queryConfiguration,
            MetamodelConfiguration metamodelConfiguration) {
        super(sqlDialect, persistConfiguration, queryConfiguration, metamodelConfiguration);
        this.entityManager = entityManager;
        this.jpaConfig = jpaConfig != null ? jpaConfig : JpaConfig.DEFAULT;
        this.connectionProvider = connectionProvider;
        this.jdbcConfig = jdbcConfig != null ? jdbcConfig : JdbcConfig.DEFAULT;
    }

    /// 获取 EntityManager
    ///
    /// @return Entity Manager 实例
    public EntityManager entityManager() {
        return entityManager;
    }

    /// 获取 JPA 配置
    ///
    /// @return JPA 配置实例
    public JpaConfig jpaConfig() {
        return jpaConfig;
    }

    /// 获取 ConnectionProvider
    ///
    /// @return Connection Provider 实例，可为 null
    public ConnectionProvider connectionProvider() {
        return connectionProvider;
    }

    /// 获取 JDBC 配置
    ///
    /// @return JDBC 配置实例
    public JdbcConfig jdbcConfig() {
        return jdbcConfig;
    }

    /// 创建构建器
    public static Builder builder() {
        return new Builder();
    }

    /// JPA 配置构建器
    public static final class Builder
            extends EntityOperationsConfiguration.Builder<Builder, SimpleJpaConfiguration> {

        private EntityManager entityManager;
        private JpaConfig jpaConfig = JpaConfig.DEFAULT;
        private ConnectionProvider connectionProvider;
        private JdbcConfig jdbcConfig = JdbcConfig.DEFAULT;

        /// 设置 EntityManager
        public Builder entityManager(@NonNull EntityManager entityManager) {
            this.entityManager = entityManager;
            return this;
        }

        /// 设置 JPA 配置
        public Builder jpaConfig(JpaConfig jpaConfig) {
            this.jpaConfig = jpaConfig;
            return this;
        }

        /// 设置 ConnectionProvider（可选，用于原生 JDBC 查询）
        public Builder connectionProvider(ConnectionProvider connectionProvider) {
            this.connectionProvider = connectionProvider;
            return this;
        }

        /// 设置 JDBC 配置（可选，用于原生 JDBC 查询）
        public Builder jdbcConfig(JdbcConfig jdbcConfig) {
            this.jdbcConfig = jdbcConfig;
            return this;
        }

        /// 构建 SimpleJpaConfiguration 实例
        @Override
        public SimpleJpaConfiguration build() {
            validateBaseFields();
            if (entityManager == null) {
                throw new IllegalStateException("entityManager must be set");
            }
            return new SimpleJpaConfiguration(
                    sqlDialect,
                    entityManager,
                    jpaConfig,
                    connectionProvider,
                    jdbcConfig,
                    persistConfiguration,
                    queryConfiguration,
                    metamodelConfiguration);
        }
    }

    /// 简单 JPA 配置实现类
    ///
    /// 用于一般场景，无需继承自定义。
    public static final class SimpleJpaConfiguration extends JpaEntityOperationsConfiguration {

        SimpleJpaConfiguration(
                SqlDialect sqlDialect,
                EntityManager entityManager,
                JpaConfig jpaConfig,
                ConnectionProvider connectionProvider,
                JdbcConfig jdbcConfig,
                PersistConfiguration persistConfiguration,
                QueryConfiguration queryConfiguration,
                MetamodelConfiguration metamodelConfiguration) {
            super(sqlDialect, entityManager, jpaConfig, connectionProvider, jdbcConfig,
                  persistConfiguration, queryConfiguration, metamodelConfiguration);
        }
    }
}