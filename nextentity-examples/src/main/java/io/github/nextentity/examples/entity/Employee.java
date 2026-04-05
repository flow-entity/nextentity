package io.github.nextentity.examples.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/// 员工实体类，用于示例演示。
///
/// 本实体演示：
/// - 基本 CRUD 操作
/// - 查询条件和投影
/// - 复杂查询和聚合
/// - 关联查询（{@link Department}）
///
/// ## 关联查询示例
///
/// ```java
/// // 嵌套路径查询（Department 实现了 Entity 接口）
/// List<Employee> employees = employeeRepository.query()
///     .where(Employee::getDepartment).get(Department::getName).eq("技术部")
///     .list();
///
/// // 急加载关联
/// List<Employee> employees = employeeRepository.query()
///     .fetch(Employee::getDepartment)
///     .list();
/// ```
@jakarta.persistence.Entity
public class Employee {

    /// 员工 ID（主键）。
    @Id
    private Long id;

    /// 员工姓名。
    private String name;

    /// 员工邮箱地址。
    private String email;

    /// 员工薪资。
    private BigDecimal salary;

    /// 员工是否在职。
    private Boolean active;

    /// 员工状态。
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    /// 部门 ID（外键）。
    private Long departmentId;

    /// 部门关联（懒加载）。
    ///
    /// 关联的 {@link Department} 实体实现了 {@link io.github.nextentity.api.Entity} 接口，
    /// 支持嵌套路径查询。
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;

    /// 入职日期。
    private LocalDate hireDate;

    /// 创建时间戳。
    private LocalDateTime createdAt;

    /// 乐观锁版本号。
    ///
    /// 用于并发控制，防止更新丢失。
    @Version
    private Integer version;

    public Employee() {
    }

    public Employee(Long id, String name, String email, BigDecimal salary, Boolean active,
                    EmployeeStatus status, Long departmentId, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.active = active;
        this.status = status;
        this.departmentId = departmentId;
        this.hireDate = hireDate;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}