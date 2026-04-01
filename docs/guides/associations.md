# 关联查询指南

本指南介绍 NextEntity 的实体关联和 Fetch 操作。

## 目录

- [简介](#简介)
- [定义关联关系](#定义关联关系)
- [懒加载（默认）](#懒加载默认)
- [Fetch 急加载](#fetch-急加载)
- [多级关联](#多级关联)
- [性能优化](#性能优化)
- [最佳实践](#最佳实践)

---

## 简介

NextEntity 支持 JPA 标准关联关系，提供懒加载和急加载两种策略。

### N+1 问题

当查询主实体后，逐个访问关联实体时会产生额外查询：

```java
// 查询员工（1 次查询）
List<Employee> employees = employeeRepository.query().getList();

// 访问每个员工的部门（N 次查询）
for (Employee e : employees) {
    Department dept = e.getDepartment();  // 每次触发一次查询
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

> 📍 **示例位置**: `entity/Employee.java` (`department` 字段定义)

> **注意**: NextEntity 目前支持 ManyToOne 关联的 `fetch()` 操作。OneToMany 和 ManyToMany 关联需要通过外键字段手动查询。

---

## 懒加载（默认）

### 默认行为

```java
// 查询员工（Department 不加载）
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();

// 访问部门时才加载（触发额外查询）
Department dept = employees.get(0).getDepartment();  // 第二次查询
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithLazyLoading` 方法)

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
    .getList();

// 访问部门无需额外查询
Department dept = employees.get(0).getDepartment();  // 已加载
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithDepartmentFetch` 方法)

生成的 SQL：

```sql
SELECT e.id, e.name, e.department_id,
       d.id, d.name, d.budget
FROM employee e
LEFT JOIN department d ON e.department_id = d.id
WHERE e.active = ?
```

### 多个关联

```java
// 同时加载部门和经理
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .fetch(Employee::getManager)  // 假设 Employee 有 manager 关联
    .where(Employee::getActive).eq(true)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithMultipleFetches` 方法)

---

## 多级关联

### 二级关联

```java
// 加载员工 -> 部门 -> 公司
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    // 部门的关联需要额外处理
    .getList();
```

### 处理建议

对于多级关联，建议：

```java
// 方案 1：先查询主实体，再查询关联
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .getList();

// 方案 2：使用投影避免复杂关联
List<EmployeeDepartmentDTO> results = employeeRepository.query()
    .select(EmployeeDepartmentDTO.class)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findEmployeeWithDepartmentInfo` 方法)

---

## 性能优化

### 避免 N+1 问题

```java
// 问题：N+1 查询
List<Employee> employees = employeeRepository.query().getList();
for (Employee e : employees) {
    System.out.println(e.getDepartment().getName());  // 每次触发查询
}

// 解决：使用 fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)  // 一次 JOIN 查询
    .getList();
for (Employee e : employees) {
    System.out.println(e.getDepartment().getName());  // 无额外查询
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithDepartmentFetch` 方法)

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
        .getList();
}

public List<EmployeeWithDept> getWithDepartment() {
    // 需要关联，使用 fetch
    return employeeRepository.query()
        .fetch(Employee::getDepartment)
        .getList();
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (fetch 示例)

---

## 最佳实践

### 1. 根据需求选择加载策略

```java
// 只需要员工信息：不 fetch
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();

// 需要部门信息：fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (懒加载 vs 急加载对比)

### 2. 使用投影替代复杂关联

```java
// 投影可以简化复杂关联查询
public class EmployeeWithDeptName {
    private String employeeName;
    private String departmentName;
    private Double salary;
}

List<EmployeeWithDeptName> results = employeeRepository.query()
    .select(EmployeeWithDeptName.class)
    .fetch(Employee::getDepartment)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithManualJoin` 方法)
> 📍 **DTO 定义**: `EmployeeRepository.java` (`EmployeeWithDept` 类)

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

> 📍 **示例位置**: `entity/Employee.java` (外键和关联字段定义)

---

## 示例

### Employee 和 Department

```java
// 不加载部门
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();
// employee.getDepartment() 为 null 或代理对象

// 加载部门
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .where(Employee::getActive).eq(true)
    .getList();
// employee.getDepartment() 已加载
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithDepartmentFetch` 方法)

---

## 下一步

关联查询掌握后，继续学习：

1. **[聚合操作指南](aggregations.md)** - 统计和聚合操作