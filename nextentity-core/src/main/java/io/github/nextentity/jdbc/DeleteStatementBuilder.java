package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.Iterators;

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
public class DeleteStatementBuilder {

    protected final StringBuilder sql = new StringBuilder();
    protected final List<EntityAttribute> paramAttr;
    protected final SqlDialect dialect;
    protected final Iterable<?> entities;
    protected final EntityType entityType;

    public DeleteStatementBuilder(Iterable<?> entities, EntityType entityType, SqlDialect dialect) {
        this.entities = entities;
        this.entityType = entityType;
        this.dialect = dialect;
        this.paramAttr = Collections.singletonList(entityType.id());
    }

    protected String leftQuotedIdentifier() {
        return dialect.leftQuotedIdentifier();
    }

    protected String rightQuotedIdentifier() {
        return dialect.rightQuotedIdentifier();
    }

    /// 构建删除语句
    public BatchSqlStatement build() {
        appendDeleteClause();
        appendWhereClause();
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

    /// 添加 WHERE 子句（DELETE 的 WHERE 在 appendDeleteClause 中已处理）
    protected void appendWhereClause() {
        // DELETE 语句的 WHERE 子句已在上面的 appendDeleteClause 中构建
    }

    /// 创建语句对象
    protected BatchSqlStatement createStatement() {
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql.toString(), parameters);
    }

    /// 获取参数列表
    protected Iterable<? extends Iterable<?>> getParameters(Iterable<?> entities,
                                                            Iterable<? extends EntityAttribute> attributes) {
        return Iterators.map(entities, entity -> Iterators.map(attributes, attr -> {
            Object value = attr.getDatabaseValue(entity);
            if (attr.isVersion() && value == null) {
                value = 0;
            }
            return value == null ? new NullParameter(attr.type()) : value;
        }));
    }
}