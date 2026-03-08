package io.github.nextentity.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 更新操作接口，提供插入、更新和删除方法。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Update<T> {

    /**
     * 插入单个实体。
     *
     * @param entity 实体对象
     * @return 插入后的实体对象
     */
    T insert(@NotNull T entity);

    /**
     * 批量插入实体。
     *
     * @param entities 实体列表
     * @return 插入后的实体列表
     */
    List<T> insert(@NotNull Iterable<T> entities);

    /**
     * 批量更新实体。
     *
     * @param entities 实体列表
     * @return 更新后的实体列表
     */
    List<T> update(@NotNull Iterable<T> entities);

    /**
     * 更新单个实体。
     *
     * @param entity 实体对象
     * @return 更新后的实体对象
     */
    T update(@NotNull T entity);

    /**
     * 批量删除实体。
     *
     * @param entities 实体列表
     */
    void delete(@NotNull Iterable<T> entities);

    /**
     * 删除单个实体。
     *
     * @param entity 实体对象
     */
    void delete(@NotNull T entity);

    /**
     * 更新实体的非空字段。
     *
     * @param entity 实体对象
     * @return 更新后的实体对象
     */
    T updateNonNullColumn(@NotNull T entity);

}
