package io.github.nextentity.integration.dto;

///
/// Interface projection for Employee basic info.
/// Used for testing projection to interface types via dynamic proxy.
public interface EmployeeInfo {

    Long getId();

    String getName();

    String getEmail();

    Double getSalary();

}
