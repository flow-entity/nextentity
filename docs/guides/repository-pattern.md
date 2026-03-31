# Repository 模式

本指南介绍如何使用 NextEntity 的 Repository 模式进行数据访问。

## 目录

- [简介](#简介)
- [创建 Repository](#创建-repository)
- [自定义查询方法](#自定义查询方法)
- [事务处理](#事务处理)

---

## 简介

Repository 模式将数据访问逻辑封装在专用类中，提供类型安全的数据访问。

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

## 自定义查询方法

### 简单查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 查询全部
    public List<Employee> findAllEmployees() {
        return query().getList();
    }

    // 按 ID 查询
    public Employee findEmployeeById(Long id) {
        return query()
            .where(Employee::getId).eq(id)
            .getFirst();
    }

    // 查询活跃员工
    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .getList();
    }

    // 按部门查询
    public List<Employee> findByDepartmentId(Long departmentId) {
        return query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .getList();
    }
}
```

### 条件查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 动态条件查询
    public List<Employee> searchEmployees(String name, Long departmentId, Double minSalary) {
        return query()
            .where(Employee::getName).containsIfNotEmpty(name)
            .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
            .where(Employee::getSalary).gtIfNotNull(minSalary)
            .getList();
    }

    // 范围查询
    public List<Employee> findBySalaryBetween(Double min, Double max) {
        return query()
            .where(Employee::getSalary).between(min, max)
            .getList();
    }

    // IN 查询
    public List<Employee> findByStatus(EmployeeStatus status) {
        return query()
            .where(Employee::getStatus).eq(status)
            .getList();
    }

    public List<Employee> findByIds(Long... ids) {
        return query()
            .where(Employee::getId).in(ids)
            .getList();
    }
}
```

### 分页查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 分页查询
    public List<Employee> findPage(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        return query()
            .orderBy(Employee::getId).asc()
            .getList(offset, pageSize);
    }

    // Slice 分页
    public Slice<Employee> findFirstSlice() {
        return query()
            .orderBy(Employee::getId).asc()
            .slice(0, 10);
    }
}
```

### 排序查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 单字段排序
    public List<Employee> findOrderedByNameAsc() {
        return query()
            .orderBy(Employee::getName).asc()
            .getList();
    }

    // 多字段排序
    public List<Employee> findByDepartmentThenSalary() {
        return query()
            .orderBy(Employee::getDepartmentId).asc()
            .orderBy(Employee::getSalary).desc()
            .getList();
    }
}
```

### 统计查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    // 计数
    public long countAllEmployees() {
        return query().count();
    }

    public long countActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .count();
    }

    // 聚合查询（使用 selectExpr）
    public Double calculateTotalSalary() {
        return query()
            .selectExpr(path(Employee::getSalary).sum())
            .getSingle();
    }
}
```

---

## 事务处理

> **注意**：推荐在 Service 层使用 Spring 的 `@Transactional` 注解。

```java
@Service
public class EmployeeService {

    @Transactional
    public void giveDepartmentRaise(Long departmentId, double percentage) {
        List<Employee> employees = employeeRepository.query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .getList();
        employeeRepository.updateAll(employees);
    }

    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        List<Employee> employees = employeeRepository.query()
            .where(Employee::getId).in(employeeIds)
            .getList();
        employees.forEach(e -> e.setDepartmentId(newDepartmentId));
        employeeRepository.updateAll(employees);
    }
}
```

---

## 下一步

- [查询构建指南](query-building.md) - 查询条件速查
- [CRUD 操作指南](crud-operations.md) - CRUD 操作速查