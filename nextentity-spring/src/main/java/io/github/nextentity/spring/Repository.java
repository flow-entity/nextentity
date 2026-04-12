package io.github.nextentity.spring;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.api.UpdateSetStep;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/// 标准 Repository 接口，提供实体 CRUD 操作和查询构建功能。
///
/// 该接口定义了通用的数据访问方法，可通过以下方式使用：
/// - 继承 {@link AbstractRepository} 创建自定义 Repository
/// - 注入 {@link GenericRepository} 或直接注入本接口
///
/// 方法命名遵循 Spring Data CrudRepository 风格。
///
/// @param <T>  实体类型
/// @param <ID> 主键类型
/// @author HuangChengwei
/// @since 2.1.4
public interface Repository<T, ID> {

    /// 获取查询构建器，用于构建类型安全的查询。
    ///
    /// @return 查询构建器实例
    EntityQuery<T> query();

    /// 查找所有实体。
    ///
    /// 注意：对于大表请谨慎使用，可能消耗大量内存。
    ///
    /// @return 所有实体列表
    List<T> findAll();

    /// 统计实体总数。
    ///
    /// @return 实体数量
    long count();

    /// 根据主键查找实体。
    ///
    /// @param id 主键值
    /// @return 包含实体的 Optional，如果未找到则返回空 Optional
    Optional<T> findById(ID id);

    /// 根据主键获取实体。
    ///
    /// @param id 主键值
    /// @return 实体对象，如果未找到则返回 null
    T getById(ID id);

    /// 根据主键集合查找所有匹配的实体。
    ///
    /// @param ids 主键值集合
    /// @return 匹配主键的实体列表
    List<T> findAllById(@NonNull Collection<? extends ID> ids);

    /// 根据主键集合查找实体并返回以 ID 为键的映射。
    ///
    /// @param ids 主键值集合
    /// @return 以 ID 为键、实体为值的映射
    Map<ID, T> findAllAsMapById(@NonNull Collection<? extends ID> ids);

    /// 查找所有实体并返回以 ID 为键的映射。
    ///
    /// 注意：此方法会将所有实体加载到内存，对于大表请谨慎使用。
    ///
    /// @return 以 ID 为键、实体为值的映射
    Map<ID, T> findAllAsMap();

    /// 检查指定主键的实体是否存在。
    ///
    /// @param id 主键值
    /// @return 如果实体存在返回 true，否则返回 false
    boolean existsById(ID id);

    /// 插入单个实体到数据库。
    ///
    /// @param entity 要插入的实体
    void insert(T entity);

    /// 批量插入多个实体到数据库。
    ///
    /// @param entities 要插入的实体集合
    void insertAll(@NonNull Iterable<T> entities);

    /// 更新单个实体。
    ///
    /// @param entity 要更新的实体
    void update(T entity);

    /// 批量更新多个实体。
    ///
    /// @param entities 要更新的实体集合
    void updateAll(@NonNull Iterable<T> entities);

    /// 删除单个实体。
    ///
    /// @param entity 要删除的实体
    void delete(T entity);

    /// 批量删除多个实体。
    ///
    /// @param entities 要删除的实体集合
    void deleteAll(@NonNull Iterable<T> entities);

    /// 删除所有实体。
    ///
    /// 警告：此方法会删除表中所有数据，请谨慎使用。
    ///
    /// 注意：此方式会绕过 JPA 生命周期回调（如 @PreRemove）。
    /// 如需触发回调，请使用 {@link #deleteAll(Iterable)} 方法。
    void deleteAll();

    /// 根据主键删除实体。
    ///
    /// @param id 主键值
    void deleteById(ID id);

    /// 根据主键集合批量删除实体。
    ///
    /// @param ids 主键值集合
    void deleteAllById(@NonNull Collection<? extends ID> ids);

    /// 创建条件更新构建器，用于带 WHERE 条件的批量更新。
    ///
    /// @return 条件更新构建器实例
    UpdateSetStep<T> update();

    /// 创建条件删除构建器，用于带 WHERE 条件的批量删除。
    ///
    /// @return 条件删除构建器实例
    DeleteWhereStep<T> delete();
}
