package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.annotation.Fetch;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
    public static class CompanyEntity {
        @Id
        private Long id;
        private String name;

        public CompanyEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @jakarta.persistence.Entity
    public static class DeptWithCompanyEntity implements io.github.nextentity.api.Entity {
        @Id
        private Long id;
        private String name;
        private Long companyId;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "companyId", insertable = false, updatable = false)
        @Fetch(FetchType.LAZY)
        private CompanyEntity company;

        public DeptWithCompanyEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public CompanyEntity getCompany() { return company; }
        public void setCompany(CompanyEntity company) { this.company = company; }
    }

    @jakarta.persistence.Entity
    public static class EmployeeWithDeptCompanyEntity {
        @Id
        private Long id;
        private String name;
        private Long departmentId;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "departmentId", insertable = false, updatable = false)
        @Fetch(FetchType.LAZY)
        private DeptWithCompanyEntity department;

        public EmployeeWithDeptCompanyEntity() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public DeptWithCompanyEntity getDepartment() { return department; }
        public void setDepartment(DeptWithCompanyEntity department) { this.department = department; }
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

    public static class Address {
        private String street;
        private String city;
        private String zipCode;

        public Address() {}

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }

    public static class FullName {
        private String firstName;
        private String lastName;

        public FullName() {}

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithEmbedded {
        @Id
        private Long id;
        private String name;
        @jakarta.persistence.Embedded
        private Address address;

        public EntityWithEmbedded() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    public static class ContactInfo {
        private String email;
        private String phone;
        @jakarta.persistence.Embedded
        private Address address;

        public ContactInfo() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithNestedEmbedded {
        @Id
        private Long id;
        @jakarta.persistence.Embedded
        private ContactInfo contactInfo;

        public EntityWithNestedEmbedded() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public ContactInfo getContactInfo() { return contactInfo; }
        public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithNestedAttributeOverride {
        @Id
        private Long id;
        @jakarta.persistence.Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "email", column = @Column(name = "contact_email")),
                @AttributeOverride(name = "address.street", column = @Column(name = "deep_street"))
        })
        private ContactInfo contactInfo;

        public EntityWithNestedAttributeOverride() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public ContactInfo getContactInfo() { return contactInfo; }
        public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithAttributeOverride {
        @Id
        private Long id;
        private String name;
        @jakarta.persistence.Embedded
        @AttributeOverride(name = "firstName", column = @Column(name = "first_name_ov"))
        private FullName fullName;

        public EntityWithAttributeOverride() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public FullName getFullName() { return fullName; }
        public void setFullName(FullName fullName) { this.fullName = fullName; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithAttributeOverrides {
        @Id
        private Long id;
        @jakarta.persistence.Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "street", column = @Column(name = "addr_street")),
                @AttributeOverride(name = "city", column = @Column(name = "addr_city"))
        })
        private Address address;

        public EntityWithAttributeOverrides() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    // ── 错层嵌套测试用 ──

    public static class ZipCode {
        private String code;

        public ZipCode() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class AddressWithZip {
        private String city;
        @jakarta.persistence.Embedded
        private ZipCode zip;

        public AddressWithZip() {}

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public ZipCode getZip() { return zip; }
        public void setZip(ZipCode zip) { this.zip = zip; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithCrossLayerEmbedded {
        @Id
        private Long id;
        @jakarta.persistence.Embedded
        private AddressWithZip address;
        @jakarta.persistence.Embedded
        private ZipCode secondaryZip;

        public EntityWithCrossLayerEmbedded() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public AddressWithZip getAddress() { return address; }
        public void setAddress(AddressWithZip address) { this.address = address; }
        public ZipCode getSecondaryZip() { return secondaryZip; }
        public void setSecondaryZip(ZipCode secondaryZip) { this.secondaryZip = secondaryZip; }
    }

    @jakarta.persistence.Entity
    public static class EntityWithCrossLayerOverride {
        @Id
        private Long id;
        @jakarta.persistence.Embedded
        @AttributeOverride(name = "city", column = @Column(name = "addr_city"))
        @AttributeOverride(name = "zip.code", column = @Column(name = "addr_zip_code"))
        private AddressWithZip address;
        @jakarta.persistence.Embedded
        @AttributeOverride(name = "code", column = @Column(name = "sec_zip_code"))
        private ZipCode secondaryZip;

        public EntityWithCrossLayerOverride() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public AddressWithZip getAddress() { return address; }
        public void setAddress(AddressWithZip address) { this.address = address; }
        public ZipCode getSecondaryZip() { return secondaryZip; }
        public void setSecondaryZip(ZipCode secondaryZip) { this.secondaryZip = secondaryZip; }
    }

    private TestEntities() {}
}
