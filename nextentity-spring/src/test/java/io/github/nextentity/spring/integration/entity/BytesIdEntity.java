package io.github.nextentity.spring.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BytesIdEntity {

    @Id
    private byte[] id;

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}
