package io.github.nextentity.core.meta;

/// 用于将投影字段映射到实体属性的投影属性接口。
///
/// 此接口扩展 {@link DatabaseColumnAttribute}，提供投影字段与其对应实体属性之间的链接。
///
/// 投影属性从其源实体属性继承值转换。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface ProjectionAttribute extends DatabaseColumnAttribute {

    /// 获取此投影属性映射的源实体属性。
    ///
    /// @return 源实体属性
    EntityAttribute source();

    /// 获取此属性的值转换器。
    ///
    /// 委托给源实体属性的值转换器。
    ///
    /// @return 来自源实体属性的值转换器
    default ValueConverter<?, ?> valueConvertor() {
        return source().valueConvertor();
    }
}
