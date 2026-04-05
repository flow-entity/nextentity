package io.github.nextentity.api.model;

import java.util.List;
import java.util.function.Function;

/// 分片结果接口，包含分片数据及相关信息。
///
/// 用于表示分页查询的结果，包含当前页数据、总记录数、偏移量和限制数。
///
/// ## 使用示例
///
/// ```java
/// // 执行分片查询
/// Slice<User> slice = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .orderBy(User::getName).asc()
///     .slice(0, 20);
///
/// // 获取数据
/// List<User> users = slice.data();
///
/// // 获取分页信息
/// long total = slice.total();    // 总记录数
/// int offset = slice.offset();   // 当前偏移量
/// int limit = slice.limit();     // 每页大小
///
/// // 计算总页数
/// int totalPages = (int) Math.ceil((double) total / limit);
///
/// // 判断是否有下一页
/// boolean hasNext = offset + limit < total;
///
/// // 类型转换：将 Slice<User> 转换为 Slice<UserDTO>
/// Slice<UserDTO> dtoSlice = slice.map(user -> new UserDTO(user.getId(), user.getName()));
///
/// // 转换为自定义分页模型
/// MyPage<User> myPage = slice.to(s -> new MyPage<>(s.data(), s.total(), s.offset(), s.limit()));
/// ```
///
/// @param <T> 数据类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Slice<T> {

    /// 获取分片数据列表。
    ///
    /// @return 数据列表
    List<T> data();

    /// 获取总记录数。
    ///
    /// @return 总记录数
    long total();

    /// 获取偏移量。
    ///
    /// @return 偏移量
    int offset();

    /// 获取限制数。
    ///
    /// @return 限制数
    int limit();

    /// 将当前分片数据转换为其他模型类型。
    ///
    /// @param converter 转换函数，接收当前 Slice 并返回目标类型
    /// @param <R>       目标类型
    /// @return 转换后的数据对象
    default <R> R to(Function<Slice<T>, R> converter) {
        return converter.apply(this);
    }

    /// 将分片数据映射为新的类型。
    ///
    /// @param mapper 类型转换函数
    /// @param <R>    目标类型
    /// @return 包含转换后数据的分片对象
    <R> Slice<R> map(Function<? super T, ? extends R> mapper);

}