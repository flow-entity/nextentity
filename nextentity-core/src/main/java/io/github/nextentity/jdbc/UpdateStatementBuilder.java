package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

///
/// 批量更新 SQL 语句构建器
///
/// 该类封装了批量更新语句的构建逻辑，使用实例字段保存构建上下文，
/// 提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class UpdateStatementBuilder extends AbstractBatchStatementBuilder {

    protected final List<EntityAttribute> paramAttr = new ArrayList<>();
    protected final Iterable<?> entities;
    protected final EntitySchema entityType;

    public UpdateStatementBuilder(Iterable<?> entities, EntitySchema entityType, SqlDialect dialect) {
        super(dialect);
        this.entities = entities;
        this.entityType = entityType;
    }

    /// 构建更新语句
    public BatchSqlStatement build() {
        ImmutableArray<? extends EntityAttribute> columns = entityType.getPrimitives();
        appendUpdateClause();
        appendSetClause(columns);
        appendWhereClause();
        return createStatement();
    }

    /// 添加 UPDATE 子句
    protected void appendUpdateClause() {
        sql.append("update ")
                .append(leftQuotedIdentifier())
                .append(entityType.tableName())
                .append(rightQuotedIdentifier())
                .append(" set ");
    }

    /// 添加 SET 子句
    protected void appendSetClause(ImmutableArray<? extends EntityAttribute> columns) {
        EntityAttribute id = entityType.id();
        EntityAttribute version = entityType.version();
        String delimiter = "";
        for (EntityAttribute attribute : columns) {
            if (Objects.equals(id, attribute) || !attribute.isUpdatable()) {
                continue;
            }
            sql.append(delimiter);
            delimiter = ",";
            sql.append(leftQuotedIdentifier())
                    .append(attribute.columnName())
                    .append(rightQuotedIdentifier())
                    .append("=");

            if (attribute == version) {
                sql.append("?+1");
            } else {
                sql.append("?");
            }
            paramAttr.add(attribute);
        }
    }

    /// 添加 WHERE 子句
    protected void appendWhereClause() {
        EntityAttribute id = entityType.id();
        EntityAttribute version = entityType.version();

        sql.append(" where ")
                .append(leftQuotedIdentifier())
                .append(id.columnName())
                .append(rightQuotedIdentifier())
                .append("=?");
        paramAttr.add(id);

        if (version != null) {
            sql.append(" and ")
                    .append(leftQuotedIdentifier())
                    .append(version.columnName())
                    .append(rightQuotedIdentifier())
                    .append("=?");
            paramAttr.add(version);
        }
    }

    /// 创建语句对象
    protected BatchSqlStatement createStatement() {
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql.toString(), parameters);
    }
}