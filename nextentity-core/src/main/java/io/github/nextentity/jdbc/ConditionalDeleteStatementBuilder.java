package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

/// 条件删除 SQL 语句构建器
///
/// 通过 SqlDialect 接口处理不同数据库的 DELETE JOIN 语法差异。
///
/// @author HuangChengwei
/// @since 2.0
public class ConditionalDeleteStatementBuilder extends AbstractConditionalStatementBuilder {

    public ConditionalDeleteStatementBuilder(EntityType entityType,
                                             Metamodel metamodel,
                                             ExpressionNode whereCondition,
                                             SqlDialect dialect,
                                             JdbcConfig config) {
        super(entityType, metamodel, whereCondition, dialect, config);
    }

    public DeleteSqlStatement build() {
        // 构建 DELETE 语句结构
        appendDeleteClause();
        appendDeleteFromClause();
        appendJoinIfNecessary();
        appendWhereClause();

        return createStatement();
    }

    /// 构建 DELETE 开头部分（委托给方言）
    protected void appendDeleteClause() {
        String table = getTableName();
        boolean hasJoin = !joins.isEmpty();

        dialect.appendDeleteClause(sql, table, fromAlias, hasJoin);
    }

    /// 构建 DELETE FROM/USING 子句（委托给方言）
    protected void appendDeleteFromClause() {
        String table = getTableName();
        boolean hasJoin = !joins.isEmpty();

        dialect.appendDeleteFromClause(sql, table, fromAlias, hasJoin);
    }

    /// 构建 JOIN 子句（如有）
    protected void appendJoinIfNecessary() {
        if (!joins.isEmpty()) {
            if (dialect.supportsUpdateJoinBeforeSetSyntax()) {
                // MySQL: FROM 子句中追加 JOIN
                appendJoin();
            } else if (dialect.supportsUpdateAliasOnlySyntax()) {
                // SQL Server: FROM 子句中追加 JOIN
                appendJoin();
            } else if (dialect.supportsDeleteUsingSyntax()) {
                // PostgreSQL: USING 子句中追加表列表（逗号分隔）
                appendJoinTablesOnly();
            }
            // H2: 不需要 JOIN 子句，使用 EXISTS 子查询
        }
    }

    protected DeleteSqlStatement createStatement() {
        return new DeleteSqlStatement(sql.toString(), args);
    }

}