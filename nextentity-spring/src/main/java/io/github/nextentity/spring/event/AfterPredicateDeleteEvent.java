package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

public class AfterPredicateDeleteEvent<T> extends EntityEvent<T> {

    public AfterPredicateDeleteEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.AFTER_PREDICATE_DELETE, affectedRows);
    }

}
