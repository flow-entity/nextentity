package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.Map;

/// 条件更新 SQL 语句构建器
///
/// UPDATE JOIN 方言差异：
/// - MySQL: UPDATE table alias JOIN ... SET ...
/// - PostgreSQL: UPDATE table AS alias SET ... FROM ...
/// - SQL Server: UPDATE alias SET ... FROM table alias ...
///
/// @author HuangChengwei
/// @since 2.0
///
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
        appendUpdateClause();
        appendSetClause();
        appendFromClauseIfNecessary();
        appendWhereWithJoinConditions();
        return createStatement();
    }

    protected void appendUpdateClause() {
        sql.append("update ");
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET -> {
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            case UPDATE_ALIAS_ONLY -> appendFromAlias();
            default -> {
                appendFromTable();
                sql.append(" as ");
                appendFromAlias();
            }
        }
        sql.append(" set ");
    }

    protected void appendFromClauseIfNecessary() {
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET -> { }
            case UPDATE_ALIAS_ONLY -> {
                sql.append(" from ");
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            default -> {
                if (!joins.isEmpty()) {
                    sql.append(" from ");
                    appendJoinTablesOnly();
                }
            }
        }
    }

    /// SET 子句中只写列名，不需要别名前缀
    protected void appendSetClause() {
        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            EntityAttribute attribute = (EntityAttribute) entityType.getAttribute(entry.getKey());
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