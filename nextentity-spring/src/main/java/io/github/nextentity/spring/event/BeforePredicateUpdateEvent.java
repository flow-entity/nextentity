package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 条件更新前事件。
///
/// 在基于条件的批量更新（如 {@code repository.update().where(...).execute()}）执行之前触发。
/// 可用于权限检查、操作审计等场景。
///
/// **注意：**由于条件更新在执行前无法确定影响范围，{@link #getEntities()} 始终返回空列表，
/// {@link #getAffectedRows()} 始终为 0。如需获取受影响的实体，请监听
/// {@link AfterPredicateUpdateEvent}，其 {@code affectedRows} 为实际影响行数。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class BeforePredicateUpdateEvent<T> extends EntityEvent<T> {

    public BeforePredicateUpdateEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.BEFORE_PREDICATE_UPDATE, affectedRows);
    }

}
