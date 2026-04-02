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

/**
 * Employee Service demonstrating @Transactional usage at Service layer.
 * <p>
 * This class demonstrates the recommended pattern for transaction management:
 * using Spring's @Transactional annotation at the Service layer rather than
 * Repository layer.
 * <p>
 * Examples for crud-operations.md documentation.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                          DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== Transactional Examples ====================

    /**
     * Create employee with department in a single transaction.
     * Example for crud-operations.md "Service 层推荐使用 @Transactional".
     */
    @Transactional
    public void createEmployeeWithDepartment(Employee emp, Department dept) {
        departmentRepository.insert(dept);
        emp.setDepartmentId(dept.getId());
        employeeRepository.insert(emp);
    }

    /**
     * Transfer employee between departments.
     * Example for crud-operations.md "@Transactional" section.
     */
    @Transactional
    public void transferEmployee(Long empId, Long newDeptId) {
        Employee emp = employeeRepository.query()
                .where(Employee::getId).eq(empId)
                .first();

        if (emp != null) {
            Long oldDeptId = emp.getDepartmentId();
            emp.setDepartmentId(newDeptId);
            employeeRepository.update(emp);

            // Update department statistics could be done here
            updateDepartmentStats(oldDeptId);
            updateDepartmentStats(newDeptId);
        }
    }

    /**
     * Hire multiple employees in a batch within a single transaction.
     */
    @Transactional
    public void hireEmployees(List<Employee> employees, Long departmentId) {
        employees.forEach(emp -> {
            emp.setDepartmentId(departmentId);
            emp.setStatus(EmployeeStatus.ACTIVE);
            emp.setActive(true);
        });
        employeeRepository.insertAll(employees);
    }

    /**
     * Terminate employee and update department stats.
     */
    @Transactional
    public void terminateEmployee(Long empId) {
        Employee emp = employeeRepository.query()
                .where(Employee::getId).eq(empId)
                .first();

        if (emp != null) {
            emp.setStatus(EmployeeStatus.TERMINATED);
            emp.setActive(false);
            employeeRepository.update(emp);

            // Update department statistics
            updateDepartmentStats(emp.getDepartmentId());
        }
    }

    /**
     * Give raise to all employees in a department.
     */
    @Transactional
    public void giveDepartmentRaise(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = employeeRepository.query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .list();

        employees.forEach(e -> {
            BigDecimal salary = e.getSalary();
            if (salary != null) {
                e.setSalary(salary.multiply(BigDecimal.ONE.add(percentage)));
            }
        });
        employeeRepository.updateAll(employees);
    }

    /**
     * Transfer multiple employees to a new department.
     */
    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        List<Employee> employees = employeeRepository.query()
                .where(Employee::getId).in(employeeIds)
                .list();

        employees.forEach(e -> e.setDepartmentId(newDepartmentId));
        employeeRepository.updateAll(employees);
    }

    // ==================== Read-only Transactional ====================

    /**
     * Read-only transaction for query operations.
     */
    @Transactional(readOnly = true)
    public List<Employee> findActiveEmployees() {
        return employeeRepository.query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /**
     * Find employees by department with read-only transaction.
     */
    @Transactional(readOnly = true)
    public List<Employee> findByDepartment(Long departmentId) {
        return employeeRepository.query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .orderBy(Employee::getName).asc()
                .list();
    }

    // ==================== Helper Methods ====================

    private void updateDepartmentStats(Long departmentId) {
        // Placeholder for department statistics update
        // In a real application, this might update cached counts, etc.
        System.out.println("Updating stats for department: " + departmentId);
    }
}
