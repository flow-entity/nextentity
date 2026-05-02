package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public interface PersonAddressInfo {

    Long getId();

    String getName();

    @EntityPath("address.street")
    String getStreet();

    @EntityPath("address.city")
    String getCity();

    @EntityPath("address.zipCode")
    String getZipCode();
}
