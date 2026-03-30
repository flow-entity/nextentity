package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.util.List;

/// Order and Pagination Example demonstrating sorting and pagination
public class OrderPaginationExample {

    private final EmployeeRepository employeeRepository;

    public OrderPaginationExample(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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

    // ==================== Simple Pagination ====================

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

    // ==================== Slice Pagination ====================

    /// Slice-based pagination with metadata
    public Slice<Employee> findFirstSlice() {
        return query()
                .orderBy(Employee::getId).asc()
                .slice(0, 10);
    }

    /// Navigate through slices
    public void processAllSlices() {
        int page = 0;
        int size = 10;
        Slice<Employee> slice;

        do {
            slice = query()
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .slice(page * size, size);

            processEmployees(slice.data());
            page++;

            if (slice.data().size() < size) {
                break;
            }
        } while (true);
    }

    /// Slice with conditions and ordering
    public Slice<Employee> findHighEarnerSlice(int page, int size) {
        return query()
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .slice(page * size, size);
    }

    // ==================== Combined Examples ====================

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

    // ==================== Helper Methods ====================

    private Select<Employee> query() {
        return employeeRepository.query();
    }

    private void processEmployees(List<Employee> employees) {
        for (Employee emp : employees) {
            System.out.println(emp.getName());
        }
    }
}