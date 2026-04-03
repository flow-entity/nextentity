package io.github.nextentity.integration.dto;

///
 /// Record projection for Employee name and salary.
 /// 测试s record projection with subset of fields.
public record EmployeeNameSalary(
        String name,
        Double salary
) {
}
