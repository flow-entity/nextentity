package io.github.nextentity.examples.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/// 客户实体类，用于测试 Repository 自动注入功能。
///
/// 本实体演示：
/// - 自动注册的 Repository Bean 注入
/// - 无需手动创建 Repository 子类
///
/// ## 自动注入示例
///
/// ```java
/// @Autowired
/// Repository<Customer, Long> customerRepository;
///
/// // 直接使用
/// customerRepository.findById(1L);
/// customerRepository.query().where(Customer::getName).eq("test").list();
/// ```
@Entity
@Table(name = "customers")
public class Customer {

    /// 客户 ID（主键）。
    @Id
    private Long id;

    /// 客户名称。
    private String name;

    /// 客户邮箱。
    private String email;

    /// 客户电话。
    private String phone;

    /// 客户地址。
    private String address;

    /// 是否为 VIP 客户。
    private Boolean vip;

    /// 创建时间。
    private LocalDateTime createdAt;

    public Customer() {
    }

    public Customer(Long id, String name, String email, String phone, String address, Boolean vip) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.vip = vip;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}