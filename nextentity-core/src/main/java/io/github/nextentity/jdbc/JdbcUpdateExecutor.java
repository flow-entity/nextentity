package io.github.nextentity.jdbc;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.UpdateSetStep;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.OptimisticLockException;
import io.github.nextentity.core.exception.SqlException;
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
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

///
/// JDBC更新执行器实现
///
/// 该类负责通过JDBC执行数据库更新操作，包括插入、更新、删除等操作。
/// 它实现了UpdateExecutor接口，提供了基于JDBC的更新能力。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class JdbcUpdateExecutor implements UpdateExecutor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcUpdateExecutor.class);
    private final JdbcUpdateSqlBuilder sqlBuilder;
    private final ConnectionProvider connectionProvider;
    private final Metamodel metamodel;
    private final SqlDialect sqlDialect;

    /// 构造JDBC更新执行器
    ///
    /// @param sqlBuilder 更新SQL构建器，用于生成更新相关的SQL语句
    /// @param connectionProvider 连接提供者，用于获取数据库连接
    /// @param metamodel 元模型，用于提供实体元数据信息
    public JdbcUpdateExecutor(JdbcUpdateSqlBuilder sqlBuilder, ConnectionProvider connectionProvider, Metamodel metamodel) {
        this(sqlBuilder, connectionProvider, metamodel, SqlDialect.MYSQL);
    }

    /// 构造JDBC更新执行器
    ///
    /// @param sqlBuilder 更新SQL构建器，用于生成更新相关的SQL语句
    /// @param connectionProvider 连接提供者，用于获取数据库连接
    /// @param metamodel 元模型，用于提供实体元数据信息
    /// @param sqlDialect SQL方言，用于生成条件更新/删除的SQL
    public JdbcUpdateExecutor(JdbcUpdateSqlBuilder sqlBuilder, ConnectionProvider connectionProvider, Metamodel metamodel, SqlDialect sqlDialect) {
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.metamodel = metamodel;
        this.sqlDialect = sqlDialect;
    }

    /// 插入所有实体
    ///
    /// @param <T> 实体类型
    /// @param entities 实体集合
    /// @param entityClass 实体类类型
    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        EntityType entity = metamodel.getEntity(entityClass);
        EntityAttribute version = entity.version();
        List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(entities, entity);
        execute(connection -> {
            for (InsertSqlStatement statement : statements) {
                doInsert(entity, connection, statement);
            }
            return null;
        });
        for (T e : entities) {
            if (version != null) {
                Object versionValue = version.get(e);
                if (versionValue == null) {
                    setNewVersion(e, version);
                }
            }
        }
    }

    /// 更新所有实体
    ///
    /// @param <T> 实体类型
    /// @param entities 实体集合
    /// @param entityClass 实体类类型
    @Override
    public <T> void updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass) {
        boolean excludeNull = false;
        updateAll(entities, entityClass, excludeNull);
    }

    /// 更新所有实体
    ///
    /// @param <T> 实体类型
    /// @param entities 实体集合
    /// @param entityClass 实体类类型
    /// @param excludeNull 是否排除空值
    /// @return 更新后的实体列表
    protected <T> @NonNull List<@NonNull T> updateAll(@NonNull Iterable<T> entities, @NonNull Class<T> entityClass, boolean excludeNull) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return list;
        }
        EntityType entityType = metamodel.getEntity(entityClass);
        BatchSqlStatement sql = sqlBuilder.buildUpdateStatement(entities, entityType, excludeNull);
        return execute(connection -> {
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
                return list;
            }
        });
    }

    /// 删除所有实体
    ///
    /// @param <T> 实体类型
    /// @param entities 实体集合
    /// @param entityType 实体类类型
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
                for (int updated : result) {
                    if (updated != 1) {
                        throw new IllegalStateException("ID does not exist or is deleted repeatedly");
                    }
                }
                return null;
            }
        });
    }

    /// 在事务中执行命令
    ///
    /// @param command 要执行的命令
    @Override
    public void doInTransaction(Runnable command) {
        try {
            connectionProvider.executeInTransaction(connection -> {
                command.run();
                return null;
            });
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

    /// 在事务中执行带返回值的命令
    ///
    /// @param <T> 返回值类型
    /// @param command 要执行的命令
    /// @return 命令执行结果
    @Override
    public <T> T doInTransaction(Supplier<T> command) {
        try {
            return connectionProvider.executeInTransaction(connection -> command.get());
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

    /// 创建条件更新构建器
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 条件更新构建器实例
    @Override
    public <T> UpdateSetStep<T> update(@NonNull Class<T> entityType) {
        return new JdbcUpdateWhereStep<>(entityType, metamodel, connectionProvider, sqlDialect);
    }

    /// 创建条件删除构建器
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 条件删除构建器实例
    @Override
    public <T> DeleteWhereStep<T> delete(@NonNull Class<T> entityType) {
        return new JdbcDeleteWhereStep<>(entityType, metamodel, connectionProvider, sqlDialect);
    }

    /// 设置新版本号
    ///
    /// @param entity 实体对象
    /// @param attribute 版本属性
    protected static void setNewVersion(Object entity, EntityAttribute attribute) {
        Object version = attribute.getDatabaseValue(entity);
        Class<?> type = attribute.type();
        if (type == Integer.class || type == int.class) {
            version = version == null ? 0 : (Integer) version + 1;
        } else if (type == Long.class || type == long.class) {
            version = version == null ? 0L : (Long) version + 1;
        } else {
            throw new IllegalStateException();
        }
        attribute.setByDatabaseValue(entity, version);
    }

    /// 执行插入操作
    ///
    /// @param entityType 实体类型
    /// @param connection 数据库连接
    /// @param insertStatement 插入SQL语句
    /// @throws SQLException SQL异常
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

    /// 执行更新操作
    ///
    /// @param statement 预编译语句
    /// @param parameters 参数集合
    /// @return 更新行数数组
    /// @throws SQLException SQL异常
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

    /// 设置参数
    ///
    /// @param statement 预编译语句
    /// @param parameters 参数集合
    /// @throws SQLException SQL异常
    protected static void setParameters(PreparedStatement statement, Iterable<?> parameters) throws SQLException {
        JdbcUtil.setParameters(statement, parameters);
    }

    /// 执行数据库操作
    ///
    /// @param action 数据库操作回调
    /// @return 操作结果
    protected <T> T execute(ConnectionCallback<T> action) {
        try {
            return connectionProvider.executeInTransaction(action);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

}
