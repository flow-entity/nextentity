package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;

@Entity
public class PersonWithRecordAddress {

    @Id
    private Long id;

    private String name;

    @Embedded
    private AddressRecord address;

    public PersonWithRecordAddress() {
    }

    public PersonWithRecordAddress(Long id, String name, AddressRecord address) {
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

    public AddressRecord getAddress() {
        return address;
    }

    public void setAddress(AddressRecord address) {
        this.address = address;
    }
}
