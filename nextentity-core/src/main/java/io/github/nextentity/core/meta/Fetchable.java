package io.github.nextentity.core.meta;

import jakarta.persistence.FetchType;

/// 可懒加载属性标记接口。
///
/// 由 {@link EntitySchemaAttribute} 和 {@link ProjectionSchemaAttribute} 共同实现，
/// 用于统一判断属性是否需要延迟加载。
public interface Fetchable {

    /// 获取加载策略。
    ///
    /// 优先级：投影级 @Fetch > source().fetchType() > 全局默认
    ///
    /// @return FetchType.LAZY 或 FetchType.EAGER，或 null（使用全局默认）
    FetchType fetchType();

}
