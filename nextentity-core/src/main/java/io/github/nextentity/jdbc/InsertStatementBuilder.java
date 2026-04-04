package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

///
/// 插入 SQL 语句构建器
///
/// 该类封装了插入语句的构建逻辑，使用实例字段保存构建上下文，
/// 提供清晰的构建流程和参数管理。
///
/// @author HuangChengwei
/// @since 2.0
///
public class InsertStatementBuilder extends AbstractBatchStatementBuilder {

    protected final Iterable<?> entities;
    protected final EntityType entityType;
    protected final List<EntityAttribute> columns = new ArrayList<>();
    protected boolean generateKey;

    public InsertStatementBuilder(SqlDialect dialect, Iterable<?> entities, EntityType entityType) {
        super(dialect);
        this.entities = entities;
        this.entityType = entityType;
        this.generateKey = hasNullId();
    }

    /// 检查是否存在空 ID
    protected boolean hasNullId() {
        EntityAttribute idAttribute = entityType.id();
        for (Object entity : entities) {
            if (idAttribute.getDatabaseValue(entity) == null) {
                return true;
            }
        }
        return false;
    }

    /// 构建插入语句
    public InsertSqlStatement build() {
        ImmutableArray<? extends EntityAttribute> selectList = entityType.getPrimitives();
        return build(selectList);
    }

    /// 构建插入语句（指定属性列表）
    public InsertSqlStatement build(Iterable<? extends EntityAttribute> attributes) {
        appendInsertClause();
        appendColumns(attributes);
        appendValuesClause();
        return createStatement();
    }

    /// 添加 INSERT INTO 子句
    protected void appendInsertClause() {
        sql.append("insert into ")
                .append(leftQuotedIdentifier())
                .append(entityType.tableName())
                .append(rightQuotedIdentifier())
                .append(" (");
    }

    /// 添加列名列表
    protected void appendColumns(Iterable<? extends EntityAttribute> attributes) {
        String delimiter = "";
        for (EntityAttribute attribute : attributes) {
            sql.append(delimiter)
                    .append(leftQuotedIdentifier())
                    .append(attribute.columnName())
                    .append(rightQuotedIdentifier());
            columns.add(attribute);
            delimiter = ",";
        }
    }

    /// 添加 VALUES 子句
    protected void appendValuesClause() {
        sql.append(") values (");
        String delimiter = "";
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            sql.append(delimiter).append("?");
            delimiter = ",";
        }
        sql.append(")");
    }

    /// 创建语句对象
    protected InsertSqlStatement createStatement() {
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, columns);
        return new InsertSqlStatement(entities, sql.toString(), parameters, generateKey);
    }
}