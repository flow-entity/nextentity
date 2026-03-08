package io.github.nextentity.api.model;

import java.util.List;

/**
 * 分页结果收集器接口，用于收集分页数据并构建分页结果。
 *
 * @param <T> 数据类型
 * @param <R> 分页结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface PageCollector<T, R> extends Pageable, Sliceable<T, R> {

    /**
     * 收集分页数据并构建分页结果。
     *
     * @param list 当前页的数据列表
     * @param total 总记录数
     * @return 分页结果
     */
    R collect(List<T> list, long total);

    /**
     * 获取分页偏移量。
     *
     * @return 分页偏移量
     */
    @Override
    default int offset() {
        return Pageable.super.offset();
    }
}
