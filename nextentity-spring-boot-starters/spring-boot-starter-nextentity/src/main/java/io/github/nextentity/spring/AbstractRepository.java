package io.github.nextentity.spring;

import io.github.nextentity.api.Repository;
import io.github.nextentity.api.StringPath;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.meta.EntityType;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository<T, ID extends Serializable> {

    protected final Repository<ID, T> repository;
    protected final Class<ID> idType;
    protected final Class<T> entityType;
    protected final EntityRoot<T> root;

    protected AbstractRepository(RepositoryFactory repositoryFactory) {
        ResolvableType type = ResolvableType.forClass(getClass()).as(AbstractJdbcRepository.class);
        this.entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        if (entityType == null) {
            throw new RuntimeException();
        }
        idType = TypeCastUtil.cast(type.resolveGeneric(1));
        EntityType entity = repositoryFactory.metamodel().getEntity(entityType);
        if (idType != entity.id().type()) {
            throw new RuntimeException();
        }
        repository = repositoryFactory.getRepository(entityType);
        root = repository.root();
    }

    protected Repository<ID, T> repository() {
        return getJdbcRepository();
    }

    protected Repository<ID, T> getJdbcRepository() {
        return repository;
    }

    protected Class<ID> getIdType() {
        return idType;
    }

    protected Class<T> getEntityType() {
        return entityType;
    }

    @Transactional
    public void insert(T entity) {
        repository().insert(entity);
    }

    @Transactional
    public T update(T entity) {
        return repository().update(entity);
    }

    @Transactional
    public void delete(T entity) {
        repository().delete(entity);
    }

    @Transactional
    public void insertAll(Iterable<T> entities) {
        repository().insert(entities);
    }

    @Transactional
    public List<T> updateAll(Iterable<T> entities) {
        return repository().update(entities);
    }

    @Transactional
    public void deleteAll(Iterable<T> entities) {
        repository().delete(entities);
    }

    public T findById(ID id) {
        return repository.get(id);
    }

    public List<T> findByIds(Iterable<? extends ID> ids) {
        return repository.getAll(ids);
    }

    public Map<ID, T> findMapByIds(Iterable<? extends ID> ids) {
        return repository.getMap(ids);
    }

    protected abstract class String_ implements StringPath<T> {
    }

}
