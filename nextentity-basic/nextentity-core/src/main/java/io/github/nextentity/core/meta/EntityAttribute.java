package io.github.nextentity.core.meta;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;

public non-sealed interface EntityAttribute extends DatabaseColumnAttribute, SelectItem {

    String columnName();

    boolean isVersion();

    boolean isId();

    default Object getDatabaseValue(Object entity) {
        Object o = get(entity);
        return valueConvertor().convertToDatabaseColumn(TypeCastUtil.unsafeCast(o));
    }

    default void setByDatabaseValue(Object entity, Object value) {
        value = valueConvertor().convertToEntityAttribute(TypeCastUtil.unsafeCast(value));
        set(entity, value);
    }

}
