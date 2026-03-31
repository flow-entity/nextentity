package io.github.nextentity.api;

/// Interface for persistable entities with a primary key.
///
/// Extends {@link Entity} with a primary key type parameter and provides
/// a method to access the entity's ID.
///
/// Example usage:
/// ```java
/// public class User implements Persistable<Long> {
///     private Long id;
///     private String name;
///
///     @Override
///     public Long getId() { return id; }
///     public void setId(Long id) { this.id = id; }
///     public String getName() { return name; }
///     public void setName(String name) { this.name = name; }
/// }
/// ```
///
/// @param <ID> The type of the entity's primary key
/// @author HuangChengwei
/// @since 1.0.0
public interface Persistable<ID> extends Entity {

    /// Returns the primary key of this entity.
    ///
    /// @return The entity's primary key, or null if the entity is new
    ID getId();

}