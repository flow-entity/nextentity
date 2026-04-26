package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体插入后事件。
///
/// 在实体插入数据库成功后触发，可用于缓存更新、索引更新、消息通知等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class AfterInsertEvent<T> extends EntityEvent<T> {

    public AfterInsertEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.AFTER_INSERT, affectedRows);
    }

}
