# CRUD 操作指南

本指南介绍 NextEntity 的增删改查（CRUD）操作。

## 目录

- [Insert 插入](#insert-插入)
- [Update 更新](#update-更新)
- [Delete 删除](#delete-删除)
- [批量操作](#批量操作)
- [事务处理](#事务处理)
- [乐观锁](#乐观锁)
- [最佳实践](#最佳实践)

---

## Insert 插入

### 单条插入

```java
Employee employee = new Employee();
employee.setId(1L);
employee.setName("John Doe");
employee.setEmail("john@example.com");
employee.setSalary(BigDecimal.valueOf(50000.0));
employee.setActive(true);

employeeRepository.insert(employee);
```

生成的 SQL：

```sql
INSERT INTO employee (id, name, email, salary, active, status, department_id)
VALUES (?, ?, ?, ?, ?, ?, ?)
```

### 设置实体属性

```java
Employee emp = new Employee();
emp.setId(generateId());          // 主键
emp.setName("Jane Smith");        // 必填字段
emp.setEmail("jane@company.com"); // 必填字段
emp.setSalary(BigDecimal.valueOf(60000.0)); // 可选字段
emp.setActive(true);              // 状态
emp.setStatus(EmployeeStatus.ACTIVE);
emp.setDepartmentId(1L);          // 外键

employeeRepository.insert(emp);
```

---

## Update 更新

### 单条更新

```java
// 先查询
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .first();

// 修改属性
employee.setSalary(BigDecimal.valueOf(65000.0));
employee.setEmail("john.new@company.com");

// 更新
employeeRepository.update(employee);
```

生成的 SQL：

```sql
UPDATE employee
SET name = ?, email = ?, salary = ?, active = ?, status = ?, department_id = ?
WHERE id = ?
```

### 更新策略

更新操作会更新实体的所有可写字段：

```java
// 部分更新需要先查询再修改
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .first();

if (emp != null) {
    emp.setSalary(newSalary);  // 只修改薪资
    employeeRepository.update(emp);  // 其他字段保持不变
}
```

### 按条件批量更新

对于统一字段修改，推荐直接使用 `update()`，避免先查询实体再回写：

```java
int updated = employeeRepository.update()
    .set(Employee::getActive, false)
    .set(Employee::getStatus, EmployeeStatus.INACTIVE)
    .where(Employee::getDepartmentId).eq(departmentId)
    .execute();
```

这种方式更适合批量状态切换、标记删除、统一字段回填等场景。

---

## Delete 删除

### 单条删除

```java
// 先查询
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .first();

// 删除
if (employee != null) {
    employeeRepository.delete(employee);
}
```

生成的 SQL：

```sql
DELETE FROM employee WHERE id = ?
```

### 按条件删除

```java
// 查询后批量删除
List<Employee> terminated = employeeRepository.query()
    .where(Employee::getStatus).eq(EmployeeStatus.TERMINATED)
    .list();

employeeRepository.deleteAll(terminated);
```

### 直接条件批量删除

如果不需要先加载实体，可以直接使用 `delete()`：

```java
int deleted = employeeRepository.delete()
    .where(Employee::getStatus).eq(EmployeeStatus.INACTIVE)
    .execute();
```

---

## 批量操作

### 批量插入

```java
List<Employee> newEmployees = List.of(
    createEmployee(1L, "John"),
    createEmployee(2L, "Jane"),
    createEmployee(3L, "Bob")
);

// 批量插入（高效）
employeeRepository.insertAll(newEmployees);
```

生成的 SQL（JDBC 批量模式）：

```sql
INSERT INTO employee (id, name, email, ...) VALUES (?, ?, ?, ...)
INSERT INTO employee (id, name, email, ...) VALUES (?, ?, ?, ...)
INSERT INTO employee (id, name, email, ...) VALUES (?, ?, ?, ...)
```

**注意**：MySQL 需配置 `rewriteBatchedStatements=true` 以优化批量操作。

### 批量更新

```java
// 批量涨薪
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(1L)
    .list();

employees.forEach(e -> {
    BigDecimal salary = e.getSalary();
    if (salary != null) {
        e.setSalary(salary.multiply(BigDecimal.valueOf(1.1)));
    }
});

employeeRepository.updateAll(employees);
```

如果更新内容对所有匹配记录都相同，优先考虑 `update()`，SQL 更直接，内存占用也更低。

### 批量删除

```java
// 删除某部门所有员工
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(deptId)
    .list();

employeeRepository.deleteAll(employees);
```

若只是按条件整批删除，优先使用 `delete()`，避免多一次查询和实体构建。

---

## 事务处理

### Service 层推荐使用 @Transactional

推荐在 Service 层使用 Spring 的 `@Transactional` 注解：

```java
@Service
public class EmployeeService {

    @Transactional
    public void createEmployeeWithDepartment(Employee emp, Department dept) {
        departmentRepository.insert(dept);
        emp.setDepartmentId(dept.getId());
        employeeRepository.insert(emp);
    }
}
```

### Repository 内部事务

对于 Repository 内部的事务操作，直接使用 Spring 的 `@Transactional` 注解：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(NextEntityContext context) {
        super(context);
    }

    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .where(Employee::getActive).eq(true)
            .list();

        employees.forEach(e -> {
            BigDecimal salary = e.getSalary();
            if (salary != null) {
                e.setSalary(salary.multiply(BigDecimal.ONE.add(percentage)));
            }
        });
        updateAll(employees);
    }
}
```

---

## 乐观锁

使用 `@Version` 注解实现乐观锁：

### 定义版本字段

```java
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    @Version
    private Integer version;  // 乐观锁版本号
}
```

### 乐观锁行为

```java
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .first();

emp.setSalary(BigDecimal.valueOf(65000.0));

employeeRepository.update(emp);  // 自动检查版本号并递增
```

生成的 SQL：

```sql
UPDATE employee
SET name = ?, salary = ?, version = ?
WHERE id = ? AND version = ?
```

如果版本不匹配（其他事务已修改），将抛出 `OptimisticLockException`。

---

## 最佳实践

### 1. 区分实体更新与条件批量更新

```java
// 需要基于当前实体值计算时：查询后修改
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(id)
    .first();

if (emp != null) {
    emp.setSalary(newSalary);
    employeeRepository.update(emp);
}

// 统一字段批量修改时：直接条件更新
employeeRepository.update()
    .set(Employee::getActive, false)
    .where(Employee::getDepartmentId).eq(departmentId)
    .execute();
```

### 2. 使用批量操作提升性能

```java
// 推荐：批量操作
employeeRepository.insertAll(newEmployees);

// 避免：逐条操作
for (Employee e : newEmployees) {
    employeeRepository.insert(e);  // 性能差
}
```

### 3. 合理使用事务

在 Repository 中使用 `@Transactional` 注解，内部可使用 `query()` 方法：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(NextEntityContext context) {
        super(context);
    }

    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .where(Employee::getActive).eq(true)
            .list();;

        employees.forEach(e -> {
            BigDecimal salary = e.getSalary();
            if (salary != null) {
                e.setSalary(salary.multiply(BigDecimal.ONE.add(percentage)));
            }
        });
        updateAll(employees);
    }
}
```

### 4. 删除前检查关联

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(NextEntityContext context) {
        super(context);
    }

    public boolean canDeleteDepartment(Long deptId) {
        long empCount = query()
            .where(Employee::getDepartmentId).eq(deptId)
            .count();
        return empCount == 0;
    }
}
```

Service 层调用：

```java
@Service
public class DepartmentService {

    @Transactional
    public void deleteDepartmentIfEmpty(Long deptId) {
        if (employeeRepository.canDeleteDepartment(deptId)) {
            departmentRepository.delete(dept);
        }
    }
}
```

### 5. 使用 Optional 包装结果

在 Repository 中提供 Optional 返回的方法：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    protected EmployeeRepository(NextEntityContext context) {
        super(context);
    }

    public Optional<Employee> findByIdOptional(Long id) {
        Employee emp = query()
            .where(Employee::getId).eq(id)
            .first();;
        return Optional.ofNullable(emp);
    }
}
```

---

## 下一步

CRUD 掌握后，学习进阶功能：

1. **[投影指南](projections.md)** - 字段选择和 DTO 投影