package io.github.nextentity.api;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.core.expression.SliceImpl;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

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

    }