package io.github.nextentity.spring;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.UncheckedSQLException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

public record RepositoryArgs(
        Metamodel metamodel,
        QueryExecutor queryExecutor,
        UpdateExecutor updateExecutor
) {

    public static RepositoryArgs jdbc(JdbcTemplate jdbcTemplate) {
        SqlDialectSelector sqlDialectSelector = new SqlDialectSelector();
        DataSource dataSource = jdbcTemplate.getDataSource();
        try {
            sqlDialectSelector.setByDataSource(Objects.requireNonNull(dataSource));
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();
        ConnectionProvider connectionProvider = getConnectionProvider(jdbcTemplate, dataSource);

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlDialectSelector, connectionProvider, new JdbcResultCollector());
        JdbcUpdateExecutor jdbcUpdateExecutor = new JdbcUpdateExecutor(sqlDialectSelector, connectionProvider, metamodel);
        return new RepositoryArgs(
                metamodel,
                jdbcQueryExecutor,
                jdbcUpdateExecutor
        );
    }

    private static ConnectionProvider getConnectionProvider(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        return new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }

            @Override
            public <T> T executeInTransaction(ConnectionCallback<T> action) {
                return transactionTemplate.execute(status -> execute(action));
            }
        };
    }

    public static RepositoryArgs jpa(EntityManager entityManager,
                                     JdbcTemplate jdbcTemplate) {
        SqlDialectSelector sqlDialectSelector = new SqlDialectSelector();
        DataSource dataSource = jdbcTemplate.getDataSource();
        try {
            sqlDialectSelector.setByDataSource(Objects.requireNonNull(dataSource));
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();

        ConnectionProvider connectionProvider = new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }

            @Override
            public <T> T executeInTransaction(ConnectionCallback<T> action) {
                EntityTransaction transaction = entityManager.getTransaction();
                if (transaction.isActive()) {
                    return execute(action);
                }
                transaction.begin();
                boolean rolledBack = false;
                try {
                    return execute(action);
                } catch (Throwable e) {
                    transaction.rollback();
                    rolledBack = true;
                    throw e;
                } finally {
                    if (rolledBack) {
                        transaction.rollback();
                    } else {
                        transaction.commit();
                    }
                }
            }
        };

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlDialectSelector, connectionProvider, new JdbcResultCollector());

        JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(
                entityManager, metamodel, jdbcQueryExecutor);
        JpaUpdateExecutor jpaUpdateExecutor = new JpaUpdateExecutor(entityManager);


        return new RepositoryArgs(
                metamodel,
                jpaQueryExecutor,
                jpaUpdateExecutor
        );
    }

}
