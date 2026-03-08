package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;

/**
 * 实体根提供者接口，用于获取实体的根路径。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface EntityRootProvider<T> {

    /**
     * 获取实体的根路径。
     *
     * @return 实体根路径
     */
    EntityRoot<T> root();
}
