package io.github.nextentity.jpa;

import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.core.*;
import io.github.nextentity.core.configuration.PersistConfiguration;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodelResolver;
import io.github.nextentity.jpa.configuration.JpaEntityOperationsConfiguration;
import io.github.nextentity.jdbc.QuerySqlBuilder;
import io.github.nextentity.jdbc.SqlBuilder;
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
        // 使用 SqlBuilder 创建原生 SQL 构建器
        QuerySqlBuilder sqlBuilder = SqlBuilder.of(config.sqlDialect());

        // 创建原生查询执行器（用于子查询）
        QueryExecutor nativeQueryExecutor = new JpaNativeQueryExecutor(
                sqlBuilder,
                config.entityManager(),
                metamodel
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