package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 条件删除前事件。
///
/// 在基于条件的批量删除（如 {@code repository.delete().where(...).execute()}）执行之前触发。
/// 可用于业务规则检查、权限检查等场景。
///
/// **注意：**由于条件删除在执行前无法确定影响范围，{@link #getEntities()} 始终返回空列表，
/// {@link #getAffectedRows()} 始终为 0。如需获取受影响的行数，请监听
/// {@link AfterPredicateDeleteEvent}，其 {@code affectedRows} 为实际影响行数。
/// 若需在删除前访问实体数据，请改用基于实体的 {@link BeforeDeleteEvent}。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class BeforePredicateDeleteEvent<T> extends EntityEvent<T> {

    public BeforePredicateDeleteEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.BEFORE_PREDICATE_DELETE, affectedRows);
    }

}
