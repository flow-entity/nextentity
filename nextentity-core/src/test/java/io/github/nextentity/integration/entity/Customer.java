package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * 客户实体，用于测试投影 @Join 注解。
 * <p>
 * 该实体不包含任何 JPA 关联注解（如 @ManyToOne），
 * 仅作为独立的实体表存在。
 */
@Entity
public class Customer {

    @Id
    private Long id;

    private String name;

    private String email;

    public Customer() {
    }

    public Customer(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
