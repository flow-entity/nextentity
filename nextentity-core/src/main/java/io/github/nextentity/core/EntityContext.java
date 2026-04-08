package io.github.nextentity.core;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

public interface EntityContext<E> {

    /// 返回实体元数据注册表。
    ///
    /// @return Metamodel 实例
    Metamodel metamodel();

    /// 返回实体类型元数据。
    ///
    /// @return EntityType 实例
    EntityType entityType();

    /// 返回实体类。
    ///
    /// @return 实体 Class 实例
    Class<E> entityClass();
}