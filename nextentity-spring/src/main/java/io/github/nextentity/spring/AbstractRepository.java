package io.github.nextentity.spring;

import io.github.nextentity.api.*;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// 抽象 Repository 基类，提供基本的数据库 CRUD 操作和查询构建功能。
///
/// 该类是 NextEntity 框架的核心抽象类，支持通过 JDBC 或 JPA 进行数据库操作。
/// 子类可以通过继承该类获得完整的 Repository 功能，包括：
/// - 插入、更新、删除操作
/// - 条件批量更新和删除
/// - 类型安全的查询构建
///
/// 使用示例：
/// ```java
/// @Component
/// public class UserRepository extends AbstractRepository<User, Long> {
///     public List<User> findActiveUsers() {
///         return query().where(path(User::getStatus)).eq("ACTIVE").list();
///     }
/// }
/// ```
///
/// @param <T>  实体类型
/// @param <ID> 主键类型
/// @author HuangChengwei
/// @since 1.0.0
public abstract class AbstractRepository<T, ID> {

    private volatile Metamodel metamodel = JpaMetamodel.of();

    /// 主键类型
    protected final Class<ID> idType;
    /// 实体类型
    protected final Class<T> entityType;

    /// 查询构建器，用于构建类型安全的查询
    private QueryBuilder<T> queryBuilder;
    /// 更新执行器，用于执行插入、更新、删除操作
    private UpdateExecutor updateExecutor;

    /// ID 路径表达式，用于构建基于主键的查询条件
    protected final Path<T, ID> idPath = getTidPath();

    /// ID 提取函数缓存，用于 findMapById 和 findMapAll 方法
    protected final Function<? super T, ? extends ID> idExtractor = getIdFunction();

    /// 获取主键路径表达式。
    ///
    /// 子类可重写此方法以自定义主键路径的获取方式。
    /// 默认实现通过 Metamodel 从实体元数据中获取 ID 属性名称。
    ///
    /// @return 主键路径表达式
    protected Path<T, ID> getTidPath() {
        EntityAttribute id = metamodel.getEntity(entityType).id();
        return Path.of(id.name());
    }

    /// 获取 ID 提取函数。
    ///
    /// 子类可重写此方法以自定义 ID 的提取方式。
    /// 默认实现通过 Metamodel 从实体中反射获取 ID 值。
    /// PersistableRepository 重写此方法使用 Persistable::getId 方法引用。
    ///
    /// @return 从实体中提取 ID 的函数
    protected Function<? super T, ? extends ID> getIdFunction() {
        EntityAttribute id = metamodel.getEntity(entityType).id();
        return entity -> TypeCastUtil.unsafeCast(id.get(entity));
    }

    /// 创建 Repository 实例。
    ///
    /// 自动检测实体类型和主键类型，
    /// 并通过 NextEntityFactory 初始化查询构建器和更新执行器。
    protected AbstractRepository() {
        GenericType<T, ID> genericType = getGenericType();
        this.idType = genericType.idType();
        this.entityType = genericType.entityType();
    }

    /// 自动注入 NextEntityFactory 并初始化组件。
    ///
    /// 该方法通过 Spring 的自动注入机制获取 NextEntityFactory，
    /// 然后创建查询构建器和更新执行器。
    ///
    /// @param factory NextEntity 工厂
    @Autowired
    protected void setFactory(NextEntityFactory factory) {
        this.queryBuilder = factory.queryBuilder(entityType);
        this.updateExecutor = factory.updateExecutor();
    }

    /// 自动注入 Metamodel 实例。
    ///
    /// 如果容器中存在 Metamodel Bean，则使用注入的实例；
    /// 否则使用默认的 JpaMetamodel 实例。
    ///
    /// @param metamodel 元模型实例
    @Autowired(required = false)
    protected void setMetamodel(Metamodel metamodel) {
        if (metamodel != null) {
            this.metamodel = metamodel;
        }
    }

    /// 获取查询构建器，用于构建类型安全的查询。
    ///
    /// @return 查询构建器实例
    protected QueryBuilder<T> query() {
        return queryBuilder;
    }

    /// 获取实体根对象，用于构建路径表达式。
    ///
    /// @return 实体根对象实例
    protected EntityRoot<T> root() {
        return EntityRoot.of();
    }

    /// 获取主键类型。
    ///
    /// @return 主键类型的 Class 对象
    public Class<ID> idType() {
        return idType;
    }

    /// 获取实体类型。
    ///
    /// @return 实体类型的 Class 对象
    public Class<T> entityType() {
        return entityType;
    }

    /// 插入单个实体到数据库。
    ///
    /// 该操作在事务中执行。
    ///
    /// @param entity 要插入的实体，不能为 null
    @Transactional
    public void insert(@NonNull T entity) {
        updateExecutor.insert(entity, entityType);
    }

    /// 批量插入多个实体到数据库。
    ///
    /// 该操作在事务中执行，支持批量优化。
    ///
    /// @param entities 要插入的实体集合，不能为 null
    @Transactional
    public void insertAll(@NonNull Iterable<T> entities) {
        updateExecutor.insertAll(entities, entityType);
    }

    /// 批量更新多个实体。
    ///
    /// 该操作在事务中执行，支持批量优化。
    ///
    /// @param entities 要更新的实体集合，不能为 null
    @Transactional
    public void updateAll(@NonNull Iterable<T> entities) {
        updateExecutor.updateAll(entities, entityType);
    }

    /// 更新单个实体。
    ///
    /// 该操作在事务中执行。
    ///
    /// @param entity 要更新的实体，不能为 null
    @Transactional
    public void update(@NonNull T entity) {
        updateExecutor.update(entity, entityType);
    }

    /// 批量删除多个实体。
    ///
    /// 该操作在事务中执行。
    ///
    /// @param entities 要删除的实体集合，不能为 null
    @Transactional
    public void deleteAll(@NonNull Iterable<T> entities) {
        updateExecutor.deleteAll(entities, entityType);
    }

    /// 删除单个实体。
    ///
    /// 该操作在事务中执行。
    ///
    /// @param entity 要删除的实体，不能为 null
    @Transactional
    public void delete(@NonNull T entity) {
        updateExecutor.delete(entity, entityType);
    }

    /// 创建条件更新构建器，用于带 WHERE 条件的批量更新。
    ///
    /// 使用示例：
    /// <pre>{@code
    /// int updated = repository.update()
    ///     .set(User::getStatus, "ARCHIVED")
    ///     .where(User::getLastLoginAt).lt(threshold)
    ///     .execute();
    /// }</pre>
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #updateAll(Iterable)} 方法。
    ///
    /// @return 条件更新构建器实例
    /// @since 2.1
    @Transactional
    public UpdateSetStep<T> update() {
        return updateExecutor.update(entityType);
    }

    /// 创建条件删除构建器，用于带 WHERE 条件的批量删除。
    ///
    /// 使用示例：
    /// <pre>{@code
    /// int deleted = repository.delete()
    ///     .where(User::getStatus).eq("INACTIVE")
    ///     .execute();
    /// }</pre>
    ///
    /// 注意：此方法不支持乐观锁机制。
    /// 如需乐观锁保护，请使用 {@link #deleteAll(Iterable)} 方法。
    ///
    /// @return 条件删除构建器实例
    /// @since 2.1
    @Transactional
    public DeleteWhereStep<T> delete() {
        return updateExecutor.delete(entityType);
    }


    /// 根据主键查找实体。
    ///
    /// 注意：此方法依赖 Metamodel 获取主键信息。
    /// 对于非 Persistable 实体，需要确保实体类标注了 JPA 注解（@Entity、@Id）。
    ///
    /// @param id 主键值
    /// @return 包含实体的 Optional，如果未找到则返回空 Optional
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(query().where(idPath).eq(id).first());
    }

    /// 根据主键获取实体。
    ///
    /// 注意：此方法依赖 Metamodel 获取主键信息。
    /// 对于非 Persistable 实体，需要确保实体类标注了 JPA 注解（@Entity、@Id）。
    ///
    /// @param id 主键值
    /// @return 实体对象，如果未找到则返回 null
    public T getById(ID id) {
        return query().where(idPath).eq(id).first();
    }

    /// 根据主键集合查找所有匹配的实体。
    ///
    /// @param ids 主键值集合
    /// @return 匹配主键的实体列表
    public List<T> findAllById(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return query().where(idPath).in(ids).list();
    }

    /// 根据主键集合获取所有匹配的实体。
    ///
    /// 此方法是 {@link #findAllById(Collection)} 的别名。
    ///
    /// @param ids 主键值集合
    /// @return 匹配主键的实体列表
    public List<T> getAllById(@NonNull Collection<ID> ids) {
        return findAllById(ids);
    }

    /// 根据主键集合查找实体并返回以 ID 为键的映射。
    ///
    /// 注意：如果结果中存在重复 ID 的实体，将抛出 IllegalStateException。
    /// 正常情况下主键不应重复，但需留意此行为。
    ///
    /// @param ids 主键值集合
    /// @return 以 ID 为键、实体为值的映射
    public Map<ID, T> findMapById(@NonNull Collection<ID> ids) {
        List<T> entities = findAllById(ids);
        return entities.stream()
                .collect(Collectors.toMap(idExtractor, Function.identity()));
    }

    /// 查找所有实体并返回以 ID 为键的映射。
    ///
    /// 注意：
    /// - 此方法会将所有实体加载到内存中，对于大表请谨慎使用。
    /// - 如果结果中存在重复 ID 的实体，将抛出 IllegalStateException。
    ///
    /// @return 以 ID 为键、实体为值的映射
    public Map<ID, T> findMapAll() {
        return query().list().stream()
                .collect(Collectors.toMap(idExtractor, Function.identity()));
    }

    /// 检查指定主键的实体是否存在。
    ///
    /// @param id 主键值
    /// @return 如果实体存在返回 true，否则返回 false
    public boolean existsById(ID id) {
        return query().where(idPath).eq(id).exists();
    }

    /// 统计指定主键集合中存在的实体数量。
    ///
    /// @param ids 主键值集合
    /// @return 存在的实体数量
    public long countById(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return 0;
        }
        return query().where(idPath).in(ids).count();
    }

    /// 根据主键删除实体。
    ///
    /// 此方法使用直接条件删除以获得更好的性能
    /// （单次 DELETE 操作，而非 SELECT + DELETE）。
    ///
    /// 注意：此方式会绕过 JPA 生命周期回调（如 @PreRemove）。
    /// 如果需要触发回调，请使用 {@link #delete(Object)} 方法。
    ///
    /// @param id 主键值
    @Transactional
    public void deleteById(ID id) {
        delete().where(idPath).eq(id).execute();
    }

    /// 根据主键集合批量删除实体。
    ///
    /// 此方法使用直接条件删除以获得更好的性能
    /// （单次 DELETE 操作，而非 SELECT + 批量 DELETE）。
    ///
    /// 注意：此方式会绕过 JPA 生命周期回调（如 @PreRemove）。
    /// 如果需要触发回调，请使用 {@link #deleteAll(Iterable)} 方法。
    ///
    /// @param ids 主键值集合
    @Transactional
    public void deleteAllById(@NonNull Collection<ID> ids) {
        if (!ids.isEmpty()) {
            delete().where(idPath).in(ids).execute();
        }
    }

    /// 创建布尔类型字段的路径表达式。
    ///
    /// 用于构建类型安全的查询条件。
    ///
    /// @param path 字段引用
    /// @return 布尔路径表达式
    protected BooleanPath<T> path(PathRef.BooleanRef<T> path) {
        return Path.of(path);
    }

    /// 创建字符串类型字段的路径表达式。
    ///
    /// 用于构建类型安全的查询条件。
    ///
    /// @param path 字段引用
    /// @return 字符串路径表达式
    protected StringPath<T> path(PathRef.StringRef<T> path) {
        return Path.of(path);
    }

    /// 创建数字类型字段的路径表达式。
    ///
    /// 用于构建类型安全的查询条件，支持数值运算。
    ///
    /// @param path 字段引用
    /// @param <U>  数字类型
    /// @return 数字路径表达式
    protected <U extends Number> NumberPath<T, U> path(PathRef.NumberRef<T, U> path) {
        return Path.of(path);
    }

    /// 创建 Long 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Long 路径表达式
    protected NumberPath<T, Long> path(PathRef.LongRef<T> path) {
        return Path.of(path);
    }

    /// 创建 Integer 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Integer 路径表达式
    protected NumberPath<T, Integer> path(PathRef.IntegerRef<T> path) {
        return Path.of(path);
    }

    /// 创建 Short 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Short 路径表达式
    protected NumberPath<T, Short> path(PathRef.ShortRef<T> path) {
        return Path.of(path);
    }

    /// 创建 Byte 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Byte 路径表达式
    protected NumberPath<T, Byte> path(PathRef.ByteRef<T> path) {
        return Path.of(path);
    }

    /// 创建 Double 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Double 路径表达式
    protected NumberPath<T, Double> path(PathRef.DoubleRef<T> path) {
        return Path.of(path);
    }

    /// 创建 Float 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return Float 路径表达式
    protected NumberPath<T, Float> path(PathRef.FloatRef<T> path) {
        return Path.of(path);
    }

    /// 创建 BigDecimal 类型字段的路径表达式。
    ///
    /// @param path 字段引用
    /// @return BigDecimal 路径表达式
    protected NumberPath<T, BigDecimal> path(PathRef.BigDecimalRef<T> path) {
        return Path.of(path);
    }

    /// 创建通用类型字段的路径表达式。
    ///
    /// 用于构建类型安全的查询条件。
    ///
    /// @param path 字段引用
    /// @param <U>  字段值类型
    /// @return 路径表达式
    protected <U> Path<T, U> path(PathRef<T, U> path) {
        return Path.of(path);
    }

    /// 创建实体关联字段的路径表达式。
    ///
    /// 用于访问关联实体属性，支持嵌套查询。
    ///
    /// @param path 字段引用
    /// @param <U>  关联实体类型
    /// @return 实体路径表达式
    protected <U extends Entity> EntityPath<T, U> path(PathRef.EntityPathRef<T, U> path) {
        return Path.of(path);
    }

    /// 获取泛型类型信息。
    ///
    /// 通过 Spring 的 ResolvableType 解析子类的泛型参数，
    /// 自动推断实体类型和主键类型。
    ///
    /// @return 包含实体类型和主键类型的泛型信息
    protected GenericType<T, ID> getGenericType() {
        ResolvableType type = ResolvableType.forClass(getClass()).as(AbstractRepository.class);
        Class<T> entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        Class<ID> idType = TypeCastUtil.cast(type.resolveGeneric(1));
        return new GenericType<>(entityType, idType);
    }

    /// 泛型类型信息记录。
    ///
    /// 用于存储通过反射解析得到的实体类型和主键类型。
    ///
    /// @param <T>  实体类型
    /// @param <ID> 主键类型
    protected record GenericType<T, ID>(Class<T> entityType, Class<ID> idType) {
    }

    /// 在事务中执行操作。
    ///
    /// 该方法已标注 @Transactional，确保操作在事务上下文中执行。
    ///
    /// @param command 要执行的操作
    /// @param <X>     操作返回类型
    /// @return 操作结果
    @Transactional
    protected <X> X doInTransaction(Supplier<X> command) {
        return command.get();
    }

}
