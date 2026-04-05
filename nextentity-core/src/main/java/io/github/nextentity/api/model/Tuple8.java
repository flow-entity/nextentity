package io.github.nextentity.api.model;

/// 8元组接口，表示包含 8 个元素的元组。
///
/// 提供类型安全的方法获取 8 个元素。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @param <F> 第六个元素类型
/// @param <G> 第七个元素类型
/// @param <H> 第八个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple8<A, B, C, D, E, F, G, H> extends Tuple7<A, B, C, D, E, F, G> {
    /// 获取第八个元素。
    ///
    /// @return 第八个元素
    default H get7() {
        return get(7);
    }
}