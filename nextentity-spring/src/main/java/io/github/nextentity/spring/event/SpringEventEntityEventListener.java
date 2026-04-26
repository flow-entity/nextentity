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
    public <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities) {
        EntityEvent<T> event = createEvent(entityType, eventType, entities);
        eventPublisher.publishEvent(event);
    }

    private <T> EntityEvent<T> createEvent(Class<T> entityType, EntityEventType eventType, List<T> entities) {
        return switch (eventType) {
            case BEFORE_INSERT -> new BeforeInsertEvent<>(this, entityType, entities);
            case AFTER_INSERT -> new AfterInsertEvent<>(this, entityType, entities);
            case BEFORE_UPDATED -> new BeforeUpdateEvent<>(this, entityType, entities);
            case AFTER_UPDATED -> new AfterUpdateEvent<>(this, entityType, entities);
            case BEFORE_DELETED -> new BeforeDeleteEvent<>(this, entityType, entities);
            case AFTER_DELETED -> new AfterDeleteEvent<>(this, entityType, entities);
        };
    }

}
