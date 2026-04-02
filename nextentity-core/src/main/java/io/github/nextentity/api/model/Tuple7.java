package io.github.nextentity.api.model;

/// 7元组接口，表示包含 7 个元素的元组。
///
/// 提供类型安全的方法获取 7 个元素。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @param <F> 第六个元素类型
/// @param <G> 第七个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple7<A, B, C, D, E, F, G> extends Tuple6<A, B, C, D, E, F> {
    /// 获取第七个元素。
    ///
    /// @return 第七个元素
    default G get6() {
        return get(6);
    }
}