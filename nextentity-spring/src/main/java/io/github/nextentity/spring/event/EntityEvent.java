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
/// @since 2.2.2
public class EntityEvent<T> extends ApplicationEvent {

    private final Class<T> entityType;
    private final List<T> entities;
    private final EntityEventType eventType;
    private final int affectedRows;

    public EntityEvent(Object source, Class<T> entityType, List<T> entities, EntityEventType eventType, int affectedRows) {
        super(source);
        this.entityType = entityType;
        this.entities = entities;
        this.eventType = eventType;
        this.affectedRows = affectedRows;
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

    /// 获取受影响行数。
    ///
    /// Before 事件始终为 0，After 事件为实际影响行数；
    /// Predicate 类事件的 After 变体反映 SQL 语句的实际影响行数。
    public int getAffectedRows() {
        return affectedRows;
    }

}
