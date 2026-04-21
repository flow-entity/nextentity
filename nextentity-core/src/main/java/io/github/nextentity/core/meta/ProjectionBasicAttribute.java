package io.github.nextentity.core.meta;

/// 投影基本属性接口，表示 DTO 投影中映射到实体基本字段的属性。
///
/// 例如投影类中的 {@code String employeeName} 映射到实体的 {@code name} 列。
///
/// @see ProjectionAttribute
/// @see EntityBasicAttribute
public non-sealed interface ProjectionBasicAttribute extends ProjectionAttribute {

    /// 获取此投影属性对应的实体基本属性。
    ///
    /// @return 源实体基本属性
    @Override
    EntityBasicAttribute source();
}
