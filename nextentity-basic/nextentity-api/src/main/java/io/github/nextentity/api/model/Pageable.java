package io.github.nextentity.api.model;

/**
 * 分页参数接口，定义了分页的基本参数。
 *
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Pageable {

    /**
     * 获取当前页码。
     *
     * @return 当前页码
     */
    int page();

    /**
     * 获取每页大小。
     *
     * @return 每页大小
     */
    int size();

    /**
     * 计算分页偏移量。
     *
     * @return 分页偏移量
     */
    default int offset() {
        return (page() - 1) * size();
    }

}
