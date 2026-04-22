package io.github.nextentity.api;

/// 查询构建器接口，用于构建类型安全的数据库查询。
///
/// 该接口是使用 NextEntity 流式 API 构建查询的入口点。
/// 它继承自 {@link SelectStep}，提供了各种投影和字段选择操作。
///
/// ## 查询流程
///
/// 查询遵循流式 API 模式：
///
/// ```
/// query() → select/fetch → where → orderBy → limit/offset → execute
/// ```
///
/// ## 基本用法
///
/// ### 简单查询
///
/// ```java
/// // 查询所有实体
/// List<Employee> employees = repository.query().list();
///
/// // 带条件查询
/// List<Employee> activeEmployees = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getName).asc()
///     .list();
/// ```
///
/// ### 投影查询
///
/// ```java
/// // 使用方法引用选择特定字段
/// List<Tuple2<String, BigDecimal>> namesAndSalaries = repository.query()
///     .select(Employee::getName, Employee::getSalary)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // 投影到 DTO 类
/// List<EmployeeDto> dtos = repository.query()
///     .select(EmployeeDto.class)
///     .where(Employee::getActive).eq(true)
///     .list();
/// ```
///
/// ### 关联查询
///
/// ```java
/// // 预加载关联数据，避免 N+1 问题
/// List<Employee> employees = repository.query()
///     .fetch(Employee::getDepartment)
///     .where(Employee::getActive).eq(true)
///     .list();
/// ```
///
/// ### 分页查询
///
/// ```java
/// // Offset + Limit 分页
/// List<Employee> page = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getId).asc()
///     .list(0, 10);  // offset=0, limit=10
///
/// // 带元数据的分片查询
/// Slice<Employee> slice = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getId).asc()
///     .slice(0, 10);
/// ```
///
/// ### 条件操作符
///
/// ```java
/// // 用于可选参数的条件操作符
/// public List<Employee> search(Long departmentId, EmployeeStatus status) {
///     return repository.query()
///         .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
///         .where(Employee::getStatus).eqIfNotNull(status)
///         .list();
/// }
/// ```
///
/// ## 类型安全
///
/// 所有查询都使用方法引用（如 `Employee::getName`），提供：
/// - 编译时类型检查
/// - IDE 自动补全
/// - 重构安全性
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see SelectStep 选择和投影操作
/// @see FetchStep 关联预加载操作
/// @see WhereStep 条件构建操作
/// @since 1.0.0
public interface EntityQuery<T> extends SelectStep<T> {

}