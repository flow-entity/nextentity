package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.JoinAttribute;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Schema;

import java.util.Map;

/// 条件 SQL 语句构建器的抽象基类
///
/// 该类封装了 WHERE 条件表达式的处理逻辑，使用实例字段保存构建上下文。
/// 支持处理 PathNode、LiteralNode、OperatorNode 等各种表达式节点类型，
/// 包括嵌套路径的子查询生成。
///
/// 提供了 UPDATE/DELETE JOIN 的通用方言处理方法：
/// - appendJoinTablesOnly(): 用于 PostgreSQL 的 FROM/USING 子句
/// - appendJoinConditions(): 用于 PostgreSQL 的 WHERE 连接条件
/// - appendWhereWithJoinConditions(): 处理 PostgreSQL 风格的 WHERE 子句
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

    /// 只追加关联表的名称和别名（不含 LEFT JOIN ON）
    /// 用于 PostgreSQL 等需要在 FROM/USING 子句中只列出关联表名的情况
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

    /// 添加连接条件（用于 PostgreSQL 的 WHERE 子句风格）
    /// 在 WHERE 子句中显式添加连接条件：t_.department_id = d0_.id
    protected void appendJoinConditions() {
        String delimiter = "";
        for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            sql.append(delimiter);
            Schema declared = k.declareBy();
            if (declared instanceof JoinAttribute schemaAttribute) {
                Integer parentIndex = joins.get(schemaAttribute);
                appendTableAlias(parentIndex);
            } else {
                appendFromAlias(sql);
            }
            if (k.isObject()) {
                sql.append(".").append(k.joinName()).append("=");
                appendTableAlias(v);
                String referenced = k.referencedColumnName();
                if (referenced.isEmpty()) {
                    referenced = k.id().columnName();
                }
                sql.append(".").append(referenced);
            } else {
                throw new IllegalStateException();
            }
            delimiter = " and ";
        }
    }

    /// 添加 WHERE 子句，处理 PostgreSQL 风格的连接条件合并
    /// PostgreSQL 的 UPDATE/DELETE 语句需要将连接条件合并到 WHERE 子句中
    protected void appendWhereWithJoinConditions() {
        ExpressionNode where = where();
        SqlDialect.UpdateJoinStyle style = dialect.getUpdateJoinStyle();

        if (style == SqlDialect.UpdateJoinStyle.FROM_CLAUSE_WITH_JOIN && !joins.isEmpty()) {
            // PostgreSQL: 连接条件合并到 WHERE 子句中
            sql.append(WHERE);
            appendJoinConditions();
            if (!ExpressionNodes.isNullOrTrue(where)) {
                sql.append(AND);
                appendPredicate(where);
            }
        } else {
            // 其他方言：正常处理 WHERE
            super.appendWhere();
        }
    }
}