# Repository 模式

本指南介绍如何使用 NextEntity 的 Repository 模式进行数据访问。

## 目录

- [简介](#简介)
- [创建 Repository](#创建-repository)
- [自定义查询方法](#自定义查询方法)
- [条件批量操作](#条件批量操作)
- [事务处理](#事务处理)

---

## 简介

Repository 模式将数据访问逻辑封装在专用类中，提供类型安全的数据访问。

---

## 创建 Repository

### AbstractRepository

继承 `AbstractRepository` 创建类型安全的 Repository：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {
}

@Repository
public class DepartmentRepository extends AbstractRepository<Department, Long> {
}
```

### PersistableRepository

对于实现 `Persistable<ID>` 接口的实体，可以使用 `PersistableRepository`：

```java
// 实体实现 Persistable 接口
@Entity
public class Product implements Persistable<Long> {
    @Id
    private Long id;

    @Override
    public Long getId() { return id; }
}

// Repository 继承 PersistableRepository
@Repository
public class ProductRepository extends PersistableRepository<Product, Long> {

    // 自动获得 findById、getById、existsById、deleteById 等方法
}
```

---

## 自定义查询方法

### 简单查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // 查询全部
    public List<Employee> findAllEmployees() {
        return query().list();
    }

    // 按 ID 查询
    public Employee findEmployeeById(Long id) {
        return query()
            .where(Employee::getId).eq(id)
            .first();
    }

    // 查询活跃员工
    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .list();
    }

    // 按部门查询
    public List<Employee> findByDepartmentId(Long departmentId) {
        return query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .list();
    }
}
```

### 条件查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {


    // 动态条件查询
    public List<Employee> searchEmployees(String name, Long departmentId, BigDecimal minSalary) {
        return query()
            .where(Employee::getName).containsIfNotEmpty(name)
            .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
            .where(Employee::getSalary).geIfNotNull(minSalary)
            .list();
    }

    // 范围查询
    public List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max) {
        return query()
            .where(Employee::getSalary).between(min, max)
            .list();
    }

    // IN 查询
    public List<Employee> findByStatus(EmployeeStatus status) {
        return query()
            .where(Employee::getStatus).eq(status)
            .list();
    }

    public List<Employee> findByIds(Long... ids) {
        return query()
            .where(Employee::getId).in(ids)
            .list();
    }
}
```

### 分页查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {


    // 分页查询
    public List<Employee> findPage(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        return query()
            .orderBy(Employee::getId).asc()
            .list(offset, pageSize);
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


    // 单字段排序
    public List<Employee> findOrderedByNameAsc() {
        return query()
            .orderBy(Employee::getName).asc()
            .list();
    }

    // 多字段排序
    public List<Employee> findByDepartmentThenSalary() {
        return query()
            .orderBy(Employee::getDepartmentId).asc()
            .orderBy(Employee::getSalary).desc()
            .list();
    }
}
```

### 统计查询

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {


    // 计数
    public long countAllEmployees() {
        return query().count();
    }

    public long countActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .count();
    }

    // 聚合查询
    public BigDecimal calculateTotalSalary() {
        return query()
            .select(path(Employee::getSalary).sum())
            .single();
    }
}
```

## 条件批量操作

当你不需要先加载实体，只想直接执行批量更新或删除时，可以使用 `update()` 和 `delete()`：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {


    public int deactivateEmployeesByDepartment(Long departmentId) {
        return update()
            .set(Employee::getActive, false)
            .set(Employee::getStatus, EmployeeStatus.INACTIVE)
            .where(Employee::getDepartmentId).eq(departmentId)
            .execute();
    }

    public int deleteInactiveEmployees() {
        return delete()
            .where(Employee::getStatus).eq(EmployeeStatus.INACTIVE)
            .execute();
    }
}
```

这种写法更适合统一字段更新或大批量删除，因为它避免了"先查出实体，再逐个回写"的开销。

---

## 事务处理

> **重要**: 推荐在 Service 层使用 Spring 的 `@Transactional` 注解，Service 应调用 Repository 的公共方法，而非直接使用 `query()`。

Service 层应该调用 Repository 提供的公共查询方法，而不是直接访问 `query()` 方法（`query()` 在 `AbstractRepository` 中是 `protected` 的）。

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 正确做法：调用 Repository 的公共方法
    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        employeeRepository.giveRaiseToDepartment(departmentId, percentage);
    }

    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        employeeRepository.transferEmployees(employeeIds, newDepartmentId);
    }

    @Transactional(readOnly = true)
    public List<Employee> findActiveEmployees() {
        return employeeRepository.findActiveEmployees();
    }
}
```

如果需要自定义查询，应在 Repository 中添加公共方法：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    // 在 Repository 内部使用 query()，对外提供公共接口
    public List<Employee> findActiveEmployees() {
        return query()
            .where(Employee::getActive).eq(true)
            .list();
    }

    @Transactional
    public List<Employee> giveRaiseToDepartment(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .list();
        employees.forEach(e -> {
            BigDecimal salary = e.getSalary();
            if (salary != null) {
                e.setSalary(salary.multiply(BigDecimal.ONE.add(percentage)));
            }
        });
        updateAll(employees);
        return employees;
    }
}
```

---

## 下一步

- [查询构建指南](query-building.md) - 查询条件速查
- [CRUD 操作指南](crud-operations.md) - CRUD 操作速查