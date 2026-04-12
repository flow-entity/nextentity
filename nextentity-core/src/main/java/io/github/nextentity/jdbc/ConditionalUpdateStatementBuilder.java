package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.Map;

///
/// 条件更新 SQL 语句构建器
///
/// 该类封装了条件更新语句的构建逻辑，继承自 AbstractConditionalStatementBuilder，
/// 使用实例字段保存构建上下文，提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class ConditionalUpdateStatementBuilder extends AbstractConditionalStatementBuilder {

    protected final Map<String, Object> setValues;

    public ConditionalUpdateStatementBuilder(EntityType entityType,
                                              Metamodel metamodel,
                                              Map<String, Object> setValues,
                                              ExpressionNode whereCondition,
                                              SqlDialect dialect,
                                              JdbcConfig jdbcConfig) {
        super(entityType, metamodel, whereCondition, dialect, jdbcConfig);
        this.setValues = setValues;
    }

    /// 构建更新语句
    public UpdateSqlStatement build() {
        appendUpdateClause();
        appendSetClause();
        appendFromClauseIfNecessary();
        appendWhereWithJoinConditions();
        return createStatement();
    }

    /// 添加 UPDATE 子句
    /// 根据方言风格生成不同的格式：
    /// - JOIN_BEFORE_SET: UPDATE table alias JOIN ... SET ...
    /// - FROM_CLAUSE_WITH_JOIN: UPDATE table AS alias SET ... FROM ...
    /// - UPDATE_ALIAS_ONLY: UPDATE alias SET ... FROM table alias ...
    protected void appendUpdateClause() {
        sql.append("update ");
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET -> {
                // MySQL: UPDATE table alias JOIN ... SET ...
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            case UPDATE_ALIAS_ONLY -> {
                // SQL Server: UPDATE alias SET ... FROM table alias ...
                // 只有别名，表名放在 FROM 子句中
                appendFromAlias();
            }
            default -> {
                // FROM_CLAUSE_WITH_JOIN (PostgreSQL): UPDATE table AS alias SET ... FROM ...
                // PostgreSQL 需要使用 AS 关键字定义别名
                appendFromTable();
                sql.append(" as ");
                appendFromAlias();
            }
        }
        sql.append(" set ");
    }

    /// 添加 FROM 子句（当需要时）
    /// 对于 FROM_CLAUSE_WITH_JOIN 和 UPDATE_ALIAS_ONLY 风格，需要在 SET 后添加 FROM 子句
    protected void appendFromClauseIfNecessary() {
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        switch (style) {
            case JOIN_BEFORE_SET -> {
                // MySQL: JOIN 已在 UPDATE 子句中处理，无需额外 FROM
            }
            case UPDATE_ALIAS_ONLY -> {
                // SQL Server: FROM table alias JOIN ...
                sql.append(" from ");
                appendFromTable();
                appendFromAlias();
                appendJoin();
            }
            default -> {
                // FROM_CLAUSE_WITH_JOIN (PostgreSQL): FROM other_table alias
                // 主表已在 UPDATE 子句中声明，FROM 子句只写关联表的名称和别名
                // 连接条件合并到 WHERE 子句中处理（使用父类的 appendWhereWithJoinConditions）
                if (!joins.isEmpty()) {
                    sql.append(" from ");
                    appendJoinTablesOnly();
                }
            }
        }
    }

    /// 添加 SET 子句
    /// SET 子句中只写列名，不需要别名前缀
    protected void appendSetClause() {
        String delimiter = "";
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            sql.append(delimiter);
            EntityAttribute attribute = (EntityAttribute) entityType.getAttribute(entry.getKey());
            // SET 子句中只写列名，不写别名前缀
            sql.append(leftQuotedIdentifier()).append(attribute.columnName()).append(rightQuotedIdentifier());
            sql.append("=");
            appendLiteralValue(entry.getValue());
            delimiter = ", ";
        }
    }

    /// 创建语句对象
    protected UpdateSqlStatement createStatement() {
        return new UpdateSqlStatement(sql.toString(), args);
    }
}