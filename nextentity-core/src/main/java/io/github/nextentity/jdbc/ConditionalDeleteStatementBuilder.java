package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

///
/// 条件删除 SQL 语句构建器
///
/// 该类封装了条件删除语句的构建逻辑，继承自 AbstractConditionalStatementBuilder，
/// 使用实例字段保存构建上下文，提供清晰的构建流程和参数管理。
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

    /// 构建删除语句
    public DeleteSqlStatement build() {
        appendDeleteClause();
        appendFromOrUsingClauseIfNecessary();
        appendWhereWithJoinConditions();
        return createStatement();
    }

    /// 添加 DELETE 子句
    /// 根据是否有 JOIN 和方言风格生成不同的格式：
    /// - 无 JOIN 时：
    ///   - MySQL/SQL Server: DELETE alias FROM table alias WHERE alias.column = ...
    ///     （别名在 DELETE 后，这样才能在 WHERE 中使用别名引用）
    ///   - PostgreSQL: DELETE FROM table AS alias WHERE alias.column = ...
    ///     （标准语法，别名在 FROM 后，需要 AS 关键字）
    /// - 有 JOIN 时：
    ///   - JOIN_BEFORE_SET (MySQL): DELETE alias FROM table alias JOIN ...
    ///   - FROM_CLAUSE_WITH_JOIN (PostgreSQL): DELETE FROM table USING ...
    ///   - UPDATE_ALIAS_ONLY (SQL Server): DELETE alias FROM table alias JOIN ...
    protected void appendDeleteClause() {
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        if (joins.isEmpty()) {
            // 无 JOIN 时的处理
            switch (style) {
                case JOIN_BEFORE_SET, UPDATE_ALIAS_ONLY -> {
                    // MySQL/SQL Server: DELETE alias FROM table alias WHERE alias.column = ...
                    // 别名必须在 DELETE 后，才能在 WHERE 中使用别名引用列
                    sql.append("delete ");
                    appendFromAlias();
                    sql.append(" from ");
                    appendFromTable();
                    appendFromAlias();
                }
                default -> {
                    // PostgreSQL: DELETE FROM table AS alias WHERE alias.column = ...
                    // 使用标准语法，别名在 FROM 后，需要 AS 关键字
                    sql.append("delete from ");
                    appendFromTable();
                    sql.append(" as ");
                    appendFromAlias();
                }
            }
        } else {
            // 有 JOIN 时，根据方言生成不同格式
            sql.append("delete ");
            switch (style) {
                case JOIN_BEFORE_SET -> {
                    // MySQL: DELETE alias FROM table alias JOIN ...
                    appendFromAlias();
                }
                case UPDATE_ALIAS_ONLY -> {
                    // SQL Server: DELETE alias FROM table alias JOIN ...
                    appendFromAlias();
                }
                default -> {
                    // FROM_CLAUSE_WITH_JOIN (PostgreSQL): DELETE FROM table USING ...
                    // 只写 DELETE FROM table，USING 子句在后面添加
                    appendFrom();
                }
            }
        }
    }

    /// 添加 FROM/USING 子句（当需要时）
    /// 对于有 JOIN 的情况，需要添加额外的 FROM/USING 子句
    protected void appendFromOrUsingClauseIfNecessary() {
        if (joins.isEmpty()) {
            return;
        }

        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET -> {
                // MySQL: FROM table alias JOIN ...
                sql.append(" from ");
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            case UPDATE_ALIAS_ONLY -> {
                // SQL Server: FROM table alias JOIN ...
                sql.append(" from ");
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            default -> {
                // FROM_CLAUSE_WITH_JOIN (PostgreSQL): USING other_table alias
                // 主表已在 DELETE FROM 中声明，USING 子句只写关联表
                sql.append(USING);
                appendJoinTablesOnly();
            }
        }
    }

    /// 创建语句对象
    protected DeleteSqlStatement createStatement() {
        return new DeleteSqlStatement(sql.toString(), args);
    }
}