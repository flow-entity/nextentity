package io.github.nextentity.api.model;

/// 4元组接口，表示包含 4 个元素的元组。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple4<A, B, C, D> extends Tuple3<A, B, C> {

    /// 获取第四个元素。
    ///
    /// @return 第四个元素
    default D get3() {
        return get(3);
    }

}