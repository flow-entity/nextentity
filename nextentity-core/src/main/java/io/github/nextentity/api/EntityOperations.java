package io.github.nextentity.api;

/// 实体操作的统一入口，合并查询构建和持久化操作。
///
/// 该接口继承 {@link EntityQuery} 的查询能力，
/// 并添加 {@link EntityPersistor} 的持久化操作，
/// 提供完整的实体 CRUD 和查询功能。
///
/// ## 功能特性
///
/// ### 查询能力（继承自 EntityQuery）
/// - 流式查询构建：select, where, orderBy, limit/offset
/// - 投影查询：select 单/多字段，投影到 DTO
/// - 关联预加载：fetch 关联实体
/// - 条件操作符：eqIfNotNull, containsIfNotEmpty 等
///
/// ### 持久化能力
/// - 单实体 CRUD：insert, update, delete
/// - 批量操作：insertAll, updateAll, deleteAll
/// - 条件更新/删除：update().set(...).where(...).execute()
/// - 事务支持：doInTransaction
///
/// ## 使用示例
///
/// ```java
/// // 查询操作
/// List<User> users = operations.list();
/// User user = operations
///     .where(User::getId).eq(1L)
///     .first();
///
/// // 持久化操作
/// operations.insert(newUser);
/// operations.update(existingUser);
/// operations.delete(oldUser);
///
/// // 条件批量更新（无需加载实体）
/// operations.update()
///     .set(User::getStatus, "INACTIVE")
///     .where(User::getLastLogin).lt(threshold)
///     .execute();
///
/// // 条件批量删除
/// operations.delete()
///     .where(User::getStatus).eq("INACTIVE")
///     .execute();
///
/// // 批量操作
/// operations.insertAll(List.of(user1, user2, user3));
///
/// // 事务支持
/// operations.doInTransaction(() -> {
///     operations.insert(user);
///     operations.update(relatedEntity);
/// });
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.2.0
/// @see EntityQuery 查询构建接口
/// @see EntityPersistor 持久化操作接口
/// @see UpdateSetStep 条件更新构建器
/// @see DeleteWhereStep 条件删除构建器
public interface EntityOperations<T> extends EntityQuery<T>, EntityPersistor<T> {

    /// 返回实体描述符，提供实体元数据访问。
    ///
    /// 实体描述符封装了实体的元信息，包括：
    /// - Metamodel：全局元数据注册表
    /// - EntityType：特定实体的类型元数据
    /// - Class<T>：实体类本身
    ///
    /// 主要用途：
    /// - 条件更新/删除时获取表名和列映射
    /// - 表达式构建时获取属性元数据
    /// - Repository 基类访问实体类型信息
    ///
    /// @return 实体描述符实例
    EntityDescriptor<T> descriptor();

}