package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.EntityPath;

public record PersonAddressRecord(
        Long id,
        String name,
        @EntityPath("address.street") String street,
        @EntityPath("address.city") String city,
        @EntityPath("address.zipCode") String zipCode
) {
}
