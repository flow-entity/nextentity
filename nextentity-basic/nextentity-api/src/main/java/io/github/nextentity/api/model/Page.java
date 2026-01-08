package io.github.nextentity.api.model;

import java.util.List;

/**
 * 分页结果接口，包含分页数据和总记录数。
 *
 * @param <T> 数据类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Page<T> {
    /**
     * 获取当前页的数据列表。
     *
     * @return 数据列表
     */
    List<T> getItems();

    /**
     * 获取总记录数。
     *
     * @return 总记录数
     */
    long getTotal();
}
