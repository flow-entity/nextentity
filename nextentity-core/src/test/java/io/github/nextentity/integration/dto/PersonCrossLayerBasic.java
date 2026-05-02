package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public class PersonCrossLayerBasic {

    private Long id;
    private String name;
    @EntityPath("address.city")
    private String city;
    @EntityPath("address.street")
    private String street;
    @EntityPath("address.zip.code")
    private String zipCode;
    @EntityPath("secondaryZip.code")
    private String secondaryCode;

    public PersonCrossLayerBasic() {
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSecondaryCode() {
        return secondaryCode;
    }

    public void setSecondaryCode(String secondaryCode) {
        this.secondaryCode = secondaryCode;
    }

    @Override
    public String toString() {
        return "PersonCrossLayerBasic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", secondaryCode='" + secondaryCode + '\'' +
                '}';
    }
}
