package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Iterators;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

///
/// 抽象更新SQL构建器
///
/// 该类提供了构建更新相关SQL语句的基础实现，包括INSERT、UPDATE、DELETE语句的构建。
/// 它实现了JdbcUpdateSqlBuilder接口，为不同数据库类型的更新SQL构建器提供了通用的功能。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractUpdateSqlBuilder implements JdbcUpdateSqlBuilder {

    /// 构建插入SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @return 插入SQL语句列表
    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        EntityAttribute idAttribute = entityType.id();
        boolean hasNullId = false;
        for (Object entity : entities) {
            Object id = idAttribute.getDatabaseValue(entity);
            if (id == null) {
                hasNullId = true;
            }
        }
        ImmutableArray<? extends EntityAttribute> selectList = entityType.getPrimitives();
        return Collections.singletonList(buildInsertStatement(entities, entityType, selectList, hasNullId));
    }

    /// 构建插入SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @param attributes 属性列表
    /// @param generateKey 是否生成键值
    /// @return 插入SQL语句对象
    protected InsertSqlStatement buildInsertStatement(Iterable<?> entities,
                                                      EntityType entityType,
                                                      Iterable<? extends EntityAttribute> attributes,
                                                      boolean generateKey) {
        String tableName = entityType.tableName();
        List<EntityAttribute> columns = new ArrayList<>();
        StringBuilder sql = new StringBuilder("insert into ")
                .append(leftTicks())
                .append(tableName)
                .append(rightTicks())
                .append(" (");
        String delimiter = "";
        for (EntityAttribute attribute : attributes) {
            sql.append(delimiter).append(leftTicks()).append(attribute.columnName()).append(rightTicks());
            columns.add(attribute);
            delimiter = ",";
        }
        sql.append(") values (");
        delimiter = "";
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            sql.append(delimiter).append("?");
            delimiter = ",";
        }
        sql.append(")");
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, attributes);
        return new InsertSqlStatement(entities, sql.toString(), parameters, generateKey);
    }

    /// 获取参数列表
    ///
    /// @param entities 实体集合
    /// @param attributes 属性列表
    /// @return 参数列表
    private static Iterable<? extends Iterable<?>> getParameters(Iterable<?> entities,
                                                                 Iterable<? extends EntityAttribute> attributes) {
        return Iterators.map(entities, entity -> Iterators.map(attributes, attr -> {
            Object value = attr.getDatabaseValue(entity);
            if (attr.isVersion() && value == null) {
                value = 0;
            }
            return value == null ? new NullParameter(attr.type()) : value;
        }));
    }

    /// 获取右侧标识符引用字符
    ///
    /// @return 右侧标识符引用字符
    @NonNull
    protected abstract String rightTicks();

    /// 获取左侧标识符引用字符
    ///
    /// @return 左侧标识符引用字符
    @NonNull
    protected abstract String leftTicks();

    /// 构建更新SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @param excludeNull 是否排除空值
    /// @return 批量SQL语句对象
    @Override
    public BatchSqlStatement buildUpdateStatement(Iterable<?> entities,
                                                  EntitySchema entityType,
                                                  boolean excludeNull) {
        ImmutableArray<? extends EntityAttribute> columns = entityType.getPrimitives();
        StringBuilder sql = new StringBuilder("update ")
                .append(leftTicks())
                .append(entityType.tableName())
                .append(rightTicks())
                .append(" set ");
        EntityAttribute id = entityType.id();
        String delimiter = "";
        List<EntityAttribute> paramAttr = new ArrayList<>(columns.size() + 1);
        EntityAttribute version = entityType.version();
        for (EntityAttribute attribute : columns) {
            if (Objects.equals(entityType.id(), attribute) || !attribute.isUpdatable()) {
                continue;
            }
            sql.append(delimiter);
            delimiter = ",";
            sql.append(leftTicks()).append(attribute.columnName()).append(rightTicks()).append("=");
            if (excludeNull) {
                sql.append("case when ").append(typedPlaceholder(attribute)).append(" is null then")
                        .append(leftTicks()).append(attribute.columnName()).append(rightTicks())
                        .append("else ").append(typedPlaceholder(attribute))
                        .append(" end");
                paramAttr.add(attribute);
                paramAttr.add(attribute);
            } else {
                if (attribute == version) {
                    sql.append("?+1");
                } else {
                    sql.append("?");
                }
                paramAttr.add(attribute);
            }
        }
        sql.append(" where ").append(leftTicks()).append(id.columnName()).append(rightTicks()).append("=?");
        paramAttr.add(id);
        if (version != null) {
            sql.append(" and ")
                    .append(leftTicks())
                    .append(version.columnName())
                    .append(rightTicks())
                    .append("=?");
            paramAttr.add(version);
        }
        return new BatchSqlStatement(sql.toString(), getParameters(entities, paramAttr));
    }

    /// 获取类型化占位符
    ///
    /// @param attribute 属性
    /// @return 占位符字符串
    protected @NonNull String typedPlaceholder(EntityAttribute attribute) {
        return "?";
    }

    /// 构建删除SQL语句
    ///
    /// @param entities 实体集合
    /// @param entity 实体类型
    /// @return 批量SQL语句对象
    @Override
    public BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity) {
        EntityAttribute id = entity.id();
        String sql = "delete from " + leftTicks() + entity.tableName() + rightTicks()
                     + " where " + leftTicks() + id.columnName() + rightTicks() + "=?";
        List<EntityAttribute> paramAttr = Collections.singletonList(id);
        Iterable<? extends Iterable<?>> parameters = getParameters(entities, paramAttr);
        return new BatchSqlStatement(sql, parameters);
    }

    /// 构建分组插入SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @return 插入SQL语句列表
    protected List<InsertSqlStatement> buildGroupedInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        EntityAttribute idAttribute = entityType.id();
        ImmutableArray<? extends EntityAttribute> basicAttributes = entityType.getPrimitives();
        List<? extends EntityAttribute> withoutId = basicAttributes.stream()
                .filter(attr -> attr != idAttribute)
                .collect(ImmutableList.collector(basicAttributes.size() - 1));
        List<InsertSqlStatement> result = new ArrayList<>();
        List<Object> entitiesHasId = new ArrayList<>();
        for (Object entity : entities) {
            List<?> single = Collections.singletonList(entity);
            if (idAttribute.get(entity) == null) {
                result.add(buildInsertStatement(single, entityType, withoutId, true));
            } else {
                entitiesHasId.add(entity);
            }
        }
        if (!entitiesHasId.isEmpty()) {
            result.add(buildInsertStatement(entitiesHasId, entityType, basicAttributes, false));
        }
        return result;
    }

}
