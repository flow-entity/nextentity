package io.github.nextentity.spring;

import io.github.nextentity.api.*;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.jdbc.ConnectionProvider;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.Supplier;

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
/// public class UserRepository extends AbstractRepository<User, Long> {
///     public UserRepository(JdbcTemplate jdbcTemplate) {
///         super(jdbcTemplate);
///     }
///
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

    /// 主键类型
    protected final Class<ID> idType;
    /// 实体类型
    protected final Class<T> entityType;

    /// 查询构建器，用于构建类型安全的查询
    protected final QueryBuilder<T> queryBuilder;
    /// 更新执行器，用于执行插入、更新、删除操作
    protected final UpdateExecutor updateExecutor;
    /// Spring JDBC 模板，用于执行原生 SQL
    protected final JdbcTemplate jdbcTemplate;
    /// NextEntity 工厂，用于创建条件更新/删除构建器
    protected final NextEntityFactory factory;

    /// 使用 JDBC 创建 Repository 实例。
    ///
    /// 该构造函数自动检测实体类型和主键类型，
    /// 并配置 JDBC 方式的数据库操作。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    protected AbstractRepository(JdbcTemplate jdbcTemplate) {
        NextEntityFactory factory = jdbc(jdbcTemplate);
        this(jdbcTemplate, factory);
    }

    /// 使用 JPA 和 JDBC 创建 Repository 实例。
    ///
    /// 该构造函数结合 JPA 和 JDBC 两种方式，
    /// JPA 用于实体操作，JDBC 用于批量操作和原生查询。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    protected AbstractRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        NextEntityFactory factory = jpa(entityManager, jdbcTemplate);
        this(jdbcTemplate, factory);
    }

    /// 使用显式类型参数创建 Repository 实例。
    ///
    /// 该构造函数适用于无法通过泛型推断类型的场景，
    /// 需要手动指定实体类型和主键类型。
    ///
    /// @param idType       主键类型
    /// @param entityType   实体类型
    /// @param queryBuilder 查询构建器
    /// @param updateExecutor 更新执行器
    /// @param jdbcTemplate Spring JDBC 模板
    public AbstractRepository(Class<ID> idType,
                              Class<T> entityType,
                              QueryBuilder<T> queryBuilder,
                              UpdateExecutor updateExecutor,
                              JdbcTemplate jdbcTemplate) {
        this.idType = idType;
        this.entityType = entityType;
        this.queryBuilder = queryBuilder;
        this.updateExecutor = updateExecutor;
        this.jdbcTemplate = jdbcTemplate;
        this.factory = null;
    }

    /// 使用工厂创建 Repository 实例。
    ///
    /// 该构造函数通过 NextEntityFactory 创建所需的组件，
    /// 并自动检测实体类型和主键类型。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @param factory      NextEntity 工厂
    protected AbstractRepository(JdbcTemplate jdbcTemplate,
                                 NextEntityFactory factory) {
        GenericType<T, ID> genericType = getGenericType();
        this.idType = genericType.idType();
        this.entityType = genericType.entityType();
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = factory.queryBuilder(entityType);
        this.updateExecutor = factory.updateExecutor();
        this.factory = factory;
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
    /// int updated = repository.updateWhere()
    ///     .set(User::getStatus, "ARCHIVED")
    ///     .where(User::getLastLoginAt).lt(threshold)
    ///     .execute();
    /// }</pre>
    ///
    /// @return 条件更新构建器实例
    /// @throws IllegalStateException 如果工厂不可用
    /// @since 2.1
    @Transactional
    public UpdateWhereStep<T> updateWhere() {
        if (factory == null) {
            throw new IllegalStateException("Factory not available. Use constructor with NextEntityFactory.");
        }
        return factory.updateWhereStep(entityType);
    }

    /// 创建条件删除构建器，用于带 WHERE 条件的批量删除。
    ///
    /// 使用示例：
    /// <pre>{@code
    /// int deleted = repository.deleteWhere()
    ///     .where(User::getStatus).eq("INACTIVE")
    ///     .execute();
    /// }</pre>
    ///
    /// @return 条件删除构建器实例
    /// @throws IllegalStateException 如果工厂不可用
    /// @since 2.1
    @Transactional
    public DeleteWhereStep<T> deleteWhere() {
        if (factory == null) {
            throw new IllegalStateException("Factory not available. Use constructor with NextEntityFactory.");
        }
        return factory.deleteWhereStep(entityType);
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

    /// 连接提供者实现类。
    ///
    /// 用于在 Spring 环境中提供数据库连接，
    /// 支持普通执行和事务执行两种模式。
    private static class ConnectionProviderImpl implements ConnectionProvider {
        AbstractRepository<?, ?> repository;
        JdbcTemplate jdbcTemplate;

        /// 使用连接执行操作。
        ///
        /// @param action 连接回调操作
        /// @param <X>    操作返回类型
        /// @return 操作结果
        @Override
        public <X> X execute(ConnectionCallback<X> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

        /// 在事务中使用连接执行操作。
        ///
        /// 通过 doInTransaction 方法确保操作在事务中执行。
        ///
        /// @param action 连接回调操作
        /// @param <X>    操作返回类型
        /// @return 操作结果
        @Override
        public <X> X executeInTransaction(ConnectionCallback<X> action) {
            return repository.doInTransaction(() -> execute(action));
        }
    }

    /// 创建基于 JDBC 的 NextEntity 工厂。
    ///
    /// 使用纯 JDBC 方式进行数据库操作，
    /// 不依赖 JPA/Hibernate。
    ///
    /// @param jdbcTemplate Spring JDBC 模板
    /// @return JDBC 方式的 NextEntity 工厂
    protected static NextEntityFactory jdbc(JdbcTemplate jdbcTemplate) {
        return DefaultNextEntityFactory.jdbc(jdbcTemplate);
    }

    /// 创建基于 JPA 的 NextEntity 工厂。
    ///
    /// 结合 JPA 和 JDBC 两种方式进行数据库操作，
    /// JPA 用于实体操作，JDBC 用于批量操作。
    ///
    /// @param entityManager JPA 实体管理器
    /// @param jdbcTemplate  Spring JDBC 模板
    /// @return JPA 方式的 NextEntity 工厂
    protected static NextEntityFactory jpa(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        return DefaultNextEntityFactory.jpa(entityManager, jdbcTemplate);
    }

}
