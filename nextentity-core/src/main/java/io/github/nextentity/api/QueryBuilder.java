package io.github.nextentity.api;

/// Query builder interface for constructing type-safe database queries.
///
/// This interface is the entry point for building queries using NextEntity's fluent API.
/// It extends {@link SelectStep} which provides various select operations for projections
/// and field selection.
///
/// ## Query Flow
///
/// The query follows a fluent API pattern:
///
/// ```
/// query() → select/fetch → where → orderBy → limit/offset → execute
/// ```
///
/// ## Basic Usage
///
/// ### Simple Query
///
/// ```java
/// // Query all entities
/// List<Employee> employees = repository.query().getList();
///
/// // Query with conditions
/// List<Employee> activeEmployees = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getName).asc()
///     .getList();
/// ```
///
/// ### Projection Query
///
/// ```java
/// // Select specific fields using method references
/// List<Tuple2<String, BigDecimal>> namesAndSalaries = repository.query()
///     .select(Employee::getName, Employee::getSalary)
///     .where(Employee::getActive).eq(true)
///     .getList();
///
/// // Select into DTO class
/// List<EmployeeDto> dtos = repository.query()
///     .select(EmployeeDto.class)
///     .where(Employee::getActive).eq(true)
///     .getList();
/// ```
///
/// ### Association Fetch
///
/// ```java
/// // Eager fetch associations to avoid N+1 problem
/// List<Employee> employees = repository.query()
///     .fetch(Employee::getDepartment)
///     .where(Employee::getActive).eq(true)
///     .getList();
/// ```
///
/// ### Pagination
///
/// ```java
/// // Offset + Limit pagination
/// List<Employee> page = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getId).asc()
///     .getList(0, 10);  // offset=0, limit=10
///
/// // Slice with metadata
/// Slice<Employee> slice = repository.query()
///     .where(Employee::getActive).eq(true)
///     .orderBy(Employee::getId).asc()
///     .slice(0, 10);
/// ```
///
/// ### Conditional Operators
///
/// ```java
/// // Conditional operators for optional parameters
/// public List<Employee> search(Long departmentId, EmployeeStatus status) {
///     return repository.query()
///         .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
///         .where(Employee::getStatus).eqIfNotNull(status)
///         .getList();
/// }
/// ```
///
/// ## Type Safety
///
/// All queries use method references (e.g., `Employee::getName`) which provide:
/// - Compile-time type checking
/// - IDE auto-completion
/// - Refactoring safety
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
/// @see SelectStep For select and projection operations
/// @see FetchStep For association fetch operations
/// @see WhereStep For condition building operations
public interface QueryBuilder<T> extends SelectStep<T> {

}
