package io.github.nextentity.api.model;

/// 9元组接口，表示包含 9 个元素的元组。
///
/// 提供类型安全的方法获取 9 个元素。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @param <F> 第六个元素类型
/// @param <G> 第七个元素类型
/// @param <H> 第八个元素类型
/// @param <I> 第九个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple9<A, B, C, D, E, F, G, H, I> extends Tuple8<A, B, C, D, E, F, G, H> {
    /// 获取第九个元素。
    ///
    /// @return 第九个元素
    default I get8() {
        return get(8);
    }
}