package io.github.nextentity.data;

import io.github.nextentity.api.Repository;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public abstract class AbstractRepository<ID extends Serializable, T> {

    protected Repository<ID, T> repository;

    @Autowired
    protected void init(RepositoryFactory entitiesFactory) {
        ResolvableType type = ResolvableType.forClass(getClass())
                .as(AbstractRepository.class);
        Class<T> entityType = TypeCastUtil.cast(type.resolveGeneric(1));
        Objects.requireNonNull(entityType);
        Class<?> idType = type.resolveGeneric(0);
        EntityType entity = entitiesFactory.getMetamodel().getEntity(entityType);
        EntityAttribute id = entity.id();
        Class<?> expected = ClassUtils.resolvePrimitiveIfNecessary(id.type());
        if (expected != idType) {
            String msg = "id class defined in " + getClass() + " does not match," +
                         " expected id " + expected + ", actual id " + idType;
            throw new EntityIdTypeMismatchException(msg);
        }

        this.repository = entitiesFactory.getRepository(entityType);
    }

    public void insert(@NotNull T entity) {
        repository.insert(entity);
    }

    public void insertAll(@NotNull Iterable<T> entities) {
        repository.insert(entities);
    }

    public List<T> updateAll(@NotNull Iterable<T> entities) {
        return repository.update(entities);
    }

    public T update(@NotNull T entity) {
        return repository.update(entity);
    }

    public T get(ID id) {
        return repository.get(id);
    }

    public List<T> getAll(Iterable<ID> ids) {
        return repository.getAll(ids);
    }

}