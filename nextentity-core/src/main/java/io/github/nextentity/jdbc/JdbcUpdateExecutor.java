package io.github.nextentity.jdbc;

import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.OptimisticLockException;
import io.github.nextentity.core.exception.TransactionRequiredException;
import io.github.nextentity.core.exception.UncheckedSQLException;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.jdbc.ConnectionProvider.ConnectionCallback;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JdbcUpdateExecutor implements UpdateExecutor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcUpdateExecutor.class);
    private final JdbcUpdateSqlBuilder sqlBuilder;
    private final ConnectionProvider connectionProvider;
    private final Metamodel metamodel;

    public JdbcUpdateExecutor(JdbcUpdateSqlBuilder sqlBuilder, ConnectionProvider connectionProvider, Metamodel metamodel) {
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.metamodel = metamodel;
    }

    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        EntityType entity = metamodel.getEntity(entityClass);
        List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(entities, entity);
        execute(connection -> {
            for (InsertSqlStatement statement : statements) {
                doInsert(entity, connection, statement);
            }
            return null;
        });
    }

    @Override
    public <T> List<T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass) {
        boolean excludeNull = false;
        return updateAll(entities, entityClass, excludeNull);
    }

    protected <T> @NonNull List<@NonNull T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass, boolean excludeNull) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return list;
        }
        EntityType entityType = metamodel.getEntity(entityClass);
        BatchSqlStatement sql = sqlBuilder.buildUpdateStatement(entities, entityType, excludeNull);
        execute(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                int[] updateRowCounts = executeUpdate(statement, sql.parameters());
                EntityAttribute version = entityType.version();
                boolean hasVersion = version != null;
                for (int rowCount : updateRowCounts) {
                    if (rowCount != 1) {
                        if (hasVersion) {
                            throw new OptimisticLockException("id not found or concurrent modified");
                        } else {
                            throw new IllegalStateException("id not found");
                        }
                    }
                }
                if (hasVersion) {
                    for (Object entity : list) {
                        setNewVersion(entity, version);
                    }
                }
                return null;
            }
        });
        return list;
    }

    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityType) {
        if (!entities.iterator().hasNext()) {
            return;
        }
        BatchSqlStatement sql = sqlBuilder.buildDeleteStatement(entities, metamodel.getEntity(entityType));
        execute(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                int[] result = executeUpdate(statement, sql.parameters());
                log.trace("executeBatch result: {}", Arrays.toString(result));
                return null;
            }
        });
    }

    @Override
    public <T> T patch(@NonNull T entity, @NonNull Class<T> entityClass) {
        return updateAll(Collections.singletonList(entity), entityClass, true).get(0);
    }

    protected static void setNewVersion(Object entity, EntityAttribute attribute) {
        Object version = attribute.getDatabaseValue(entity);
        if (version instanceof Integer) {
            version = (Integer) version + 1;
        } else if (version instanceof Long) {
            version = (Long) version + 1;
        } else {
            throw new IllegalStateException();
        }
        attribute.setByDatabaseValue(entity, version);
    }

    protected void doInsert(EntitySchema entityType, Connection connection, InsertSqlStatement insertStatement) throws SQLException {
        insertStatement.debug();
        boolean generateKey = insertStatement.returnGeneratedKeys();
        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement statement = generateKey
                ? connection.prepareStatement(insertStatement.sql(), Statement.RETURN_GENERATED_KEYS)
                : connection.prepareStatement(insertStatement.sql())) {
            executeUpdate(statement, insertStatement.parameters());
            if (generateKey) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    Iterator<?> iterator = insertStatement.entities().iterator();
                    while (keys.next()) {
                        Object entity = iterator.next();
                        EntityAttribute idField = entityType.id();
                        Object key = JdbcUtil.getValue(keys, 1, idField.valueConvertor());
                        idField.set(entity, key);
                    }
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
        }
    }

    protected int[] executeUpdate(PreparedStatement statement, Iterable<? extends Iterable<?>> parameters) throws SQLException {
        Iterator<? extends Iterable<?>> iterator = parameters.iterator();
        if (iterator.hasNext()) {
            setParameters(statement, iterator.next());
        }
        boolean batch = iterator.hasNext();
        while (iterator.hasNext()) {
            statement.addBatch();
            setParameters(statement, iterator.next());
        }
        if (batch) {
            statement.addBatch();
            return statement.executeBatch();
        } else {
            return new int[]{statement.executeUpdate()};
        }
    }

    protected static void setParameters(PreparedStatement statement, Iterable<?> parameters) throws SQLException {
        JdbcUtil.setParameters(statement, parameters);
    }

    protected <T> void execute(ConnectionCallback<T> action) {
        try {
            connectionProvider.execute(connection -> {
                if (connection.getAutoCommit()) {
                    throw new TransactionRequiredException();
                }
                return action.doInConnection(connection);
            });
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

}
