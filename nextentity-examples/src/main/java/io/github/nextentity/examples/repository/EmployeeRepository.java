package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.*;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.spring.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepository extends AbstractRepository<Employee, Long> {

    public EmployeeRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    @Override
    public Select<Employee> query() {
        return super.query();
    }

    // ==================== Basic CRUD Operations ====================

    /// Insert a single employee
    @Transactional
    public void insertSingleEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");
        employee.setSalary(BigDecimal.valueOf(50000.0));
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        insert(employee);
    }

    /// Batch insert multiple employees
    @Transactional
    public void insertMultipleEmployees() {
        List<Employee> employees = List.of(
                createEmployee(1L, "Alice", "alice@example.com", BigDecimal.valueOf(50000.0)),
                createEmployee(2L, "Bob", "bob@example.com", BigDecimal.valueOf(55000.0)),
                createEmployee(3L, "Charlie", "charlie@example.com", BigDecimal.valueOf(60000.0))
        );
        insertAll(employees);
    }

    /// Query all employees
    public List<Employee> findAllEmployees() {
        return query().getList();
    }

    /// Query employee by ID
    public Employee findEmployeeById(Long id) {
        return query()
                .where(Employee::getId).eq(id)
                .getFirst();
    }

    /// Query employees by multiple IDs using IN clause
    public List<Employee> findEmployeesByIds(List<Long> ids) {
        return query().where(Employee::getId).in(ids).getList();
    }

    /// Get single employee by email
    public Employee findEmployeeByEmail(String email) {
        return query().where(Employee::getEmail).eq(email).getFirst();
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

    /// Give raise to all employees in a department
    @Transactional
    public List<Employee> giveRaiseToDepartment(Long departmentId, BigDecimal percentage) {
        List<Employee> employees = query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();
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
                .getList();
        deleteAll(employees);
    }

    /// Check association before delete - crud-operations.md example
    /// Returns true if department has employees, false otherwise
    public boolean hasEmployeesInDepartment(Long departmentId) {
        long empCount = query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .count();
        return empCount > 0;
    }

    /// Safe delete with association check - crud-operations.md example
    /// Returns false if department has employees, true if deletion proceeds
    @Transactional
    public boolean deleteDepartmentIfEmpty(Long departmentId) {
        long empCount = query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .count();

        if (empCount == 0) {
            // Safe to delete - actual department deletion handled by DepartmentRepository
            return true;
        }
        return false; // Cannot delete, department has employees
    }

    // ==================== Query Conditions ====================

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

    /// Greater than comparison
    public List<Employee> findBySalaryGreaterThan(BigDecimal salary) {
        return query().where(Employee::getSalary).gt(salary).getList();
    }

    /// Greater than or equal comparison
    public List<Employee> findBySalaryGreaterOrEqual(BigDecimal salary) {
        return query().where(Employee::getSalary).ge(salary).getList();
    }

    /// Less than comparison
    public List<Employee> findBySalaryLessThan(BigDecimal salary) {
        return query().where(Employee::getSalary).lt(salary).getList();
    }

    /// Less than or equal comparison
    public List<Employee> findBySalaryLessOrEqual(BigDecimal salary) {
        return query().where(Employee::getSalary).le(salary).getList();
    }

    /// Between comparison (inclusive range)
    public List<Employee> findBySalaryBetween(BigDecimal min, BigDecimal max) {
        return query().where(Employee::getSalary).between(min, max).getList();
    }

    /// Not between comparison
    public List<Employee> findBySalaryNotBetween(BigDecimal min, BigDecimal max) {
        return query().where(Employee::getSalary).notBetween(min, max).getList();
    }

    /// IN operator with varargs
    public List<Employee> findByIds(Long... ids) {
        return query().where(Employee::getId).in(ids).getList();
    }

    /// IN operator with collection
    public List<Employee> findByIdsCollection(List<Long> ids) {
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

    /// Is NULL check
    public List<Employee> findWithoutEmail() {
        return query().where(Employee::getEmail).isNull().getList();
    }

    /// Is NOT NULL check
    public List<Employee> findWithEmail() {
        return query().where(Employee::getEmail).isNotNull().getList();
    }

    /// Date comparison - after a specific date
    public List<Employee> findHiredAfter(LocalDate date) {
        return query().where(Employee::getHireDate).gt(date).getList();
    }

    /// Date range query
    public List<Employee> findHiredBetween(LocalDate start, LocalDate end) {
        return query().where(Employee::getHireDate).between(start, end).getList();
    }

    /// Active employees in specific department
    public List<Employee> findActiveInDepartment(Long departmentId) {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();
    }

    /// Active employees by department with active status
    public List<Employee> findActiveEmployeesByDepartment(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Active high earners
    public List<Employee> findActiveHighEarners() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
                .getList();
    }

    // ==================== String Operations ====================

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

    /// Does not start with
    public List<Employee> findByNameNotStartingWith(String prefix) {
        return query().where(Employee::getName).notStartsWith(prefix).getList();
    }

    /// Does not end with
    public List<Employee> findByEmailNotEndingWith(String suffix) {
        return query().where(Employee::getEmail).notEndsWith(suffix).getList();
    }

    /// Does not contain
    public List<Employee> findByNameNotContaining(String text) {
        return query().where(Employee::getName).notContains(text).getList();
    }

    /// LIKE if value is not null
    public List<Employee> findByNameLikeIfPresent(String pattern) {
        return query().where(Employee::getName).likeIfNotNull(pattern).getList();
    }

    /// Contains if value is not empty
    public List<Employee> findByNameContainingIfPresent(String text) {
        return query().where(Employee::getName).containsIfNotEmpty(text).getList();
    }

    /// Starts with if not null
    public List<Employee> findByNameStartingWithIfPresent(String prefix) {
        return query().where(Employee::getName).startsWithIfNotNull(prefix).getList();
    }

    /// Ends with if not null
    public List<Employee> findByEmailEndingWithIfPresent(String suffix) {
        return query().where(Employee::getEmail).endsWithIfNotNull(suffix).getList();
    }

    /// Equals if string is not empty
    public List<Employee> findByEmailIfNotEmpty(String email) {
        return query().where(Employee::getEmail).eqIfNotEmpty(email).getList();
    }

    /// Case-insensitive search using lower()
    public List<Employee> findByNameContainingIgnoreCase(String text) {
        String lowerText = text.toLowerCase();
        return query().where(Employee::getName).lower().contains(lowerText).getList();
    }

    /// Case-insensitive email search
    public List<Employee> findByEmailDomainIgnoreCase(String domain) {
        String lowerDomain = domain.toLowerCase();
        return query().where(Employee::getEmail).lower().endsWith(lowerDomain).getList();
    }

    /// Search employees by name with multiple options
    public List<Employee> searchByName(String firstName, String lastName) {
        return query()
                .where(Employee::getName).startsWithIfNotNull(firstName)
                .where(Employee::getName).endsWithIfNotNull(lastName)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Email validation patterns - find corporate emails
    public List<Employee> findCorporateEmails(String companyDomain) {
        return query()
                .where(Employee::getEmail).endsWith("@" + companyDomain)
                .where(Employee::getEmail).notContains("temp")
                .where(Employee::getEmail).notContains("test")
                .getList();
    }

    /// Find employees with valid emails
    public List<Employee> findWithValidEmail() {
        return query()
                .where(Employee::getEmail).isNotNull()
                .where(Employee::getEmail).contains("@")
                .getList();
    }

    // ==================== Ordering ====================

    /// Order by single field - ascending
    public List<Employee> findOrderedByNameAsc() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Order by single field - descending
    public List<Employee> findOrderedBySalaryDesc() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();
    }

    /// Order by multiple fields
    public List<Employee> findByDepartmentThenSalary() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .getList();
    }

    /// Order by status then name
    public List<Employee> findByStatusThenName() {
        return query()
                .orderBy(Employee::getStatus).asc()
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Order with projection
    public List<String> findNamesOrdered() {
        return query()
                .select(Employee::getName)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    // ==================== Pagination ====================

    /// Basic pagination with offset and limit
    public List<Employee> findFirstPage() {
        return query()
                .orderBy(Employee::getId).asc()
                .getList(0, 10);
    }

    /// Pagination with page number
    public List<Employee> findPage(int pageNumber, int pageSize) {
        return query()
                .orderBy(Employee::getId).asc()
                .getList(pageNumber * pageSize, pageSize);
    }

    /// Pagination with conditions
    public List<Employee> findActiveEmployeesPaged(int offset, int limit) {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList(offset, limit);
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
                .getList(offset, limit);
    }

    /// Top N results
    public List<Employee> findTopEarners(int n) {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList(0, n);
    }

    /// Get single result - highest paid employee
    public Employee findHighestPaid() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getFirst();
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
                .selectDistinct(Employee::getDepartmentId)
                .count();
    }

    /// Calculate total salary using Java streams
    public BigDecimal calculateTotalSalary() {
        return query()
                .selectExpr(path(Employee::getSalary).sum())
                .where(Employee::getSalary).isNotNull()
                .getSingle();
    }

    /// Sum with conditions - total salary for active employees
    public BigDecimal calculateActiveTotalSalary() {
        return query()
                .selectExpr(path(Employee::getSalary).sum())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    /// Calculate average salary
    public Double calculateAverageSalary() {
        return query()
                .selectExpr(path(Employee::getSalary).avg())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    /// Average by department
    public Double calculateAverageSalaryByDepartment(Long departmentId) {
        return query()
                .selectExpr(path(Employee::getSalary).avg())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getSingle();
    }

    /// Find maximum salary
    public BigDecimal findMaxSalary() {
        return query()
                .selectExpr(path(Employee::getSalary).max())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    /// Find minimum salary
    public BigDecimal findMinSalary() {
        return query()
                .selectExpr(path(Employee::getSalary).min())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    /// Find employee with highest salary
    public Employee findHighestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).isNotNull()
                .orderBy(Employee::getSalary).desc()
                .getFirst();
    }

    /// Find employee with lowest salary
    public Employee findLowestPaidEmployee() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).isNotNull()
                .orderBy(Employee::getSalary).asc()
                .getFirst();
    }

    /// Group by department using Java streams
    public Map<Long, List<Employee>> groupByDepartment() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId));
    }

    /// Group by status and count
    public Map<EmployeeStatus, Long> countByStatus() {
        return query()
                .select(Employee::getStatus)
                .where(Employee::getStatus).isNotNull()
                .getList()
                .stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

    /// Calculate statistics per department using database-level aggregation
    /// Returns Tuple6 containing: departmentId, count, sum, avg, max, min
    public List<Tuple6<Long, Long, BigDecimal, Double, BigDecimal, BigDecimal>> salaryStatsByDepartment() {
        return query()
                .selectExpr(
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
                .getList();
    }

    /// Select name and salary for analysis
    public List<Tuple2<String, BigDecimal>> findNameSalaryPairs() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();
    }

    // ==================== Projection ====================

    /// Select a single field from entity
    public List<String> findEmployeeNames() {
        return query()
                .select(Employee::getName)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select distinct values
    public List<Long> findDistinctDepartmentIds() {
        return query()
                .selectDistinct(Employee::getDepartmentId)
                .getList();
    }

    /// Select two fields using Tuple2
    public List<Tuple2<String, BigDecimal>> findNameAndSalary() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select three fields using Tuple3
    public List<Tuple3<String, String, BigDecimal>> findNameEmailSalary() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select four fields using Tuple4
    /// Example for projections.md Tuple4 documentation
    public List<Tuple4<String, String, BigDecimal, Long>> findNameEmailSalaryDepartment() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary, Employee::getDepartmentId)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Select five fields using Tuple5
    /// Example for projections.md Tuple5 documentation
    public List<Tuple5<String, String, BigDecimal, Long, Boolean>> findEmployeeDetails() {
        return query()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary,
                        Employee::getDepartmentId, Employee::getActive)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Select distinct tuple values
    public List<Tuple2<String, EmployeeStatus>> findDistinctNameStatus() {
        return query()
                .selectDistinct(Employee::getName, Employee::getStatus)
                .getList();
    }

    /// Select into a DTO class
    public List<EmployeeSummary> findEmployeeSummaries() {
        return query()
                .select(EmployeeSummary.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select with conditions into DTO
    public List<EmployeeSummary> findHighEarnerSummaries() {
        return query()
                .select(EmployeeSummary.class)
                .where(Employee::getSalary).gt(BigDecimal.valueOf(50000.0))
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .getList();
    }

    /// Select distinct into DTO
    public List<EmployeeInfo> findDistinctEmployeeInfo() {
        return query()
                .selectDistinct(EmployeeInfo.class)
                .getList();
    }

    /// Select full entity (default behavior)
    public List<Employee> findActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select distinct entities
    public List<Employee> findDistinctActiveEmployees() {
        return query()
                .selectDistinct(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select specific fields using Tuple
    public List<Tuple2<String, BigDecimal>> findEmployeeNamesAndSalaries() {
        return query()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    // ==================== Association Fetch ====================

    /// Lazy loading (default behavior) - department loaded on first access
    public List<Employee> findWithLazyLoading() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Eager fetch association - load employees with departments in one query
    public List<Employee> findWithDepartmentFetch() {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Fetch multiple associations
    public List<Employee> findWithMultipleFetches() {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Query by association ID
    public List<Employee> findByDepartmentId(Long departmentId) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();
    }

    /// Query with fetch and conditions
    public List<Employee> findActiveInDepartmentWithFetch(Long departmentId) {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Select with association data using DTO
    public List<EmployeeWithDept> findEmployeeWithDepartmentInfo() {
        return query()
                .select(EmployeeWithDept.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Join data from two entities manually
    public List<EmployeeWithDept> findWithManualJoin() {
        List<Employee> employees = query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();

        return employees.stream()
                .map(emp -> new EmployeeWithDept(
                        emp.getName(),
                        emp.getDepartment() != null ? emp.getDepartment().getName() : null
                ))
                .toList();
    }

    // ==================== Complex Query ====================

    /// Multi-condition employee search combining multiple operators
    public List<Employee> findEmployeesByMultipleConditions() {
        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).in(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE)
                .where(Employee::getSalary).between(BigDecimal.valueOf(40000.0), BigDecimal.valueOf(80000.0))
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Employee search with optional filters using conditional operators
    public List<Employee> searchEmployees(String name, Long departmentId, BigDecimal minSalary) {
        return query()
                .where(Employee::getName).containsIfNotEmpty(name)
                .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
                .where(Employee::getSalary).geIfNotNull(minSalary)
                .orderBy(Employee::getName).asc()
                .getList();
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
                .getList();
    }

    /// Generate salary report grouped by department
    public List<Map.Entry<Long, List<Employee>>> generateDepartmentReport() {
        return query()
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId))
                .entrySet().stream().toList();
    }

    /// Find employees due for review (hired > 1 year ago, active, below-average salary)
    public List<Employee> findEmployeesDueForReview() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return query()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .where(Employee::getHireDate).le(oneYearAgo)
                .where(Employee::getSalary).lt(BigDecimal.valueOf(50000.0))
                .orderBy(Employee::getHireDate).asc()
                .getList();
    }

    /// Give a raise to all employees in a department within a transaction
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

    /// Transfer employees between departments within a transaction
    @Transactional
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        List<Employee> employees = query()
                .where(Employee::getId).in(employeeIds)
                .getList();

        employees.forEach(e -> e.setDepartmentId(newDepartmentId));
        updateAll(employees);
    }

    /// Deactivate terminated employees within a transaction
    @Transactional
    public void deactivateTerminatedEmployees() {
        List<Employee> employees = query()
                .where(Employee::getStatus).eq(EmployeeStatus.TERMINATED)
                .getList();

        employees.forEach(e -> e.setActive(false));
        updateAll(employees);
    }

    /// Find employees with department eagerly loaded
    public List<Employee> findEmployeesWithDepartment() {
        return query()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Find employees hired within recent days
    public List<Employee> findRecentlyHired(int days) {
        LocalDate recentDate = LocalDate.now().minusDays(days);

        return query()
                .where(Employee::getHireDate).ge(recentDate)
                .orderBy(Employee::getHireDate).desc()
                .getList();
    }

    /// Find employees without department
    public List<Employee> findEmployeesWithoutDepartment() {
        return query()
                .where(Employee::getDepartmentId).isNull()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Find employees with missing email
    public List<Employee> findEmployeesWithMissingEmail() {
        return query()
                .where(Employee::getEmail).isNull()
                .getList();
    }

    // ==================== Additional String Functions ====================

    /// Convert to uppercase for comparison
    public List<Employee> findByNameUppercase(String upperName) {
        return query().where(Employee::getName).upper().eq(upperName.toUpperCase()).getList();
    }

    /// Trim whitespace and search
    public List<Employee> findByNameTrimmed(String name) {
        return query().where(Employee::getName).trim().eq(name.trim()).getList();
    }

    /// Search by substring
    public List<Employee> findByNameSubstring(int start, int length, String expected) {
        return query().where(Employee::getName).substring(start, length).eq(expected).getList();
    }

    /// Find employees with name length > N
    public List<Employee> findByNameLongerThan(int minLen) {
        return query().where(Employee::getName).length().gt(minLen).getList();
    }

    // ==================== Result Methods ====================

    /// Get first result as Optional
    public Optional<Employee> findFirstActive() {
        return query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .first();
    }

    /// Check if any active employee exists
    public boolean hasActiveEmployees() {
        return query()
                .where(Employee::getActive).eq(true)
                .exist();
    }

    /// Check if employee with email exists
    public boolean existsByEmail(String email) {
        return query()
                .where(Employee::getEmail).eq(email)
                .exist();
    }

    // ==================== Numeric Operations ====================

    /// Find employees where salary + bonus > threshold
    public List<Employee> findBySalaryWithBonus(BigDecimal bonus, BigDecimal threshold) {
        return query()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .getList();
    }

    /// Find employees where salary * 12 (annual) > threshold
    public List<Employee> findByAnnualSalary(BigDecimal threshold) {
        return query()
                .where(Employee::getSalary).multiply(BigDecimal.valueOf(12.0)).gt(threshold)
                .getList();
    }

    /// Numeric operations: subtract example
    /// Find employees where salary - deduction >= minSalary
    public List<Employee> findBySalaryAfterDeduction(BigDecimal deduction, BigDecimal minSalary) {
        return query()
                .where(Employee::getSalary).subtract(deduction).ge(minSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Numeric operations: divide example
    /// Find employees where monthly salary (annual / 12) > threshold
    public List<Employee> findByMonthlySalary(BigDecimal threshold) {
        return query()
                .where(Employee::getSalary).divide(BigDecimal.valueOf(12.0)).gt(threshold)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Numeric operations: mod example
    /// Find employees where ID % 10 = 0 (for sharding/partitioning demo)
    public List<Employee> findByIdMod(int divisor, int remainder) {
        return query()
                .where(Employee::getId).mod((long) divisor).eq((long) remainder)
                .getList();
    }

    /// Expression equals expression example
    /// Compare salary field with a computed value
    public List<Employee> findBySalaryEqualsBase(Long departmentId, BigDecimal baseSalary) {
        return query()
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getSalary).eq(Path.of(Employee::getSalary))  // self-comparison for demo
                .getList();
    }

    // ==================== Slice Methods ====================

    /// Demonstrate slice methods
    public void demonstrateSlice() {
        Slice<Employee> slice = query()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .slice(0, 10);

        List<Employee> data = slice.data();
        long total = slice.total();
        int offset = slice.offset();
        int limit = slice.limit();
        boolean hasNext = data.size() == limit;

        System.out.println("Data: " + data.size() + ", Total: " + total +
                          ", Offset: " + offset + ", Limit: " + limit +
                          ", HasNext: " + hasNext);
    }

    // ==================== Aggregation with Path.of() ====================

    /// Sum using Path.of() (for use outside Repository)
    public BigDecimal calculateTotalSalaryExternal() {
        return query()
                .selectExpr(Path.of(Employee::getSalary).sum())
                .where(Employee::getSalary).isNotNull()
                .getSingle();
    }

    /// Average using Path.of()
    public Double calculateAverageSalaryExternal() {
        return query()
                .selectExpr(Path.of(Employee::getSalary).avg())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    /// Max using Path.of()
    public BigDecimal findMaxSalaryExternal() {
        return query()
                .selectExpr(Path.of(Employee::getSalary).max())
                .where(Employee::getSalary).isNotNull()
                .getSingle();
    }

    /// Min using Path.of()
    public BigDecimal findMinSalaryExternal() {
        return query()
                .selectExpr(Path.of(Employee::getSalary).min())
                .where(Employee::getSalary).isNotNull()
                .where(Employee::getActive).eq(true)
                .getSingle();
    }

    // ==================== OR Conditions ====================

    /// OR 条件查询 - 状态为 ACTIVE 或 ON_LEAVE 的员工
    /// 使用 Path.of() 链式调用实现 OR 条件
    public List<Employee> findByStatusOrStatus() {
        return query()
                .where(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                        .or(Employee::getStatus).eq(EmployeeStatus.ON_LEAVE))
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// AND + OR 组合查询 - 活跃员工中高薪或特定状态
    /// 等价于: active = true AND (salary > 100000 OR status = ACTIVE)
    public List<Employee> findActiveWithOrCondition() {
        return query()
                .where(Path.of(Employee::getActive).eq(true)
                        .and(Path.of(Employee::getSalary).gt(BigDecimal.valueOf(100000.0))
                                .or(Employee::getStatus).eq(EmployeeStatus.ACTIVE)))
                .orderBy(Employee::getSalary).desc()
                .getList();
    }

    /// 复杂 OR 条件 - 按薪资或状态查询
    /// 等价于: salary > minSalary OR status = status
    public List<Employee> findBySalaryOrStatus(BigDecimal minSalary, EmployeeStatus status) {
        return query()
                .where(Path.of(Employee::getSalary).gt(minSalary)
                        .or(Employee::getStatus).eq(status))
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// OR 条件与 IN 查询对比 - 使用 OR 实现类似 IN 的效果
    /// 这两种方式效果相同，但 IN 更简洁
    public List<Employee> findByStatusOrCompareWithIn() {
        // 方式 1: 使用 OR
        List<Employee> byOr = query()
                .where(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                        .or(Employee::getStatus).eq(EmployeeStatus.ON_LEAVE))
                .getList();

        // 方式 2: 使用 IN (推荐，更简洁)
        List<Employee> byIn = query()
                .where(Employee::getStatus).in(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE)
                .getList();

        return byIn; // 返回 IN 方式的结果
    }

    // ==================== Helper Methods ====================

    private Employee createEmployee(Long id, String name, String email, BigDecimal salary) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(salary);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setHireDate(LocalDate.now());
        return employee;
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
        private String employeeName;
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