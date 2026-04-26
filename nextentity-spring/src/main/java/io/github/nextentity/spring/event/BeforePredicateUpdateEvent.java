package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

public class BeforePredicateUpdateEvent<T> extends EntityEvent<T> {

    public BeforePredicateUpdateEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.BEFORE_PREDICATE_UPDATE, affectedRows);
    }

}
