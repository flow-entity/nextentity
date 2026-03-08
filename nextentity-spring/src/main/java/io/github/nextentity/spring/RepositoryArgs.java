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
import org.springframework.jdbc.core.JdbcTemplate;

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
        ConnectionProvider connectionProvider = new ConnectionProvider() {
            @Override
            public <T> T execute(ConnectionCallback<T> action) {
                return jdbcTemplate.execute(action::doInConnection);
            }
        };

        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlDialectSelector, connectionProvider, new JdbcResultCollector());
        JdbcUpdateExecutor jdbcUpdateExecutor = new JdbcUpdateExecutor(sqlDialectSelector, connectionProvider, metamodel);
        return new RepositoryArgs(
                metamodel,
                jdbcQueryExecutor,
                jdbcUpdateExecutor
        );
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
