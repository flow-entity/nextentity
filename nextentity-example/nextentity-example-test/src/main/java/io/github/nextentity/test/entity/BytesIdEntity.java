package io.github.nextentity.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class BytesIdEntity {

    @Id
    private byte[] id;

}
