package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.JoinAttribute;
import io.github.nextentity.core.meta.Metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// 条件 SQL 语句构建器的抽象基类
///
/// 该类封装了 WHERE 条件表达式的处理逻辑，使用实例字段保存构建上下文。
/// 实现了 WhereClauseContext 接口，提供方言构建 WHERE 子句所需的所有信息。
///
/// @author HuangChengwei
/// @since 2.0
public abstract class AbstractConditionalStatementBuilder extends AbstractStatementBuilder
        implements SqlDialect.WhereClauseContext {

    protected final EntityType entityType;
    protected final Metamodel metamodel;
    protected final ExpressionNode whereCondition;

    /// JOIN 表信息缓存
    private List<SqlDialect.JoinTableInfo> joinTableInfos;

    /// JOIN 条件字符串缓存
    private List<String> joinConditionStrings;

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

    // ========== WhereClauseContext 接口实现 ==========

    @Override
    public String mainTableAlias() {
        return fromAlias;
    }

    @Override
    public List<SqlDialect.JoinTableInfo> joinTables() {
        if (joinTableInfos == null) {
            joinTableInfos = new ArrayList<>();
            for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
                JoinAttribute attr = entry.getKey();
                Integer index = entry.getValue();
                joinTableInfos.add(new JoinTableInfoImpl(attr, index));
            }
        }
        return joinTableInfos;
    }

    @Override
    public List<String> joinConditions() {
        if (joinConditionStrings == null) {
            joinConditionStrings = new ArrayList<>();
            StringBuilder tempSql = new StringBuilder();
            for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
                tempSql.setLength(0);
                appendJoinConditionTo(tempSql, entry.getKey(), entry.getValue());
                joinConditionStrings.add(tempSql.toString());
            }
        }
        return joinConditionStrings;
    }

    @Override
    public ExpressionNode whereCondition() {
        return whereCondition;
    }

    @Override
    public void appendPredicate(ExpressionNode where) {
        super.appendPredicate(where);
    }

    @Override
    public void appendJoinTable(String delimiter, SqlDialect.JoinTableInfo tableInfo) {
        sql.append(delimiter);
        if (tableInfo instanceof JoinTableInfoImpl impl) {
            appendTable(sql, impl.attribute.getTargetEntityType());
            appendTableAlias(impl.index);
        } else {
            sql.append(tableInfo.tableName()).append(" ").append(tableInfo.tableAlias());
        }
    }

    @Override
    public void appendJoinCondition(String delimiter, int index) {
        sql.append(delimiter);
        if (index >= 0 && index < joinConditions().size()) {
            sql.append(joinConditions().get(index));
        }
    }

    // ========== 辅助方法 ==========

    /// 构建 JOIN 条件字符串（追加到指定的 StringBuilder）
    protected void appendJoinConditionTo(StringBuilder sb,
                                         JoinAttribute k,
                                         Integer v) {
        Object declared = k.declareBy();
        if (declared instanceof JoinAttribute schemaAttribute) {
            Integer parentIndex = joins.get(schemaAttribute);
            appendTableAliasTo(sb, parentIndex);
        } else {
            sb.append(fromAlias);
        }
        if (k.isObject()) {
            sb.append(".").append(k.getSourceAttribute().columnName()).append("=");
            appendTableAliasTo(sb, v);
            sb.append(".").append(k.getTargetAttribute().columnName());
        } else {
            throw new IllegalStateException();
        }
    }

    /// 构建表别名（追加到指定的 StringBuilder）
    protected void appendTableAliasTo(StringBuilder sb, Integer index) {
        String tableName = entityType.type().getSimpleName();
        sb.append(shortAlias(tableName));
        if (subIndex > 0) {
            sb.append(subIndex).append("_");
        }
        sb.append(index).append("_");
    }

    /// 追加 WHERE 子句（委托给方言）
    protected void appendWhereClause() {
        dialect.appendWhereClause(sql, this);
    }

    /// 追加 FROM 子句（用于 DELETE 等）
    protected void appendFrom() {
        appendBlank().append(FROM);
        appendFromTable();
        appendFromAlias();
    }

    /// 追加 JOIN 表列表（逗号分隔，用于 PostgreSQL FROM/USING）
    protected void appendJoinTablesOnly() {
        String delimiter = "";
        for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            sql.append(delimiter);
            appendTable(sql, k.getTargetEntityType());
            appendTableAlias(v);
            delimiter = ", ";
        }
    }

    /// 追加 JOIN 子句（LEFT JOIN ... ON ...）
    protected void appendJoin() {
        for (Map.Entry<JoinAttribute, Integer> entry : joins.entrySet()) {
            JoinAttribute k = entry.getKey();
            Integer v = entry.getValue();
            sql.append(LEFT_JOIN);
            appendTable(sql, k.getTargetEntityType());
            appendTableAlias(v);
            sql.append(ON);
            appendJoinConditionTo(sql, k, v);
        }
    }

    /// 返回主表名（已引用）
    protected String getTableName() {
        StringBuilder sb = new StringBuilder();
        appendTable(sb, entityType);
        return sb.toString();
    }

    // ========== JOIN 表信息实现类 ==========

    private class JoinTableInfoImpl implements SqlDialect.JoinTableInfo {
        final JoinAttribute attribute;
        final Integer index;

        JoinTableInfoImpl(JoinAttribute attribute, Integer index) {
            this.attribute = attribute;
            this.index = index;
        }

        @Override
        public String tableName() {
            StringBuilder sb = new StringBuilder();
            appendTable(sb, attribute.getTargetEntityType());
            return sb.toString();
        }

        @Override
        public String tableAlias() {
            StringBuilder sb = new StringBuilder();
            appendTableAliasTo(sb, index);
            return sb.toString();
        }
    }

}