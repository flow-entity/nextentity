package io.github.nextentity.spring;

import io.github.nextentity.api.Persistable;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.Select;
import io.github.nextentity.core.UpdateExecutor;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Abstract repository for persistable entities with ID-based query methods.
///
/// Extends AbstractRepository with additional methods for querying by ID,
/// since entities implementing {@link Persistable} expose their primary key.
///
/// Example usage:
/// ```java
/// public class UserRepository extends PersistableRepository<User, Long> {
///     // ID-based methods are automatically available
///     public User findById(Long id) { return getById(id); }
///     public List<User> findByIds(Collection<Long> ids) { return getAllById(ids); }
/// }
/// ```
///
/// @param <T>  Entity type (must implement Persistable)
/// @param <ID> Primary key type
/// @author HuangChengwei
/// @since 1.0.0
public abstract class PersistableRepository<T extends Persistable<ID>, ID> extends AbstractRepository<T, ID> {

    public PersistableRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public PersistableRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    public PersistableRepository(Class<ID> idType,
                                 Class<T> entityType,
                                 Select<T> queryBuilder,
                                 UpdateExecutor updateExecutor,
                                 JdbcTemplate jdbcTemplate) {
        super(idType, entityType, queryBuilder, updateExecutor, jdbcTemplate);
    }

    protected PersistableRepository(JdbcTemplate jdbcTemplate, NextEntityFactory factory) {
        super(jdbcTemplate, factory);
    }

    /// Returns the path reference for the ID attribute.
    ///
    /// Default implementation uses Persistable::getId.
    /// Subclasses can override if needed.
    ///
    /// @return Path reference for the ID attribute
    protected PathRef<T, ID> idPath() {
        return Persistable::getId;
    }

    /// Finds an entity by its primary key.
    ///
    /// @param id Primary key value
    /// @return Optional containing the entity, or empty if not found
    public Optional<T> findById(ID id) {
        return query().where(idPath()).eq(id).first();
    }

    /// Gets an entity by its primary key.
    ///
    /// @param id Primary key value
    /// @return The entity, or null if not found
    public T getById(ID id) {
        return query().where(idPath()).eq(id).getFirst();
    }

    /// Finds all entities by their primary keys.
    ///
    /// @param ids Collection of primary key values
    /// @return List of entities with matching IDs
    public List<T> findAllById(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return query().where(idPath()).in(ids).getList();
    }

    /// Gets all entities by their primary keys.
    ///
    /// @param ids Collection of primary key values
    /// @return List of entities with matching IDs
    public List<T> getAllById(@NonNull Collection<ID> ids) {
        return findAllById(ids);
    }

    /// Finds all entities by their primary keys and returns a map keyed by ID.
    ///
    /// @param ids Collection of primary key values
    /// @return Map of ID to entity
    public Map<ID, T> findMapById(@NonNull Collection<ID> ids) {
        List<T> entities = findAllById(ids);
        return entities.stream()
                .collect(Collectors.toMap(Persistable::getId, Function.identity()));
    }

    /// Finds all entities and returns a map keyed by ID.
    ///
    /// @return Map of ID to entity
    public Map<ID, T> findMapAll() {
        return query().getList()
                .stream()
                .collect(Collectors.toMap(Persistable::getId, Function.identity()));
    }

    /// Checks if an entity exists by its primary key.
    ///
    /// @param id Primary key value
    /// @return true if entity exists, false otherwise
    public boolean existsById(ID id) {
        return query().where(idPath()).eq(id).exist();
    }

    /// Counts the number of entities with the given IDs.
    ///
    /// @param ids Collection of primary key values
    /// @return Number of existing entities
    public long countById(@NonNull Collection<ID> ids) {
        if (ids.isEmpty()) {
            return 0;
        }
        return query().where(idPath()).in(ids).count();
    }

    /// Deletes an entity by its primary key.
    ///
    /// @param id Primary key value
    @Transactional
    public void deleteById(ID id) {
        T entity = getById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    /// Deletes all entities by their primary keys.
    ///
    /// @param ids Collection of primary key values
    @Transactional
    public void deleteAllById(@NonNull Collection<ID> ids) {
        List<T> entities = findAllById(ids);
        if (!entities.isEmpty()) {
            deleteAll(entities);
        }
    }

}