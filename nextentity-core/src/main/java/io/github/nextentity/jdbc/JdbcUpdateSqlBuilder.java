package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;

import java.util.List;

///
/// JDBC更新SQL构建器接口
///
/// 该接口定义了构建更新相关SQL语句的方法，包括插入、更新和删除语句的构建。
/// 为不同数据库类型的更新SQL构建器提供统一的规范。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface JdbcUpdateSqlBuilder {

    /// 构建插入SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @return 插入SQL语句列表
    List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, EntityType entityType);

    /// 构建更新SQL语句
    ///
    /// @param entities 实体集合
    /// @param entityType 实体类型
    /// @param excludeNull 是否排除空值
    /// @return 批量SQL语句对象
    BatchSqlStatement buildUpdateStatement(Iterable<?> entities,
                                           EntitySchema entityType,
                                           boolean excludeNull);

    /// 构建删除SQL语句
    ///
    /// @param entities 实体集合
    /// @param entity 实体类型
    /// @return 批量SQL语句对象
    BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity);

}
