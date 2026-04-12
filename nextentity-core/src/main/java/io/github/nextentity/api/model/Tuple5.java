package io.github.nextentity.api.model;

/// 5元组接口。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @param <E> 第五个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple5<A, B, C, D, E> extends Tuple4<A, B, C, D> {

    /// 获取第五个元素。
    ///
    /// @return 第五个元素
    default E get4() {
        return get(4);
    }

}