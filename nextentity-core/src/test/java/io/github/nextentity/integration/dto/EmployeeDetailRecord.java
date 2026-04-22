package io.github.nextentity.integration.dto;

///
/// Record projection with nested record.
/// 测试s record projection with multiple related fields.
public record EmployeeDetailRecord(
        Long id,
        String name,
        Double salary,
        Boolean active,
        Long departmentId
) {
}
