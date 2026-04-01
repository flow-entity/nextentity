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

> 📍 **示例位置**: `EmployeeRepository.java` (`insertSingleEmployee` 方法)

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

> 📍 **示例位置**: `EmployeeRepository.java` (设置实体属性示例)

---

## Update 更新

### 单条更新

```java
// 先查询
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

// 修改属性
employee.setSalary(65000.0);
employee.setEmail("john.new@company.com");

// 更新
employeeRepository.update(employee);
```

> 📍 **示例位置**: `EmployeeRepository.java` (`updateEmployeeSalary` 方法)

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
    .getFirst();

if (emp != null) {
    emp.setSalary(newSalary);  // 只修改薪资
    employeeRepository.update(emp);  // 其他字段保持不变
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (更新策略示例)

---

## Delete 删除

### 单条删除

```java
// 先查询
Employee employee = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

// 删除
if (employee != null) {
    employeeRepository.delete(employee);
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`deleteEmployee` 方法)

生成的 SQL：

```sql
DELETE FROM employee WHERE id = ?
```

### 按条件删除

```java
// 查询后批量删除
List<Employee> terminated = employeeRepository.query()
    .where(Employee::getStatus).eq(EmployeeStatus.TERMINATED)
    .getList();

employeeRepository.deleteAll(terminated);
```

> 📍 **示例位置**: `EmployeeRepository.java` (`deleteEmployeesByDepartment` 方法)

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

> 📍 **示例位置**: `EmployeeRepository.java` (`insertMultipleEmployees` 方法)

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
    .getList();

employees.forEach(e -> e.setSalary(e.getSalary() * 1.1));

employeeRepository.updateAll(employees);
```

> 📍 **示例位置**: `EmployeeRepository.java` (`giveRaiseToDepartment` 方法)

### 批量删除

```java
// 删除某部门所有员工
List<Employee> employees = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(deptId)
    .getList();

employeeRepository.deleteAll(employees);
```

> 📍 **示例位置**: `EmployeeRepository.java` (`deleteEmployeesByDepartment` 方法)

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

> 📍 **示例位置**: `service/EmployeeService.java` (`createEmployeeWithDepartment` 方法)

### Repository 内部事务

对于 Repository 内部的事务操作，可以使用 `doInTransaction` 方法（protected 方法，仅限子类内部使用）：

```java
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = query()
            .where(Employee::getDepartmentId).eq(departmentId)
            .where(Employee::getActive).eq(true)
            .getList();

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

> 📍 **示例位置**: `EmployeeRepository.java` (`giveDepartmentRaise` 方法)

### Spring @Transactional

```java
@Service
public class EmployeeService {

    @Transactional
    public void transferEmployee(Long empId, Long newDeptId) {
        Employee emp = employeeRepository.query()
            .where(Employee::getId).eq(empId)
            .getFirst();

        emp.setDepartmentId(newDeptId);
        employeeRepository.update(emp);

        // 更新部门统计
        updateDepartmentStats(oldDeptId);
        updateDepartmentStats(newDeptId);
    }
}
```

> 📍 **示例位置**: `service/EmployeeService.java` (`transferEmployee` 方法)

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

> 📍 **示例位置**: `entity/Employee.java` (`version` 字段)

### 乐观锁行为

```java
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(1L)
    .getFirst();

emp.setSalary(65000.0);

employeeRepository.update(emp);  // 自动检查版本号并递增
```

> 📍 **示例位置**: `EmployeeRepository.java` (更新操作自动处理版本)

生成的 SQL：

```sql
UPDATE employee
SET name = ?, salary = ?, version = ?
WHERE id = ? AND version = ?
```

如果版本不匹配（其他事务已修改），将抛出 `OptimisticLockException`。

---

## 最佳实践

### 1. 先查询再修改

```java
// 推荐：查询后修改
Employee emp = employeeRepository.query()
    .where(Employee::getId).eq(id)
    .getFirst();

if (emp != null) {
    emp.setSalary(newSalary);
    employeeRepository.update(emp);
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`updateEmployeeSalary` 方法)

### 2. 使用批量操作提升性能

```java
// 推荐：批量操作
employeeRepository.insertAll(newEmployees);

// 避免：逐条操作
for (Employee e : newEmployees) {
    employeeRepository.insert(e);  // 性能差
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`insertMultipleEmployees` 方法)

### 3. 合理使用事务

```java
// 相关操作放在同一事务
employeeRepository.doInTransaction(() -> {
    departmentRepository.insert(dept);
    employeeRepository.insertAll(employees);
});
```

> 📍 **示例位置**: `EmployeeRepository.java` (`giveDepartmentRaise` 方法)

### 4. 删除前检查关联

```java
// 删除部门前检查是否有员工
long empCount = employeeRepository.query()
    .where(Employee::getDepartmentId).eq(deptId)
    .count();

if (empCount == 0) {
    departmentRepository.delete(dept);
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`hasEmployeesInDepartment`, `deleteDepartmentIfEmpty` 方法)

### 5. 使用 Optional 包装结果

```java
public Optional<Employee> findEmployee(Long id) {
    Employee emp = employeeRepository.query()
        .where(Employee::getId).eq(id)
        .getFirst();
    return Optional.ofNullable(emp);
}
```

> 📍 **示例位置**: `EmployeeRepository.java` (`findFirstActive` 方法)

---

## 下一步

CRUD 掌握后，学习进阶功能：

1. **[投影指南](projections.md)** - 字段选择和 DTO 投影