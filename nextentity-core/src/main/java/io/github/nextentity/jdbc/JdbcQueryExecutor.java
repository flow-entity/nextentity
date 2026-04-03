package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.exception.TransactionRequiredException;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.Metamodel;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

///
/// JDBC查询执行器实现
///
/// 该类负责通过JDBC执行数据库查询操作，包括SQL语句构建、参数设置、结果集处理等功能。
/// 它实现了QueryExecutor接口，提供了基于JDBC的查询能力。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class JdbcQueryExecutor implements QueryExecutor {

    @NonNull
    private final Metamodel metamodel;
    @NonNull
    private final QuerySqlBuilder sqlBuilder;
    @NonNull
    private final ConnectionProvider connectionProvider;
    @NonNull
    private final ResultCollector collector;

    /// 构造JDBC查询执行器
    ///
    /// @param metamodel 元模型，用于提供实体元数据信息
    /// @param sqlBuilder SQL构建器，用于生成SQL语句
    /// @param connectionProvider 连接提供者，用于获取数据库连接
    /// @param collector 结果收集器，用于处理查询结果
    public JdbcQueryExecutor(@NonNull Metamodel metamodel,
                             @NonNull QuerySqlBuilder sqlBuilder,
                             @NonNull ConnectionProvider connectionProvider,
                             @NonNull ResultCollector collector) {
        this.metamodel = metamodel;
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.collector = collector;
    }

    /// 执行查询并返回结果列表
    ///
    /// @param <R> 查询结果类型
    /// @param queryStructure 查询结构，包含查询的所有相关信息
    /// @return 查询结果列表
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

    /// 查询SQL构建器接口
    ///
    /// 用于构建查询相关的SQL语句
    public interface QuerySqlBuilder {
        /// 构建查询SQL语句
        ///
        /// @param context 查询上下文
        /// @return 查询SQL语句对象
        QuerySqlStatement build(QueryContext context);
    }


    /// 结果收集器接口
    ///
    /// 用于处理从数据库查询返回的结果集
    public interface ResultCollector {
        /// 解析结果集
        ///
        /// @param <T> 结果类型
        /// @param resultSet 结果集
        /// @param context 查询上下文
        /// @return 解析后的结果列表
        /// @throws SQLException SQL异常
        <T> List<T> resolve(ResultSet resultSet, QueryContext context) throws SQLException;
    }
}

