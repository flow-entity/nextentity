package io.github.nextentity.integration.dto;

///
/// Record projection with all nullable fields.
/// 测试s record projection with potential null values.
public record EmployeeNullableRecord(
        Long id,
        String name,
        String email,
        Double salary,
        Boolean active
) {
}
