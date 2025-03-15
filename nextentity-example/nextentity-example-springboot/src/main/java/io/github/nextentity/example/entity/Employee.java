package io.github.nextentity.example.entity;

import io.github.nextentity.core.Persistable;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Employee implements Persistable<Integer> {
    @Id
    private Integer id;
    private String name;
    private Integer age;
    private Integer companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", updatable = false, insertable = false)
    private Company company;
}
