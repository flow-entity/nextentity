package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

/// 条件删除 SQL 语句构建器
///
/// DELETE JOIN 方言差异：
/// - MySQL: DELETE t FROM table t JOIN other ON ... WHERE ...
/// - PostgreSQL: DELETE FROM table USING other WHERE table.join_col = other.col AND ...
/// - SQL Server: DELETE t FROM table t JOIN other ON ... WHERE ...
///
/// @author HuangChengwei
/// @since 2.0
///
public class ConditionalDeleteStatementBuilder extends AbstractConditionalStatementBuilder {

    public ConditionalDeleteStatementBuilder(EntityType entityType,
                                              Metamodel metamodel,
                                              ExpressionNode whereCondition,
                                              SqlDialect dialect,
                                              JdbcConfig config) {
        super(entityType, metamodel, whereCondition, dialect, config);
    }

    public DeleteSqlStatement build() {
        appendDeleteClause();
        appendFromOrUsingClauseIfNecessary();
        appendWhereWithJoinConditions();
        return createStatement();
    }

    protected void appendDeleteClause() {
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        if (joins.isEmpty()) {
            switch (style) {
                // MySQL/SQL Server 别名必须在 DELETE 后，才能在 WHERE 中使用别名引用列
                case JOIN_BEFORE_SET, UPDATE_ALIAS_ONLY -> {
                    sql.append("delete ");
                    appendFromAlias();
                    sql.append(" from ");
                    appendFromTable();
                    appendFromAlias();
                }
                default -> {
                    sql.append("delete from ");
                    appendFromTable();
                    sql.append(" as ");
                    appendFromAlias();
                }
            }
        } else {
            sql.append("delete ");
            switch (style) {
                case JOIN_BEFORE_SET, UPDATE_ALIAS_ONLY -> appendFromAlias();
                default -> appendFrom();
            }
        }
    }

    protected void appendFromOrUsingClauseIfNecessary() {
        if (joins.isEmpty()) {
            return;
        }

        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET, UPDATE_ALIAS_ONLY -> {
                sql.append(" from ");
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            default -> {
                sql.append(USING);
                appendJoinTablesOnly();
            }
        }
    }

    protected DeleteSqlStatement createStatement() {
        return new DeleteSqlStatement(sql.toString(), args);
    }
}