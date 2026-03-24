package io.github.nextentity.integration.dto;

/**
 * Interface projection with default method.
 * Tests interface projection with default method implementation.
 */
public interface EmployeeWithStatus {

    String getName();

    Boolean getActive();

    /**
     * Default method to determine employee status.
     */
    default String getStatus() {
        return Boolean.TRUE.equals(getActive()) ? "Active" : "Inactive";
    }
}