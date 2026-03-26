package io.github.nextentity.integration.config;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;

/**
 * Database configuration for integration tests.
 * Encapsulates DataSource, QueryExecutor, UpdateExecutor, and test data.
 *
 * @author HuangChengwei
 */
public class IntegrationTestContextImpl implements IntegrationTestContext {

    private final DataSource dataSource;
    private final Metamodel metamodel;
    private final QueryExecutor queryExecutor;
    private final UpdateExecutor updateExecutor;
    private final String dialect;
    private final String implType;
    private final ContainerContext containerContext;

    public IntegrationTestContextImpl(ContainerContext containerContext,
                                      DataSource dataSource,
                                      Metamodel metamodel,
                                      QueryExecutor queryExecutor,
                                      UpdateExecutor updateExecutor,
                                      String dialect,
                                      String implType) {
        this.containerContext = containerContext;
        this.dataSource = dataSource;
        this.metamodel = metamodel;
        this.queryExecutor = queryExecutor;
        this.updateExecutor = updateExecutor;
        this.dialect = dialect;
        this.implType = implType;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    private Metamodel getMetamodel() {
        return metamodel;
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    @Override
    public UpdateExecutor getUpdateExecutor() {
        return updateExecutor;
    }

    public String getDialect() {
        return dialect;
    }

    public @NonNull IntegrationTestContext reset() {
        containerContext.reset(this);
        return this;
    }

    @Override
    public String toString() {
        return dialect + "-" + implType;
    }
}
