package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;

import java.util.Collections;
import java.util.List;

///
/// 批量删除 SQL 语句构建器
///
/// 该类封装了批量删除语句的构建逻辑，使用实例字段保存构建上下文，
/// 提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class DeleteStatementBuilder extends AbstractBatchStatementBuilder {

    protected final List<EntityAttribute> paramAttr;
    protected final Iterable<?> entities;
    protected final EntityType entityType;

    public DeleteStatementBuilder(SqlDialect dialect, Iterable<?> entities, EntityType entityType) {
        super(dialect);
        this.entities = entities;
        this.entityType = entityType;
        this.paramAttr = Collections.singletonList(entityType.id());
    }

    /// 构建删除语句
    public BatchSqlStatement build() {
        appendDeleteClause();
        return createStatement();
    }

    /// 添加 DELETE FROM 子句
    protected void appendDeleteClause() {
        EntityAttribute id = entityType.id();
        sql.append("delete from ")
                .append(leftQuotedIdentifier())
                .append(entityType.tableName())
                .append(rightQuotedIdentifier())
                .append(" where ")
                .append(leftQuotedIdentifier())
                .append(id.columnName())
                .append(rightQuotedIdentifier())
                .append("=?");
    }

    /// 创建语句对象
    protected BatchSqlStatement createStatement() {
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql.toString(), parameters);
    }
}