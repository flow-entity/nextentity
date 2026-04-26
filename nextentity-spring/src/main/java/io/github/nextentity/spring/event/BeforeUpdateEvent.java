package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体更新前事件。
///
/// 在实体更新数据库之前触发，可用于数据验证、旧值备份、权限检查等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class BeforeUpdateEvent<T> extends EntityEvent<T> {

    public BeforeUpdateEvent(Object source, Class<T> entityType, List<T> entities) {
        super(source, entityType, entities, EntityEventType.BEFORE_UPDATED);
    }

}
