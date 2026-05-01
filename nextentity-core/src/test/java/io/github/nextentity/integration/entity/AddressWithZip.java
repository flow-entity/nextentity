package io.github.nextentity.integration.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class AddressWithZip {

    private String city;
    private String street;

    @Embedded
    private ZipCode zip;

    public AddressWithZip() {
    }

    public AddressWithZip(String city, String street, ZipCode zip) {
        this.city = city;
        this.street = street;
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public ZipCode getZip() {
        return zip;
    }

    public void setZip(ZipCode zip) {
        this.zip = zip;
    }
}
