package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

public interface ProjectionAttribute extends Attribute {

    EntityAttribute source();

    default ValueConvertor valueConvertor() {
        return source().valueConvertor();
    }
}
