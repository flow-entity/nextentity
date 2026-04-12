package io.github.nextentity.api.model;

/// 6元组接口。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @param <F> 第六个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple6<A, B, C, D, E, F> extends Tuple5<A, B, C, D, E> {
    /// 获取第六个元素。
    ///
    /// @return 第六个元素
    default F get5() {
        return get(5);
    }
}