package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityBasicAttribute;

import java.util.Collection;

public class EntityAttributeSet extends AttributeSet<EntityAttribute> {
    private final EntityBasicAttribute id;
    private final EntityBasicAttribute version;

    public EntityAttributeSet(Collection<EntityAttribute> attributes, EntityBasicAttribute id, EntityBasicAttribute version) {
        super(attributes);
        this.id = id;
        this.version = version;
    }

    public EntityBasicAttribute id() {
        return id;
    }

    public EntityBasicAttribute version() {
        return version;
    }
}
