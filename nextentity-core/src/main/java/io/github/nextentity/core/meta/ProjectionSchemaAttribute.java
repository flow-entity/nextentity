package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import jakarta.persistence.FetchType;

public non-sealed interface ProjectionSchemaAttribute extends ProjectionAttribute, SchemaAttribute {
    EntitySchemaAttribute source();

    /// 获取加载策略。
    ///
    /// 优先级：投影级 @Fetch > source().fetchType() > 全局默认
    ///
    /// @return FetchType.LAZY 或 FetchType.EAGER，或 null（使用全局默认）
    FetchType fetchType();
}
