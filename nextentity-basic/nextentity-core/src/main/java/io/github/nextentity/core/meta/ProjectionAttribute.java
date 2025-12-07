package io.github.nextentity.core.meta;

public interface ProjectionAttribute extends DatabaseColumnAttribute {

    EntityAttribute source();

    default ValueConvertor<?, ?> valueConvertor() {
        return source().valueConvertor();
    }
}
