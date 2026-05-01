package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public class PersonAddressOnlyEmbedded {

    @EntityPath("address.street")
    private String street;
    @EntityPath("address.city")
    private String city;
    @EntityPath("address.zipCode")
    private String zipCode;

    public PersonAddressOnlyEmbedded() {
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
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
