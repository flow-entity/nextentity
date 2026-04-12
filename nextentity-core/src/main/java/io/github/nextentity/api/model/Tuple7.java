package io.github.nextentity.api.model;

/// 7元组接口。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @param <F> 第六个元素类型
/// @param <G> 第七个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple7<A, B, C, D, E, F, G> extends Tuple6<A, B, C, D, E, F> {
    /// 获取第七个元素。
    ///
    /// @return 第七个元素
    default G get6() {
        return get(6);
    }
}