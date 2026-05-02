package io.github.nextentity.integration.dto;

public class PersonWithNestedAddressEmbedded {

    private Long id;
    private String name;
    private AddressDto address;

    public PersonWithNestedAddressEmbedded() {
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

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonWithNestedAddressEmbedded{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
