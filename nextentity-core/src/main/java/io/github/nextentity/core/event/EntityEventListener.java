package io.github.nextentity.core.event;

import java.util.List;

/// 实体事件监听器接口。
///
/// 监听实体 CRUD 操作前后发布的事件。当注册多个监听器时，按注册顺序依次调用；
/// 若某个监听器抛出异常，后续监听器将不会被调用。
///
/// **Before 事件的异常契约：**Before 事件（如 {@link EntityEventType#BEFORE_INSERT}）
/// 在实际操作执行之前触发。若 Before 事件监听器抛出异常，该异常将向上传播，
/// 原定操作（insert/update/delete）将不会执行，After 事件也不会触发。
/// 监听器可利用此机制实现数据验证、权限检查等 veto 逻辑。
///
/// @author HuangChengwei
/// @since 2.1.0
public interface EntityEventListener {

    /// 处理实体事件。
    ///
    /// @param <T>         实体类型
    /// @param entityType  实体类
    /// @param eventType   事件类型
    /// @param entities    受影响的实体列表；
    ///                    对于 Predicate 类事件（如 {@link EntityEventType#BEFORE_PREDICATE_UPDATE}），
    ///                    因操作前无法确定影响范围，此列表始终为空
    /// @param affectedRows 受影响行数；
    ///                     Before 事件始终为 0，After 事件为实际影响行数；
    ///                     Predicate Before 事件为 0
    <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows);

}
