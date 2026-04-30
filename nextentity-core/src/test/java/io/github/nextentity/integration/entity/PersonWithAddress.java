package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;

@Entity
public class PersonWithAddress {

    @Id
    private Long id;

    private String name;

    @Embedded
    private Address address;

    public PersonWithAddress() {
    }

    public PersonWithAddress(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
