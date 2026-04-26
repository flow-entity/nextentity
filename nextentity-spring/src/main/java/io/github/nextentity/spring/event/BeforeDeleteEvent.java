package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 实体删除前事件。
///
/// 在实体从数据库删除之前触发，可用于业务规则检查、级联处理、权限检查等场景。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class BeforeDeleteEvent<T> extends EntityEvent<T> {

    public BeforeDeleteEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.BEFORE_DELETE, affectedRows);
    }

}
