# 聚合操作指南

本指南介绍 NextEntity 的聚合和统计功能。

## 目录

- [简介](#简介)
- [Count 计数](#count-计数)
- [Sum 求和](#sum-求和)
- [Avg 平均值](#avg-平均值)
- [Max/Min 最大最小](#maxmin-最大最小)
- [Distinct 统计](#distinct-统计)
- [分组统计](#分组统计)
- [最佳实践](#最佳实践)

---

## 简介

NextEntity 提供数据库级别的聚合操作，在 SQL 层面完成统计计算。

### 可用聚合函数

| 函数 | 描述 | SQL 对应 |
|------|------|----------|
| `count()` | 统计数量 | `COUNT(*)` |
| `sum()` | 求和 | `SUM(field)` |
| `avg()` | 平均值 | `AVG(field)` |
| `max()` | 最大值 | `MAX(field)` |
| `min()` | 最小值 | `MIN(field)` |

### 路径表达式 API

聚合操作需要使用路径表达式创建：

| 场景 | API | 示例 |
|------|-----|------|
| **Repository 外部**（Service 等） | `Path.of()` | `Path.of(Employee::getSalary).sum()` |
| **Repository 内部** | `path()` | `path(Employee::getSalary).sum()` |

> ⚠️ 注意：
> - `path()` 是 `AbstractRepository` 的 protected 方法，只能在 Repository 子类内部使用
> - 聚合表达式需要使用 `selectExpr()` 方法，而不是 `select()`

---

## Count 计数

### 统计全部

```java
// 统计所有员工
long total = employeeRepository.query().count();

// 生成的 SQL: SELECT COUNT(*) FROM employee
```

### 条件统计

```java
// 统计活跃员工
long activeCount = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .count();

// 统计高薪员工
long highSalaryCount = employeeRepository.query()
    .where(Employee::getSalary).gt(100000.0)
    .count();

// 多条件统计
long count = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .where(Employee::getDepartmentId).eq(1L)
    .where(Employee::getStatus).in(ACTIVE, ON_LEAVE)
    .count();
```

---

## Sum 求和

### 基本用法

```java
// 所有员工薪资总和
Double totalSalary = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).sum())
    .getSingle();

// 生成的 SQL: SELECT SUM(salary) FROM employee
```

### 条件求和

```java
// 活跃员工薪资总和
Double activeTotal = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).sum())
    .where(Employee::getActive).eq(true)
    .getSingle();

// 某部门薪资总和
Double deptTotal = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).sum())
    .where(Employee::getDepartmentId).eq(1L)
    .getSingle();

// 多条件求和
Double total = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).sum())
    .where(Employee::getActive).eq(true)
    .where(Employee::getDepartmentId).eq(1L)
    .where(Employee::getSalary).gt(50000.0)
    .getSingle();
```

---

## Avg 平均值

### 基本用法

```java
// 平均薪资
Double avgSalary = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).avg())
    .getSingle();

// 生成的 SQL: SELECT AVG(salary) FROM employee
```

### 条件平均值

```java
// 活跃员工平均薪资
Double activeAvg = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).avg())
    .where(Employee::getActive).eq(true)
    .getSingle();

// 某部门平均薪资
Double deptAvg = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).avg())
    .where(Employee::getDepartmentId).eq(1L)
    .getSingle();
```

---

## Max/Min 最大最小

### 最大值

```java
// 最高薪资
Double maxSalary = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).max())
    .getSingle();

// 活跃员工最高薪资
Double activeMax = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).max())
    .where(Employee::getActive).eq(true)
    .getSingle();

// 最早入职日期
LocalDate earliestHire = employeeRepository.query()
    .selectExpr(Path.of(Employee::getHireDate).min())
    .getSingle();
```

### 最小值

```java
// 最低薪资
Double minSalary = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).min())
    .getSingle();

// 活跃员工最低薪资
Double activeMin = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).min())
    .where(Employee::getActive).eq(true)
    .getSingle();

// 最近入职日期
LocalDate latestHire = employeeRepository.query()
    .selectExpr(Path.of(Employee::getHireDate).max())
    .getSingle();
```

---

## Distinct 统计

### Distinct Count

```java
// 不同部门数量
long distinctDeptCount = employeeRepository.query()
    .selectDistinct(Employee::getDepartmentId)
    .count();

// 不同状态数量
long distinctStatusCount = employeeRepository.query()
    .selectDistinct(Employee::getStatus)
    .count();

// 不同薪资值数量
long distinctSalaryCount = employeeRepository.query()
    .selectDistinct(Employee::getSalary)
    .count();
```

### Distinct Sum/Avg

```java
// 不同薪资值的总和
Double distinctSum = employeeRepository.query()
    .selectDistinct(Employee::getSalary)
    .getList()
    .stream()
    .mapToDouble(Double::doubleValue)
    .sum();
```

---

## 分组统计

NextEntity 在数据库层面完成聚合，分组可通过 Java Stream 处理：

### 按部门分组

```java
// 先在数据库查询并过滤
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .where(Employee::getDepartmentId).isNotNull()
    .getList();

// 在 Java 中分组统计
Map<Long, DoubleSummaryStatistics> statsByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartmentId,
        Collectors.summarizingDouble(e -> e.getSalary() != null ? e.getSalary() : 0.0)
    ));

// 结果分析
for (Map.Entry<Long, DoubleSummaryStatistics> entry : statsByDept.entrySet()) {
    Long deptId = entry.getKey();
    DoubleSummaryStatistics stats = entry.getValue();
    System.out.println("部门 " + deptId + ": " +
        "人数=" + stats.getCount() +
        ", 总薪资=" + stats.getSum() +
        ", 平均=" + stats.getAverage());
}
```

### 按状态分组

```java
List<Employee> employees = employeeRepository.query()
    .where(Employee::getSalary).isNotNull()
    .getList();

Map<EmployeeStatus, List<Employee>> byStatus = employees.stream()
    .collect(Collectors.groupingBy(Employee::getStatus));

Map<EmployeeStatus, Double> avgByStatus = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getStatus,
        Collectors.averagingDouble(e -> e.getSalary())
    ));
```

### 多级分组

```java
// 先按部门，再按状态分组
Map<Long, Map<EmployeeStatus, List<Employee>>> multiLevel = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartmentId,
        Collectors.groupingBy(Employee::getStatus)
    ));
```

---

## 最佳实践

### 1. 使用数据库聚合

```java
// 推荐：数据库聚合
Double total = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).sum())
    .getSingle();

// 避免：Java 流聚合（数据量大时性能差）
Double total = employeeRepository.query()
    .getList()
    .stream()
    .mapToDouble(Employee::getSalary)
    .sum();
```

### 2. 使用条件过滤

```java
// 在聚合前过滤
Double activeAvg = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).avg())
    .where(Employee::getActive).eq(true)  // 数据库过滤
    .getSingle();
```

### 3. 使用 first() 获取 Optional 结果

```java
// 获取 Optional 包装的结果
Optional<Double> maxSalary = employeeRepository.query()
    .selectExpr(Path.of(Employee::getSalary).max())
    .where(Employee::getDepartmentId).eq(deptId)
    .first();
```

### 4. 使用 exist() 检查存在性

```java
// 检查是否存在符合条件的记录
boolean hasHighEarners = employeeRepository.query()
    .where(Employee::getSalary).gt(100000.0)
    .exist();
```

### 3. 合理使用分组

```java
// 数据量小：先查询再分组
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();

Map<Long, Double> avgByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartmentId,
        Collectors.averagingDouble(Employee::getSalary)
    ));

// 数据量大：按部门分别查询
for (Long deptId : departmentIds) {
    Double avg = employeeRepository.query()
        .select(Path.of(Employee::getSalary).avg())
        .where(Employee::getDepartmentId).eq(deptId)
        .getSingle();
}
```

### 4. 处理 NULL 值

```java
// 过滤 NULL 值
Double avg = employeeRepository.query()
    .select(Path.of(Employee::getSalary).avg())
    .where(Employee::getSalary).isNotNull()  // 排除 NULL
    .getSingle();

// 在 Java 中处理 NULL
List<Employee> employees = employeeRepository.query()
    .where(Employee::getSalary).isNotNull()
    .getList();
```

---

## 示例

### 部门统计报告

```java
@Service
public class DepartmentStatsService {

    public DepartmentReport generateReport(Long departmentId) {
        // 人数
        long count = employeeRepository.query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .count();

        // 薪资统计
        Double total = employeeRepository.query()
            .selectExpr(Path.of(Employee::getSalary).sum())
            .where(Employee::getDepartmentId).eq(departmentId)
            .getSingle();

        Double avg = employeeRepository.query()
            .selectExpr(Path.of(Employee::getSalary).avg())
            .where(Employee::getDepartmentId).eq(departmentId)
            .getSingle();

        Double max = employeeRepository.query()
            .selectExpr(Path.of(Employee::getSalary).max())
            .where(Employee::getDepartmentId).eq(departmentId)
            .getSingle();

        return new DepartmentReport(departmentId, count, total, avg, max);
    }
}
```

---

## 下一步

核心功能学习完成，学习最佳实践：

1. **[Repository 模式指南](repository-pattern.md)** - 封装数据访问、自定义方法、最佳实践