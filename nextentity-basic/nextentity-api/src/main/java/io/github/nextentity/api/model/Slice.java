package io.github.nextentity.api.model;

import java.util.List;

/**
 * 切片结果接口，包含切片数据和相关信息。
 *
 * @param <T> 数据类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Slice<T> {

    /**
     * 获取切片数据列表。
     *
     * @return 数据列表
     */
    List<T> data();

    /**
     * 获取总记录数。
     *
     * @return 总记录数
     */
    long total();

    /**
     * 获取偏移量。
     *
     * @return 偏移量
     */
    int offset();

    /**
     * 获取限制数量。
     *
     * @return 限制数量
     */
    int limit();

}