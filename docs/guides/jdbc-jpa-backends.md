# JDBC 和 JPA 后端指南

本指南介绍 NextEntity 支持的后端配置和选择。

## 目录

- [简介](#简介)
- [后端选择](#后端选择)
- [功能对比](#功能对比)
- [JDBC 后端](#jdbc-后端)
- [JPA 后端](#jpa-后端)
- [数据库支持](#数据库支持)
- [性能对比](#性能对比)
- [迁移指南](#迁移指南)

---

## 简介

NextEntity 支持两种数据库后端：

| 后端 | 描述 | 适用场景 |
|------|------|----------|
| JDBC | 纯 JDBC 实现 | 高性能、简单场景 |
| JPA | Hibernate 实现 | 需要实体管理、复杂关联 |

两者提供相同的 API，可按需选择或切换。

---

## 后端选择

### JDBC 后端优势

- **高性能**：直接 SQL 操作，无 ORM 转换开销
- **轻量级**：无需 Hibernate 依赖
- **简单**：适合简单 CRUD 场景
- **灵活**：直接控制 SQL

### JPA 后端优势

- **实体管理**：自动脏检查、懒加载
- **复杂映射**：多表关联、继承映射
- **缓存**：一级缓存、二级缓存支持
- **标准**：JPA 标准接口

### 选择建议

| 场景 | 推荐后端 |
|------|----------|
| 简单 CRUD | JDBC |
| 高性能要求 | JDBC |
| 复杂关联映射 | JPA |
| 需要懒加载 | JPA |
| 需要缓存 | JPA |

---

## 功能对比

### 核心功能对比

| 功能 | JDBC 后端 | JPA 后端 |
|------|:---------:|:--------:|
| 基本查询 (select/where/order) | ✅ | ✅ |
| CRUD 操作 (insert/update/delete) | ✅ | ✅ |
| 批量操作 (insertAll/updateAll) | ✅ | ✅ |
| 投影 (select/DTO) | ✅ | ✅ |
| 聚合 (count/sum/avg/max/min) | ✅ | ✅ |
| 分页 (getList/slice) | ✅ | ✅ |
| 显式 Fetch | ✅ | ✅ |

### JPA 高级功能

| 功能 | JDBC 后端 | JPA 后端 | 说明 |
|------|:---------:|:--------:|------|
| **乐观锁 (@Version)** | ✅ | ✅ | 自动版本控制 |
| **懒加载 (Lazy Loading)** | ❌ | ✅ | JDBC 需显式 `fetch()` |
| **脏检查 (Dirty Checking)** | ❌ | ✅ | JDBC 需显式调用 `update()` |
| **一级缓存 (Session Cache)** | ❌ | ✅ | 同一事务内重复查询无缓存 |
| **二级缓存 (Second Level Cache)** | ❌ | ✅ | 跨 Session 缓存不可用 |
| **自动 DDL (ddl-auto)** | ❌ | ✅ | JDBC 需手动建表或使用其他工具 |
| **实体生命周期回调** | ❌ | ✅ | @PrePersist/@PostLoad 等无效 |
| **继承映射策略** | ❌ | ✅ | SINGLE_TABLE/JOINED 等不支持 |
| **多对多关联 (@ManyToMany)** | ❌ | ✅ | JDBC 需手动处理中间表 |
| **嵌入对象 (@Embedded)** | ❌ | ✅ | JDBC 不支持嵌入式对象 |
| **集合映射 (@ElementCollection)** | ❌ | ✅ | JDBC 不支持集合字段映射 |
| **枚举映射 (@Enumerated)** | 部分 | ✅ | JDBC 只支持简单映射 |
| **级联操作 (Cascade)** | ❌ | ✅ | JDBC 需手动处理级联 |
| **Orphan Removal** | ❌ | ✅ | JDBC 需手动删除孤立记录 |
| **Entity Graph** | ❌ | ✅ | JDBC 不支持动态抓取计划 |
| **Criteria API** | ❌ | ✅ | NextEntity 提供替代 DSL |
| **JPQL 原生查询** | ❌ | ✅ | JDBC 使用 SQL 原生查询 |

### JDBC 后端限制详解

#### 1. 懒加载不可用

```java
// ❌ JDBC 后端：懒加载无效，关联始终为 null
@Entity
public class Employee {
    @ManyToOne(fetch = FetchType.LAZY)  // 注解无效
    private Department department;
}

Employee emp = repository.query().getFirst();
Department dept = emp.getDepartment();  // JDBC: 返回 null

// ✅ 正确做法：显式 fetch
Employee emp = repository.query()
    .fetch(Employee::getDepartment)
    .getFirst();
```

#### 2. 无自动脏检查

```java
// ❌ JDBC 后端：事务内修改不会自动更新
@Transactional
public void updateSalary(Long id) {
    Employee emp = repository.query().where(Employee::getId).eq(id).getFirst();
    emp.setSalary(BigDecimal.valueOf(60000.0));
    // 事务结束不会自动保存！
}

// ✅ 正确做法：显式调用 update
@Transactional
public void updateSalary(Long id) {
    Employee emp = repository.query().where(Employee::getId).eq(id).getFirst();
    emp.setSalary(BigDecimal.valueOf(60000.0));
    repository.update(emp);  // 必须显式调用
}
```

#### 3. 无缓存机制

```java
// ❌ JDBC 后端：同一实体查询多次返回不同对象
Employee emp1 = repository.query().where(Employee::getId).eq(1L).getFirst();
Employee emp2 = repository.query().where(Employee::getId).eq(1L).getFirst();
// emp1 != emp2（不同对象实例）

// ✅ JPA 后端：一级缓存保证同一对象
Employee emp1 = repository.query().where(Employee::getId).eq(1L).getFirst();
Employee emp2 = repository.query().where(Employee::getId).eq(1L).getFirst();
// emp1 == emp2（同一对象实例）
```

#### 4. 级联操作需手动处理

```java
// ❌ JDBC 后端：级联注解无效
@Entity
public class Department {
    @OneToMany(cascade = CascadeType.ALL)  // 无效
    private List<Employee> employees;
}

// ✅ 手动处理级联
public void saveDepartmentWithEmployees(Department dept) {
    departmentRepository.insert(dept);
    for (Employee emp : dept.getEmployees()) {
        emp.setDepartmentId(dept.getId());
        employeeRepository.insert(emp);
    }
}
```

### 选择建议（详细场景）

| 场景 | 推荐后端 | 原因 |
|------|----------|------|
| 简单 CRUD 表格 | JDBC | 无需 ORM 特性，性能更好 |
| 高并发读取 | JDBC | 避免 ORM 转换开销 |
| 批量数据导入 | JDBC | 配合 rewriteBatchedStatements 更快 |
| 复杂对象关系 | JPA | 支持多级关联、继承映射 |
| 需要懒加载 | JPA | 按需加载，减少初始数据量 |
| 需要缓存优化 | JPA | 一级/二级缓存减少数据库访问 |
| 需要乐观锁 | 两者均可 | @Version 自动版本控制 |
| 需要实体生命周期回调 | JPA | @PrePersist 等自动触发 |
| 微服务简单服务 | JDBC | 轻量级，依赖少 |

---

## JDBC 后端

### 依赖配置

```xml
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>${nextentity.version}</version>
</dependency>
```

### 数据源配置

```yaml
spring:
  datasource:
    url: jdbc:mysql:///nextentity?rewriteBatchedStatements=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

### 实体定义（JDBC）

JDBC 后端不依赖 JPA 注解，但可以使用：

```java
@Data
public class Employee {

    private Long id;           // 主键
    private String name;
    private String email;
    private BigDecimal salary;
    private Boolean active;
    private Long departmentId;

    // 关联需要手动处理
    private Department department;  // 不会自动加载
}
```

### Repository 定义

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }
}
```

### JDBC 后端特点与限制

#### 支持的功能

- ✅ 类型安全的查询 DSL
- ✅ CRUD 和批量操作
- ✅ 投影、聚合、分页
- ✅ 显式 `fetch()` 加载关联

#### 不支持的 JPA 功能

- ❌ 懒加载（必须显式 `fetch()`）
- ❌ 自动脏检查（必须显式 `update()`）
- ❌ 一级/二级缓存
- ❌ 自动 DDL（需手动建表）
- ❌ 级联操作 CascadeType
- ❌ 实体生命周期回调
- ❌ 继承映射策略
- ❌ 嵌入对象 @Embedded
- ❌ 多对多关联自动处理

#### 代码示例差异

**1. 关联加载**

```java
// ❌ 错误：依赖懒加载（JDBC 无效）
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();
Department dept = emp.getDepartment();  // 返回 null！

// ✅ 正确：显式 fetch
Employee emp = employeeRepository.query()
    .fetch(Employee::getDepartment)  // 显式加载
    .where(Employee::getId).eq(1L)
    .getFirst();
Department dept = emp.getDepartment();  // 已加载
```

**2. 更新操作**

```java
// ❌ 错误：依赖自动脏检查（JDBC 无效）
@Transactional
public void raiseSalary(Long id) {
    Employee emp = employeeRepository.query()
        .where(Employee::getId).eq(id)
        .getFirst();
    emp.setSalary(emp.getSalary().multiply(BigDecimal.valueOf(1.1)));
    // 事务结束不会自动保存！
}

// ✅ 正确：显式调用 update
@Transactional
public void raiseSalary(Long id) {
    Employee emp = employeeRepository.query()
        .where(Employee::getId).eq(id)
        .getFirst();
    emp.setSalary(emp.getSalary().multiply(BigDecimal.valueOf(1.1)));
    employeeRepository.update(emp);  // 必须显式调用
}
```

**3. 批量操作优化**

```yaml
# MySQL 批量优化配置
spring:
  datasource:
    url: jdbc:mysql:///mydb?rewriteBatchedStatements=true
```

```java
// 批量插入（高效）
employeeRepository.insertAll(employees);
```

---

## JPA 后端

### 依赖配置

```xml
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>${nextentity.version}</version>
</dependency>
```

### 数据源配置

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
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
```

### 实体定义（JPA）

使用标准 JPA 注解：

```java
@Entity
@Table(name = "employee")
@Data
public class Employee {

    @Id
    private Long id;

    private String name;

    private String email;

    private BigDecimal salary;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;  // 懒加载
}
```

### JPA 后端优势功能

JPA 后端提供完整的 Hibernate ORM 功能：

#### 1. 懒加载（Lazy Loading）

```java
@Entity
public class Employee {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private Department department;
}

// 查询时不加载关联
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

// 访问时自动加载（触发额外查询）
Department dept = emp.getDepartment();  // 自动加载
```

#### 2. 自动脏检查（Dirty Checking）

```java
@Transactional
public void updateSalary(Long id, BigDecimal salary) {
    Employee emp = employeeRepository.query()
        .where(Employee::getId).eq(id)
        .getFirst();

    emp.setSalary(salary);
    // 事务结束自动更新，无需显式调用 update()
}
```

#### 3. 一级缓存（Session Cache）

```java
// 同一事务内，相同 ID 返回同一对象实例
@Transactional
public void demo() {
    Employee emp1 = employeeRepository.query()
        .where(Employee::getId).eq(1L).getFirst();
    Employee emp2 = employeeRepository.query()
        .where(Employee::getId).eq(1L).getFirst();

    System.out.println(emp1 == emp2);  // true（同一实例）
}
```

#### 4. 乐观锁（@Version）

```java
@Entity
public class Employee {
    @Id
    private Long id;

    @Version
    private Integer version;  // 自动版本控制
}

// 更新时自动检查版本
Employee emp = employeeRepository.query().getFirst();
emp.setSalary(BigDecimal.valueOf(60000.0));
employeeRepository.update(emp);  // 自动检查版本，冲突抛出异常
```

#### 5. 级联操作（Cascade）

```java
@Entity
public class Department {
    @Id
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Employee> employees;
}

// 保存部门时自动保存员工
Department dept = new Department();
dept.setEmployees(List.of(emp1, emp2));
departmentRepository.insert(dept);  // 员工自动插入
```

#### 6. 实体生命周期回调

```java
@Entity
public class Employee {
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // 插入前自动设置
    }

    @PostLoad
    public void postLoad() {
        this.loaded = true;  // 加载后自动处理
    }
}
```

#### 7. 二级缓存配置

```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.ehcache.EhCacheRegionFactory
```

---

## 数据库支持

### MySQL

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb?rewriteBatchedStatements=true
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### MSSQL

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=mydb
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### PostgreSQL

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    driver-class-name: org.postgresql.Driver
```

### Oracle

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:orcl
    driver-class-name: oracle.jdbc.OracleDriver
```

---

## 性能对比

### JDBC vs JPA

| 操作 | JDBC | JPA |
|------|------|------|
| 简单查询 | 更快 | 略慢（ORM 转换） |
| 批量插入 | 更快（rewriteBatchedStatements） | 中等 |
| 更新操作 | 需显式调用 | 自动脏检查 |
| 关联查询 | 需显式 fetch | 懒加载 |
| 缓存 | 无 | 一级/二级缓存 |

### 性能优化建议

#### JDBC 优化

```yaml
# MySQL 批量优化
spring:
  datasource:
    url: jdbc:mysql:///mydb?rewriteBatchedStatements=true&useServerPrepStmts=true
```

#### JPA 优化

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
        default_batch_fetch_size: 100
```

---

## 迁移指南

### 从 JDBC 到 JPA

1. 更换依赖：

```xml
<!-- 无需更换依赖，只需添加 JPA 配置 -->
<artifactId>nextentity-spring</artifactId>
```

2. 添加 JPA 配置：

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

3. 添加 JPA 注解：

```java
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private Department department;
}
```

### 从 JPA 到 JDBC

**⚠️ 重要警告**：切换到 JDBC 后端会失去以下 JPA 功能：

| 失去的功能 | 影响 |
|------------|------|
| 懒加载 | 关联属性默认为 null，需显式 `fetch()` |
| 自动脏检查 | 修改实体后必须显式调用 `update()` |
| 一级缓存 | 重复查询返回不同对象实例 |
| 级联操作 | 需手动处理关联实体的保存/删除 |
| 实体回调 | @PrePersist/@PostLoad 不触发 |
| 自动 DDL | 表结构不会自动创建/更新 |

**迁移步骤：**

1. 无需更换依赖：

```xml
<!-- 无需更换依赖 -->
<artifactId>nextentity-spring</artifactId>
```

2. 移除 JPA 配置。

3. **调整代码（必须）**：

```java
// ❌ JPA 代码（JDBC 后会失效）
@Transactional
public void processOrder(Long orderId) {
    Order order = orderRepository.query()
        .where(Order::getId).eq(orderId)
        .getFirst();

    // 懒加载 - JDBC 会返回 null
    Customer customer = order.getCustomer();

    // 自动脏检查 - JDBC 不会自动保存
    order.setStatus(COMPLETED);
}

// ✅ JDBC 正确代码
@Transactional
public void processOrder(Long orderId) {
    Order order = orderRepository.query()
        .fetch(Order::getCustomer)     // 显式 fetch
        .where(Order::getId).eq(orderId)
        .getFirst();

    Customer customer = order.getCustomer();  // 已加载

    order.setStatus(COMPLETED);
    orderRepository.update(order);             // 显式 update
}
```

4. **手动处理级联**：

```java
// ❌ JPA 级联（JDBC 失效）
departmentRepository.insert(dept);  // CascadeType.ALL 无效

// ✅ JDBC 手动级联
departmentRepository.insert(dept);
for (Employee emp : dept.getEmployees()) {
    emp.setDepartmentId(dept.getId());
    employeeRepository.insert(emp);
}
```

---

## 最佳实践

### 1. 根据场景选择后端

```java
// 简单 CRUD：JDBC
@Repository
public class LogRepository extends AbstractRepository<Log, Long> {
    // JDBC 足够
}

// 复杂关联：JPA
@Repository
public class OrderRepository extends AbstractRepository<Order, Long> {
    // 需要懒加载订单项、客户等
}
```

### 2. 配置批量优化

```yaml
# JDBC（MySQL）
rewriteBatchedStatements=true

# JPA
hibernate.jdbc.batch_size=50
```

### 3. 合理使用 fetch

```java
// JDBC：始终使用 fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .getList();

// JPA：按需使用 fetch 或懒加载
List<Employee> employees = employeeRepository.query()
    .getList();  // 懒加载

// 或显式 fetch
List<Employee> employees = employeeRepository.query()
    .fetch(Employee::getDepartment)
    .getList();
```

---

## 下一步

选择后端后，继续学习核心功能：

1. **[Spring Boot 集成](spring-integration.md)** - 配置 Spring Boot 项目