package io.github.nextentity.integration.dto;

/**
 * Record projection for Employee basic info.
 * Used for testing projection to Java Record types.
 */
public record EmployeeRecord(
        Long id,
        String name,
        String email,
        Double salary
) {
}