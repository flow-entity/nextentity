package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 数据访问层接口，提供基本的CRUD操作。
 *
 * @param <ID> 实体ID类型
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Repository<ID extends Serializable, T>
        extends Select<T>, Update<T>, EntityRoot<T> {

    /**
     * 根据ID获取实体。
     *
     * @param id 实体ID
     * @return 实体对象
     */
    T get(@NotNull ID id);

    /**
     * 根据ID列表获取多个实体。
     *
     * @param ids ID列表
     * @return 实体列表
     */
    List<T> getAll(@NotNull Iterable<? extends ID> ids);

    /**
     * 根据ID列表获取实体映射。
     *
     * @param ids ID列表
     * @return 实体映射，键为ID，值为实体对象
     */
    Map<ID, T> getMap(@NotNull Iterable<? extends ID> ids);

}
