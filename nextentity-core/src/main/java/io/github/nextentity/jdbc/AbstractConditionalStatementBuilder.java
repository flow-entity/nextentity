package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.JoinAttribute;
import io.github.nextentity.core.meta.Metamodel;

import java.util.Map;

/// 条件 SQL 语句构建器的抽象基类
///
/// 该类封装了 WHERE 条件表达式的处理逻辑，使用实例字段保存构建上下文。
/// 提供了 UPDATE/DELETE JOIN 的通用方言处理方法。
///
/// @author HuangChengwei
/// @since 2.0
///
public abstract class AbstractConditionalStatementBuilder extends AbstractStatementBuilder {

    protected final EntityType entityType;
    protected final Metamodel metamodel;
    protected final ExpressionNode whereCondition;

    protected AbstractConditionalStatementBuilder(EntityType entityType,
                                                  Metamodel metamodel,
                                                  ExpressionNode whereCondition,
                                                  SqlDialect dialect,
                                                  JdbcConfig config) {
        super(dialect, config);
        this.entityType = entityType;
        this.metamodel = metamodel;
        this.whereCondition = whereCondition;
        addJoin(where());
    }

    @Override
    protected QueryContext newContext(QueryStructure queryStructure) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected EntityType getEntityType() {
        return entityType;
    }

    @Override
    protected ExpressionNode where() {
        return whereCondition;
    }

    protected void appendFrom() {
        appendBlank().append(FROM);
        appendFromTable();
        appendFromAlias();
    }

    /// PostgreSQL USING 子句只列出关联表名
    /// @see #appendJoinCondition(JoinAttribute, Integer) ON/WHERE 共用的连接条件逻辑
    protected void appendJoinTablesOnly() {
        String delimiter = "";
        for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            sql.append(delimiter);
            appendTable(sql, k);
            appendTableAlias(v);
            delimiter = ", ";
        }
    }

    /// PostgreSQL WHERE 子句显式添加连接条件
    /// @see #appendJoinCondition(JoinAttribute, Integer) ON/WHERE 共用的连接条件逻辑
    protected void appendJoinConditions() {
        String delimiter = "";
        for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            sql.append(delimiter);
            appendJoinCondition(entry.getKey(), entry.getValue());
            delimiter = " and ";
        }
    }

    /// PostgreSQL 将连接条件合并到 WHERE 子句
    /// @see #appendJoinConditions() 连接条件拼接
    protected void appendWhereWithJoinConditions() {
        ExpressionNode where = where();
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        if (style == SqlDialect.UpdateJoinStyle.FROM_CLAUSE_WITH_JOIN && !joins.isEmpty()) {
            sql.append(WHERE);
            appendJoinConditions();
            if (!ExpressionNodes.isNullOrTrue(where)) {
                sql.append(AND);
                appendPredicate(where);
            }
        } else {
            super.appendWhere();
        }
    }
}