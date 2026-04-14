package io.github.nextentity.api;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/// 实体引用基类，提供延迟加载功能。
///
/// 用于投影类中定义延迟加载的关联实体字段：
/// - 查询时只加载 ID，不加载完整实体
/// - 调用 get() 时触发延迟加载
/// - 加载后缓存实体，避免重复查询
///
/// ## 使用示例
/// ```java
/// // 定义投影类
/// public class OrderRef {
///     private Long id;
///     private UserRef user;  // 延迟加载用户
/// }
///
/// public class UserRef extends EntityReference<User, Long> {
///     private String name;  // 也可包含其他字段
/// }
///
/// // 使用
/// OrderRef order = repository.query().select(OrderRef.class).first();
/// Long userId = order.user.getId();    // 直接获取 ID（无查询）
/// User user = order.user.get();        // 触发延迟加载
/// ```
///
/// @param <T> 引用的实体类型
/// @param <ID> 实体 ID 类型
/// @author HuangChengwei
/// @since 2.2.0
public abstract class EntityReference<T, ID> {

    @Nullable
    protected ID id;

    @Nullable
    protected T entity;

    @Nullable
    protected Supplier<T> loader;

    /// 获取实体 ID。
    ///
    /// 此方法不会触发延迟加载，直接返回存储的 ID。
    ///
    /// @return 实体 ID，可能为 null
    @Nullable
    public ID getId() {
        return id;
    }

    /// 设置实体 ID。
    ///
    /// @param id 实体 ID
    public void setId(@Nullable ID id) {
        this.id = id;
    }

    /// 获取实体（延迟加载）。
    ///
    /// 如果实体未加载且有加载器，触发延迟加载。
    /// 加载后缓存实体，后续调用直接返回缓存。
    ///
    /// @return 实体实例，可能为 null（实体不存在）
    /// @throws IllegalStateException 如果 ID 或 loader 为 null
    @Nullable
    public T get() {
        if (entity == null && loader != null && id != null) {
            entity = loader.get();
        }
        if (entity == null && id == null) {
            throw new IllegalStateException("Cannot load entity: id is null");
        }
        if (entity == null && loader == null) {
            throw new IllegalStateException("Cannot load entity: loader is null");
        }
        return entity;
    }

    /// 检查实体是否已加载。
    ///
    /// @return 如果已加载返回 true，否则返回 false
    public boolean isLoaded() {
        return entity != null;
    }

    /// 设置延迟加载器。
    ///
    /// 加载器由 ProjectionFieldHandler.postProcess() 注入。
    ///
    /// @param loader 延迟加载 Supplier
    @SuppressWarnings("unchecked")
    public void setLoader(@Nullable Supplier<?> loader) {
        this.loader = (Supplier<T>) loader;
    }

    /// 返回实体或默认值。
    ///
    /// 如果实体未加载或为 null，返回默认值。
    /// 会触发延迟加载（如果有加载器）。
    ///
    /// @param defaultEntity 默认实体
    /// @return 实体或默认值
    public T orElse(T defaultEntity) {
        T result = getOrNull();
        return result != null ? result : defaultEntity;
    }

    /// 返回实体或 null（不抛出异常）。
    ///
    /// 如果无法加载实体，返回 null 而不抛出异常。
    ///
    /// @return 实体实例或 null
    @Nullable
    public T getOrNull() {
        if (id == null) {
            return null;
        }
        if (entity == null && loader != null) {
            entity = loader.get();
        }
        return entity;
    }

    /// 重置加载状态。
    ///
    /// 清除已加载的实体，下次调用 get() 会重新加载。
    /// 用于需要重新获取最新数据的场景。
    public void reset() {
        entity = null;
    }
}