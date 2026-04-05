package io.github.nextentity.integration.dto;

///
 /// Interface projection with default 方法.
 /// 测试s interface projection with default 方法 implementation.
public interface EmployeeWithStatus {

    String getName();

    Boolean getActive();

///
     /// Default 方法 to determine employee status.
    default String getStatus() {
        return Boolean.TRUE.equals(getActive()) ? "Active" : "Inactive";
    }
}
