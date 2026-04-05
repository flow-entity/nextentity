package io.github.nextentity.api;

import io.github.nextentity.api.model.*;
import io.github.nextentity.core.expression.SliceImpl;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// 查询结果收集器接口。
///
/// 提供用于获取结果的终止操作，包括 offset/limit 风格的分页查询。
///
/// ## 分页注意事项
///
/// 当使用带 offset 参数的分页方法（如 `list(offset, limit)`、`slice(offset, limit)`）时，
/// 如果查询未指定排序字段（ORDER BY），系统将自动添加主键排序。
///
/// 建议显式调用 `orderBy()` 方法以确保结果的一致性和可预测性。
///
/// ```java
/// // 推荐：显式指定排序
/// repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .orderBy(User::getId).asc()
///     .list(0, 10);
///
/// // 不推荐：依赖自动排序
/// repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .list(0, 10);  // 自动添加主键排序
/// ```
///
/// ## 使用示例
///
/// ```java
/// // 基本查询 - 获取所有结果
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .list();
///
/// // 前 N 条结果 - 前 20 条记录
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .list(20);
///
/// // 分页结果 - 跳过 10 条，获取接下来 20 条
/// List<User> users = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .list(10, 20);
///
/// // 带锁查询
/// User user = repository.query()
///     .where(User::getId).eq(1L)
///     .lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
///     .first();
///
/// // 分片查询（带总数）
/// Slice<User> slice = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .slice(0, 20);
/// System.out.println("总数: " + slice.total());
/// ```
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Collector<T> {

    /// 获取总记录数。
    ///
    /// @return 总记录数
    long count();

    /// 检查记录是否存在。
    ///
    /// @return 是否存在记录
    boolean exists();

    /// 检查从指定偏移量开始是否存在记录。
    ///
    /// @param offset 检查存在性前跳过的记录数
    /// @return 在给定偏移量处或之后是否存在记录
    boolean exists(int offset);

    /// 获取所有结果列表。
    ///
    /// @return 所有结果列表
    List<T> list();

    /// 获取前 {@code limit} 条结果（offset = 0）。
    ///
    /// 注意：如果查询未指定排序字段，将自动添加主键排序。
    ///
    /// @param limit 返回的最大结果数
    /// @return 结果列表
    default List<T> list(int limit) {
        return list(0, limit);
    }

    /// 获取指定偏移量和限制数的结果。
    ///
    /// 注意：如果查询未指定排序字段，将自动添加主键排序。
    /// 建议显式调用 `orderBy()` 方法以确保结果的一致性和可预测性。
    ///
    /// @param offset 跳过的记录数
    /// @param limit  返回的最大结果数
    /// @return 结果列表
    List<T> list(int offset, int limit);

    /// 获取第一条结果。
    ///
    /// 注意：如果查询未指定排序字段，将自动添加主键排序。
    /// 建议显式调用 `orderBy()` 方法以确保结果的一致性和可预测性。
    ///
    /// @return 第一条结果，不存在则返回 null
    default T first() {
        List<T> list = list(1);
        return list.isEmpty() ? null : list.getFirst();
    }

    /// 获取单个结果。
    ///
    /// @return 单个结果，不存在则返回 null
    /// @throws IllegalStateException 如果找到多个结果
    T single();

    /// 获取指定偏移量和限制数的分片结果。
    ///
    /// 注意：如果查询未指定排序字段，将自动添加主键排序。
    /// 建议显式调用 `orderBy()` 方法以确保结果的一致性和可预测性。
    ///
    /// @param offset 偏移量
    /// @param limit  最大结果数
    /// @return 分片结果
    default Slice<T> slice(int offset, int limit) {
        long count = count();
        if (count <= offset) {
            return new SliceImpl<>(ImmutableList.of(), count, offset, limit);
        }
        return new SliceImpl<>(list(offset, limit), count, offset, limit);
    }

    /// 将查询转换为子查询构建器。
    ///
    /// @param <X> 子查询类型
    /// @return 子查询构建器
    <X> SubQueryBuilder<X, T> toSubQuery();

    /// 获取指定偏移量和最大结果数的列表，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param maxResult    最大结果数
    /// @param lockModeType 锁定模式
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link #list(int, int)} 配合 {@link LockStep#lock(LockModeType)} 方法代替。
    @Deprecated
    List<T> getList(int offset, int maxResult, LockModeType lockModeType);

    /// 获取指定偏移量和最大结果数的列表。
    ///
    /// @param offset    偏移量
    /// @param maxResult 最大结果数
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link #list(int, int)} 方法代替。
    @Deprecated
    default List<T> getList(int offset, int maxResult) {
        return getList(offset, maxResult, null);
    }

    /// 从指定偏移量开始获取所有结果。
    ///
    /// @param offset 偏移量
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link #list(int, int)} 方法代替，传入 limit = -1 或 Integer.MAX_VALUE。
    @Deprecated
    default List<T> offset(int offset) {
        return getList(offset, -1, null);
    }

    /// 获取指定数量的结果。
    ///
    /// @param limit 最大结果数
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link #list(int)} 方法代替。
    @Deprecated
    default List<T> limit(int limit) {
        return getList(0, limit, null);
    }

    /// 检查从指定偏移量开始是否存在记录。
    ///
    /// @param offset 偏移量
    /// @return 是否存在记录
    /// @deprecated 已废弃，请使用 {@link #exists(int)} 方法代替。
    @Deprecated
    default boolean exist(int offset) {
        return exists(offset);
    }

    /// 从指定偏移量开始获取第一个结果的Optional。
    ///
    /// @param offset 偏移量
    /// @return 第一个结果的Optional
    /// @deprecated 已废弃，请使用 {@link #list(int, int)} 方法配合 Optional.ofNullable 处理。
    @Deprecated
    default Optional<T> first(int offset) {
        return Optional.ofNullable(getFirst(offset));
    }

    /// 获取第一个结果。
    ///
    /// @return 第一个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link #first()} 方法代替。
    @Deprecated
    default T getFirst() {
        return getFirst(-1);
    }

    /// 从指定偏移量开始获取第一个结果。
    ///
    /// @param offset 偏移量
    /// @return 第一个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link #list(int, int)} 方法获取结果后取第一个元素。
    @Deprecated
    default T getFirst(int offset) {
        List<T> list = getList(offset, 1);
        return list.isEmpty() ? null : list.getFirst();
    }

    /// 获取单个结果，如果不存在则抛出异常。
    ///
    /// @return 单个结果
    /// @throws NullPointerException 如果结果不存在
    /// @deprecated 已废弃，请使用 {@link #single()} 方法配合 Objects.requireNonNull 处理。
    @Deprecated
    default T requireSingle() {
        return Objects.requireNonNull(getSingle(-1));
    }

    /// 从指定偏移量开始获取单个结果的Optional。
    ///
    /// @param offset 偏移量
    /// @return 单个结果的Optional
    /// @deprecated 已废弃，请使用 {@link #single()} 方法代替。
    @Deprecated
    default Optional<T> single(int offset) {
        return Optional.ofNullable(getSingle(offset));
    }

    /// 获取单个结果。
    ///
    /// @return 单个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link #single()} 方法代替。
    @Deprecated
    default T getSingle() {
        return getSingle(-1);
    }

    /// 从指定偏移量开始获取单个结果。
    ///
    /// @param offset 偏移量
    /// @return 单个结果，不存在则返回null
    /// @throws IllegalStateException 如果找到多个结果
    /// @deprecated 已废弃，请使用 {@link #single()} 方法代替。
    @Deprecated
    default T getSingle(int offset) {
        List<T> list = getList(offset, 2);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.getFirst();
    }

    /// 获取所有结果。
    ///
    /// @return 所有结果的列表
    /// @deprecated 已废弃，请使用 {@link #list()} 方法代替。
    @Deprecated
    default List<T> getList() {
        return getList(-1, -1);
    }

    /// 检查是否存在记录。
    ///
    /// @return 是否存在记录
    /// @deprecated 已废弃，请使用 {@link #exists()} 方法代替。
    @Deprecated
    default boolean exist() {
        return exist(-1);
    }

    /// 获取第一个结果的Optional，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 第一个结果的Optional
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #first()} 方法代替。
    @Deprecated
    default Optional<T> first(LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(lockModeType));
    }

    /// 从指定偏移量开始获取第一个结果的Optional，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param lockModeType 锁定模式
    /// @return 第一个结果的Optional
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #list(int, int)} 方法代替。
    @Deprecated
    default Optional<T> first(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(offset, lockModeType));
    }

    /// 获取第一个结果，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 第一个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #first()} 方法代替。
    @Deprecated
    default T getFirst(LockModeType lockModeType) {
        return getFirst(-1, lockModeType);
    }

    /// 从指定偏移量开始获取第一个结果，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param lockModeType 锁定模式
    /// @return 第一个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #list(int, int)} 方法代替。
    @Deprecated
    default T getFirst(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 1, lockModeType);
        return list.isEmpty() ? null : list.getFirst();
    }

    /// 获取单个结果，如果不存在则抛出异常，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 单个结果
    /// @throws NullPointerException 如果结果不存在
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #single()} 方法代替。
    @Deprecated
    default T requireSingle(LockModeType lockModeType) {
        return Objects.requireNonNull(getSingle(-1, lockModeType));
    }

    /// 获取单个结果的Optional，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 单个结果的Optional
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #single()} 方法代替。
    @Deprecated
    default Optional<T> single(LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(lockModeType));
    }

    /// 从指定偏移量开始获取单个结果的Optional，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param lockModeType 锁定模式
    /// @return 单个结果的Optional
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #single()} 方法代替。
    @Deprecated
    default Optional<T> single(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(offset, lockModeType));
    }

    /// 获取单个结果，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 单个结果，不存在则返回null
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #single()} 方法代替。
    @Deprecated
    default T getSingle(LockModeType lockModeType) {
        return getSingle(-1, lockModeType);
    }

    /// 从指定偏移量开始获取单个结果，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param lockModeType 锁定模式
    /// @return 单个结果，不存在则返回null
    /// @throws IllegalStateException 如果找到多个结果
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #single()} 方法代替。
    @Deprecated
    default T getSingle(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 2, lockModeType);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.getFirst();
    }

    /// 从指定偏移量开始获取所有结果，并指定锁定模式。
    ///
    /// @param offset       偏移量
    /// @param lockModeType 锁定模式
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #list(int, int)} 方法代替。
    @Deprecated
    default List<T> offset(int offset, LockModeType lockModeType) {
        return getList(offset, -1, lockModeType);
    }

    /// 获取指定数量的结果，并指定锁定模式。
    ///
    /// @param limit        最大结果数
    /// @param lockModeType 锁定模式
    /// @return 结果列表
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #list(int)} 方法代替。
    @Deprecated
    default List<T> limit(int limit, LockModeType lockModeType) {
        return getList(0, limit, lockModeType);
    }

    /// 获取所有结果，并指定锁定模式。
    ///
    /// @param lockModeType 锁定模式
    /// @return 所有结果的列表
    /// @deprecated 已废弃，请使用 {@link LockStep#lock(LockModeType)} 配合 {@link #list()} 方法代替。
    @Deprecated
    default List<T> getList(LockModeType lockModeType) {
        return getList(-1, -1, lockModeType);
    }

    /// 使用指定的切片器对结果进行切片。
    ///
    /// @param sliceable 切片器
    /// @param <R>       结果类型
    /// @return 切片结果
    /// @deprecated 已废弃，请使用 {@link #slice(int, int)} 方法代替。
    @Deprecated
    default <R> R slice(Sliceable<T, R> sliceable) {
        long total = count();
        if (total <= 0) {
            return sliceable.collect(ImmutableList.of(), total);
        }
        List<T> list = list(sliceable.offset(), sliceable.limit());
        return sliceable.collect(list, total);
    }


    /// 将查询转换为子查询构建器。
    ///
    /// @param <X> 子查询类型
    /// @return 子查询构建器
    /// @deprecated 已废弃，请使用 {@link #toSubQuery()} 方法代替。
    @Deprecated
    default <X> SubQueryBuilder<X, T> asSubQuery() {
        return toSubQuery();
    }


    /// 获取分页结果。
    ///
    /// @param pageable 分页参数
    /// @return 分页结果
    /// @deprecated 已废弃，请使用 {@link #slice(int, int)} 方法代替。
    @Deprecated
    default Page<T> getPage(Pageable pageable) {
        long count = count();
        if (count <= 0) {
            return new Page<>() {
                @Override
                public List<T> getItems() {
                    return List.of();
                }

                @Override
                public long getTotal() {
                    return 0;
                }
            };
        }
        List<T> list = list(pageable.offset(), pageable.size());
        return new Page<>() {
            @Override
            public List<T> getItems() {
                return list;
            }

            @Override
            public long getTotal() {
                return count;
            }
        };
    }

    /// 使用指定的页面收集器获取分页结果。
    ///
    /// @param collector 页面收集器
    /// @param <R>       结果类型
    /// @return 分页结果
    /// @deprecated 已废弃，请使用 {@link #slice(int, int)} 方法代替。
    @Deprecated
    default <R> R getPage(PageCollector<T, R> collector) {
        return slice(collector);
    }
}