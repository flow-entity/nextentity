package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.Map;

/// 条件更新 SQL 语句构建器
///
/// 通过 SqlDialect 接口处理不同数据库的 UPDATE JOIN 语法差异。
///
/// @author HuangChengwei
/// @since 2.0
public class ConditionalUpdateStatementBuilder extends AbstractConditionalStatementBuilder {

    private final Map<String, Object> setValues;

    public ConditionalUpdateStatementBuilder(EntityType entityType,
                                             Metamodel metamodel,
                                             Map<String, Object> setValues,
                                             ExpressionNode whereCondition,
                                             SqlDialect dialect,
                                             JdbcConfig jdbcConfig) {
        super(entityType, metamodel, whereCondition, dialect, jdbcConfig);
        this.setValues = setValues;
    }

    public UpdateSqlStatement build() {
        // 构建 UPDATE 语句结构
        boolean hasJoin = !joins.isEmpty();

        appendUpdateClause();

        // MySQL: JOIN 在 SET 之前，需要先追加 JOIN
        if (hasJoin && dialect.supportsUpdateJoinBeforeSetSyntax()) {
            appendJoin();
        }

        sql.append(" set ");
        appendSetClause();

        // FROM 子句（在 SET 之后）
        appendUpdateFromClause();

        // 其他方言: JOIN 在 SET 之后（FROM 子句中）
        if (hasJoin && !dialect.supportsUpdateJoinBeforeSetSyntax()) {
            appendJoinIfNecessary();
        }

        appendWhereClause();

        return createStatement();
    }

    /// 构建 UPDATE 开头部分（委托给方言）
    protected void appendUpdateClause() {
        String table = getTableName();
        String alias = fromAlias;
        boolean hasJoin = !joins.isEmpty();

        dialect.appendUpdateClause(sql, table, alias, hasJoin);
    }

    /// 构建 UPDATE FROM 子句（委托给方言）
    protected void appendUpdateFromClause() {
        String table = getTableName();
        String alias = fromAlias;
        boolean hasJoin = !joins.isEmpty();

        dialect.appendUpdateFromClause(sql, table, alias, hasJoin);
    }

    /// 构建 JOIN 子句（如有）
    protected void appendJoinIfNecessary() {
        if (!joins.isEmpty()) {
            if (dialect.supportsUpdateAliasOnlySyntax()) {
                // SQL Server: FROM 子句中追加 JOIN
                appendJoin();
            } else if (dialect.supportsUpdateFromSyntax()) {
                // PostgreSQL: FROM 子句中追加表列表（逗号分隔）
                appendJoinTablesOnly();
            }
            // MySQL: JOIN 已在 SET 之前添加（在 build() 方法中）
            // H2: 不需要 JOIN 子句，使用 EXISTS 子查询
        }
    }

    /// 构建 SET 子句
    protected void appendSetClause() {
        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            var attribute = (EntityBasicAttribute) entityType.getAttribute(entry.getKey());
            sql.append(leftQuotedIdentifier()).append(attribute.columnName()).append(rightQuotedIdentifier());
            sql.append("=");
            appendLiteralValue(entry.getValue());
            delimiter = ", ";
        }
    }

    protected UpdateSqlStatement createStatement() {
        return new UpdateSqlStatement(sql.toString(), args);
    }

}