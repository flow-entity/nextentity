package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;

@Entity
public class PersonWithNestedAddress {

    @Id
    private Long id;

    private String name;

    @Embedded
    private ContactInfo contactInfo;

    public PersonWithNestedAddress() {
    }

    public PersonWithNestedAddress(Long id, String name, ContactInfo contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
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

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
}
