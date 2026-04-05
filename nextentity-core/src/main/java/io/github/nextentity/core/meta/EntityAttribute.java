package io.github.nextentity.core.meta;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;

/// 表示可持久化到数据库的实体属性的接口。
///
/// 扩展 {@link DatabaseColumnAttribute} 以支持实体特定操作，如
/// 列名、版本支持和标识支持。
public non-sealed interface EntityAttribute extends DatabaseColumnAttribute, SelectItem {

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
    /// @param value 数据库值
    default void setByDatabaseValue(Object entity, Object value) {
        value = valueConvertor().convertToEntityAttribute(TypeCastUtil.unsafeCast(value));
        set(entity, value);
    }

}
