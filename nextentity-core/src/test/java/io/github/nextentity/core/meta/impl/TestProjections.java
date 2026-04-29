package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.core.annotation.Join;
import jakarta.persistence.FetchType;

/**
 * 测试用投影类型集合，用于 DefaultMetamodel 投影解析测试。
 * 包含接口投影、类投影、record 投影、@Join 投影等场景。
 */
public class TestProjections {

    // ── 接口投影 ──

    public interface SimpleProjection {
        Long getId();
        String getName();
    }

    public interface EntityPathProjection {
        Long getId();
        String getName();
        @EntityPath("department.name")
        String getDepartmentName();
    }

    public interface FetchLazyProjection {
        Long getId();
        String getName();
        @Fetch(FetchType.LAZY)
        TestEntities.DepartmentEntity getDepartment();
    }

    public interface NestedInterfaceProjection {
        Long getId();
        String getName();
        @EntityPath("department")
        @Fetch(FetchType.LAZY)
        DeptProjection getDepartment();

        interface DeptProjection {
            Long getId();
            String getName();
        }
    }

    // ── 类投影 ──

    public static class JavaBeanProjection {
        private Long id;
        private String name;
        @EntityPath("department.name")
        private String departmentName;

        public JavaBeanProjection() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    }

    // ── Record 投影 ──

    public record RecordProjection(Long id, String name, Double salary) {}

    public record SimpleRecordProjection(Long id, String name) {}

    // ── @Join 投影 ──

    public interface JoinProjection {
        Long getId();
        String getOrderNo();
        @Join(target = TestEntities.CustomerEntity.class, sourceAttribute = "customerId", targetAttribute = "id")
        TestEntities.CustomerEntity getCustomer();
    }

    private TestProjections() {}
}
