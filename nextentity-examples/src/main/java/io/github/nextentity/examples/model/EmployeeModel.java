package io.github.nextentity.examples.model;

public interface EmployeeModel {

    Long getId();

    String getName();

    Dep getDepartment();

    record Dep(Long id, Boolean unknown, String name) {
    }

}

