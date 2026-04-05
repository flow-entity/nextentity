package io.github.nextentity.core.meta;

/// 实体和数据库类型之间的类型转换的数据库类型接口。
///
/// 该接口提供在 Java 实体属性类型和相应的数据库列类型之间转换值的方法。
///
/// 实现处理特定类型的转换，如枚举到字符串/整数的转换，
/// 时间戳转换和自定义类型映射。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface DatabaseType {

    /// 获取此属性的数据库列类型。
    ///
    /// @return 数据库类型类
    Class<?> databaseType();

    /// 将值从实体属性类型转换为数据库列类型。
    ///
    /// @param value 实体属性值
    /// @return 转换后的数据库值
    Object toDatabaseType(Object value);

    /// 将值从数据库列类型转换为实体属性类型。
    ///
    /// @param value 数据库列值
    /// @return 转换后的实体属性值
    Object toAttributeType(Object value);

}
