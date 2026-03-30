package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.util.List;

/// String Operations Example demonstrating string-specific operations
public class StringOperationsExample {

    private final EmployeeRepository employeeRepository;

    public StringOperationsExample(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ==================== Pattern Matching ====================

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

    // ==================== Conditional Operations ====================

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

    // ==================== Case-Insensitive Search ====================

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

    // ==================== Practical Examples ====================

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

    // ==================== Helper Methods ====================

    private Select<Employee> query() {
        return employeeRepository.query();
    }
}