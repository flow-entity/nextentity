package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

public interface DatabaseColumnAttribute extends Attribute {

    ValueConvertor<?, ?> valueConvertor();

    default Class<?> getDatabaseColumnType() {
        Class<?> databaseType = valueConvertor().getDatabaseColumnType();
        if (databaseType == null) {
            return type();
        }
        return databaseType;
    }

}
