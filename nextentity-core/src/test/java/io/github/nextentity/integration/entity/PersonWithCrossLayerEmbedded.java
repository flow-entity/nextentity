package io.github.nextentity.integration.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PersonWithCrossLayerEmbedded {

    @Id
    private Long id;

    private String name;

    @Embedded
    private AddressWithZip address;

    @Embedded
    @AttributeOverride(name = "code", column = @Column(name = "alt_code"))
    private ZipCode secondaryZip;

    public PersonWithCrossLayerEmbedded() {
    }

    public PersonWithCrossLayerEmbedded(Long id, String name, AddressWithZip address, ZipCode secondaryZip) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.secondaryZip = secondaryZip;
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

    public AddressWithZip getAddress() {
        return address;
    }

    public void setAddress(AddressWithZip address) {
        this.address = address;
    }

    public ZipCode getSecondaryZip() {
        return secondaryZip;
    }

    public void setSecondaryZip(ZipCode secondaryZip) {
        this.secondaryZip = secondaryZip;
    }
}
