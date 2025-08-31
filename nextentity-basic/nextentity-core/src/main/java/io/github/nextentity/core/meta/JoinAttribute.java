package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;

public interface JoinAttribute extends SchemaAttribute, Attribute, EntitySchema {

    String tableName();

    String joinName();

    String referencedColumnName();

}
