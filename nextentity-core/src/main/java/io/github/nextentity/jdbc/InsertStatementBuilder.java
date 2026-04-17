package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

///
/// 插入 SQL 语句构建器
///
/// 该类封装了插入语句的构建逻辑，使用实例字段保存构建上下文，
/// 提供清晰的构建流程和参数管理。
/// 支持将 ID 为 null 和非 null 的实体分开处理，返回多个插入语句。
///
/// @author HuangChengwei
/// @since 2.0
///
public class InsertStatementBuilder extends AbstractBatchStatementBuilder {

    protected final Iterable<?> entities;
    protected final EntityType entityType;
    protected final SqlDialect dialect;

    public InsertStatementBuilder(SqlDialect dialect, Iterable<?> entities, EntityType entityType) {
        super(dialect);
        this.dialect = dialect;
        this.entities = entities;
        this.entityType = entityType;
    }

    /// 构建插入语句列表
    /// 将 ID 为 null 和非 null 的实体分开处理，返回对应的插入语句
    ///
    /// @return 插入SQL语句列表
    public List<InsertSqlStatement> build() {
        List<InsertSqlStatement> statements = new ArrayList<>();

        // 按 ID 是否为 null 分组
        List<Object> nullIdEntities = new ArrayList<>();
        List<Object> nonNullIdEntities = new ArrayList<>();
        EntityBasicAttribute idAttribute = entityType.id();

        for (Object entity : entities) {
            if (idAttribute.getDatabaseValue(entity) == null) {
                nullIdEntities.add(entity);
            } else {
                nonNullIdEntities.add(entity);
            }
        }

        // 为 ID 为 null 的实体构建语句（排除 ID 列，让数据库生成）
        if (!nullIdEntities.isEmpty()) {
            statements.add(buildStatement(nullIdEntities, true));
        }

        // 为 ID 非 null 的实体构建语句（包含 ID 列）
        if (!nonNullIdEntities.isEmpty()) {
            statements.add(buildStatement(nonNullIdEntities, false));
        }

        return statements;
    }

    /// 构建单个插入语句
    ///
    /// @param entityList  实体列表
    /// @param excludeId   是否排除 ID 列
    /// @return 插入SQL语句
    protected InsertSqlStatement buildStatement(List<?> entityList, boolean excludeId) {
        ImmutableArray<? extends EntityAttribute> selectList = entityType.getPrimitives();
        List<EntityBasicAttribute> columns = new ArrayList<>();

        // 确定要插入的列
        if (excludeId) {
            EntityAttribute idAttribute = entityType.id();
            for (EntityAttribute attr : selectList) {
                if (attr != idAttribute) {
                    columns.add((EntityBasicAttribute) attr);
                }
            }
        } else {
            for (EntityAttribute attr : selectList) {
                columns.add((EntityBasicAttribute) attr);
            }
        }

        // 构建 SQL
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ")
                .append(leftQuotedIdentifier())
                .append(entityType.tableName())
                .append(rightQuotedIdentifier())
                .append(" (");

        String delimiter = "";
        for (EntityBasicAttribute attribute : columns) {
            sql.append(delimiter)
                    .append(leftQuotedIdentifier())
                    .append(attribute.columnName())
                    .append(rightQuotedIdentifier());
            delimiter = ",";
        }

        sql.append(") values (");
        delimiter = "";
        for (int i = 0; i < columns.size(); i++) {
            sql.append(delimiter).append("?");
            delimiter = ",";
        }
        sql.append(")");

        // 构建参数
        Iterable<? extends Iterable<?>> parameters = getParameters(entityList, columns);

        // 确定执行策略
        boolean batchInsert = !excludeId || dialect.supportsBatchGeneratedKeys();

        return new InsertSqlStatement(entityList, sql.toString(), parameters, excludeId, batchInsert);
    }
}