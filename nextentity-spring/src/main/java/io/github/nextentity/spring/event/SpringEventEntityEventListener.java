package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventListener;
import io.github.nextentity.core.event.EntityEventType;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/// Spring 事件发布桥接监听器。
///
/// 实现 {@link EntityEventListener} 接口，将 NextEntity 核心模块的事件
/// 转换为 Spring ApplicationEvent 并通过 {@link ApplicationEventPublisher} 发布。
///
/// 使用方式：
/// 1. 配置 {@code nextentity.event-publishing=true}
/// 2. 使用 @EventListener 注解监听具体事件类型
///
/// @author HuangChengwei
/// @since 2.1.0
public class SpringEventEntityEventListener implements EntityEventListener {

    private final ApplicationEventPublisher eventPublisher;

    public SpringEventEntityEventListener(@NonNull ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows) {
        EntityEvent<T> event = createEvent(entityType, eventType, entities, affectedRows);
        eventPublisher.publishEvent(event);
    }

    private <T> EntityEvent<T> createEvent(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows) {
        return switch (eventType) {
            case BEFORE_INSERT -> new BeforeInsertEvent<>(this, entityType, entities, affectedRows);
            case AFTER_INSERT -> new AfterInsertEvent<>(this, entityType, entities, affectedRows);
            case BEFORE_UPDATE -> new BeforeUpdateEvent<>(this, entityType, entities, affectedRows);
            case AFTER_UPDATE -> new AfterUpdateEvent<>(this, entityType, entities, affectedRows);
            case BEFORE_PREDICATE_UPDATE -> new BeforePredicateUpdateEvent<>(this, entityType, entities, affectedRows);
            case AFTER_PREDICATE_UPDATE -> new AfterPredicateUpdateEvent<>(this, entityType, entities, affectedRows);
            case BEFORE_DELETE -> new BeforeDeleteEvent<>(this, entityType, entities, affectedRows);
            case AFTER_DELETE -> new AfterDeleteEvent<>(this, entityType, entities, affectedRows);
            case BEFORE_PREDICATE_DELETE -> new BeforePredicateDeleteEvent<>(this, entityType, entities, affectedRows);
            case AFTER_PREDICATE_DELETE -> new AfterPredicateDeleteEvent<>(this, entityType, entities, affectedRows);
        };
    }

}
