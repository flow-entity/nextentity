package io.github.nextentity.core;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.api.UpdateSetStep;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/// 更新执行器接口，用于执行 INSERT、UPDATE 和 DELETE 操作。
///
/// 该接口提供批量和单实体持久化操作的方法，
/// 以及事务管理功能。
///
/// 实现通常使用 JDBC 批处理操作或 JPA 实体管理器
/// 来与数据库交互。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface UpdateExecutor {

    ///
    /// 插入单个实体到数据库中。
    ///
    /// 这是一个便利方法，封装了 `insertAll(Iterable, EntityContext)`。
    ///
    /// @param <T> 实体类型
    /// @param entity 要插入的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    ///
    default <T> void insert(@NonNull T entity, @NonNull EntityDescriptor<T> descriptor) {
        insertAll(ImmutableList.of(entity), descriptor);
    }

    ///
    /// 在数据库中批量插入多个实体。
    ///
    /// 该方法针对批量插入进行了优化，通常使用
    /// JDBC 批量更新或 JPA 批量处理。
    ///
    /// @param <T> 实体类型
    /// @param entities 要插入的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void insertAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor);

    ///
    /// 更新数据库中的多个实体并返回更新后的实例。
    ///
    /// 返回的实体可能包含更新后的值，例如生成的 ID
    /// 或更新操作后的版本号。
    ///
    /// @param <T> 实体类型
    /// @param entities 要更新的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void updateAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor);

    ///
    /// 更新数据库中的单个实体。
    ///
    /// 这是一个便利方法，封装了 `updateAll(Iterable, EntityContext)`。
    ///
    /// @param <T> 实体类型
    /// @param entity 要更新的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    ///
    default <T> void update(@NonNull T entity, @NonNull EntityDescriptor<T> descriptor) {
        updateAll(ImmutableList.of(entity), descriptor);
    }

    ///
    /// 从数据库中删除多个实体。
    ///
    /// @param <T> 实体类型
    /// @param entities 要删除的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull EntityDescriptor<T> descriptor);

    ///
    /// 从数据库中删除单个实体。
    ///
    /// 这是一个便利方法，封装了 `deleteAll(Iterable, EntityContext)`。
    ///
    /// @param <T> 实体类型
    /// @param entity 要删除的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    ///
    default <T> void delete(@NonNull T entity, @NonNull EntityDescriptor<T> descriptor) {
        deleteAll(ImmutableList.of(entity), descriptor);
    }

    ///
    /// 在事务中执行命令。
    ///
    /// 这是一个用于不返回值的操作的便利方法。
    ///
    /// @param command 要执行的命令
    ///
    default void doInTransaction(Runnable command) {
        doInTransaction(() -> {
            command.run();
            return null;
        });
    }

    ///
    /// 在事务中执行命令并返回其结果。
    ///
    /// 如果命令成功，事务会自动提交，
    /// 如果抛出异常，则回滚事务。
    ///
    /// @param <T> 命令的返回类型
    /// @param command 要执行的命令
    /// @return 命令的结果
    /// @throws RuntimeException 如果事务失败
    ///
    <T> T doInTransaction(Supplier<T> command);

    ///
    /// 为指定实体类型创建条件更新构建器。
    ///
    /// 条件更新构建器支持带 WHERE 条件的批量 UPDATE 操作。
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #updateAll(Iterable, EntityDescriptor)} 方法。
    ///
    /// @param <T> 实体类型
    /// @param descriptor 实体上下文
    /// @return 条件更新构建器实例
    /// @since 2.0.0
    <T> UpdateSetStep<T> update(@NonNull EntityDescriptor<T> descriptor);

    ///
    /// 为指定实体类型创建条件删除构建器。
    ///
    /// 条件删除构建器支持带 WHERE 条件的批量 DELETE 操作。
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #deleteAll(Iterable, EntityDescriptor)} 方法。
    ///
    /// @param <T> 实体类型
    /// @param descriptor 实体上下文
    /// @return 条件删除构建器实例
    /// @since 2.0.0
    <T> DeleteWhereStep<T> delete(@NonNull EntityDescriptor<T> descriptor);
}
