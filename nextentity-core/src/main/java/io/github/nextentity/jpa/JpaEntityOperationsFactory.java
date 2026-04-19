package io.github.nextentity.jpa;

import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.core.*;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.configuration.QueryConfiguration;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodelResolver;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcConfig;
import io.github.nextentity.jdbc.JdbcQueryExecutor;
import io.github.nextentity.jdbc.JdbcResultCollector;
import io.github.nextentity.jdbc.QuerySqlBuilder;
import io.github.nextentity.jdbc.SqlBuilder;
import io.github.nextentity.jpa.configuration.JpaEntityOperationsConfiguration;
import org.jspecify.annotations.NonNull;

/// JPA 实体操作工厂
///
/// 通过 JpaEntityOperationsConfiguration 创建配置好的 EntityOperations 实例。
/// 内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public class JpaEntityOperationsFactory implements EntityOperationsFactory {

    private final DefaultMetamodel metamodel;
    private final QueryExecutor queryExecutor;
    private final PersistExecutor persistExecutor;
    private final PaginationConfig paginationConfig;
    private final JpaEntityOperationsConfiguration config;

    /// 构造 JPA 实体操作工厂
    ///
    /// @param config JPA 实体操作配置
    public JpaEntityOperationsFactory(@NonNull JpaEntityOperationsConfiguration config) {
        this.config = config;
        // 内部创建 DefaultMetamodel，使用默认的 DefaultMetamodelResolver
        this.metamodel = DefaultMetamodel.of(DefaultMetamodelResolver.of());
        this.queryExecutor = createQueryExecutor(config);
        this.persistExecutor = createPersistExecutor(config);
        this.paginationConfig = config.queryConfiguration().paginationConfig();
    }

    @Override
    public <T> EntityTemplate<T> operations(@NonNull Class<T> entityType) {
        EntityTemplateDescriptor<T> descriptor = new EntityTemplateDescriptor<>(
                persistExecutor,
                queryExecutor,
                paginationConfig,
                metamodel,
                metamodel.getEntity(entityType),
                entityType
        );
        return new EntityTemplate<>(descriptor);
    }

    /// 创建查询执行器
    private QueryExecutor createQueryExecutor(JpaEntityOperationsConfiguration config) {
        QuerySqlBuilder sqlBuilder = SqlBuilder.of(config.sqlDialect());

        // 获取或创建 ConnectionProvider
        ConnectionProvider connectionProvider = config.connectionProvider();
        if (connectionProvider == null) {
            // 从 EntityManager 自动创建
            connectionProvider = new EntityManagerConnectionProvider(config.entityManager());
        }

        JdbcConfig jdbcConfig = config.jdbcConfig();

        // 使用 JdbcQueryExecutor 作为原生查询执行器
        QueryExecutor nativeQueryExecutor = new JdbcQueryExecutor(
                metamodel,
                sqlBuilder,
                connectionProvider,
                new JdbcResultCollector(),
                jdbcConfig
        );

        // 创建主查询执行器
        return new JpaQueryExecutor(
                config.entityManager(),
                metamodel,
                nativeQueryExecutor,
                config.jpaConfig()
        );
    }

    /// 创建持久化执行器
    private PersistExecutor createPersistExecutor(JpaEntityOperationsConfiguration config) {
        PersistExecutor executor = new JpaPersistExecutor(config.entityManager());

        PersistConfiguration persistConfiguration = config.persistConfiguration();
        if (persistConfiguration != null) {
            var postProcessors = persistConfiguration.getPostProcessors();
            for (var processor : postProcessors) {
                executor = processor.process(executor);
            }
        }
        return executor;
    }

    public JpaEntityOperationsConfiguration getConfig() {
        return config;
    }
}