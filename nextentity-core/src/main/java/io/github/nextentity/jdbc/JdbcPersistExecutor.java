package io.github.nextentity.jdbc;

import io.github.nextentity.core.PersistDescriptor;
import io.github.nextentity.core.PersistExecutor;
import io.github.nextentity.core.exception.OptimisticLockException;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.UpdateStructure;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Iterators;
import io.github.nextentity.jdbc.ConnectionProvider.ConnectionCallback;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/// JDBC更新执行器实现
///
/// 该类负责通过JDBC执行数据库更新操作，包括插入、更新、删除等操作。
/// 它实现了UpdateExecutor接口，提供了基于JDBC的更新能力。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class JdbcPersistExecutor implements PersistExecutor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcPersistExecutor.class);
    private final JdbcUpdateSqlBuilder sqlBuilder;
    private final ConnectionProvider connectionProvider;
    private final JdbcConfig config;

    /// 构造JDBC更新执行器（使用默认配置）
    ///
    /// @param sqlBuilder         更新SQL构建器，用于生成更新相关的SQL语句
    /// @param connectionProvider 连接提供者，用于获取数据库连接
    public JdbcPersistExecutor(JdbcUpdateSqlBuilder sqlBuilder,
                               ConnectionProvider connectionProvider) {
        this(sqlBuilder, connectionProvider, JdbcConfig.DEFAULT);
    }

    /// 构造JDBC更新执行器
    ///
    /// @param sqlBuilder         更新SQL构建器，用于生成更新相关的SQL语句
    /// @param connectionProvider 连接提供者，用于获取数据库连接
    /// @param config             JDBC配置
    public JdbcPersistExecutor(JdbcUpdateSqlBuilder sqlBuilder,
                               ConnectionProvider connectionProvider,
                               JdbcConfig config) {
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.config = config;
    }

    /// 插入所有实体
    ///
    /// @param <T>        实体类型
    /// @param entities   实体集合
    /// @param descriptor 实体上下文
    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        EntityType entity = descriptor.entityType();
        var version = entity.version();
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
    /// @param <T>        实体类型
    /// @param entities   实体集合
    /// @param descriptor 实体上下文
    @Override
    public <T> void updateAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<@NonNull T> list = ImmutableList.ofIterable(entities);
        if (list.isEmpty()) {
            return;
        }
        EntityType entityType = descriptor.entityType();
        BatchSqlStatement sql = sqlBuilder.buildUpdateStatement(entities, entityType);
        execute(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                int[] updateRowCounts = executeUpdate(statement, sql.parameters());
                var version = entityType.version();
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
    /// @param <T>        实体类型
    /// @param entities   实体集合
    /// @param descriptor 实体上下文
    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        if (!entities.iterator().hasNext()) {
            return;
        }
        BatchSqlStatement sql = sqlBuilder.buildDeleteStatement(entities, descriptor.entityType());
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

    @Override
    public <T> int update(UpdateStructure structure, @NonNull PersistDescriptor<T> descriptor) {
        if (structure.setClauses().isEmpty()) {
            return 0;
        }
        EntityType entityType = descriptor.entityType();
        UpdateSqlStatement sql = sqlBuilder.buildConditionalUpdateStatement(
                entityType,
                descriptor.metamodel(),
                structure.setClauses(),
                structure.where()
        );
        return execute(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    @Override
    public <T> int delete(ExpressionNode predicate, @NonNull PersistDescriptor<T> descriptor) {
        EntityType entityType = descriptor.entityType();
        DeleteSqlStatement sql = sqlBuilder.buildConditionalDeleteStatement(
                entityType,
                descriptor.metamodel(),
                predicate
        );
        return execute(connection -> {
            sql.debug();
            //noinspection SqlSourceToSinkFlow
            try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                setParameters(statement, sql.parameters());
                return statement.executeUpdate();
            }
        });
    }

    /// 设置新版本号
    ///
    /// @param entity    实体对象
    /// @param attribute 版本属性
    protected static void setNewVersion(Object entity, EntityBasicAttribute attribute) {
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
    /// @param entityType      实体类型
    /// @param connection      数据库连接
    /// @param insertStatement 插入SQL语句
    /// @throws SQLException SQL异常
    protected void doInsert(EntitySchema entityType, Connection connection, InsertSqlStatement insertStatement) throws SQLException {
        insertStatement.debug();
        // 应用返回生成键配置
        boolean generateKey = config.returnGeneratedKeys() && insertStatement.returnGeneratedKeys();
        //noinspection SqlSourceToSinkFlow
        try (PreparedStatement statement = generateKey
                ? connection.prepareStatement(insertStatement.sql(), Statement.RETURN_GENERATED_KEYS)
                : connection.prepareStatement(insertStatement.sql())) {

            if (generateKey) {
                if (insertStatement.batchInsert()) {
                    // 批量执行插入并获取生成键
                    executeBatchInsertWithGeneratedKeys(statement, insertStatement, entityType);
                } else {
                    // 逐条执行插入并获取生成键
                    executeInsertWithGeneratedKeys(statement, insertStatement, entityType);
                }
            } else {
                // 不需要生成键时，使用批量执行提高效率
                executeUpdate(statement, insertStatement.parameters());
            }
        }
    }

    /// 执行批量插入并获取生成键（适用于支持批量 getGeneratedKeys 的数据库）
    ///
    /// @param statement       预编译语句
    /// @param insertStatement 插入SQL语句
    /// @param entityType      实体类型
    /// @throws SQLException SQL异常
    protected void executeBatchInsertWithGeneratedKeys(PreparedStatement statement,
                                                       InsertSqlStatement insertStatement,
                                                       EntitySchema entityType) throws SQLException {
        // 添加到批处理
        for (Iterable<?> params : insertStatement.parameters()) {
            setParameters(statement, params);
            statement.addBatch();
        }

        // 执行批处理
        statement.executeBatch();

        // 获取生成的键
        Iterator<?> entityIterator = insertStatement.entities().iterator();
        EntityBasicAttribute idField = entityType.id();

        try (ResultSet keys = statement.getGeneratedKeys()) {
            while (keys.next() && entityIterator.hasNext()) {
                Object entity = entityIterator.next();
                Object key = JdbcUtil.getValue(keys, 1, idField.valueConvertor());
                idField.set(entity, key);
            }
        } catch (Exception e) {
            log.warn("Failed to get generated keys from batch insert", e);
        }
    }

    /// 执行插入并获取生成键（逐条执行，适用于不支持批量 getGeneratedKeys 的数据库）
    ///
    /// @param statement       预编译语句
    /// @param insertStatement 插入SQL语句
    /// @param entityType      实体类型
    /// @throws SQLException SQL异常
    protected void executeInsertWithGeneratedKeys(PreparedStatement statement,
                                                  InsertSqlStatement insertStatement,
                                                  EntitySchema entityType) throws SQLException {
        Iterator<?> entityIterator = insertStatement.entities().iterator();
        var idField = entityType.id();

        for (Iterable<?> params : insertStatement.parameters()) {
            setParameters(statement, params);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next() && entityIterator.hasNext()) {
                    Object entity = entityIterator.next();
                    Object key = JdbcUtil.getValue(keys, 1, idField.valueConvertor());
                    idField.set(entity, key);
                }
            } catch (Exception e) {
                log.warn("Failed to get generated key", e);
            }
        }
    }

    /// 执行更新操作
    ///
    /// @param statement  预编译语句
    /// @param parameters 参数集合
    /// @return 更新行数数组
    /// @throws SQLException SQL异常
    protected int[] executeUpdate(PreparedStatement statement, Iterable<? extends Iterable<?>> parameters) throws SQLException {
        Iterator<? extends Iterable<?>> iterator = parameters.iterator();

        // 检查是否有参数
        if (!iterator.hasNext()) {
            return new int[0];
        }

        // 应用批处理配置
        boolean useBatch = config.batchEnabled();
        int batchSize = config.batchSize();

        int size = Iterators.size(parameters);
        if (!useBatch) {
            // 不使用批处理，逐条执行
            int[] results = new int[size];
            int i = 0;
            while (iterator.hasNext()) {
                setParameters(statement, iterator.next());
                results[i++] = statement.executeUpdate();
            }
            return results;
        }

        // 使用批处理
        int batchCount = 0;
        int[] results = new int[0];

        while (iterator.hasNext()) {
            setParameters(statement, iterator.next());
            statement.addBatch();
            batchCount++;

            // 达到批处理大小时执行
            if (batchCount >= batchSize || !iterator.hasNext()) {
                int[] batchResults = statement.executeBatch();
                results = mergeResults(results, batchResults);
                batchCount = 0;
            }
        }

        return results;
    }

    /// 合并批处理结果
    private int[] mergeResults(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /// 设置参数
    ///
    /// @param statement  预编译语句
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
            return connectionProvider.execute(action);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }

}
