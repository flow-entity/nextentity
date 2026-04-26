package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

public class BeforePredicateDeleteEvent<T> extends EntityEvent<T> {

    public BeforePredicateDeleteEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.BEFORE_PREDICATE_DELETE, affectedRows);
    }

}
