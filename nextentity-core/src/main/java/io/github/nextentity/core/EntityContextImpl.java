package io.github.nextentity.core;

import io.github.nextentity.api.EntityContext;
import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

/// EntityContext 接口的默认实现。
///
/// 该实现组合 Metamodel、QueryExecutor、UpdateExecutor 和 PaginationConfig，
/// 为指定实体类型创建 EntityOperations 实例。
///
/// ## 功能特性
///
/// - **EntityQuery**：流式查询构建，支持 select、where、orderBy、分页等
/// - **EntityPersistor**：实体持久化操作，支持 insert、update、delete 及批量操作
/// - **EntityOperations**：合并查询与持久化的统一入口
///
/// ## 使用示例
///
/// ```java
/// // 创建工厂实例
/// EntityContext factory = new EntityContextImpl(
///     metamodel, queryExecutor, updateExecutor, paginationConfig);
///
/// // 创建统一操作入口（查询 + CRUD）
/// EntityOperations<User> ops = factory.operations(User.class);
/// ops.where(User::getStatus).eq("ACTIVE").list();  // 查询
/// ops.insert(newUser);                              // 持久化
/// ```
///
/// ## 实例创建说明
///
/// 每次调用 operations() 都会创建新的 EntityOperations 实例，
/// 因为 EntityOperations 在构建查询过程中会累积状态，不可共享。
///
/// @param metamodel        实体元数据注册表
/// @param queryExecutor    查询执行引擎
/// @param updateExecutor   更新执行器
/// @param paginationConfig 分页配置
/// @author HuangChengwei
/// @since 2.2.0
@Deprecated
public record EntityContextImpl(
        @NonNull Metamodel metamodel,
        @NonNull QueryExecutor queryExecutor,
        @NonNull UpdateExecutor updateExecutor,
        @NonNull PaginationConfig paginationConfig
) implements EntityContext {

    /// 创建指定实体类型的统一操作入口。
    ///
    /// EntityOperations 同时提供查询和持久化操作能力。
    /// 每次调用都会创建新实例。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 新的统一操作实例
    @Override
    public <T> EntityOperations<T> operations(@NonNull Class<T> entityType) {
        EntityType type = metamodel.getEntity(entityType);
        QueryDescriptor<T> context = new SimpleQueryDescriptor<>(
                metamodel, queryExecutor, paginationConfig, type, entityType);
        return new EntityOperationsImpl<>(context, updateExecutor);
    }
}