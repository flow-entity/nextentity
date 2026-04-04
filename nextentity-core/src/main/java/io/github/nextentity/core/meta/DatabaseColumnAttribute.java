package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

/// 表示映射到数据库列的属性接口。
///
/// 扩展 {@link Attribute} 以提供数据库特定的元数据，
/// 如值转换器和更新行为。
public interface DatabaseColumnAttribute extends Attribute {

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

    /// 获取数据库列类型。
    ///
    /// 如果值转换器指定了数据库类型，则返回该类型。
    /// 否则返回实体属性类型。
    ///
    /// @return 数据库列类型
    default Class<?> getDatabaseColumnType() {
        Class<?> databaseType = valueConvertor().getDatabaseColumnType();
        if (databaseType == null) {
            return type();
        }
        return databaseType;
    }

}
