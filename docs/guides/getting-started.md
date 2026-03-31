# 快速入门指南

本指南帮助您快速上手 NextEntity，一个类型安全的 Java SQL DSL 框架。

## 目录

- [简介](#简介)
- [环境要求](#环境要求)
- [安装依赖](#安装依赖)
- [数据库配置](#数据库配置)
- [定义实体](#定义实体)
- [创建 Repository](#创建-repository)
- [基本使用](#基本使用)
- [下一步](#下一步)

---

## 简介

NextEntity 是一个类型安全的 SQL DSL 框架，提供：

- **类型安全**：使用方法引用，编译时检查
- **流式 API**：链式调用构建复杂查询
- **Spring 集成**：无缝集成 Spring Boot

---

## 环境要求

| 组件 | 版本 |
|------|------|
| Java | 25+ |
| Spring Boot | 4.0+ |

---

## 安装依赖

```xml
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>${nextentity.version}</version>
</dependency>
```

`nextentity-spring` 同时支持 JDBC 和 JPA 两种后端，根据配置自动选择。

---

## 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql:///nextentity?rewriteBatchedStatements=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 定义实体

使用标准 JPA 注解定义实体：

```java
@Entity
@Table(name = "employee")
@Data  // Lombok
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
}
```

---

## 创建 Repository

### AbstractRepository

继承 `AbstractRepository` 创建类型安全的 Repository：

```java
// JDBC 后端
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
}

// JPA 后端
@Repository
public class DepartmentRepository extends AbstractRepository<Department, Long> {

    public DepartmentRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }
}
```

### PersistableRepository

对于实现 `Persistable<ID>` 接口的实体，可以使用 `PersistableRepository`：

```java
// 实体实现 Persistable 接口
public class Product implements Persistable<Long> {
    private Long id;

    @Override
    public Long getId() { return id; }
}

// Repository 继承 PersistableRepository
@Repository
public class ProductRepository extends PersistableRepository<Product, Long> {

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 自动获得 findById、getById、existsById、deleteById 等方法
}
```

---

## 基本使用

### 查询操作

```java
// 查询全部
List<Employee> employees = employeeRepository.query().getList();

// 按 ID 查询
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

// 条件查询
List<Employee> activeEmployees = employeeRepository.query()
    .where(Employee::getActive).eq(true)
    .orderBy(Employee::getName).asc()
    .getList();

// 范围查询
List<Employee> employeesBySalary = employeeRepository.query()
    .where(Employee::getSalary).between(40000.0, 80000.0)
    .getList();
```

### CRUD 操作

```java
// 插入
Employee emp = new Employee();
emp.setId(1L);
emp.setName("John Doe");
emp.setEmail("john@example.com");
employeeRepository.insert(emp);

// 批量插入
employeeRepository.insertAll(List.of(emp1, emp2, emp3));

// 更新
emp.setSalary(60000.0);
employeeRepository.update(emp);

// 批量更新
employeeRepository.updateAll(employees);

// 删除
employeeRepository.delete(emp);

// 批量删除
employeeRepository.deleteAll(employees);
```

---

## 下一步

1. **[Repository 模式](repository-pattern.md)** - 自定义查询方法
2. **[示例项目](../../nextentity-examples)** - 完整示例代码