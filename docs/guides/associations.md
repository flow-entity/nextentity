# 关联查询指南

本指南介绍 NextEntity 的实体关联和 Fetch 操作。

## 目录

- [简介](#简介)
- [定义关联关系](#定义关联关系)
- [嵌套路径查询](#嵌套路径查询)
- [懒加载（默认）](#懒加载默认)
- [Fetch 急加载](#fetch-急加载)
- [多级关联](#多级关联)
- [性能优化](#性能优化)
- [最佳实践](#最佳实践)

---

## 简介

NextEntity 支持 JPA 标准关联关系，提供懒加载和急加载两种策略。

> **重要**: `query()` 方法为公共方法，关联查询在 Repository 内部实现最便捷。以下示例均为 Repository 内部方法实现。

### N+1 问题

当查询主实体后，逐个访问关联实体时会产生额外查询：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(EntityOperationsFactory factory) {
        super(factory);
    }

    // 查询员工（1 次查询）
    public List<Employee> findAll() {
        return query().list();
    }

    // 外部访问每个员工的部门时会产生 N 次查询
    // for (Employee e : employees) {
    //     Department dept = e.getDepartment();  // 每次触发一次查询
    // }
}
```

使用 `fetch()` 可以避免 N+1 问题。

---

## 定义关联关系

### ManyToOne（多对一）

```java
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    private Long departmentId;  // 外键字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;
}
```

> **重要**: 关联实体类需要实现 `io.github.nextentity.api.Entity` 接口，以便支持嵌套路径查询：
>
> ```java
> @Entity
> public class Department implements io.github.nextentity.api.Entity {
>     private Long id;
>     private String name;
>     // ...
> }
> ```
>
> 这样就可以在查询中直接访问嵌套属性：
> ```java
> .where(Employee::getDepartment).get(Department::getName).eq("技术部")
> ```

> **注意**: NextEntity 目前支持 ManyToOne 关联的 `fetch()` 操作。OneToMany 和 ManyToMany 关联需要通过外键字段手动查询。

---

## 嵌套路径查询

当关联实体实现了 `Entity` 接口后，可以在 `where()` 中直接访问嵌套属性：

### 按关联属性筛选

```java
// Department 实现了 Entity 接口
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartment).get(Department::getName).eq("技术部")
    .list();

// 等价于 SQL：
// SELECT e.* FROM employee e
// LEFT JOIN department d ON e.department_id = d.id
// WHERE d.name = '技术部'
```

### 多条件组合

```java
// 嵌套属性与其他条件组合
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartment).get(Department::getName).eq("技术部")
    .where(Employee::getDepartment).get(Department::getActive).eq(true)
    .where(Employee::getActive).eq(true)
    .orderBy(Employee::getName).asc()
    .list();
```

### 与 Fetch 结合

```java
// 同时使用 fetch 加载关联数据和嵌套路径筛选
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)                     // 急加载部门
    .where(Employee::getDepartment).get(Department::getName).eq("技术部")
    .list();

// 查询后可以直接访问部门（无需额外查询）
for (Employee e : employees) {
    System.out.println(e.getName() + " - " + e.getDepartment().getName());
}
```

---

## 懒加载（默认）

### 默认行为

```java
// 查询员工（Department 不加载）
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .list();

// 访问部门时才加载（触发额外查询）
Department dept = employees.get(0).getDepartment();  // 第二次查询
```

### 适用场景

- 关联数据不总是需要
- 减少初始查询数据量
- 按需加载关联

---

## Fetch 急加载

### 单个关联

```java
// 同时加载员工和部门
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)  // 急加载部门
    .where(Employee::getActive).eq(true)
    .list();

// 访问部门无需额外查询
Department dept = employees.get(0).getDepartment();  // 已加载
```

生成的 SQL：

```sql
SELECT e.id, e.name, e.department_id,
       d.id, d.name, d.budget
FROM employee e
LEFT JOIN department d ON e.department_id = d.id
WHERE e.active = ?
```

### Fetch 示例

```java
// 加载员工和部门
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .list();
```

---

## 多级关联

### 二级关联

```java
// 加载员工 -> 部门 -> 公司
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    // 部门的关联需要额外处理
    .list();
```

### 处理建议

对于多级关联，建议：

```java
// 方案 1：先查询主实体，再查询关联
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .list();

// 方案 2：使用投影避免复杂关联
List<EmployeeDepartmentDTO> results = employeeRepository.query()
    .select(EmployeeDepartmentDTO.class)
    .list();
```

---

## 性能优化

### 避免 N+1 问题

```java
// 问题：N+1 查询
List<Employee> employees = employeeRepository.query().list();
for (Employee e : employees) {
    System.out.println(e.getDepartment().getName());  // 每次触发查询
}

// 解决：使用 fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)  // 一次 JOIN 查询
    .list();
for (Employee e : employees) {
    System.out.println(e.getDepartment().getName());  // 无额外查询
}
```

### 批量大小配置

对于 Hibernate 后端，配置批量加载：

```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

### 按需加载

```java
// 只在需要关联时使用 fetch
public List<EmployeeSummary> getSummaries() {
    // 不需要关联，不用 fetch
    return employeeRepository.query()
        .select(EmployeeSummary.class)
        .list();
}

public List<EmployeeWithDept> getWithDepartment() {
    // 需要关联，使用 fetch
    return employeeRepository.query()
        .fetch(Employee::getDepartment)
        .list();
}
```

---

## 最佳实践

### 1. 根据需求选择加载策略

```java
// 只需要员工信息：不 fetch
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .list();

// 需要部门信息：fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .list();
```

### 2. 使用投影替代复杂关联

```java
// 投影可以简化复杂关联查询
// 使用 @EntityPath 注解映射关联字段
public class EmployeeWithDept {
    @EntityPath("name")
    private String employeeName;
    @EntityPath("department.name")
    private String departmentName;
}

List<EmployeeWithDept> results = employeeRepository.query()
    .select(EmployeeWithDept.class)
    .where(Employee::getActive).eq(true)
    .list();
```

### 3. 注意外键配置

```java
@Entity
public class Employee {

    private Long departmentId;  // 可写的外键字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId",
                insertable = false,
                updatable = false)  // 不可写，避免冲突
    private Department department;
}
```

---

## 示例

### Employee 和 Department

```java
// 不加载部门
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .list();
// employee.getDepartment() 为 null 或代理对象

// 加载部门
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .list();
// employee.getDepartment() 已加载
```

---

## 下一步

关联查询掌握后，继续学习：

1. **[聚合操作指南](aggregations.md)** - 统计和聚合操作