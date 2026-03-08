package io.github.nextentity.api.model;

/**
 * 三元组接口，表示包含三个元素的元组。
 * <p>
 * 提供类型安全的方法获取三个元素。
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Tuple3<A, B, C> extends Tuple2<A, B> {
    /**
     * 获取第三个元素。
     *
     * @return 第三个元素
     */
    default C get2() {
        return get(2);
    }
}
