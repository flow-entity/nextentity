package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.api.model.*;
import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.spring.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Employee Repository demonstrating NextEntity query capabilities.
 * <p>
 * This repository shows core patterns for:
 * - Basic CRUD operations
 * - Query conditions (comparison, IN, null checks)
 * - String operations (LIKE, contains)
 * - Ordering and pagination
 * - Aggregation (count, sum, avg, min, max)
 * - Projection (Tuple, DTO)
 * - Association fetch
 * - Complex queries with conditional operators
 * - Transaction management
 */
@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    // ==================== Basic CRUD Operations ====================

    /// Query all employees
    public List<Employee> findAllEmployees() {
        return query().list();
    }

    /// Query employee by ID
    public Employee findEmployeeById(Long id) {
        return query()
                .where(Employee::getId).eq(id)
                .first();
    }

    /// Query employees by multiple IDs using IN clause
    public List<Employee> findEmployeesByIds(List<Long> ids) {
        return query().where(Employee::getId).in(ids).list();
    }

    /// Get single employee by email
    public Employee findEmployeeByEmail(String email) {
        return query().where(Employee::getEmail).eq(email).first();
    }

    /// Update an existing employee's salary
    @Transactional
    public void updateEmployeeSalary(Long id, BigDecimal newSalary) {
        Employee employee = findEmployeeById(id);
        if (employee != null) {
            employee.setSalary(newSalary);
            update(employee);
        }
    }

    /// Batch update active flag by department without loading entities
    @Transactional
    public int deactivateEmployeesByDepartment(Long departmentId) {
        return updateWhere()
                .set(Employee::getActive, false)
                .set(Employee::getStatus, EmployeeStatus.INACTIVE)
                .where(Employee::getDepartmentId).eq(departmentId)
                .execute();
    }

    /// Give raise to all employees in a department
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

    /// Delete a single employee
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        if (employee != null) {
            delete(employee);
        }
    }

    /// Delete all employees in a department
    @Transactional
    public void deleteEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .list();
        deleteAll(employees);
    }

    /// Batch delete inactive employees without loading entities
    @Transactional
    public int deleteInactiveEmployees() {
        return deleteWhere()
                .where(Employee::getStatus).eq(EmployeeStatus.INACTIVE)
                .execute();
    }

    /// Check if department has employees
    public boolean hasEmployeesInDepartment(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .exists();
    }

    /// Safe delete with association check
    @Transactional
    public boolean deleteDepartmentIfEmpty(Long departmentId) {
        long empCount = query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .count();
        return empCount == 0;
    }

    // ==================== Query Conditions ====================

    /// Equal comparison
    public List<Employee> findByStatus(EmployeeStatus status) {
        return query().where(Employee::getStatus).eq(status).list();
    }

    /// Not equal comparison
    public List<Employee> findNotTerminated() {
        return query().where(Employee::getStatus).ne(EmployeeStatus.TERMINATED).list();
    }

    /// Conditional equality - only applies if value is not null
    public List<Employee> findByStatusIfPresent(EmployeeStatus status) {
        return query().where(Employee::getStatus).eqIfNotNull(status).list();
    }

    /// Between comparison (inclusive range)
    public List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max) {
        return query().where(Employee::getSalary).between(min, max).list();
    }

    /// IN operator with varargs
    public List<Employee> findByIds(Long... ids) {
        return query().where(Employee::getId).in(ids).list();
    }

    /// IN operator with collection
    public List<Employee> findByIdsCollection(List<Long> ids) {
        return query().where(Employee::getId).in(ids).list();
    }

    /// IN operator with enum values
    public List<Employee> findByStatuses(EmployeeStatus... statuses) {
        return query().where(Employee::getStatus).in(statuses).list();
    }

    /// NOT IN operator
    public List<Employee> findByStatusNotIn(EmployeeStatus... statuses) {
        return query().where(Employee::getStatus).notIn(statuses).list();
    }

    /// Is NULL check
    public List<Employee> findWithoutEmail() {
        return query().where(Employee::getEmail).isNull().list();
    }

    /// Is NOT NULL check
    public List<Employee> findWithEmail() {
        return query().where(Employee::getEmail).isNotNull().list();
    }

    /// Date range query
    public List<Employee> findHiredAfter(LocalDate date) {
        return query().where(Employee::getHireDate).gt(date).list();
    }

    /// Date range query
    public List<Employee> findHiredBetween(LocalDate start, LocalDate end) {
        return query().where(Employee::getHireDate).between(start, end).list();
    }

    /// Active employees in specific department
    public List<Employee> findActiveInDepartment(Long departmentId) {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(departmentId)
                .list();
    }

    /// Active high earners
    public List<Employee> findActiveHighEarners() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
                .list();
    }

    // ==================== String Operations ====================

    /// SQL LIKE pattern matching
    public List<Employee> findByNameLike(String pattern) {
        return query().where(Employee::getName).like(pattern).list();
    }

    /// Starts with prefix
    public List<Employee> findByNameStartingWith(String prefix) {
        return query().where(Employee::getName).startsWith(prefix).list();
    }

    /// Ends with suffix
    public List<Employee> findByEmailEndingWith(String suffix) {
        return query().where(Employee::getEmail).endsWith(suffix).list();
    }

    /// Contains text
    public List<Employee> findByNameContaining(String text) {
        return query().where(Employee::getName).contains(text).list();
    }

    /// NOT LIKE pattern
    public List<Employee> findByNameNotLike(String pattern) {
        return query().where(Employee::getName).notLike(pattern).list();
    }

    /// Does not start with
    public List<Employee> findByNameNotStartingWith(String prefix) {
        return query().where(Employee::getName).notStartsWith(prefix).list();
    }

    /// Does not end with
    public List<Employee> findByEmailNotEndingWith(String suffix) {
        return query().where(Employee::getEmail).notEndsWith(suffix).list();
    }

    /// Does not contain
    public List<Employee> findByNameNotContaining(String text) {
        return query().where(Employee::getName).notContains(text).list();
    }

    /// LIKE if value is not null
    public List<Employee> findByNameLikeIfPresent(String pattern) {
        return query().where(Employee::getName).likeIfNotNull(pattern).list();
    }

    /// Contains if value is not empty
    public List<Employee> findByNameContainingIfPresent(String text) {
        return query().where(Employee::getName).containsIfNotEmpty(text).list();
    }

    /// Case-insensitive search using lower()
    public List<Employee> findByNameContainingIgnoreCase(String text) {
        String lowerText = text.toLowerCase();
        return query().where(Employee::getName).lower().contains(lowerText).list();
    }

    /// Find employees with valid emails
    public List<Employee> findWithValidEmail() {
        return query()
                .where(Employee::getEmail).isNotNull()
                .where(Employee::getEmail).contains("@")
                .list();
    }

    // ==================== Ordering ====================

    /// Order by single field - ascending
    public List<Employee> findOrderedByNameAsc() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Order by single field - descending
    public List<Employee> findOrderedBySalaryDesc() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list();
    }

    /// Order by multiple fields
    public List<Employee> findByDepartmentThenSalary() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .list();
    }

    /// Order by status then name
    public List<Employee> findByStatusThenName() {
        return query()
                .orderBy(Employee::getStatus).asc()
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Order with projection
    public List<String> findNamesOrdered() {
        return query()
                .select(Employee::getName)
                .orderBy(Employee::getName).asc()
                .list();
    }

    // ==================== Pagination ====================

    /// Basic pagination with limit
    public List<Employee> findFirstPage() {
        return query()
                .orderBy(Employee::getId).asc()
                .list(10);
    }

    /// Pagination with page number
    public List<Employee> findPage(int pageNumber, int pageSize) {
        return query()
                .orderBy(Employee::getId).asc()
                .list(pageNumber * pageSize, pageSize);
    }

    /// Pagination with conditions
    public List<Employee> findActiveEmployeesPaged(int offset, int limit) {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list(offset, limit);
    }

    /// Slice-based pagination with metadata
    public Slice<Employee> findFirstSlice() {
        return query()
                .orderBy(Employee::getId).asc()
                .slice(0, 10);
    }

    /// Slice with conditions and ordering
    public Slice<Employee> findHighEarnerSlice(int page, int size) {
        return query()
                .where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .slice(page * size, size);
    }

    /// Complex query with all features
    public List<Employee> findComplexQuery(Long departmentId, int offset, int limit) {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .orderBy(Employee::getHireDate).desc()
                .orderBy(Employee::getName).asc()
                .list(offset, limit);
    }

    /// Top N results
    public List<Employee> findTopEarners(int n) {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list(n);
    }

    /// Get single result - highest paid employee
    public Employee findHighestPaid() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .first();
    }

    /// Find employees hired within recent days
    public List<Employee> findRecentlyHired(int days) {
        LocalDate recentDate = LocalDate.now().minusDays(days);
        return query()
                .where(Employee::getHireDate).ge(recentDate)
                .orderBy(Employee::getHireDate).desc()
                .list();
    }

    // ==================== Aggregation ====================

    /// Count all records
    public long countAllEmployees() {
        return query().count();
    }

    /// Count with conditions
    public long countActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .count();
    }

    /// Count by department
    public long countByDepartment(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .count();
    }

    /// Count distinct values
    public long countDistinctDepartments() {
        return query()
                .select(path(Employee::getDepartmentId).countDistinct())
                .single();
    }

    /// Calculate total salary
    public BigDecimal calculateTotalSalary() {
        return query()
                .select(path(Employee::getSalary).sum())
                .where(Employee::getSalary).isNotNull()
                .single();
    }

    /// Sum with conditions
    public BigDecimal calculateActiveTotalSalary() {
        return query()
                .select(path(Employee::getSalary).sum())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .single();
    }

    /// Calculate average salary
    public Double calculateAverageSalary() {
        return query()
                .select(path(Employee::getSalary).avg())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .single();
    }

    /// Average by department
    public Double calculateAverageSalaryByDepartment(Long departmentId) {
        return query()
                .select(path(Employee::getSalary).avg())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getDepartmentId).eq(departmentId)
                .single();
    }

    /// Find maximum salary
    public BigDecimal findMaxSalary() {
        return query()
                .select(path(Employee::getSalary).max())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .single();
    }

    /// Find minimum salary
    public BigDecimal findMinSalary() {
        return query()
                .select(path(Employee::getSalary).min())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .single();
    }

    /// Find employee with highest salary
    public Employee findHighestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).isNotNull()
                .orderBy(Employee::getSalary).desc()
                .first();
    }

    /// Find employee with lowest salary
    public Employee findLowestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).isNotNull()
                .orderBy(Employee::getSalary).asc()
                .first();
    }

    /// Group by department using Java streams
    public Map<Long, List<Employee>> groupByDepartment() {
        return query()
                .where(Employee::getActive).eq(true)
                .list()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId));
    }

    /// Group by status and count
    public Map<EmployeeStatus, Long> countByStatus() {
        return query()
                .select(Employee::getStatus)
                .where(Employee::getStatus).isNotNull()
                .list()
                .stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

    /// Calculate statistics per department using database-level aggregation
    public List<Tuple6<Long, Long, BigDecimal, Double, BigDecimal, BigDecimal>> salaryStatsByDepartment() {
        return query()
                .select(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(),
                        Path.of(Employee::getSalary).avg(),
                        Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).min()
                )
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).isNotNull()
                .groupBy(Employee::getDepartmentId)
                .list();
    }

    /// Select name and salary for analysis
    public List<Tuple2<String, BigDecimal>> findNameSalaryPairs() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list();
    }

    // ==================== Projection ====================

    /// Select a single field from entity
    public List<String> findEmployeeNames() {
        return query()
                .select(Employee::getName)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Select distinct values
    public List<Long> findDistinctDepartmentIds() {
        return query()
                .selectDistinct(Employee::getDepartmentId)
                .list();
    }

    /// Select two fields using Tuple2
    public List<Tuple2<String, BigDecimal>> findNameAndSalary() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Select three fields using Tuple3
    public List<Tuple3<String, String, BigDecimal>> findNameEmailSalary() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Select four fields using Tuple4
    public List<Tuple4<String, String, BigDecimal, Long>> findNameEmailSalaryDepartment() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary, Employee::getDepartmentId)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Select five fields using Tuple5
    public List<Tuple5<String, String, BigDecimal, Long, Boolean>> findEmployeeDetails() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary,
                        Employee::getDepartmentId, Employee::getActive)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Select distinct tuple values
    public List<Tuple2<String, EmployeeStatus>> findDistinctNameStatus() {
        return query()
                .selectDistinct(Employee::getName, Employee::getStatus)
                .list();
    }

    /// Select into a DTO class
    public List<EmployeeSummary> findEmployeeSummaries() {
        return query()
                .select(EmployeeSummary.class)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Select with conditions into DTO
    public List<EmployeeSummary> findHighEarnerSummaries() {
        return query()
                .select(EmployeeSummary.class)
                .where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .list();
    }

    /// Select distinct into DTO
    public List<EmployeeInfo> findDistinctEmployeeInfo() {
        return query()
                .selectDistinct(EmployeeInfo.class)
                .list();
    }

    /// Select full entity (default behavior)
    public List<Employee> findActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Select distinct entities
    public List<Employee> findDistinctActiveEmployees() {
        return query()
                .selectDistinct(Employee.class)
                .where(Employee::getActive).eq(true)
                .list();
    }

    // ==================== Association Fetch ====================

    /// Eager fetch association - load employees with departments in one query
    public List<Employee> findWithDepartmentFetch() {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Query by association ID
    public List<Employee> findByDepartmentId(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .list();
    }

    /// Query with fetch and conditions
    public List<Employee> findActiveInDepartmentWithFetch(Long departmentId) {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Select with association data using DTO
    public List<EmployeeWithDept> findEmployeeWithDepartmentInfo() {
        return query()
                .select(EmployeeWithDept.class)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Join data from two entities manually
    public List<EmployeeWithDept> findWithManualJoin() {
        List<Employee> employees = query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .list();

        return employees.stream()
                .map(emp -> new EmployeeWithDept(
                        emp.getName(),
                        emp.getDepartment() != null ? emp.getDepartment().getName() : null
                ))
                .toList();
    }

    // ==================== Complex Query ====================

    /// Multi-condition employee search
    public List<Employee> findEmployeesByMultipleConditions() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).in(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE)
                .where(Employee::getSalary).between(BigDecimal.valueOf(40000.0), BigDecimal.valueOf(80000.0))
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Employee search with optional filters
    public List<Employee> searchEmployees(String name, Long departmentId, BigDecimal minSalary) {
        return query()
                .where(Employee::getName).containsIfNotEmpty(name)
                .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
                .where(Employee::getSalary).geIfNotNull(minSalary)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Advanced search with many optional parameters
    public List<Employee> advancedSearch(
            String name, String email, Long departmentId, EmployeeStatus status,
            Boolean active, BigDecimal minSalary, BigDecimal maxSalary, LocalDate hireAfter) {

        return query()
                .where(Employee::getName).containsIfNotEmpty(name)
                .where(Employee::getEmail).endsWithIfNotNull(email)
                .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
                .where(Employee::getStatus).eqIfNotNull(status)
                .where(Employee::getActive).eqIfNotNull(active)
                .where(Employee::getSalary).geIfNotNull(minSalary)
                .where(Employee::getSalary).leIfNotNull(maxSalary)
                .where(Employee::getHireDate).geIfNotNull(hireAfter)
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// Generate salary report grouped by department
    public List<Map.Entry<Long, List<Employee>>> generateDepartmentReport() {
        return query()
                .where(Employee::getActive).eq(true)
                .list()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId))
                .entrySet().stream().toList();
    }

    /// Find employees due for review
    public List<Employee> findEmployeesDueForReview() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .where(Employee::getHireDate).le(oneYearAgo)
                .where(Employee::getSalary).lt(BigDecimal.valueOf(50000.0))
                .orderBy(Employee::getHireDate).asc()
                .list();
    }

    /// Transfer employees between departments
    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        List<Employee> employees = query()
                .where(Employee::getId).in(employeeIds)
                .list();

        employees.forEach(e -> e.setDepartmentId(newDepartmentId));
        updateAll(employees);
    }

    /// Find employees without department
    public List<Employee> findEmployeesWithoutDepartment() {
        return query()
                .where(Employee::getDepartmentId).isNull()
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Find employees with missing email
    public List<Employee> findEmployeesWithMissingEmail() {
        return query()
                .where(Employee::getEmail).isNull()
                .list();
    }

    // ==================== Result Methods ====================

    /// Get first result as Optional
    public Optional<Employee> findFirstActive() {
        return Optional.ofNullable(query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .first());
    }

    /// Check if any active employee exists
    public boolean hasActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .exists();
    }

    /// Check if employee with email exists
    public boolean existsByEmail(String email) {
        return query()
                .where(Employee::getEmail).eq(email)
                .exists();
    }

    // ==================== Numeric Operations ====================

    /// Find employees where salary + bonus > threshold
    public List<Employee> findBySalaryWithBonus(BigDecimal bonus, BigDecimal threshold) {
        return query()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .list();
    }

    /// Find employees where salary * 12 (annual) > threshold
    public List<Employee> findByAnnualSalary(BigDecimal threshold) {
        return query()
                .where(Employee::getSalary).multiply(BigDecimal.valueOf(12.0)).gt(threshold)
                .list();
    }

    /// Find employees where salary - deduction >= minSalary
    public List<Employee> findBySalaryAfterDeduction(BigDecimal deduction, BigDecimal minSalary) {
        return query()
                .where(Employee::getSalary).subtract(deduction).ge(minSalary)
                .where(Employee::getActive).eq(true)
                .list();
    }

    /// Find employees where ID % divisor = remainder
    public List<Employee> findByIdMod(int divisor, int remainder) {
        return query()
                .where(Employee::getId).mod((long) divisor).eq((long) remainder)
                .list();
    }

    // ==================== OR Conditions ====================

    /// OR condition query
    public List<Employee> findByStatusOrStatus() {
        return query()
                .where(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                        .or(Employee::getStatus).eq(EmployeeStatus.ON_LEAVE))
                .orderBy(Employee::getName).asc()
                .list();
    }

    /// AND + OR combined query
    public List<Employee> findActiveWithOrCondition() {
        return query()
                .where(Path.of(Employee::getActive).eq(true)
                        .and(Path.of(Employee::getSalary).gt(BigDecimal.valueOf(100000.0))
                                .or(Employee::getStatus).eq(EmployeeStatus.ACTIVE)))
                .orderBy(Employee::getSalary).desc()
                .list();
    }

    /// Complex OR condition
    public List<Employee> findBySalaryOrStatus(BigDecimal minSalary, EmployeeStatus status) {
        return query()
                .where(Path.of(Employee::getSalary).gt(minSalary)
                        .or(Employee::getStatus).eq(status))
                .orderBy(Employee::getName).asc()
                .list();
    }

    // ==================== DTO Classes ====================

    /// DTO for employee summary projection
    public static class EmployeeSummary {
        private String name;
        private String email;
        private BigDecimal salary;
        private EmployeeStatus status;

        public EmployeeSummary() {}

        public EmployeeSummary(String name, String email, BigDecimal salary, EmployeeStatus status) {
            this.name = name;
            this.email = email;
            this.salary = salary;
            this.status = status;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public BigDecimal getSalary() { return salary; }
        public void setSalary(BigDecimal salary) { this.salary = salary; }
        public EmployeeStatus getStatus() { return status; }
        public void setStatus(EmployeeStatus status) { this.status = status; }
    }

    /// DTO for basic employee info
    public static class EmployeeInfo {
        private Long id;
        private String name;
        private String email;

        public EmployeeInfo() {}

        public EmployeeInfo(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /// DTO combining employee and department info
    public static class EmployeeWithDept {
        @EntityPath("name")
        private String employeeName;
        @EntityPath("department.name")
        private String departmentName;

        public EmployeeWithDept() {}

        public EmployeeWithDept(String employeeName, String departmentName) {
            this.employeeName = employeeName;
            this.departmentName = departmentName;
        }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    }
}