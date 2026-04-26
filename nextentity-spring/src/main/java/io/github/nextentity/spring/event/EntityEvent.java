package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/// NextEntity 实体事件基类。
///
/// 所有实体 CRUD 事件的基类，包含实体类型、实体列表和事件类型信息。
/// 可通过 Spring 的 @EventListener 注解监听具体事件类型。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class EntityEvent<T> extends ApplicationEvent {

    private final Class<T> entityType;
    private final List<T> entities;
    private final EntityEventType eventType;

    public EntityEvent(Object source, Class<T> entityType, List<T> entities, EntityEventType eventType) {
        super(source);
        this.entityType = entityType;
        this.entities = List.copyOf(entities);
        this.eventType = eventType;
    }

    /// 获取实体类型。
    public Class<T> getEntityType() {
        return entityType;
    }

    /// 获取实体列表（不可变）。
    public List<T> getEntities() {
        return entities;
    }

    /// 获取事件类型。
    public EntityEventType getEventType() {
        return eventType;
    }

}
