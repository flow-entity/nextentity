package io.github.nextentity.spring;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.annotation.SubSelect;
import io.github.nextentity.core.exception.RepositoryConfigurationException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.util.Paths;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@SubSelect("")
public abstract class AbstractRepository<T, ID extends Serializable> {

    private final Class<ID> idType;
    private final Class<T> entityType;

    private final Select<T> queryBuilder;
    private final EntityManager entityManager;
    private final UpdateExecutor updateExecutor;

    public AbstractRepository(Class<T> entityType, Class<ID> idType, EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this(entityType, idType, RepositoryArgs.jpa(entityManager, jdbcTemplate), entityManager);
    }

    public AbstractRepository(Class<T> entityType, Class<ID> idType, JdbcTemplate jdbcTemplate) {
        this(entityType, idType, RepositoryArgs.jdbc(jdbcTemplate), null);
    }

    public AbstractRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this(RepositoryArgs.jpa(entityManager, jdbcTemplate), entityManager);
    }

    public AbstractRepository(JdbcTemplate jdbcTemplate) {
        this(RepositoryArgs.jdbc(jdbcTemplate), null);
    }

    protected AbstractRepository(RepositoryArgs args, EntityManager entityManager) {
        ResolvableType type = ResolvableType.forClass(getClass()).as(AbstractRepository.class);
        Class<T> entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        Class<ID> idType = TypeCastUtil.cast(type.resolveGeneric(1));
        this.entityType = entityType;
        if (this.entityType == null) {
            throw new RepositoryConfigurationException("Cannot resolve entity type, AbstractRepository must specify generic parameter T");
        }
        this.idType = idType;
        this.queryBuilder = new QueryBuilder<>(
                args.metamodel(),
                args.queryExecutor(),
                entityType()
        );
        this.updateExecutor = args.updateExecutor();
        this.entityManager = entityManager;
    }

    protected AbstractRepository(Class<T> entityType, Class<ID> idType, RepositoryArgs args, EntityManager entityManager) {
        this.entityType = entityType;
        if (this.entityType == null) {
            throw new RepositoryConfigurationException("Cannot resolve entity type, AbstractRepository must specify generic parameter T");
        }
        this.idType = idType;
        this.queryBuilder = new QueryBuilder<>(
                args.metamodel(),
                args.queryExecutor(),
                entityType()
        );
        this.updateExecutor = args.updateExecutor();
        this.entityManager = entityManager;
    }

    protected Select<T> query() {
        return queryBuilder;
    }

    protected EntityRoot<T> root() {
        return Paths.root();
    }

    public Class<ID> idType() {
        return idType;
    }

    public Class<T> entityType() {
        return entityType;
    }


    protected EntityManager entityManager() {
        return entityManager;
    }

    @Transactional
    public void insert(@NonNull T entity) {
        updateExecutor.insert(entity, entityType);
    }

    @Transactional
    public void insertAll(@NonNull Iterable<T> entities) {
        updateExecutor.insertAll(entities, entityType);
    }

    @Transactional
    public List<T> updateAll(@NonNull Iterable<T> entities) {
        return updateExecutor.updateAll(entities, entityType);
    }

    @Transactional
    public T update(@NonNull T entity) {
        return updateExecutor.update(entity, entityType);
    }

    @Transactional
    public void deleteAll(@NonNull Iterable<T> entities) {
        updateExecutor.deleteAll(entities, entityType);
    }

    @Transactional
    public void delete(@NonNull T entity) {
        updateExecutor.delete(entity, entityType);
    }



    @Transactional
    public T patch(@NonNull T entity) {
        return updateExecutor.patch(entity, entityType);
    }

    protected void flush() {
        if (entityManager != null) {
            entityManager.flush();
        }
    }

    /**
     * Convert a Path to PathNode, preserving the full path including nested properties.
     * Uses PathNode.of() which internally uses PathReference to correctly extract
     * the field name from method reference paths.
     *
     * @param path the path to convert
     * @return PathNode representing the path
     */
    protected PathNode getPathNode(Path<?, ?> path) {
        return PathNode.of(path);
    }

    protected BooleanPath<T> path(Path.BooleanRef<T> path) {
        return new PredicateImpl<>(getPathNode(path));
    }

    protected StringPath<T> path(Path.StringRef<T> path) {
        return new StringExpressionImpl<>(getPathNode(path));
    }

    protected <U extends Number> NumberPath<T, U> path(Path.NumberRef<T, U> path) {
        return getNumberExpression(path);
    }

    private <U extends Number> @NonNull NumberExpressionImpl<T, U> getNumberExpression(Path.NumberRef<T, U> path) {
        return new NumberExpressionImpl<>(getPathNode(path));
    }

    protected NumberPath<T, Long> path(Path.LongRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Integer> path(Path.IntegerRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Short> path(Path.ShortRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Byte> path(Path.ByteRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Double> path(Path.DoubleRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, Float> path(Path.FloatRef<T> path) {
        return getNumberExpression(path);
    }

    protected NumberPath<T, BigDecimal> path(Path.BigDecimalRef<T> path) {
        return getNumberExpression(path);
    }

    protected <U> PathExpression<T, U> path(Path<T, U> path) {
        return new SimpleExpressionImpl<>(getPathNode(path));
    }


}
