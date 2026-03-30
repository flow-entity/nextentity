package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.util.List;

/// Aggregation and Grouping Example demonstrating aggregate functions and GROUP BY operations
public class AggregationExample {

    private final EmployeeRepository employeeRepository;

    public AggregationExample(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ==================== Count Operations ====================

    /// Count all records
    public int countAllEmployees() {
        return query().getList().size();
    }

    /// Count with conditions
    public int countActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList()
                .size();
    }

    /// Count by department
    public int countByDepartment(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList()
                .size();
    }

    /// Count distinct values
    public int countDistinctDepartments() {
        return query()
                .selectDistinct(Employee::getDepartmentId)
                .getList()
                .size();
    }

    // ==================== Sum Operations ====================

    /// Calculate total salary using Java streams
    public double calculateTotalSalary() {
        return query()
                .select(Employee::getSalary)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /// Sum with conditions - total salary for active employees
    public double calculateActiveTotalSalary() {
        return query()
                .select(Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    // ==================== Average Operations ====================

    /// Calculate average salary using Java streams
    public double calculateAverageSalary() {
        return query()
                .select(Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /// Average by department
    public double calculateAverageSalaryByDepartment(Long departmentId) {
        return query()
                .select(Employee::getSalary)
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // ==================== Min/Max Operations ====================

    /// Find maximum salary
    public double findMaxSalary() {
        return query()
                .select(Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    /// Find minimum salary
    public double findMinSalary() {
        return query()
                .select(Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .filter(s -> s != null)
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    /// Find employee with highest salary
    public Employee findHighestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getFirst();
    }

    /// Find employee with lowest salary
    public Employee findLowestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).asc()
                .getFirst();
    }

    // ==================== Grouping ====================

    /// Group by department using Java streams
    public java.util.Map<Long, List<Employee>> groupByDepartment() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(Employee::getDepartmentId));
    }

    /// Group by status and count
    public java.util.Map<EmployeeStatus, Long> countByStatus() {
        return query()
                .select(Employee::getStatus)
                .getList()
                .stream()
                .filter(s -> s != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        java.util.function.Function.identity(),
                        java.util.stream.Collectors.counting()
                ));
    }

    /// Calculate statistics per department
    public java.util.Map<Long, java.util.DoubleSummaryStatistics> salaryStatsByDepartment() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .filter(e -> e.getSalary() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Employee::getDepartmentId,
                        java.util.stream.Collectors.summarizingDouble(Employee::getSalary)
                ));
    }

    // ==================== Tuple Aggregation ====================

    /// Select name and salary for analysis
    public List<Tuple2<String, Double>> findNameSalaryPairs() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();
    }

    // ==================== Helper Methods ====================

    private Select<Employee> query() {
        return employeeRepository.query();
    }
}