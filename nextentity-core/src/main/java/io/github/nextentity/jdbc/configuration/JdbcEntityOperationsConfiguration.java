package io.github.nextentity.jdbc.configuration;

import io.github.nextentity.core.configuration.EntityOperationsConfiguration;
import io.github.nextentity.core.configuration.MetamodelConfiguration;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.configuration.QueryConfiguration;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.ResultInterceptor;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcConfig;
import io.github.nextentity.jdbc.SqlDialect;
import org.jspecify.annotations.NonNull;

import java.util.List;

/// JDBC 实体操作配置类
///
/// 定义 JDBC 实体操作所需的所有配置项。
/// 继承 EntityOperationsConfiguration 基类，添加 JDBC 特定配置。
/// 工厂内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public abstract class JdbcEntityOperationsConfiguration extends EntityOperationsConfiguration {

    protected final ConnectionProvider connectionProvider;
    protected final JdbcConfig jdbcConfig;

    /// 构造 JDBC 实体操作配置
    ///
    /// @param sqlDialect          SQL 方言（必填）
    /// @param connectionProvider   数据库连接提供者（必填）
    /// @param jdbcConfig           JDBC 配置（可为 null，使用默认值）
    /// @param persistConfiguration 持久化配置（可为 null）
    /// @param queryConfiguration  查询配置（可为 null，使用默认值）
    /// @param metamodelConfiguration 元模型配置（可为 null，使用默认值）
    /// @param constructInterceptors 构造拦截器列表（可为 null）
    /// @param resultInterceptors 结果拦截器列表（可为 null）
    protected JdbcEntityOperationsConfiguration(
            @NonNull SqlDialect sqlDialect,
            @NonNull ConnectionProvider connectionProvider,
            JdbcConfig jdbcConfig,
            PersistConfiguration persistConfiguration,
            QueryConfiguration queryConfiguration,
            MetamodelConfiguration metamodelConfiguration,
            List<ConstructInterceptor> constructInterceptors,
            List<ResultInterceptor> resultInterceptors) {
        super(sqlDialect, persistConfiguration, queryConfiguration, metamodelConfiguration,
              constructInterceptors, resultInterceptors);
        this.connectionProvider = connectionProvider;
        this.jdbcConfig = jdbcConfig != null ? jdbcConfig : JdbcConfig.DEFAULT;
    }

    /// 数据库连接提供者
    ///
    /// @return Connection Provider 实例
    public ConnectionProvider connectionProvider() {
        return connectionProvider;
    }

    /// JDBC 配置
    ///
    /// @return JDBC 配置实例
    public JdbcConfig jdbcConfig() {
        return jdbcConfig;
    }

    /// 创建构建器
    public static Builder builder() {
        return new Builder();
    }

    /// JDBC 配置构建器
    public static final class Builder
            extends EntityOperationsConfiguration.Builder<Builder, SimpleJdbcConfiguration> {

        private ConnectionProvider connectionProvider;
        private JdbcConfig jdbcConfig = JdbcConfig.DEFAULT;

        /// 设置数据库连接提供者
        public Builder connectionProvider(@NonNull ConnectionProvider connectionProvider) {
            this.connectionProvider = connectionProvider;
            return this;
        }

        /// 设置 JDBC 配置
        public Builder jdbcConfig(JdbcConfig jdbcConfig) {
            this.jdbcConfig = jdbcConfig;
            return this;
        }

        /// 构建 SimpleJdbcConfiguration 实例
        @Override
        public SimpleJdbcConfiguration build() {
            validateBaseFields();
            if (connectionProvider == null) {
                throw new IllegalStateException("connectionProvider must be set");
            }
            return new SimpleJdbcConfiguration(
                    sqlDialect,
                    connectionProvider,
                    jdbcConfig,
                    persistConfiguration,
                    queryConfiguration,
                    metamodelConfiguration,
                    constructInterceptors,
                    resultInterceptors);
        }
    }

    /// 简单 JDBC 配置实现类
    ///
    /// 用于一般场景，无需继承自定义。
    public static final class SimpleJdbcConfiguration extends JdbcEntityOperationsConfiguration {

        SimpleJdbcConfiguration(
                SqlDialect sqlDialect,
                ConnectionProvider connectionProvider,
                JdbcConfig jdbcConfig,
                PersistConfiguration persistConfiguration,
                QueryConfiguration queryConfiguration,
                MetamodelConfiguration metamodelConfiguration,
                List<ConstructInterceptor> constructInterceptors,
                List<ResultInterceptor> resultInterceptors) {
            super(sqlDialect, connectionProvider, jdbcConfig, persistConfiguration, queryConfiguration,
                  metamodelConfiguration, constructInterceptors, resultInterceptors);
        }
    }
}