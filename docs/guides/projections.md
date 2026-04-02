# 投影指南

本指南介绍 NextEntity 的字段选择和投影功能。

## 目录

- [简介](#简介)
- [单字段投影](#单字段投影)
- [多字段投影](#多字段投影)
- [DTO 投影](#dto-投影)
- [Distinct 投影](#distinct-投影)
- [聚合投影](#聚合投影)
- [最佳实践](#最佳实践)

---

## 简介

投影允许只查询需要的字段，减少数据传输，提升性能。

```java
// 查询全部字段（完整实体）
List<Employee> employees = employeeRepository.query().getList();

// 投影查询（只查询需要的字段）
List<String> names = employeeRepository.query()
    .select(Employee::getName)
    .getList();
```

---

## 单字段投影

### 基本用法

```java
// 查询所有员工姓名
List<String> names = employeeRepository.query()
    .select(Employee::getName)
    .getList();

// 查询所有员工邮箱
List<String> emails = employeeRepository.query()
    .select(Employee::getEmail)
    .getList();

// 查询所有部门 ID
List<Long> deptIds = employeeRepository.query()
    .select(Employee::getDepartmentId)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findEmployeeNames` 方法)

### 带条件查询

```java
// 活跃员工的姓名
List<String> activeNames = employeeRepository.query()
    .select(Employee::getName)
    .where(Employee::getActive).eq(true)
    .getList();

// 高薪员工的邮箱
List<String> highSalaryEmails = employeeRepository.query()
    .select(Employee::getEmail)
    .where(Employee::getSalary).gt(BigDecimal.valueOf(100000.0))
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findEmployeeNames` 方法)

---

## 多字段投影

### Tuple2（两个字段）

```java
// 姓名和薪资
List<Tuple2<String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .where(Employee::getActive).eq(true)
    .getList();

// 访问结果
for (Tuple2<String, BigDecimal> tuple : results) {
    String name = tuple.get0();
    BigDecimal salary = tuple.get1();
    System.out.println(name + ": " + salary);
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findNameAndSalary` 方法)

### Tuple3（三个字段）

```java
// 姓名、邮箱和薪资
List<Tuple3<String, String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail, Employee::getSalary)
    .getList();

for (Tuple3<String, String, BigDecimal> tuple : results) {
    String name = tuple.get0();
    String email = tuple.get1();
    BigDecimal salary = tuple.get2();
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findNameEmailSalary` 方法)

### Tuple4 到 Tuple10

```java
// 四个字段
List<Tuple4<String, String, BigDecimal, Long>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail,
            Employee::getSalary, Employee::getDepartmentId)
    .getList();

// 更多字段（最多 10 个）
List<Tuple5<String, String, BigDecimal, Long, Boolean>> results =
    employeeRepository.query()
    .select(Employee::getName, Employee::getEmail,
            Employee::getSalary, Employee::getDepartmentId,
            Employee::getActive)
    .getList();
```

> 📍 **示例位置**:
> - `Tuple4`: `EmployeeRepository.java` (`findNameEmailSalaryDepartment` 方法)
> - `Tuple5`: `EmployeeRepository.java` (`findEmployeeDetails` 方法)

---

## DTO 投影

### 定义 DTO 类

```java
public class EmployeeSummary {

    private String name;
    private String email;
    private BigDecimal salary;
    private EmployeeStatus status;

    // 必须有无参构造函数
    public EmployeeSummary() {}

    // Getter 和 Setter 方法
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }
}

// 或者使用 Lombok
@Data
@NoArgsConstructor
public class EmployeeSummary {
    private String name;
    private String email;
    private BigDecimal salary;
    private EmployeeStatus status;
}
```

### 使用 DTO 投影

```java
List<EmployeeSummary> summaries = employeeRepository.query()
    .select(EmployeeSummary.class)
    .where(Employee::getActive).eq(true)
    .getList();

for (EmployeeSummary summary : summaries) {
    System.out.println(summary.getName() + ": " + summary.getSalary());
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findEmployeeSummaries` 方法)
> 📍 **DTO 定义**: `EmployeeRepository.java` (`EmployeeSummary` 类)

### 字段映射规则

DTO 字段名称需要与实体字段名称匹配：

| 实体字段 | DTO 字段 | 匹配规则 |
|----------|----------|----------|
| `name` | `name` | 名称相同 |
| `email` | `email` | 名称相同 |
| `salary` | `salary` | 名称相同 |
| `status` | `status` | 名称相同 |

---

## Distinct 投影

### 单字段 Distinct

```java
// 所有不同的部门 ID
List<Long> distinctDeptIds = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .getList();

// 所有不同的状态
List<EmployeeStatus> distinctStatuses = employeeRepository.query()
    .selectDistinct(Employee::getStatus)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findDistinctDepartmentIds` 方法)

### 多字段 Distinct

```java
// 不同的姓名-状态组合
List<Tuple2<String, EmployeeStatus>> results = employeeRepository.query()
    .selectDistinct(Employee::getName, Employee::getStatus)
    .getList();

// 不同的部门-薪资组合
List<Tuple2<Long, BigDecimal>> results = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId, Employee::getSalary)
    .getList();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findDistinctNameStatus` 方法)

### Distinct Count

```java
// 不同部门数量
long distinctDeptCount = employeeRepository.query()
    .select(path(Employee::getDepartmentId).countDistinct())
    .getSingle();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`countDistinctDepartments` 方法)

---

## 聚合投影

使用 `Path.of()` 静态方法构建聚合表达式，配合 `select()` 方法：

### Sum（求和）

```java
BigDecimal totalSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).sum())
    .where(Employee::getActive).eq(true)
    .getSingle();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`calculateTotalSalary` 方法)

### Avg（平均值）

```java
Double avgSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).avg())
    .where(Employee::getDepartmentId).eq(1L)
    .getSingle();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`calculateAverageSalary` 方法)

### Max（最大值）

```java
BigDecimal maxSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).max())
    .getSingle();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findMaxSalary` 方法)

### Min（最小值）

```java
BigDecimal minSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).min())
    .where(Employee::getActive).eq(true)
    .getSingle();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findMinSalary` 方法)

### Count（计数）

```java
long count = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .count();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`countAllEmployees` 方法)


---

## 最佳实践

### 1. 使用投影减少数据传输

```java
// 推荐：只查询需要的字段
List<Tuple2<String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .getList();

// 避免：查询全部字段
List<Employee> employees = employeeRepository.query()
    .getList();
// 然后只使用 name 和 salary
```

### 2. 使用 Tuple 简化简单场景

```java
// 两个字段用 Tuple2
List<Tuple2<String, BigDecimal>> nameSalary = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .getList();

// 三个字段用 Tuple3
List<Tuple3<String, String, BigDecimal>> details = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail, Employee::getSalary)
    .getList();
```

### 3. 使用 DTO 处理复杂场景

```java
// 多字段使用 DTO
List<EmployeeSummary> summaries = employeeRepository.query()
    .select(EmployeeSummary.class)
    .where(Employee::getActive).eq(true)
    .getList();
```

### 4. Distinct 消除重复

```java
// 获取唯一部门列表
List<Long> uniqueDeptIds = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .getList();
```

---

## 下一步

投影掌握后，继续学习：

1. **[关联查询指南](associations.md)** - 实体关联和 Fetch 操作