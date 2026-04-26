package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体删除后事件。
///
/// 在实体从数据库删除成功后触发，可用于缓存清理、索引清理、消息通知等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class AfterDeleteEvent<T> extends EntityEvent<T> {

    public AfterDeleteEvent(Object source, Class<T> entityType, List<T> entities) {
        super(source, entityType, entities, EntityEventType.AFTER_DELETED);
    }

}
