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

> **重要**: `query()` 方法在 `AbstractRepository` 中是 `protected` 的，以下示例均为 Repository 内部方法实现。

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(NextEntityFactory factory) {
        super(factory);
    }

    // 查询全部字段（完整实体）
    public List<Employee> findAll() {
        return query().list();;
    }

    // 投影查询（只查询需要的字段）
    public List<String> findAllNames() {
        return query()
            .select(Employee::getName)
            .list();;
    }
}
```

---

## 单字段投影

### 基本用法

```java
// 查询所有员工姓名
List<String> names = employeeRepository.query()
    .select(Employee::getName)
    .list();

// 查询所有员工邮箱
List<String> emails = employeeRepository.query()
    .select(Employee::getEmail)
    .list();

// 查询所有部门 ID
List<Long> deptIds = employeeRepository.query()
    .select(Employee::getDepartmentId)
    .list();
```

### 带条件查询

```java
// 活跃员工的姓名
List<String> activeNames = employeeRepository.query()
    .select(Employee::getName)
    .where(Employee::getActive).eq(true)
    .list();

// 高薪员工的邮箱
List<String> highSalaryEmails = employeeRepository.query()
    .select(Employee::getEmail)
    .where(Employee::getSalary).gt(BigDecimal.valueOf(100000.0))
    .list();
```

---

## 多字段投影

### Tuple2（两个字段）

```java
// 姓名和薪资
List<Tuple2<String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .where(Employee::getActive).eq(true)
    .list();

// 访问结果
for (Tuple2<String, BigDecimal> tuple : results) {
    String name = tuple.get0();
    BigDecimal salary = tuple.get1();
    System.out.println(name + ": " + salary);
}
```

### Tuple3（三个字段）

```java
// 姓名、邮箱和薪资
List<Tuple3<String, String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail, Employee::getSalary)
    .list();

for (Tuple3<String, String, BigDecimal> tuple : results) {
    String name = tuple.get0();
    String email = tuple.get1();
    BigDecimal salary = tuple.get2();
}
```

### Tuple4 到 Tuple10

```java
// 四个字段
List<Tuple4<String, String, BigDecimal, Long>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail,
            Employee::getSalary, Employee::getDepartmentId)
    .list();

// 更多字段（最多 10 个）
List<Tuple5<String, String, BigDecimal, Long, Boolean>> results =
    employeeRepository.query()
    .select(Employee::getName, Employee::getEmail,
            Employee::getSalary, Employee::getDepartmentId,
            Employee::getActive)
    .list();
```

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
    .list();

for (EmployeeSummary summary : summaries) {
    System.out.println(summary.getName() + ": " + summary.getSalary());
}
```

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
    .list();

// 所有不同的状态
List<EmployeeStatus> distinctStatuses = employeeRepository.query()
    .selectDistinct(Employee::getStatus)
    .list();
```

### 多字段 Distinct

```java
// 不同的姓名-状态组合
List<Tuple2<String, EmployeeStatus>> results = employeeRepository.query()
    .selectDistinct(Employee::getName, Employee::getStatus)
    .list();

// 不同的部门-薪资组合
List<Tuple2<Long, BigDecimal>> results = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId, Employee::getSalary)
    .list();
```

### Distinct Count

```java
// 不同部门数量
long distinctDeptCount = employeeRepository.query()
    .select(path(Employee::getDepartmentId).countDistinct())
    .single();
```

---

## 聚合投影

使用 `Path.of()` 静态方法构建聚合表达式，配合 `select()` 方法：

### Sum（求和）

```java
BigDecimal totalSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).sum())
    .where(Employee::getActive).eq(true)
    .single();
```

### Avg（平均值）

```java
Double avgSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).avg())
    .where(Employee::getDepartmentId).eq(1L)
    .single();
```

### Max（最大值）

```java
BigDecimal maxSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).max())
    .single();
```

### Min（最小值）

```java
BigDecimal minSalary = employeeRepository.query()
    .select(Path.of(Employee::getSalary).min())
    .where(Employee::getActive).eq(true)
    .single();
```

### Count（计数）

```java
long count = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .count();
```

---

## 最佳实践

### 1. 使用投影减少数据传输

```java
// 推荐：只查询需要的字段
List<Tuple2<String, BigDecimal>> results = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .list();

// 避免：查询全部字段
List<Employee> employees = employeeRepository.query()
    .list();
// 然后只使用 name 和 salary
```

### 2. 使用 Tuple 简化简单场景

```java
// 两个字段用 Tuple2
List<Tuple2<String, BigDecimal>> nameSalary = employeeRepository.query()
    .select(Employee::getName, Employee::getSalary)
    .list();

// 三个字段用 Tuple3
List<Tuple3<String, String, BigDecimal>> details = employeeRepository.query()
    .select(Employee::getName, Employee::getEmail, Employee::getSalary)
    .list();
```

### 3. 使用 DTO 处理复杂场景

```java
// 多字段使用 DTO
List<EmployeeSummary> summaries = employeeRepository.query()
    .select(EmployeeSummary.class)
    .where(Employee::getActive).eq(true)
    .list();
```

### 4. Distinct 消除重复

```java
// 获取唯一部门列表
List<Long> uniqueDeptIds = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .list();
```

---

## 下一步

投影掌握后，继续学习：

1. **[关联查询指南](associations.md)** - 实体关联和 Fetch 操作