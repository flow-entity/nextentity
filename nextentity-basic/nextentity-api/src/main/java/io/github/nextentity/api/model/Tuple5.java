package io.github.nextentity.api.model;

/**
 * 五元组接口，表示包含五个元素的元组。
 * <p>
 * 提供类型安全的方法获取五个元素。
 *
 * @param <A> 第一个元素类型
 * @param <B> 第二个元素类型
 * @param <C> 第三个元素类型
 * @param <D> 第四个元素类型
 * @param <E> 第五个元素类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Tuple5<A, B, C, D, E> extends Tuple4<A, B, C, D> {

    /**
     * 获取第五个元素。
     *
     * @return 第五个元素
     */
    default E get4() {
        return get(4);
    }

}
