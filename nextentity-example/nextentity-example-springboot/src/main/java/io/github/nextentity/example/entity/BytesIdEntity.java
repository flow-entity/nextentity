package io.github.nextentity.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class BytesIdEntity {

    @Id
    private UUID id;


}
