package io.github.nextentity.api.model;

/**
 * 四元组接口，表示包含四个元素的元组。
 * <p>
 * 提供类型安全的方法获取四个元素。
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 * @param <D> 第四个元素类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Tuple4<A, B, C, D> extends Tuple3<A, B, C> {

    /**
     * 获取第四个元素。
     *
     * @return 第四个元素
     */
    default D get3() {
        return get(3);
    }

}
