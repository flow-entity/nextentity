package io.github.nextentity.integration.dto;

import io.github.nextentity.api.EntityReference;
import io.github.nextentity.core.annotation.ReferenceId;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;

/// 包含 EntityReference 延迟加载字段的投影类。
///
/// 用于验证 EntityReference 延迟加载功能：
/// - 查询时只加载 ID（userId）
/// - 调用 user.get() 时延迟加载 User 实体
/// - 调用 department.get() 时延迟加载 Department 实体
///
/// @author HuangChengwei
/// @since 2.2.0
public class EmployeeWithLazyReference {

    private Long id;
    private String name;
    private String email;

    /// 延迟加载的 Employee 引用。
    /// ID 来源：managerId 字段（通过 @ReferenceId 注解指定）
    @ReferenceId("managerId")
    public EmployeeRef manager;

    /// 延迟加载的 Department 引用。
    /// ID 来源：departmentId 字段（默认推断）
    public DepartmentRef department;

    private Long managerId;
    private Long departmentId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public EmployeeRef getManager() {
        return manager;
    }

    public void setManager(EmployeeRef manager) {
        this.manager = manager;
    }

    public DepartmentRef getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentRef department) {
        this.department = department;
    }

    /// Employee 引用类。
    public static class EmployeeRef extends EntityReference<Employee, Long> {
        public EmployeeRef() {}
    }

    /// Department 引用类。
    public static class DepartmentRef extends EntityReference<Department, Long> {
        public DepartmentRef() {}
    }
}