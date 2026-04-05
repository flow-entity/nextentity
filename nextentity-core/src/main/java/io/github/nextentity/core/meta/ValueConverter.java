package io.github.nextentity.core.meta;

/// 在实体属性类型和数据库列类型之间转换的转换器接口。
///
/// @param <X> 实体属性类型
/// @param <Y> 数据库列类型
public interface ValueConverter<X, Y> {

    /// 将实体属性值转换为数据库列值。
    ///
    /// @param attributeValue 实体属性值
    /// @return 数据库列值
    Y convertToDatabaseColumn(X attributeValue);

    /// 将数据库列值转换为实体属性值。
    ///
    /// @param databaseValue 数据库列值
    /// @return 实体属性值
    X convertToEntityAttribute(Y databaseValue);

    /// 获取数据库列类型。
    ///
    /// @return 数据库列类型，如果未指定则返回 null
    default Class<? extends Y> getDatabaseColumnType() {
        return null;
    }

}
