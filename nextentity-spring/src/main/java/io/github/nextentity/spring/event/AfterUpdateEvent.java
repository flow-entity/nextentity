package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体更新后事件。
///
/// 在实体更新数据库成功后触发，可用于缓存更新、索引更新、消息通知等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class AfterUpdateEvent<T> extends EntityEvent<T> {

    public AfterUpdateEvent(Object source, Class<T> entityType, List<T> entities) {
        super(source, entityType, entities, EntityEventType.AFTER_UPDATED);
    }

}
