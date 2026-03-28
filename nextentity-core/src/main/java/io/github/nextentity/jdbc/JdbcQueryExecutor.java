package io.github.nextentity.jdbc;

import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.exception.TransactionRequiredException;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcQueryExecutor implements QueryExecutor {

    @NonNull
    private final Metamodel metamodel;
    @NonNull
    private final QuerySqlBuilder sqlBuilder;
    @NonNull
    private final ConnectionProvider connectionProvider;
    @NonNull
    private final ResultCollector collector;

    public JdbcQueryExecutor(@NonNull Metamodel metamodel,
                             @NonNull QuerySqlBuilder sqlBuilder,
                             @NonNull ConnectionProvider connectionProvider,
                             @NonNull ResultCollector collector) {
        this.metamodel = metamodel;
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.collector = collector;
    }

    @Override
    @NonNull
    public <R> List<R> getList(@NonNull QueryStructure queryStructure) {
        QueryContext context = QueryContext.create(queryStructure, metamodel, true);
        QuerySqlStatement sql = sqlBuilder.build(context);
        sql.debug();
        try {
            return connectionProvider.execute(connection -> {
                LockModeType locked = queryStructure.lockType();
                if (locked != null && locked != LockModeType.NONE && connection.getAutoCommit()) {
                    throw new TransactionRequiredException();
                }
                // noinspection SqlSourceToSinkFlow
                try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                    JdbcUtil.setParameters(statement, sql.parameters());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return collector.resolve(resultSet, context);
                    }
                }
            });
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

    public interface QuerySqlBuilder {
        QuerySqlStatement build(QueryContext context);
    }


    public interface ResultCollector {
        <T> List<T> resolve(ResultSet resultSet, QueryContext context) throws SQLException;
    }
}

