package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public class PersonAddressWrongType {

    private Long id;
    private String name;
    @EntityPath("address.street")
    private Long street;
    @EntityPath("address.city")
    private String city;
    @EntityPath("address.zipCode")
    private String zipCode;

    public PersonAddressWrongType() {
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

    public Long getStreet() {
        return street;
    }

    public void setStreet(Long street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
