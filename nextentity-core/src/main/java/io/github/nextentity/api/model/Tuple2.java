package io.github.nextentity.api.model;

/// 2元组接口，表示包含 2 个元素的元组。
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @author HuangChengwei
/// @see Tuple 元组的使用示例
/// @since 1.0.0
public interface Tuple2<A, B> extends Tuple {

    /// 获取第一个元素。
    ///
    /// @return 第一个元素
    default A get0() {
        return get(0);
    }

    /// 获取第二个元素。
    ///
    /// @return 第二个元素
    default B get1() {
        return get(1);
    }

}