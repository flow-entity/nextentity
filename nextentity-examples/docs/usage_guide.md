# NextEntity Usage Guide

A comprehensive guide to using NextEntity, a type-safe SQL DSL framework for Java.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Configuration](#configuration)
3. [Basic CRUD Operations](#basic-crud-operations)
4. [Query Conditions](#query-conditions)
5. [Projections](#projections)
6. [Ordering and Pagination](#ordering-and-pagination)
7. [Associations and Fetching](#associations-and-fetching)
8. [Aggregations](#aggregations)
9. [Transactions](#transactions)
10. [Best Practices](#best-practices)

---

## Quick Start

### Maven Dependencies

```xml
<!-- For Spring Boot with JPA -->
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- Or for pure JDBC -->
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-core</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Define Entity

```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    private String email;
    private Double salary;
    private Boolean active;
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;

    // Getters and setters...
}
```

### Define Repository

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {
}

@Repository
public class DepartmentRepository extends AbstractRepository<Department, Long> {
}
```

### Basic Query

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findActiveEmployees() {
        return employeeRepository.query()
            .where(Employee::getActive).eq(true)
            .orderBy(Employee::getName).asc()
            .getList();
    }
}
```

---

## Configuration

### Spring Boot with JPA

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Repository Configuration

Extend `AbstractRepository` for each entity type:

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {
    // Custom query methods can be added here
}
```

---

## Basic CRUD Operations

### Insert

```java
// Single insert
Employee employee = new Employee();
employee.setId(1L);
employee.setName("John Doe");
employee.setEmail("john@example.com");
employeeRepository.insert(employee);

// Batch insert
List<Employee> employees = List.of(emp1, emp2, emp3);
employeeRepository.insertAll(employees);
```

### Update

```java
// Single update
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();
employee.setSalary(60000.0);
employeeRepository.update(employee);

// Batch update
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(1L)
    .getList();
employees.forEach(e -> e.setSalary(e.getSalary() * 1.1));
employeeRepository.updateAll(employees);
```

### Delete

```java
// Single delete
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();
employeeRepository.delete(employee);

// Batch delete
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(1L)
    .getList();
employeeRepository.deleteAll(employees);
```

---

## Query Conditions

### Comparison Operators

```java
// Equality
.where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
.where(Employee::getStatus).ne(EmployeeStatus.TERMINATED)

// Conditional (skip if null)
.where(Employee::getStatus).eqIfNotNull(maybeStatus)

// Comparison
.where(Employee::getSalary).gt(50000.0)
.where(Employee::getSalary).ge(50000.0)
.where(Employee::getSalary).lt(100000.0)
.where(Employee::getSalary).le(100000.0)

// Range
.where(Employee::getSalary).between(40000.0, 80000.0)
.where(Employee::getSalary).notBetween(40000.0, 80000.0)
```

### IN / NOT IN

```java
// Varargs
.where(Employee::getId).in(1L, 2L, 3L)

// Collection
.where(Employee::getId).in(idList)

// NOT IN
.where(Employee::getStatus).notIn(EmployeeStatus.TERMINATED)
```

### NULL Checks

```java
.where(Employee::getEmail).isNull()
.where(Employee::getEmail).isNotNull()
```

### String Operators

```java
// Pattern matching
.where(Employee::getName).like("%John%")
.where(Employee::getName).startsWith("John")
.where(Employee::getName).endsWith("Doe")
.where(Employee::getName).contains("hn")

// Conditional
.where(Employee::getName).likeIfNotNull(pattern)
.where(Employee::getName).containsIfNotEmpty(text)
```

### Logical Combinations

```java
// Multiple conditions (AND)
.where(Employee::getActive).eq(true)
.where(Employee::getSalary).gt(50000.0)
```

---

## Projections

### Single Field

```java
List<String> names = employeeRepository.query()
    .select(Employee::getName)
    .where(Employee::getActive).eq(true)
    .getList();
```

### Tuple Selection

```java
// Two fields
List<Tuple2<String, Double>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .where(Employee::getActive).eq(true)
    .getList();

for (Tuple2<String, Double> tuple : results) {
    String name = tuple.get0();
    Double salary = tuple.get1();
}

// Three fields
List<Tuple3<String, String, Double>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail, Employee::getSalary)
    .getList();
```

### DTO Projection

```java
public class EmployeeSummary {
    private String name;
    private String email;
    private Double salary;

    // Getters and setters...
}

List<EmployeeSummary> summaries = employeeRepository.query()
    .select(EmployeeSummary.class)
    .where(Employee::getActive).eq(true)
    .getList();
```

### Distinct

```java
// Distinct single field
List<Long> deptIds = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .getList();

// Distinct tuple
List<Tuple2<String, EmployeeStatus>> results = employeeRepository.query()
    .selectDistinct(Employee::getName, Employee::getStatus)
    .getList();
```

---

## Ordering and Pagination

### Ordering

```java
// Single sort
.orderBy(Employee::getName).asc()
.orderBy(Employee::getSalary).desc()

// Multiple sorts
.orderBy(Employee::getDepartmentId).asc()
.orderBy(Employee::getSalary).desc()
```

### Pagination

```java
// Offset and limit
List<Employee> page1 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .getList(0, 10);  // First 10 records

List<Employee> page2 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .getList(10, 10);  // Next 10 records

// Slice with metadata
Slice<Employee> slice = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .slice(0, 10);

List<Employee> content = slice.data();
boolean hasNext = slice.hasNext();
```

---

## Associations and Fetching

### Lazy Loading (Default)

```java
// Department loaded on first access
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();

// Triggers separate query
Department dept = employees.get(0).getDepartment();
```

### Eager Fetch

```java
// Fetch association in single query
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .getList();

// No additional query needed
Department dept = employees.get(0).getDepartment();
```

---

## Aggregations

NextEntity supports database-level aggregations using `path()` with aggregate functions:

### Count

```java
// Count all
long count = employeeRepository.query().count();

// Count with conditions
long activeCount = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .count();

// Count distinct
long distinctDeptCount = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .count();
```

### Sum, Average, Max, Min

```java
// Sum
double totalSalary = employeeRepository.query()
    .select(path(Employee::getSalary).sum())
    .getSingle();

// Average
double avgSalary = employeeRepository.query()
    .select(path(Employee::getSalary).avg())
    .where(Employee::getActive).eq(true)
    .getSingle();

// Maximum
double maxSalary = employeeRepository.query()
    .select(path(Employee::getSalary).max())
    .where(Employee::getActive).eq(true)
    .getSingle();

// Minimum
double minSalary = employeeRepository.query()
    .select(path(Employee::getSalary).min())
    .where(Employee::getActive).eq(true)
    .getSingle();
```

### Group By (Java Streams)

For complex grouping operations, use Java streams after filtering at database level:

```java
// Group by - filter nulls in query, then group in Java
Map<Long, List<Employee>> byDept = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .where(Employee::getDepartmentId).isNotNull()
    .getList()
    .stream()
    .collect(Collectors.groupingBy(Employee::getDepartmentId));

// Group by with statistics
Map<Long, DoubleSummaryStatistics> statsByDept = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .where(Employee::getSalary).isNotNull()
    .getList()
    .stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartmentId,
        Collectors.summarizingDouble(Employee::getSalary)
    ));
```

---

## Transactions

```java
// Execute in transaction
employeeRepository.doInTransaction(() -> {
    // Insert department
    Department dept = new Department(1L, "Engineering", "Building A", 100000.0, true);
    departmentRepository.insert(dept);

    // Insert employees
    Employee emp = new Employee();
    emp.setDepartmentId(1L);
    employeeRepository.insert(emp);
});
```

---

## Best Practices

### 1. Use Method References

Always use method references for type safety:

```java
// Good
.where(Employee::getName).eq("John")

// Avoid (not type-safe)
.where("name").eq("John")
```

### 2. Create Dedicated Repository Classes

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // Custom query methods
    public List<Employee> findActiveByDepartment(Long departmentId) {
        return query()
            .where(Employee::getActive).eq(true)
            .where(Employee::getDepartmentId).eq(departmentId)
            .orderBy(Employee::getName).asc()
            .getList();
    }
}
```

### 3. Use Conditional Operators for Optional Parameters

```java
public List<Employee> search(String name, Long deptId) {
    return employeeRepository.query()
        .where(Employee::getName).containsIfNotEmpty(name)
        .where(Employee::getDepartmentId).eqIfNotNull(deptId)
        .getList();
}
```

### 4. Fetch Associations When Needed

```java
// Avoid N+1 problem
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)  // Eager fetch
    .where(Employee::getActive).eq(true)
    .getList();
```

### 5. Use Batch Operations

```java
// Efficient for large datasets
employeeRepository.insertAll(employees);
employeeRepository.updateAll(employees);
```

---

## Next Steps

- See [api_reference.md](api_reference.md) for detailed API documentation
- Check example classes in the `io.github.nextentity.examples` package