package io.github.nextentity.core.meta;

public interface ProjectionAttribute extends DatabaseColumnAttribute {

    EntityAttribute source();

    default ValueConverter<?, ?> valueConvertor() {
        return source().valueConvertor();
    }
}
