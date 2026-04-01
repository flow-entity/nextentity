# 查询构建指南

本指南详细介绍 NextEntity 的查询条件构建系统。

## 目录

- [查询流程](#查询流程)
- [基本条件](#基本条件)
- [比较运算符](#比较运算符)
- [范围运算符](#范围运算符)
- [IN 运算符](#in-运算符)
- [NULL 检查](#null-检查)
- [字符串运算符](#字符串运算符)
- [数值运算符](#数值运算符)
- [条件运算符](#条件运算符)
- [逻辑组合](#逻辑组合)
- [排序](#排序)
- [分页](#分页)
- [结果获取](#结果获取)

---

## 查询流程

NextEntity 使用流式 API 构建查询：

```
query() → where() → orderBy() → limit/offset → execute()
```

```java
List<Employee> employees = employeeRepository.query()
    .where(Employee::getActive).eq(true)    // 条件
    .orderBy(Employee::getName).asc()        // 排序
    .getList(0, 10);                         // 分页 + 执行
```

---

## 基本条件

### 使用方法引用

```java
// 推荐：方法引用（类型安全）
.where(Employee::getName).eq("John")

// 不推荐：字符串（无类型检查）
.where("name").eq("John")
```

方法引用提供：
- 编译时类型检查
- IDE 自动补全
- 重构安全

---

## 比较运算符

### 等值比较

```java
// 等于
.where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)

// 不等于
.where(Employee::getStatus).ne(EmployeeStatus.TERMINATED)

// 等于表达式
.where(Employee::getSalary).eq(Path.of(Employee::getBaseSalary))
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByStatus`, `findNotTerminated`, `findByStatusIfPresent` 方法)

### 大小比较

```java
// 大于
.where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))

// 大于等于
.where(Employee::getSalary).ge(BigDecimal.valueOf(50000.0))

// 小于
.where(Employee::getSalary).lt(BigDecimal.valueOf(100000.0))

// 小于等于
.where(Employee::getSalary).le(BigDecimal.valueOf(100000.0))
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findBySalaryGreaterThan`, `findBySalaryGreaterOrEqual` 等方法)

---

## 范围运算符

```java
// 在范围内 [min, max]
.where(Employee::getSalary).between(BigDecimal.valueOf(40000.0), BigDecimal.valueOf(80000.0))

// 不在范围内
.where(Employee::getSalary).notBetween(BigDecimal.valueOf(40000.0), BigDecimal.valueOf(80000.0))

// 日期范围
.where(Employee::getHireDate).between(startDate, endDate)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findBySalaryBetween`, `findBySalaryNotBetween` 方法)
> 📍 **日期范围**: `EmployeeRepository.java` (`findHiredBetween` 方法)

---

## IN 运算符

### Varargs 参数

```java
// 多个值
.where(Employee::getId).in(1L, 2L, 3L)

// 枚举值
.where(Employee::getStatus).in(ACTIVE, ON_LEAVE, INACTIVE)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByIds`, `findByStatuses` 方法)

### Collection 参数

```java
// List
List<Long> ids = List.of(1L, 2L, 3L);
.where(Employee::getId).in(ids)

// Set
Set<EmployeeStatus> statuses = Set.of(ACTIVE, ON_LEAVE);
.where(Employee::getStatus).in(statuses)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByIdsCollection` 方法)

### NOT IN

```java
.where(Employee::getStatus).notIn(TERMINATED, SUSPENDED)
.where(Employee::getId).notIn(excludedIds)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByStatusNotIn` 方法)

---

## NULL 检查

```java
// 为 NULL
.where(Employee::getEmail).isNull()

// 不为 NULL
.where(Employee::getEmail).isNotNull()

// 组合使用
.where(Employee::getManagerId).isNotNull()
.where(Employee::getEmail).isNull()
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findWithoutEmail`, `findWithEmail` 方法)

---

## 字符串运算符

### 模式匹配

```java
// LIKE 模式
.where(Employee::getName).like("%John%")

// 开头匹配
.where(Employee::getName).startsWith("John")

// 结尾匹配
.where(Employee::getName).endsWith("Doe")

// 包含
.where(Employee::getName).contains("hn")

// NOT LIKE
.where(Employee::getName).notLike("%test%")
```

> 📍 **示例位置**: `EmployeeRepository.java` (字符串匹配方法)
> - `findByNameLike`
> - `findByNameStartingWith`
> - `findByEmailEndingWith`
> - `findByNameContaining`
> - `findByNameNotLike`

### 字符串函数

```java
// 大小写转换后比较
.where(Employee::getName).lower().eq("john")
.where(Employee::getName).upper().eq("JOHN")

// 去空格
.where(Employee::getName).trim().eq("John")

// 长度
.where(Path.of(Employee::getName).length()).gt(5)

// 子串
.where(Employee::getName).substring(0, 4).eq("John")
```

> 📍 **示例位置**: `EmployeeRepository.java` (字符串函数方法)
> - `findByNameUppercase`
> - `findByNameTrimmed`
> - `findByNameSubstring`
> - `findByNameLongerThan`

---

## 数值运算符

数值字段支持算术运算：

> **API 说明**：
> - Repository 外部使用 `Path.of(Employee::getSalary)`
> - Repository 内部可使用 `path(Employee::getSalary)`

```java
// 加法
.where(Path.of(Employee::getSalary).add(1000)).gt(60000.0)

// 减法
.where(Path.of(Employee::getSalary).subtract(5000)).ge(40000.0)

// 乘法
.where(Path.of(Employee::getHours).multiply(rate)).gt(1000.0)

// 除法
.where(Path.of(Employee::getSalary).divide(12)).gt(5000.0)

// 取模
.where(Path.of(Employee::getId).mod(10)).eq(0)
```

> 📍 **示例位置**: `EmployeeRepository.java` (数值运算方法)
> - `findBySalaryWithBonus` (add)
> - `findByAnnualSalary` (multiply)
> - `findBySalaryAfterDeduction` (subtract)
> - `findByMonthlySalary` (divide)
> - `findByIdMod` (mod)
> - `findBySalaryEqualsBase` (表达式比较)

---

## 条件运算符

用于可选参数的场景：

### `eqIfNotNull` / `neIfNotNull`

```java
// 当值为 null 时跳过该条件
public List<Employee> search(Long departmentId, EmployeeStatus status) {
    return employeeRepository.query()
        .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
        .where(Employee::getStatus).eqIfNotNull(status)
        .getList();
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByStatusIfPresent` 方法)
> 📍 **多条件搜索**: `EmployeeRepository.java` (`searchEmployees` 方法)

### 比较条件

```java
.where(Employee::getSalary).gtIfNotNull(minSalary)
.where(Employee::getSalary).ltIfNotNull(maxSalary)
.where(Employee::getSalary).geIfNotNull(minSalary)
.where(Employee::getSalary).leIfNotNull(maxSalary)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`advancedSearch` 方法)

### 字符串条件

```java
// 非空时应用
.where(Employee::getName).likeIfNotEmpty(searchName)
.where(Employee::getName).containsIfNotEmpty(text)
.where(Employee::getName).startsWithIfNotNull(prefix)
.where(Employee::getName).endsWithIfNotNull(suffix)
```

> 📍 **示例位置**: `EmployeeRepository.java` (条件字符串方法)

---

## 逻辑组合

> **注意**：OR 条件和复杂组合需要使用 `Path.of()` 方法，请确保已导入：
> ```java
> import io.github.nextentity.api.Path;
> ```

### AND 组合（默认）

```java
// 多个 where 自动组合为 AND
.where(Employee::getActive).eq(true)
.where(Employee::getSalary).gt(50000.0)
// 等价于: active = true AND salary > 50000
```

> 📍 **示例位置**: `EmployeeRepository.java` (多条件 AND 查询)

### OR 组合

```java
// 使用 Path.of() 链式调用
.where(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
        .or(Employee::getStatus).eq(EmployeeStatus.ON_LEAVE))
// 等价于: status = ACTIVE OR status = ON_LEAVE
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByStatusOrStatus` 方法)

### 复杂组合

```java
// AND + OR 组合
.where(Path.of(Employee::getActive).eq(true)
        .and(Path.of(Employee::getSalary).gt(100000.0)
                .or(Employee::getStatus).eq(EmployeeStatus.ACTIVE)))
// 等价于: active = true AND (salary > 100000 OR status = ACTIVE)
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findActiveWithOrCondition` 方法)

---

## 排序

### 单字段排序

```java
// 升序
.orderBy(Employee::getName).asc()

// 降序
.orderBy(Employee::getSalary).desc()
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findOrderedByNameAsc`, `findOrderedBySalaryDesc` 方法)

### 多字段排序

```java
// 先按部门升序，再按薪资降序
.orderBy(Employee::getDepartmentId).asc()
.orderBy(Employee::getSalary).desc()
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findByDepartmentThenSalary` 方法)

### Null 排序处理

```java
// NULL 值排在最后
.orderBy(Employee::getManagerId).asc()
```

---

## 分页

### Offset + Limit

```java
// 第一页（10条）
List<Employee> page1 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .getList(0, 10);

// 第二页（10条）
List<Employee> page2 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .getList(10, 10);

// 第三页（10条）
List<Employee> page3 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .getList(20, 10);
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findFirstPage`, `findPage` 方法)

### Slice（带元数据）

```java
Slice<Employee> slice = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .slice(0, 10);

List<Employee> content = slice.data();    // 数据列表
long totalCount = slice.total();          // 总记录数
int offset = slice.offset();              // 偏移量
int limit = slice.limit();                // 限制数量

// 判断是否有下一页
boolean hasNext = slice.data().size() == slice.limit();
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findFirstSlice`, `findHighEarnerSlice` 方法)
> 📍 **Slice 演示**: `EmployeeRepository.java` (`demonstrateSlice` 方法)

---

## 结果获取

### 终端方法

| 方法 | 返回类型 | 描述 |
|------|----------|------|
| `getList()` | `List<T>` | 获取全部结果 |
| `getList(offset, limit)` | `List<T>` | 分页获取 |
| `getFirst()` | `T` | 获取第一条（null 如果空） |
| `getSingle()` | `T` | 获取单个结果（聚合用） |
| `count()` | `long` | 统计数量 |
| `slice(offset, limit)` | `Slice<T>` | 分片结果 |
| `stream()` | `Stream<T>` | 流式结果 |

### 使用示例

```java
// 获取全部
List<Employee> all = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .getList();

// 获取第一条
Employee first = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

// 统计数量
long count = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .count();

// 流式处理
employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .stream()
    .forEach(System.out::println);
```

> 📍 **示例位置**:
> - `getList()`: `EmployeeRepository.java` (`findAllEmployees`)
> - `getFirst()`: `EmployeeRepository.java:78` (`findEmployeeByEmail`)
> - `count()`: `EmployeeRepository.java` (`countAllEmployees`)
> - `first()`: `EmployeeRepository.java` (`findFirstActive`)
> - `exist()`: `EmployeeRepository.java` (`hasActiveEmployees`, `existsByEmail`)

---

## 下一步

查询条件掌握后，继续学习：

1. **[CRUD 操作指南](crud-operations.md)** - 学习增删改查操作