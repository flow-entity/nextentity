# Spring Boot 集成指南

本指南介绍如何将 NextEntity 集成到 Spring Boot 项目中。

## 目录

- [依赖配置](#依赖配置)
- [数据库配置](#数据库配置)
- [Repository 定义](#repository-定义)
- [路径表达式](#路径表达式)
- [条件更新与删除](#条件更新与删除)
- [事务管理](#事务管理)

---

## 依赖配置

```xml
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>${nextentity.version}</version>
</dependency>
```

`nextentity-spring` 同时支持 JDBC 和 JPA 两种后端，根据配置自动选择。

### 数据库驱动

```xml
<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

---

## 数据库配置

### JDBC 后端

```yaml
spring:
  datasource:
    url: jdbc:mysql:///nextentity?rewriteBatchedStatements=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### JPA 后端

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

`rewriteBatchedStatements=true` 参数将批量操作合并为单条 SQL，提升性能。

---

## Repository 定义

NextEntity 提供两种 Repository 使用方式：

| 方式                        | 适用场景       | 特点           |
|---------------------------|------------|--------------|
| 继承 `AbstractRepository`   | 需要自定义查询方法  | 可添加业务特定的查询逻辑 |
| 注入 `Repository<T, ID>` 接口 | 简单 CRUD 操作 | 无需创建子类，自动注入  |

### 方式一：继承 AbstractRepository 基类

继承 `AbstractRepository` 并定义构造方法，注入 `EntityTemplateFactory`：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(EntityTemplateFactory factory) {
        super(factory);
    }
}
```

AbstractRepository 提供以下基于 ID 的方法：
- `findById(id)` → `Optional<T>`
- `getById(id)` → `T`（可能为 null）
- `findAll()` → `List<T>`
- `findAllById(ids)` / `getAllById(ids)` → `List<T>`
- `findAllAsMapById(ids)` → `Map<ID, T>`
- `findAllAsMap()` → `Map<ID, T>`
- `count()` → `long`
- `existsById(id)` → `boolean`
- `countById(ids)` → `long`
- `deleteById(id)` / `deleteAllById(ids)`
- `deleteAll()` → 删除所有实体

### 方式二：注入 Repository 接口

对于简单的 CRUD 操作，无需创建 Repository 子类，直接注入 `Repository<T, ID>` 接口：

```java
@Service
public class CustomerService {

    @Autowired
    private Repository<Customer, Long> customerRepository;

    public Customer getById(Long id) {
        return customerRepository.getById(id);
    }

    public List<Customer> findAll() {
        return customerRepository.query().list();
    }
}
```

Spring Boot 自动配置会根据注入点的泛型参数自动创建对应的 Repository Bean。

> **配置开关**：可通过 `nextentity.generic-repository=false` 禁用 Repository 自动注入：
> ```yaml
> nextentity:
>   enabled: true
>   generic-repository: false  # 禁用 Repository 自动注入
> ```

> **注意**：
> - `Repository<T, ID>` 接口中 `query()` 是公共方法，可以在 Service 中直接调用
> - `AbstractRepository` 中 `query()` 是 `protected` 的，只能在子类内部使用

### 使用示例

在 Service 层注入 Repository：

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long id) {
        return userRepository.getById(id);
    }

    public Map<Long, User> getUsersByIds(List<Long> ids) {
        return userRepository.findAllAsMapById(ids);
    }

    public boolean exists(Long id) {
        return userRepository.existsById(id);
    }
}
```

---

## 路径表达式

`AbstractRepository` 提供类型安全的 `path()` 方法族，用于构建查询条件：

```java
@Repository
public class OrderRepository extends AbstractRepository<Order, Long> {

    protected OrderRepository(EntityTemplateFactory factory) {
        super(factory);
    }

    public List<Order> findByStatus(String status) {
        return query()
            .where(path(Order::getStatus)).eq(status)  // 字符串路径
            .list();
    }

    public List<Order> findHighValueOrders(BigDecimal threshold) {
        return query()
            .where(path(Order::getTotalAmount)).gt(threshold)  // BigDecimal 路径
            .list();
    }

    public List<Order> findRecentOrders(LocalDate since) {
        return query()
            .where(path(Order::getCreatedAt)).ge(since)  // 通用路径
            .orderBy(path(Order::getCreatedAt)).desc()
            .list();
    }
}
```

### 类型特化方法

| 方法 | 适用类型 | 特殊操作 |
|------|---------|---------|
| `path(StringRef)` | String | `like`, `startsWith`, `endsWith`, `contains` |
| `path(NumberRef)` | Number | `gt`, `lt`, `ge`, `le`, `add`, `subtract` |
| `path(BooleanRef)` | Boolean | `isTrue`, `isFalse` |
| `path(EntityPathRef)` | 关联实体 | 嵌套属性访问 |

---

## 条件更新与删除

`AbstractRepository` 提供条件批量更新和删除功能：

### 条件更新

```java
@Repository
public class UserRepository extends AbstractRepository<User, Long> {

    protected UserRepository(EntityTemplateFactory factory) {
        super(factory);
    }

    @Transactional
    public int archiveInactiveUsers(LocalDate threshold) {
        return update()
            .set(User::getStatus, "ARCHIVED")
            .where(path(User::getLastLoginAt)).lt(threshold)
            .execute();
    }

    @Transactional
    public int bulkUpdateDepartment(Long oldDeptId, Long newDeptId) {
        return update()
            .set(User::getDepartmentId, newDeptId)
            .where(path(User::getDepartmentId)).eq(oldDeptId)
            .execute();
    }
}
```

### 条件删除

```java
@Repository
public class LogRepository extends AbstractRepository<Log, Long> {

    protected LogRepository(EntityTemplateFactory factory) {
        super(factory);
    }

    @Transactional
    public int deleteOldLogs(LocalDate before) {
        return delete()
            .where(path(Log::getCreatedAt)).lt(before)
            .execute();
    }

    @Transactional
    public int deleteByStatus(String... statuses) {
        return delete()
            .where(path(Log::getStatus)).in(statuses)
            .execute();
    }
}
```

---

## 事务管理

### @Transactional 注解

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void hireEmployee(Employee emp, Department dept) {
        departmentRepository.insert(dept);
        emp.setDepartmentId(dept.getId());
        employeeRepository.insert(emp);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.query().list();
    }
}
```

### 事务传播

```java
@Transactional(propagation = Propagation.REQUIRED)   // 默认
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Transactional(propagation = Propagation.SUPPORTS)
```

---

## 下一步

- [查询构建指南](query-building.md) - 学习条件运算符、排序、分页
- [CRUD 操作指南](crud-operations.md) - CRUD 操作速查
- [JDBC/JPA 后端指南](jdbc-jpa-backends.md) - 后端功能差异对比