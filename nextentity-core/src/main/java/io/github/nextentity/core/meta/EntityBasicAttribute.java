package io.github.nextentity.core.meta;

import io.github.nextentity.core.TypeCastUtil;

public non-sealed interface EntityBasicAttribute extends EntityAttribute {

    @Override
    EntitySchema declareBy();

    /// 获取此属性的数据库列名。
    ///
    /// @return 列名
    String columnName();

    /// 检查此属性是否是乐观锁的版本字段。
    ///
    /// @return 如果是版本字段则返回 {@code true}，否则返回 {@code false}
    boolean isVersion();

    /// 检查此属性是否是标识（主键）字段。
    ///
    /// @return 如果是标识字段则返回 {@code true}，否则返回 {@code false}
    boolean isId();

    /// 使用值转换器从实体获取数据库值。
    ///
    /// @param entity 实体实例
    /// @return 数据库值
    default Object getDatabaseValue(Object entity) {
        Object o = get(entity);
        return valueConvertor().convertToDatabaseColumn(TypeCastUtil.unsafeCast(o));
    }

    /// 使用值转换器从数据库值设置实体属性值。
    ///
    /// @param entity 实体实例
    /// @param value  数据库值
    default void setByDatabaseValue(Object entity, Object value) {
        value = valueConvertor().convertToEntityAttribute(TypeCastUtil.unsafeCast(value));
        set(entity, value);
    }

    /// 获取此属性的值转换器。
    ///
    /// 转换器负责在实体属性类型和数据库列类型之间进行转换。
    ///
    /// @return 值转换器
    ValueConverter<?, ?> valueConvertor();

    /// 检查此属性是否可更新。
    ///
    /// @return 如果列可以更新则返回 {@code true}，否则返回 {@code false}
    boolean isUpdatable();
}
