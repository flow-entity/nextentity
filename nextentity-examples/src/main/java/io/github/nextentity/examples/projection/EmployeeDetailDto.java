package io.github.nextentity.examples.projection;

import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.examples.entity.Department;
import jakarta.persistence.FetchType;

/// Class 投影示例 - 员工详细信息（多属性延迟加载）
///
/// **特点**：
/// - 多个延迟加载属性
/// - 支持嵌套属性路径
///
/// **使用示例**：
/// ```java
/// List<EmployeeDetailDto> results = repository.query()
///     .select(EmployeeDetailDto.class)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // 首次访问 department 触发批量加载
/// Department dept = results.get(0).getDepartment();
///
/// // 后续访问直接从缓存返回
/// Department dept2 = results.get(1).getDepartment();  // 无数据库查询
/// ```
public class EmployeeDetailDto {

    private Long id;
    private String name;
    private String email;
    private java.math.BigDecimal salary;
    private Boolean active;
    private Department department;

    /// 无参构造函数（CGLIB 代理必需）
    public EmployeeDetailDto() {}

    // ==================== Getter 方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public java.math.BigDecimal getSalary() { return salary; }
    public void setSalary(java.math.BigDecimal salary) { this.salary = salary; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    /// 部门信息（延迟加载）
    @Fetch(FetchType.LAZY)
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}