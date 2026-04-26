package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体插入前事件。
///
/// 在实体插入数据库之前触发，可用于数据验证、字段填充、审计日志等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class BeforeInsertEvent<T> extends EntityEvent<T> {

    public BeforeInsertEvent(Object source, Class<T> entityType, List<T> entities) {
        super(source, entityType, entities, EntityEventType.BEFORE_INSERT);
    }

}
