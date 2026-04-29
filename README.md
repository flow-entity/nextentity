# NextEntity

一个类型安全的 Java SQL DSL 框架，提供流式 API 和 Spring Boot 集成。

## 特性

- **类型安全**：使用方法引用，编译时检查
- **流式 API**：链式调用构建复杂查询
- **Spring 集成**：无缝集成 Spring Boot

## 快速开始

### 依赖

```xml
<dependency>
    <groupId>io.github.flow-entity</groupId>
    <artifactId>nextentity-spring</artifactId>
    <version>${nextentity.version}</version>
</dependency>
```

`nextentity-spring` 同时支持 JDBC 和 JPA 两种后端，根据配置自动选择。

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql:///nextentity?rewriteBatchedStatements=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 定义实体

```java
@Entity
@Table(name = "employee")
@Data
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

### 创建 Repository

有两种方式可以使用 Repository：

#### 方式一：继承 AbstractRepository（推荐用于复杂业务）

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(EntityOperationsFactory factory) {
        super(factory);
    }

    // 自定义查询方法
    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .list();
    }
}
```

#### 方式二：自动注入 Repository 接口（适用于简单 CRUD）

无需创建 Repository 子类，直接注入 `Repository<T, ID>` 接口：

```java
@Service
public class CustomerService {

    @Autowired
    private Repository<Customer, Long> customerRepository;

    public Customer getById(Long id) {
        return customerRepository.getById(id);
    }
}
```

### 使用示例

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // 查询
    public List<Employee> findActiveEmployees() {
        return employeeRepository.query()
            .where(Employee::getActive).eq(true)
            .orderBy(Employee::getName).asc()
            .list();
    }

    // 插入
    public void createEmployee(Employee emp) {
        employeeRepository.insert(emp);
    }

    // 更新
    public void updateSalary(Long id, Double newSalary) {
        Employee emp = employeeRepository.query()
            .where(Employee::getId).eq(id)
            .first();
        if (emp != null) {
            emp.setSalary(newSalary);
            employeeRepository.update(emp);
        }
    }

    // 条件批量更新
    public int deactivateDepartment(Long departmentId) {
        return employeeRepository.update()
            .set(Employee::getActive, false)
            .set(Employee::getStatus, EmployeeStatus.INACTIVE)
            .where(Employee::getDepartmentId).eq(departmentId)
            .execute();
    }

    // 删除
    public void deleteEmployee(Employee emp) {
        employeeRepository.delete(emp);
    }
}
```

## 文档

- [快速入门](docs/guides/getting-started.md)
- [Repository 模式](docs/guides/repository-pattern.md)
- [查询构建](docs/guides/query-building.md)
- [CRUD 操作](docs/guides/crud-operations.md)

## 示例项目

完整示例代码请参考 [nextentity-examples](nextentity-examples) 模块。

## 环境要求

- Java 25+
- Spring Boot 4.0+

## License

Apache License 2.0
