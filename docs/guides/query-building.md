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
- [嵌套路径表达式](#嵌套路径表达式)
- [结果获取](#结果获取)

---

## 查询流程

NextEntity 使用流式 API 构建查询：

```
query() → where() → orderBy() → list()/list(offset, limit)/slice(offset, limit)/first()/single()/count()
```

> **重要**: `query()` 方法在 `AbstractRepository` 中是 `protected` 的，只能在 Repository 内部使用。以下示例均为 Repository 内部的方法实现。

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // Repository 内部使用 query() 的示例
    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)    // 条件
            .orderBy(Employee::getName).asc()        // 排序
            .list(0, 10);                            // 分页获取
    }
}
```

---

## 基本条件

### 使用方法引用

```java
// 方法引用（类型安全）
.where(Employee::getName).eq("John")
```

方法引用提供：
- 编译时类型检查
- IDE 自动补全
- 重构安全

> ⚠️ **注意**: 字符串形式的属性名（如 `.where("name")`）不被支持，必须使用方法引用。

---

## 比较运算符

### 等值比较

```java
// 等于
.where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)

// 不等于
.where(Employee::getStatus).ne(EmployeeStatus.TERMINATED)

// 等于表达式
.where(Employee::getSalary).eq(Path.of(Employee::getSalary))  // 自比较示例
```

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

---

## IN 运算符

### Varargs 参数

```java
// 多个值
.where(Employee::getId).in(1L, 2L, 3L)

// 枚举值
.where(Employee::getStatus).in(ACTIVE, ON_LEAVE, INACTIVE)
```

### Collection 参数

```java
// List
List<Long> ids = List.of(1L, 2L, 3L);
.where(Employee::getId).in(ids)

// Set
Set<EmployeeStatus> statuses = Set.of(ACTIVE, ON_LEAVE);
.where(Employee::getStatus).in(statuses)
```

### NOT IN

```java
.where(Employee::getStatus).notIn(TERMINATED, INACTIVE)
.where(Employee::getId).notIn(excludedIds)
```

---

## NULL 检查

```java
// 为 NULL
.where(Employee::getEmail).isNull()

// 不为 NULL
.where(Employee::getEmail).isNotNull()

// 组合使用
.where(Employee::getDepartmentId).isNotNull()
.where(Employee::getEmail).isNull()
```

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

### 字符串函数

```java
// 大小写转换后比较
.where(Employee::getName).lower().eq("john")
.where(Employee::getName).upper().eq("JOHN")

// 去空格
.where(Employee::getName).trim().eq("John")

// 长度
.where(Employee::getName).length().gt(5)

// 子串
.where(Employee::getName).substring(0, 4).eq("John")
```

---

## 数值运算符

数值字段支持算术运算：

> **API 说明**：
> - Repository 外部使用 `Path.of(Employee::getSalary)`
> - Repository 内部可使用 `path(Employee::getSalary)`

```java
// 加法
.where(Path.of(Employee::getSalary).add(BigDecimal.valueOf(1000))).gt(BigDecimal.valueOf(60000.0))

// 减法
.where(Path.of(Employee::getSalary).subtract(BigDecimal.valueOf(5000))).ge(BigDecimal.valueOf(40000.0))

// 乘法
.where(Path.of(Employee::getSalary).multiply(BigDecimal.valueOf(12))).gt(BigDecimal.valueOf(100000.0))

// 除法
.where(Path.of(Employee::getSalary).divide(BigDecimal.valueOf(12))).gt(BigDecimal.valueOf(5000.0))

// 取模
.where(Path.of(Employee::getId).mod(10)).eq(0)
```

---

## 条件运算符

用于可选参数的场景：

### `eqIfNotNull` / `neIfNotNull`

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // 当值为 null 时跳过该条件
    public List<Employee> search(Long departmentId, EmployeeStatus status) {
        return query()
            .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
            .where(Employee::getStatus).eqIfNotNull(status)
            .list();
    }
}
```

### 比较条件

```java
.where(Employee::getSalary).gtIfNotNull(minSalary)
.where(Employee::getSalary).ltIfNotNull(maxSalary)
.where(Employee::getSalary).geIfNotNull(minSalary)
.where(Employee::getSalary).leIfNotNull(maxSalary)
```

### 字符串条件

```java
// 非空时应用
.where(Employee::getName).likeIfNotEmpty(searchName)
.where(Employee::getName).containsIfNotEmpty(text)
.where(Employee::getName).startsWithIfNotNull(prefix)
.where(Employee::getName).endsWithIfNotNull(suffix)
```

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
.where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
// 等价于: active = true AND salary > 50000
```

### OR 组合

```java
// 使用 Path.of() 链式调用
.where(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
        .or(Employee::getStatus).eq(EmployeeStatus.ON_LEAVE))
// 等价于: status = ACTIVE OR status = ON_LEAVE
```

### 复杂组合

```java
// AND + OR 组合
.where(Path.of(Employee::getActive).eq(true)
        .and(Path.of(Employee::getSalary).gt(BigDecimal.valueOf(100000.0))
                .or(Employee::getStatus).eq(EmployeeStatus.ACTIVE)))
// 等价于: active = true AND (salary > 100000 OR status = ACTIVE)
```

---

## 排序

### 单字段排序

```java
// 升序
.orderBy(Employee::getName).asc()

// 降序
.orderBy(Employee::getSalary).desc()
```

### 多字段排序

```java
// 先按部门升序，再按薪资降序
.orderBy(Employee::getDepartmentId).asc()
.orderBy(Employee::getSalary).desc()
```

### Null 排序处理

```java
// NULL 值排在最后
.orderBy(Employee::getDepartmentId).asc()
```

---

## 分页

### list(offset, limit)

```java
// 第一页（10条）
List<Employee> page1 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .list(0, 10);

// 第二页（10条）
List<Employee> page2 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .list(10, 10);

// 第三页（10条）
List<Employee> page3 = employeeRepository.query()
    .orderBy(Employee::getId).asc()
    .list(20, 10);
```

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

---

## 嵌套路径表达式

当关联实体实现了 `io.github.nextentity.api.Entity` 接口时，可以直接在 `where()` 中访问嵌套属性：

### 基本用法

```java
// Department 实现了 Entity 接口
@Entity
public class Department implements io.github.nextentity.api.Entity {
    private Long id;
    private String name;
    // ...
}

// Employee 关联 Department
@Entity
public class Employee {
    private Department department;
    // ...
}

// 直接访问嵌套属性
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartment).get(Department::getName).eq("技术部")
    .list();
```

生成的 SQL：

```sql
SELECT e.* FROM employee e
LEFT JOIN department d ON e.department_id = d.id
WHERE d.name = '技术部'
```

### 多级嵌套

```java
// 先筛选部门属性，再查询
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartment).get(Department::getLocation).startsWith("北京")
    .where(Employee::getDepartment).get(Department::getActive).eq(true)
    .list();
```

### 结合其他条件

```java
// 嵌套条件与其他条件组合
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartment).get(Department::getName).eq("技术部")
    .where(Employee::getActive).eq(true)
    .where(Employee::getSalary).gt(BigDecimal.valueOf(50000))
    .orderBy(Employee::getName).asc()
    .list();
```

> **重要提示**：要使用此功能，关联实体类必须实现 `io.github.nextentity.api.Entity` 接口。这会让编译器正确识别方法引用类型。

---

## 结果获取

### 终端方法

| 方法 | 返回类型 | 描述 |
|------|----------|------|
| `list()` | `List<T>` | 获取全部结果 |
| `list(int limit)` | `List<T>` | 获取前 N 条结果 |
| `list(int offset, int limit)` | `List<T>` | 分页获取结果 |
| `slice(int offset, int limit)` | `Slice<T>` | 分片结果（带元数据） |
| `lock(LockModeType)` | `Collector<T>` | 设置锁模式 |
| `first()` | `T` | 获取第一条（null 如果空） |
| `single()` | `T` | 获取单个结果（不存在或多条时抛异常） |
| `exists()` | `boolean` | 判断是否存在结果 |
| `exists(int offset)` | `boolean` | 判断指定偏移量后是否存在结果 |
| `count()` | `long` | 统计数量 |

### 使用示例

以下示例均为 Repository 内部方法：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // 获取全部
    public List<Employee> findAllActive() {
        return query()
            .where(Employee::getActive).eq(true)
            .list();
    }

    // 获取前 20 条
    public List<Employee> findTop20Active() {
        return query()
            .where(Employee::getActive).eq(true)
            .list(20);
    }

    // 分页获取（跳过 10 条，取 20 条）
    public List<Employee> findPage(int offset, int limit) {
        return query()
            .where(Employee::getActive).eq(true)
            .orderBy(Employee::getName).asc()
            .list(offset, limit);
    }

    // 获取第一条
    public Employee findById(Long id) {
        return query()
            .where(Employee::getId).eq(id)
            .first();
    }

    // 统计数量
    public long countActive() {
        return query()
            .where(Employee::getActive).eq(true)
            .count();
    }
}
```

---

## 下一步

查询条件掌握后，继续学习：

1. **[CRUD 操作指南](crud-operations.md)** - 学习增删改查操作