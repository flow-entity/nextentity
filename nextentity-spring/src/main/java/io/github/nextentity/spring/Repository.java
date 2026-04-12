package io.github.nextentity.spring;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.api.UpdateSetStep;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Repository<T, ID> {
    EntityQuery<T> query();

    @Transactional
    void insert(T entity);

    @Transactional
    void insertAll(@NonNull Iterable<T> entities);

    @Transactional
    void updateAll(@NonNull Iterable<T> entities);

    @Transactional
    void update(T entity);

    @Transactional
    void deleteAll(@NonNull Iterable<T> entities);

    @Transactional
    void delete(T entity);

    @Transactional
    UpdateSetStep<T> update();

    @Transactional
    DeleteWhereStep<T> delete();

    Optional<T> findById(ID id);

    T getById(ID id);

    List<T> findAllById(@NonNull Collection<? extends ID> ids);

    Map<ID, T> findMapById(@NonNull Collection<? extends ID> ids);

    Map<ID, T> findMapAll();

    boolean existsById(ID id);

    @Transactional
    void deleteById(ID id);

    @Transactional
    void deleteAllById(@NonNull Collection<? extends ID> ids);
}
