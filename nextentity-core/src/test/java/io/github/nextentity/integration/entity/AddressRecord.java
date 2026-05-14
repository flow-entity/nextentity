package io.github.nextentity.integration.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record AddressRecord(String street, String city, String zipCode) {
}
