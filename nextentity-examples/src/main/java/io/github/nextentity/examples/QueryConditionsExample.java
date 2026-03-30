package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;

/// Query Conditions Example demonstrating various query condition operators
public class QueryConditionsExample {

    private final EmployeeRepository employeeRepository;

    public QueryConditionsExample(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ==================== Equality Operators ====================

    /// Equal comparison
    public List<Employee> findByStatus(EmployeeStatus status) {
        return query().where(Employee::getStatus).eq(status).getList();
    }

    /// Not equal comparison
    public List<Employee> findNotTerminated() {
        return query().where(Employee::getStatus).ne(EmployeeStatus.TERMINATED).getList();
    }

    /// Conditional equality - only applies if value is not null
    public List<Employee> findByStatusIfPresent(EmployeeStatus status) {
        return query().where(Employee::getStatus).eqIfNotNull(status).getList();
    }

    // ==================== Comparison Operators ====================

    /// Greater than comparison
    public List<Employee> findBySalaryGreaterThan(Double salary) {
        return query().where(Employee::getSalary).gt(salary).getList();
    }

    /// Greater than or equal comparison
    public List<Employee> findBySalaryGreaterOrEqual(Double salary) {
        return query().where(Employee::getSalary).ge(salary).getList();
    }

    /// Less than comparison
    public List<Employee> findBySalaryLessThan(Double salary) {
        return query().where(Employee::getSalary).lt(salary).getList();
    }

    /// Less than or equal comparison
    public List<Employee> findBySalaryLessOrEqual(Double salary) {
        return query().where(Employee::getSalary).le(salary).getList();
    }

    /// Between comparison (inclusive range)
    public List<Employee> findBySalaryBetween(Double min, Double max) {
        return query().where(Employee::getSalary).between(min, max).getList();
    }

    /// Not between comparison
    public List<Employee> findBySalaryNotBetween(Double min, Double max) {
        return query().where(Employee::getSalary).notBetween(min, max).getList();
    }

    // ==================== IN/NOT IN Operators ====================

    /// IN operator with varargs
    public List<Employee> findByIds(Long... ids) {
        return query().where(Employee::getId).in(ids).getList();
    }

    /// IN operator with collection
    public List<Employee> findByIds(List<Long> ids) {
        return query().where(Employee::getId).in(ids).getList();
    }

    /// IN operator with enum values
    public List<Employee> findByStatuses(EmployeeStatus... statuses) {
        return query().where(Employee::getStatus).in(statuses).getList();
    }

    /// NOT IN operator
    public List<Employee> findByStatusNotIn(EmployeeStatus... statuses) {
        return query().where(Employee::getStatus).notIn(statuses).getList();
    }

    // ==================== NULL Operators ====================

    /// Is NULL check
    public List<Employee> findWithoutEmail() {
        return query().where(Employee::getEmail).isNull().getList();
    }

    /// Is NOT NULL check
    public List<Employee> findWithEmail() {
        return query().where(Employee::getEmail).isNotNull().getList();
    }

    // ==================== String Operators ====================

    /// SQL LIKE pattern matching
    public List<Employee> findByNameLike(String pattern) {
        return query().where(Employee::getName).like(pattern).getList();
    }

    /// Starts with prefix
    public List<Employee> findByNameStartingWith(String prefix) {
        return query().where(Employee::getName).startsWith(prefix).getList();
    }

    /// Ends with suffix
    public List<Employee> findByEmailEndingWith(String suffix) {
        return query().where(Employee::getEmail).endsWith(suffix).getList();
    }

    /// Contains text
    public List<Employee> findByNameContaining(String text) {
        return query().where(Employee::getName).contains(text).getList();
    }

    /// NOT LIKE pattern
    public List<Employee> findByNameNotLike(String pattern) {
        return query().where(Employee::getName).notLike(pattern).getList();
    }

    /// Conditional LIKE - only applies if value is not null
    public List<Employee> findByNameLikeIfPresent(String pattern) {
        return query().where(Employee::getName).likeIfNotNull(pattern).getList();
    }

    /// LIKE if not empty - skips if null or empty string
    public List<Employee> findByNameContainingIfPresent(String text) {
        return query().where(Employee::getName).containsIfNotEmpty(text).getList();
    }

    // ==================== Logical Operators ====================

    /// Multiple where clauses are combined with AND
    public List<Employee> findActiveHighEarners() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).gt(50000.0)
                .getList();
    }

    /// Active employees in specific department
    public List<Employee> findActiveInDepartment(Long departmentId) {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();
    }

    /// Simplified complex condition
    public List<Employee> findComplexCondition() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).gt(50000.0)
                .getList();
    }

    // ==================== Date Comparisons ====================

    /// Date comparison - after a specific date
    public List<Employee> findHiredAfter(LocalDate date) {
        return query().where(Employee::getHireDate).gt(date).getList();
    }

    /// Date range query
    public List<Employee> findHiredBetween(LocalDate start, LocalDate end) {
        return query().where(Employee::getHireDate).between(start, end).getList();
    }

    // ==================== Tuple Selection ====================

    /// Select specific fields using Tuple
    public List<Tuple2<String, Double>> findEmployeeNamesAndSalaries() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    // ==================== Helper Methods ====================

    private Select<Employee> query() {
        return employeeRepository.query();
    }
}