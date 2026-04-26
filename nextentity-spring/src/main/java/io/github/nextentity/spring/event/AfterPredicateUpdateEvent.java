package io.github.nextentity.spring.event;

import io.github.nextentity.core.event.EntityEventType;

import java.util.List;

/// 条件更新后事件。
///
/// 在基于条件的批量更新（如 {@code repository.update().where(...).execute()}）执行成功后触发。
/// 可用于缓存清理、索引更新、消息通知等场景。
///
/// **注意：**条件更新不加载实体数据，{@link #getEntities()} 始终返回空列表；
/// 实际影响行数可通过 {@link #getAffectedRows()} 获取。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.1.0
public class AfterPredicateUpdateEvent<T> extends EntityEvent<T> {

    public AfterPredicateUpdateEvent(Object source, Class<T> entityType, List<T> entities, int affectedRows) {
        super(source, entityType, entities, EntityEventType.AFTER_PREDICATE_UPDATE, affectedRows);
    }

}
