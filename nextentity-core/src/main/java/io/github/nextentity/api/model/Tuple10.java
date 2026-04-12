package io.github.nextentity.api.model;

/// 10元组接口。
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
/// @param <J> 第十个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple10<A, B, C, D, E, F, G, H, I, J> extends Tuple9<A, B, C, D, E, F, G, H, I> {
    /// 获取第十个元素。
    ///
    /// @return 第十个元素
    default J get9() {
        return get(9);
    }
}