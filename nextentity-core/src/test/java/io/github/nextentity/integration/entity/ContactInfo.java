package io.github.nextentity.integration.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class ContactInfo {

    private String email;
    private String phone;

    @Embedded
    private Address address;

    public ContactInfo() {
    }

    public ContactInfo(String email, String phone, Address address) {
        this.email = email;
        this.phone = phone;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
