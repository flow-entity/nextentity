package io.github.nextentity.core.event;

import java.util.List;

public interface EntityEventListener {

    <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows);

}
