package io.github.nextentity.api;

import java.util.function.Supplier;

/// 继承 QueryBuilder 并添加实体更新能力的接口。
///
/// 该接口将查询构建和实体持久化操作融合为单一 API，
/// 提供统一的实体操作入口。
///
/// ## 功能特性
///
/// ### 查询能力（继承自 QueryBuilder）
/// - 流式查询构建：select, where, orderBy, limit/offset
/// - 投影查询：select 单/多字段，投影到 DTO
/// - 关联预加载：fetch 关联实体
/// - 条件操作符：eqIfNotNull, containsIfNotEmpty 等
///
/// ### 实体操作能力
/// - 单实体 CRUD：insert, update, delete
/// - 批量操作：insertAll, updateAll, deleteAll
/// - 条件更新/删除：update().set(...).where(...).execute()
/// - 事务支持：doInTransaction
///
/// ## 使用示例
///
/// ```java
/// // 查询操作
/// List<User> users = repository.query().list();
/// User user = repository.query()
///     .where(User::getId).eq(1L)
///     .first();
///
/// // 实体操作
/// repository.query().insert(newUser);
/// repository.query().update(existingUser);
/// repository.query().delete(oldUser);
///
/// // 条件批量更新（无需加载实体）
/// repository.query().update()
///     .set(User::getStatus, "INACTIVE")
///     .where(User::getLastLogin).lt(threshold)
///     .execute();
///
/// // 条件批量删除
/// repository.query().delete()
///     .where(User::getStatus).eq("INACTIVE")
///     .execute();
///
/// // 批量操作
/// repository.query().insertAll(List.of(user1, user2, user3));
///
/// // 事务支持
/// repository.query().doInTransaction(() -> {
///     repository.query().insert(user);
///     repository.query().update(relatedEntity);
/// });
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see EntityQuery 查询构建接口
/// @see UpdateSetStep 条件更新构建器
/// @see DeleteWhereStep 条件删除构建器
/// @since 2.0.0
public interface EntityPersistor<T> {

    /// 插入单个实体到数据库。
    ///
    /// @param entity 要插入的实体
    void insert(T entity);

    /// 批量插入多个实体到数据库。
    ///
    /// 该方法针对批量插入进行了优化，通常使用 JDBC 批量更新。
    ///
    /// @param entities 要插入的实体集合
    void insertAll(Iterable<T> entities);

    /// 更新数据库中的单个实体。
    ///
    /// @param entity 要更新的实体
    void update(T entity);

    /// 更新数据库中的多个实体。
    ///
    /// @param entities 要更新的实体集合
    void updateAll(Iterable<T> entities);

    /// 从数据库中删除单个实体。
    ///
    /// @param entity 要删除的实体
    void delete(T entity);

    /// 从数据库中删除多个实体。
    ///
    /// @param entities 要删除的实体集合
    void deleteAll(Iterable<T> entities);

    /// 创建条件更新构建器，用于带 WHERE 条件的批量更新。
    ///
    /// 条件更新允许在不先加载实体的情况下执行批量 UPDATE 操作。
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #update(Object)} 方法。
    ///
    /// @return 条件更新构建器实例
    UpdateSetStep<T> update();

    /// 创建条件删除构建器，用于带 WHERE 条件的批量删除。
    ///
    /// 条件删除允许在不先加载实体的情况下执行批量 DELETE 操作。
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #delete(Object)} 方法。
    ///
    /// @return 条件删除构建器实例
    DeleteWhereStep<T> delete();

    /// 在事务中执行不返回值的操作。
    ///
    /// @param command 要执行的命令
    void doInTransaction(Runnable command);

    /// 在事务中执行操作并返回结果。
    ///
    /// 如果操作成功，事务会自动提交；
    /// 如果抛出异常，则回滚事务。
    ///
    /// @param command 要执行的命令
    /// @param <X>     命令的返回类型
    /// @return 命令的结果
    <X> X doInTransaction(Supplier<X> command);
}