package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.annotation.Fetch;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 测试用 JPA 实体类集合，用于 DefaultMetamodel 单元测试。
 * 每个内部类对应一种需要验证的元数据解析场景。
 */
public class TestEntities {

    @jakarta.persistence.Entity
    public static class SimpleEntity {
        @Id
        private Long id;
        private String name;
        private Integer age;

        public SimpleEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }

    @jakarta.persistence.Entity
    @Table(name = "custom_tbl")
    public static class TableOverrideEntity {
        @Id
        private Long id;
        private String label;

        public TableOverrideEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }

    @jakarta.persistence.Entity(name = "CustomName")
    public static class EntityNameOverrideEntity {
        @Id
        private Long id;
        private String value;

        public EntityNameOverrideEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    @jakarta.persistence.Entity(name = "EntityNameValue")
    @Table(name = "table_name_value")
    public static class BothTableAndEntityNameEntity {
        @Id
        private Long id;

        public BothTableAndEntityNameEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @jakarta.persistence.Entity
    public static class VersionedEntity {
        @Id
        private Long id;
        private String data;
        @Version
        private Long version;

        public VersionedEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public Long getVersion() { return version; }
        public void setVersion(Long version) { this.version = version; }
    }

    @jakarta.persistence.Entity
    public static class AutoIdEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String label;

        public AutoIdEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }

    @jakarta.persistence.Entity
    public static class DepartmentEntity implements io.github.nextentity.api.Entity {
        @Id
        private Long id;
        private String name;
        private String location;

        public DepartmentEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    @jakarta.persistence.Entity
    public static class EmployeeEntity {
        @Id
        private Long id;
        private String name;
        private Double salary;
        private Long departmentId;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "departmentId", insertable = false, updatable = false)
        @Fetch(FetchType.LAZY)
        private DepartmentEntity department;

        public EmployeeEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getSalary() { return salary; }
        public void setSalary(Double salary) { this.salary = salary; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public DepartmentEntity getDepartment() { return department; }
        public void setDepartment(DepartmentEntity department) { this.department = department; }
    }

    @jakarta.persistence.Entity
    public static class EagerAssocEntity {
        @Id
        private Long id;
        private String code;
        private Long departmentId;
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "departmentId", insertable = false, updatable = false)
        @Fetch(FetchType.EAGER)
        private DepartmentEntity department;

        public EagerAssocEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public DepartmentEntity getDepartment() { return department; }
        public void setDepartment(DepartmentEntity department) { this.department = department; }
    }

    @jakarta.persistence.Entity
    public static class ColumnAnnotatedEntity {
        @Id
        private Long id;
        @Column(name = "full_name", updatable = false)
        private String name;
        @Column(name = "email_addr")
        private String email;

        public ColumnAnnotatedEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @jakarta.persistence.Entity
    public static class TransientFieldEntity {
        @Id
        private Long id;
        private String name;
        @Transient
        private String computedField;
        private transient String keywordTransientField;

        public TransientFieldEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getComputedField() { return computedField; }
        public void setComputedField(String computedField) { this.computedField = computedField; }
        public String getKeywordTransientField() { return keywordTransientField; }
        public void setKeywordTransientField(String keywordTransientField) { this.keywordTransientField = keywordTransientField; }
    }

    @jakarta.persistence.Entity
    public static class SelfRefEntity implements io.github.nextentity.api.Entity {
        @Id
        private Long id;
        private String name;
        private Long parentId;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parentId", insertable = false, updatable = false)
        @Fetch(FetchType.LAZY)
        private SelfRefEntity parent;

        public SelfRefEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        public SelfRefEntity getParent() { return parent; }
        public void setParent(SelfRefEntity parent) { this.parent = parent; }
    }

    @jakarta.persistence.Entity
    public static class NoIdAnnotationEntity {
        private Long id;
        private String name;

        public NoIdAnnotationEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @jakarta.persistence.Entity
    public static class NoIdAtAllEntity {
        private String code;
        private String value;

        public NoIdAtAllEntity() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    @jakarta.persistence.Entity
    public static class OrderEntity {
        @Id
        private Long id;
        private String orderNo;
        private Long customerId;

        public OrderEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
    }

    @jakarta.persistence.Entity
    public static class CustomerEntity {
        @Id
        private Long id;
        private String name;

        public CustomerEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public enum TestStatus {
        ACTIVE, INACTIVE
    }

    @jakarta.persistence.Entity
    public static class EnumFieldEntity {
        @Id
        private Long id;
        private TestStatus status;

        public EnumFieldEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public TestStatus getStatus() { return status; }
        public void setStatus(TestStatus status) { this.status = status; }
    }

    @jakarta.persistence.Entity
    public static class UnsupportedVersionTypeEntity {
        @Id
        private Long id;
        @Version
        private String version;

        public UnsupportedVersionTypeEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    @jakarta.persistence.Entity
    public static class ProfileEntity {
        @Id
        private Long id;
        private String bio;

        public ProfileEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

    @jakarta.persistence.Entity
    public static class OneToOneOwnerEntity {
        @Id
        private Long id;
        private String username;
        private Long profileId;
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "profileId", insertable = false, updatable = false)
        @Fetch(FetchType.LAZY)
        private ProfileEntity profile;

        public OneToOneOwnerEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Long getProfileId() { return profileId; }
        public void setProfileId(Long profileId) { this.profileId = profileId; }
        public ProfileEntity getProfile() { return profile; }
        public void setProfile(ProfileEntity profile) { this.profile = profile; }
    }

    @jakarta.persistence.Entity
    public static class DateTimeFieldEntity {
        @Id
        private Long id;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        private LocalTime localTime;
        private Instant instant;

        public DateTimeFieldEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDate getLocalDate() { return localDate; }
        public void setLocalDate(LocalDate localDate) { this.localDate = localDate; }
        public LocalDateTime getLocalDateTime() { return localDateTime; }
        public void setLocalDateTime(LocalDateTime localDateTime) { this.localDateTime = localDateTime; }
        public LocalTime getLocalTime() { return localTime; }
        public void setLocalTime(LocalTime localTime) { this.localTime = localTime; }
        public Instant getInstant() { return instant; }
        public void setInstant(Instant instant) { this.instant = instant; }
    }

    private TestEntities() {}
}
