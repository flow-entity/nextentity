package io.github.nextentity.api;

/// 实体工厂接口，用于创建实体操作入口。
///
/// 提供类型安全的查询构建和实体操作的统一入口。
///
/// ## 功能特性
///
/// - **EntityQuery**：流式查询构建，支持 select、where、orderBy、分页等
/// - **EntityPersistor**：实体持久化操作，支持 insert、update、delete 及批量操作
///
/// ## 使用示例
///
/// ```java
/// // 创建查询构建器（仅查询）
/// EntityQuery<User> query = factory.query(User.class);
/// List<User> activeUsers = query
///     .where(User::getStatus).eq("ACTIVE")
///     .orderBy(User::getName).asc()
///     .list();
///
/// // 创建持久化操作器（仅 CRUD）
/// EntityPersistor<User> persistor = factory.persistor(User.class);
/// persistor.insert(newUser);
/// persistor.update()
///     .set(User::getStatus, "INACTIVE")
///     .where(User::getId).eq(1L)
///     .execute();
/// ```
///
/// ## 实例创建说明
///
/// 每次调用都会创建新实例，因为 EntityQuery 和 EntityPersistor
/// 在构建查询过程中会累积状态，不可共享。
///
/// @author HuangChengwei
/// @see EntityQuery 查询构建接口
/// @see EntityPersistor 持久化操作接口
/// @since 2.1.2
public interface EntityContext {

    /// 创建指定实体类型的查询构建器。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 新的查询构建器实例
    <T> EntityQuery<T> query(Class<T> entityType);

    /// 创建指定实体类型的持久化操作器。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 新的持久化操作器实例
    <T> EntityPersistor<T> persistor(Class<T> entityType);
}