# Spring Boot 集成指南

本指南介绍如何将 NextEntity 集成到 Spring Boot 项目中。

## 目录

- [依赖配置](#依赖配置)
- [数据库配置](#数据库配置)
- [Repository 定义](#repository-定义)
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

### 创建 Repository

继承 `AbstractRepository` 即可，依赖由 Spring 自动注入：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {
}
```

添加 Spring Boot 自动配置依赖后，`NextEntityFactory` 会根据 classpath 自动选择 JDBC 或 JPA 后端，无需手动构造。

如果需要自定义查询方法：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .list();
    }
}
```

### 使用 Repository

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findActive() {
        return employeeRepository.query()
            .where(Employee::getActive).eq(true)
            .list();
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