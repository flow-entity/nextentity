package io.github.nextentity.api.model;

import java.util.List;

/**
 * 切片参数接口，定义了切片的基本参数。
 *
 * @param <T> 数据类型
 * @param <U> 结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Sliceable<T, U> {

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

    /**
     * 收集切片数据并构建结果。
     *
     * @param list 数据列表
     * @param total 总记录数
     * @return 结果对象
     */
    U collect(List<T> list, long total);

}
