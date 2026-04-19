package io.github.nextentity.jdbc;

import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.core.*;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodelResolver;
import io.github.nextentity.jdbc.JdbcQueryExecutor.ResultCollector;
import io.github.nextentity.jdbc.configuration.JdbcEntityOperationsConfiguration;
import org.jspecify.annotations.NonNull;

/// JDBC 实体操作工厂
///
/// 通过 EntityOperationsConfiguration 创建配置好的 EntityOperations 实例。
/// 内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public class JdbcEntityOperationsFactory implements EntityOperationsFactory {

    private final DefaultMetamodel metamodel;
    private final QueryExecutor queryExecutor;
    private final PersistExecutor persistExecutor;
    private final PaginationConfig paginationConfig;
    private final JdbcEntityOperationsConfiguration config;

    /// 构造 JDBC 实体操作工厂
    ///
    /// @param config JDBC 实体操作配置
    public JdbcEntityOperationsFactory(@NonNull JdbcEntityOperationsConfiguration config) {
        this.config = config;
        // 内部创建 DefaultMetamodel，使用配置中的 metamodelConfiguration
        this.metamodel = DefaultMetamodel.of(config.metamodelConfiguration());
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
    private QueryExecutor createQueryExecutor(JdbcEntityOperationsConfiguration config) {
        QuerySqlBuilder sqlBuilder = SqlBuilder.of(config.sqlDialect());
        ResultCollector collector = new JdbcResultCollector();
        JdbcConfig jdbcConfig = config.jdbcConfig();
        if (jdbcConfig == null) {
            jdbcConfig = JdbcConfig.DEFAULT;
        }
        // 创建拦截器选择器
        InterceptorSelector<ConstructInterceptor> interceptorSelector = new InterceptorSelector<>(
                config.constructInterceptors());
        return new JdbcQueryExecutor(
                metamodel,
                sqlBuilder,
                config.connectionProvider(),
                collector,
                jdbcConfig,
                interceptorSelector
        );
    }

    /// 创建持久化执行器
    private PersistExecutor createPersistExecutor(JdbcEntityOperationsConfiguration config) {
        var sqlBuilder = SqlBuilder.of(config.sqlDialect());
        PersistExecutor executor = new JdbcPersistExecutor(
                sqlBuilder,
                config.connectionProvider(),
                JdbcConfig.DEFAULT
        );
        var persistConfiguration = config.persistConfiguration();
        if (persistConfiguration != null) {
            var postProcessors = persistConfiguration.getPostProcessors();
            for (var processor : postProcessors) {
                executor = processor.process(executor);
            }
        }
        return executor;
    }

    public JdbcEntityOperationsConfiguration getConfig() {
        return config;
    }
}
