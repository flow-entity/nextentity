package io.github.nextentity.integration.dto;

/**
 * Record projection for Department info.
 * Tests record projection for different entity.
 */
public record DepartmentRecord(
        Long id,
        String name,
        String location
) {
}