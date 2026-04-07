package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import org.jspecify.annotations.NonNull;

import java.util.*;

/// 统一的 SQL 构建器
///
/// 该类合并了查询和更新 SQL 构建功能，同时实现 QuerySqlBuilder 和 JdbcUpdateSqlBuilder 接口。
/// 通过 SqlDialect 支持所有数据库方言（MySQL、PostgreSQL、SQL Server），方言差异由 SqlDialect 处理。
/// 所有 SQL 构建通过各自的 StatementBuilder 类完成，实现上下文封装和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
public class DefaultSqlBuilder implements SqlBuilder {

    private final SqlDialect dialect;
    private final JdbcConfig config;

    /// 构造 SQL 构建器
    ///
    /// @param dialect SQL 方言
    /// @param config  JDBC 配置
    public DefaultSqlBuilder(SqlDialect dialect, JdbcConfig config) {
        this.dialect = dialect;
        this.config = config;
    }

    /// 构建查询 SQL 语句
    ///
    /// @param context 查询上下文
    /// @return 查询 SQL 语句对象
    @Override
    public QuerySqlStatement buildQueryStatement(QueryContext context) {
        return new QueryStatementBuilder(context, dialect, config).build();
    }

    /// 构建插入 SQL 语句
    ///
    /// @param entities   实体集合
    /// @param entityType 实体类型
    /// @return 插入 SQL 语句列表
    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        return new InsertStatementBuilder(dialect, entities, entityType).build();
    }

    /// 构建更新 SQL 语句
    ///
    /// @param entities   实体集合
    /// @param entityType 实体类型
    /// @return 批量 SQL 语句对象
    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities, EntitySchema entityType) {
        return new UpdateStatementBuilder(dialect, entities, entityType).build();
    }

    /// 构建删除 SQL 语句
    ///
    /// @param entities 实体集合
    /// @param entity   实体类型
    /// @return 批量 SQL 语句对象
    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        return new DeleteStatementBuilder(dialect, entities, entity).build();
    }

    /// 构建条件更新 SQL 语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型
    /// @param setValues      set 子句的列名和值映射
    /// @param whereCondition where 条件表达式
    /// @return 更新 SQL 语句对象
    @Override
    public UpdateSqlStatement buildConditionalUpdateStatement(EntityType entity,
                                                              Metamodel metamodel,
                                                              Map<String, Object> setValues,
                                                              ExpressionNode whereCondition) {
        return new ConditionalUpdateStatementBuilder(entity, metamodel, setValues, whereCondition, dialect).build();
    }

    /// 构建条件删除 SQL 语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型
    /// @param whereCondition where 条件表达式
    /// @return 删除 SQL 语句对象
    @Override
    public DeleteSqlStatement buildConditionalDeleteStatement(EntityType entity,
                                                              Metamodel metamodel,
                                                              ExpressionNode whereCondition) {
        return new ConditionalDeleteStatementBuilder(entity, metamodel, whereCondition, dialect).build();
    }

}