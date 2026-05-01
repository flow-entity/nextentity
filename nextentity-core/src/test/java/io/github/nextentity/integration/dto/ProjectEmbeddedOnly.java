package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public class ProjectEmbeddedOnly {

    @EntityPath("address.city")
    private String city;

    public ProjectEmbeddedOnly() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
