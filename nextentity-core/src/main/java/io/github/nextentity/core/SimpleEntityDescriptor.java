package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.meta.EntityType;

/// EntityContext 的简单 record 实现。
///
/// 提供实体上下文的基本信息，用于更新操作。
///
/// @param <E>         实体类型
/// @param entityType  实体类型元数据
/// @param entityClass 实体类
/// @author HuangChengwei
/// @since 2.1.2
public record SimpleEntityDescriptor<E>(
        EntityType entityType,
        Class<E> entityClass
) implements EntityDescriptor<E> {
}