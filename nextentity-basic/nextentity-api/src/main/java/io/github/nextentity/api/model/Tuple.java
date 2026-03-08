package io.github.nextentity.api.model;

import java.util.List;

/**
 * 元组接口，表示一个包含多个元素的对象。
 * <p>
 * 用于表示查询结果中的一行数据，支持通过索引获取元素。
 *
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Tuple extends Iterable<Object> {

    /**
     * 根据索引获取元素。
     *
     * @param index 索引
     * @param <T> 元素类型
     * @return 元素
     */
    <T> T get(int index);

    /**
     * 获取元组的大小。
     *
     * @return 元组大小
     */
    int size();

    /**
     * 转换为列表。
     *
     * @return 元素列表
     */
    List<Object> toList();

    /**
     * 转换为数组。
     *
     * @return 元素数组
     */
    Object[] toArray();

}
