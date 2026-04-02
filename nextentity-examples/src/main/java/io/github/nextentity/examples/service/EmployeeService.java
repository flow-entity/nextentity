package io.github.nextentity.examples.service;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/// 员工服务，演示 Service 层的 @Transactional 使用。
///
/// 推荐模式：在 Service 层使用 Spring 的 @Transactional 注解，
/// 而不是在 Repository 层。
///
/// 用于 crud-operations.md 文档的示例。
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                          DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== 事务示例 ====================

    /// 在单个事务中创建员工和部门。
    ///
    /// 示例用于 crud-operations.md "Service 层推荐使用 @Transactional"。
    @Transactional
    public void createEmployeeWithDepartment(Employee emp, Department dept) {
        departmentRepository.insert(dept);
        emp.setDepartmentId(dept.getId());
        employeeRepository.insert(emp);
    }

    /// 在部门间调动员工。
    ///
    /// 示例用于 crud-operations.md "@Transactional" 章节。
    @Transactional
    public void transferEmployee(Long empId, Long newDeptId) {
        Employee emp = employeeRepository.findEmployeeById(empId);

        if (emp != null) {
            Long oldDeptId = emp.getDepartmentId();
            emp.setDepartmentId(newDeptId);
            employeeRepository.update(emp);

            // 可以在此更新部门统计信息
            updateDepartmentStats(oldDeptId);
            updateDepartmentStats(newDeptId);
        }
    }

    /// 在单个事务中批量入职员工。
    @Transactional
    public void hireEmployees(List<Employee> employees, Long departmentId) {
        employees.forEach(emp -> {
            emp.setDepartmentId(departmentId);
            emp.setStatus(EmployeeStatus.ACTIVE);
            emp.setActive(true);
        });
        employeeRepository.insertAll(employees);
    }

    /// 终止员工并更新部门统计。
    @Transactional
    public void terminateEmployee(Long empId) {
        Employee emp = employeeRepository.findEmployeeById(empId);

        if (emp != null) {
            emp.setStatus(EmployeeStatus.TERMINATED);
            emp.setActive(false);
            employeeRepository.update(emp);

            // 更新部门统计
            updateDepartmentStats(emp.getDepartmentId());
        }
    }

    /// 给部门内所有员工加薪。
    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        employeeRepository.giveRaiseToDepartment(departmentId, percentage);
    }

    /// 批量调动员工到新部门。
    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        employeeRepository.transferEmployees(employeeIds, newDepartmentId);
    }

    // ==================== 只读事务 ====================

    /// 只读事务用于查询操作。
    @Transactional(readOnly = true)
    public List<Employee> findActiveEmployees() {
        return employeeRepository.findActiveEmployees();
    }

    /// 根据部门查询员工（只读事务）。
    @Transactional(readOnly = true)
    public List<Employee> findByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    // ==================== 辅助方法 ====================

    private void updateDepartmentStats(Long departmentId) {
        // 部门统计更新占位符
        // 实际应用中可能更新缓存的计数等
        System.out.println("Updating stats for department: " + departmentId);
    }
}