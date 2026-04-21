package io.github.nextentity.core;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.UpdateStructure;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

public interface PersistExecutor {

    /// 插入单个实体到数据库中。
    ///
    /// 这是一个便利方法，封装了 `insertAll(Iterable, EntityContext)`。
    ///
    /// @param <T>        实体类型
    /// @param entity     要插入的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    default <T> void insert(@NonNull T entity, @NonNull PersistDescriptor<T> descriptor) {
        insertAll(ImmutableList.of(entity), descriptor);
    }

    /// 在数据库中批量插入多个实体。
    ///
    /// 该方法针对批量插入进行了优化，通常使用
    /// JDBC 批量更新或 JPA 批量处理。
    ///
    /// @param <T>        实体类型
    /// @param entities   要插入的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void insertAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor);

    /// 更新数据库中的多个实体并返回更新后的实例。
    ///
    /// 返回的实体可能包含更新后的值，例如生成的 ID
    /// 或更新操作后的版本号。
    ///
    /// @param <T>        实体类型
    /// @param entities   要更新的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void updateAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor);

    /// 更新数据库中的单个实体。
    ///
    /// 这是一个便利方法，封装了 `updateAll(Iterable, EntityContext)`。
    ///
    /// @param <T>        实体类型
    /// @param entity     要更新的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    ///
    default <T> void update(@NonNull T entity, @NonNull PersistDescriptor<T> descriptor) {
        updateAll(ImmutableList.of(entity), descriptor);
    }

    /// 从数据库中删除多个实体。
    ///
    /// @param <T>        实体类型
    /// @param entities   要删除的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entities 或 descriptor 为 null
    ///
    <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor);

    /// 从数据库中删除单个实体。
    ///
    /// 这是一个便利方法，封装了 `deleteAll(Iterable, EntityContext)`。
    ///
    /// @param <T>        实体类型
    /// @param entity     要删除的实体
    /// @param descriptor 实体上下文
    /// @throws NullPointerException 如果 entity 或 descriptor 为 null
    ///
    default <T> void delete(@NonNull T entity, @NonNull PersistDescriptor<T> descriptor) {
        deleteAll(ImmutableList.of(entity), descriptor);
    }

    <T> int update(UpdateStructure structure, @NonNull PersistDescriptor<T> descriptor);

    <T> int delete(ExpressionNode predicate, @NonNull PersistDescriptor<T> descriptor);

}
