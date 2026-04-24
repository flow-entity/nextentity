package io.github.nextentity.jdbc;

import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.exception.TransactionRequiredException;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
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
    private final QuerySqlBuilder sqlBuilder;
    @NonNull
    private final ConnectionProvider connectionProvider;
    @NonNull
    private final ResultCollector collector;
    @NonNull
    private final JdbcConfig config;

    /// 构造JDBC查询执行器（含拦截器）
    ///
    /// @param metamodel           元模型，用于提供实体元数据信息
    /// @param sqlBuilder          SQL构建器，用于生成SQL语句
    /// @param connectionProvider  连接提供者，用于获取数据库连接
    /// @param collector           结果收集器，用于处理查询结果
    /// @param config              JDBC配置
    /// @param interceptorSelector 拦截器选择器
    public JdbcQueryExecutor(@NonNull Metamodel metamodel,
                             @NonNull QuerySqlBuilder sqlBuilder,
                             @NonNull ConnectionProvider connectionProvider,
                             @NonNull ResultCollector collector,
                             @NonNull JdbcConfig config,
                             @NonNull InterceptorSelector<ConstructInterceptor> interceptorSelector) {
        this.sqlBuilder = sqlBuilder;
        this.connectionProvider = connectionProvider;
        this.collector = collector;
        this.config = config;
    }

    /// 执行查询并返回结果列表
    ///
    /// @param <R>     查询结果类型
    /// @param context 查询上下文，包含查询的所有相关信息
    /// @return 查询结果列表
    @Override
    @NonNull
    public <R> List<R> getList(@NonNull QueryContext context) {
        // JDBC 默认展开引用路径
        context.setExpandReferencePath(true);
        // 调用 init 完成初始化
        QuerySqlStatement sql = sqlBuilder.buildQueryStatement(context);
        sql.debug();
        try {
            return connectionProvider.execute(connection -> {
                LockModeType locked = context.getStructure().lockType();
                if (locked != null && locked != LockModeType.NONE && connection.getAutoCommit()) {
                    throw new TransactionRequiredException();
                }
                // noinspection SqlSourceToSinkFlow
                try (PreparedStatement statement = connection.prepareStatement(sql.sql())) {
                    // 应用查询超时配置
                    if (config.queryTimeout() != null) {
                        statement.setQueryTimeout(config.queryTimeout());
                    }
                    // 应用获取大小配置
                    if (config.fetchSize() > 0) {
                        statement.setFetchSize(config.fetchSize());
                    }
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


    /// 结果收集器接口
    ///
    /// 用于处理从数据库查询返回的结果集
    public interface ResultCollector {
        /// 解析结果集
        ///
        /// @param <T>       结果类型
        /// @param resultSet 结果集
        /// @param context   查询上下文
        /// @return 解析后的结果列表
        /// @throws SQLException SQL异常
        <T> List<T> resolve(ResultSet resultSet, QueryContext context) throws SQLException;
    }
}

