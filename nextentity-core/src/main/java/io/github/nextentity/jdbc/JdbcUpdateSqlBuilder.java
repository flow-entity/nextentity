package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

import java.util.List;
import java.util.Map;

/// JDBC更新SQL构建器接口
///
/// 该接口定义了构建更新相关SQL语句的方法，包括插入、更新和删除语句的构建。
/// 为不同数据库类型的更新SQL构建器提供统一的规范。
///
/// @author HuangChengwei
/// @since 1.0.0
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
    /// @return 批量SQL语句对象
    BatchSqlStatement buildUpdateStatement(Iterable<?> entities,
                                           EntitySchema entityType);

    /// 构建删除SQL语句
    ///
    /// @param entities 实体集合
    /// @param entity 实体类型
    /// @return 批量SQL语句对象
    BatchSqlStatement buildDeleteStatement(Iterable<?> entities, EntityType entity);

    /// 构建条件更新SQL语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型，用于处理嵌套路径
    /// @param setValues      SET子句的列名和值映射
    /// @param whereCondition WHERE条件表达式，可以为null
    /// @return 更新SQL语句对象
    UpdateSqlStatement buildConditionalUpdateStatement(EntityType entity,
                                                       Metamodel metamodel,
                                                       Map<String, Object> setValues,
                                                       ExpressionNode whereCondition);

    /// 构建条件删除SQL语句
    ///
    /// @param entity         实体类型
    /// @param metamodel      实体元模型，用于处理嵌套路径
    /// @param whereCondition WHERE条件表达式，可以为null
    /// @return 删除SQL语句对象
    DeleteSqlStatement buildConditionalDeleteStatement(EntityType entity,
                                                       Metamodel metamodel,
                                                       ExpressionNode whereCondition);

}
