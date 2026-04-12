package io.github.nextentity.api.model;

/// 3元组接口，表示包含 3 个元素的元组。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple3<A, B, C> extends Tuple2<A, B> {
    /// 获取第三个元素。
    ///
    /// @return 第三个元素
    default C get2() {
        return get(2);
    }
}