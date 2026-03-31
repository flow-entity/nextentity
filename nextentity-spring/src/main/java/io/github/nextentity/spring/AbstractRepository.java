package io.github.nextentity.spring;

import io.github.nextentity.api.*;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.jdbc.ConnectionProvider;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.Supplier;

public abstract class AbstractRepository<T, ID> {

    protected final Class<ID> idType;
    protected final Class<T> entityType;

    protected final Select<T> queryBuilder;
    protected final UpdateExecutor updateExecutor;
    protected final JdbcTemplate jdbcTemplate;

    public AbstractRepository(Class<ID> idType,
                              Class<T> entityType,
                              Select<T> queryBuilder,
                              UpdateExecutor updateExecutor,
                              JdbcTemplate jdbcTemplate) {
        this.idType = idType;
        this.entityType = entityType;
        this.queryBuilder = queryBuilder;
        this.updateExecutor = updateExecutor;
        this.jdbcTemplate = jdbcTemplate;
    }

    protected AbstractRepository(JdbcTemplate jdbcTemplate,
                                 NextEntityFactory factory) {
        GenericType<T, ID> genericType = getGenericType();
        this.idType = genericType.idType();
        this.entityType = genericType.entityType();
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = factory.queryBuilder(entityType);
        this.updateExecutor = factory.updateExecutor();
    }

    protected Select<T> query() {
        return queryBuilder;
    }

    protected EntityRoot<T> root() {
        return EntityRoot.of();
    }

    public Class<ID> idType() {
        return idType;
    }

    public Class<T> entityType() {
        return entityType;
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
    public void updateAll(@NonNull Iterable<T> entities) {
        updateExecutor.updateAll(entities, entityType);
    }

    @Transactional
    public void update(@NonNull T entity) {
        updateExecutor.update(entity, entityType);
    }

    @Transactional
    public void deleteAll(@NonNull Iterable<T> entities) {
        updateExecutor.deleteAll(entities, entityType);
    }

    @Transactional
    public void delete(@NonNull T entity) {
        updateExecutor.delete(entity, entityType);
    }

    protected BooleanPath<T> path(PathRef.BooleanRef<T> path) {
        return Path.of(path);
    }

    protected StringPath<T> path(PathRef.StringRef<T> path) {
        return Path.of(path);
    }

    protected <U extends Number> NumberPath<T, U> path(PathRef.NumberRef<T, U> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Long> path(PathRef.LongRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Integer> path(PathRef.IntegerRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Short> path(PathRef.ShortRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Byte> path(PathRef.ByteRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Double> path(PathRef.DoubleRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, Float> path(PathRef.FloatRef<T> path) {
        return Path.of(path);
    }

    protected NumberPath<T, BigDecimal> path(PathRef.BigDecimalRef<T> path) {
        return Path.of(path);
    }

    protected <U> Path<T, U> path(PathRef<T, U> path) {
        return Path.of(path);
    }

    protected <U extends Entity> EntityPath<T, U> path(PathRef.EntityPathRef<T, U> path) {
        return Path.of(path);
    }

    protected GenericType<T, ID> getGenericType() {
        ResolvableType type = ResolvableType.forClass(getClass()).as(AbstractRepository.class);
        Class<T> entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        Class<ID> idType = TypeCastUtil.cast(type.resolveGeneric(1));
        return new GenericType<>(entityType, idType);
    }

    protected record GenericType<T, ID>(Class<T> entityType, Class<ID> idType) {
    }

    @Transactional
    protected <X> X doInTransaction(Supplier<X> command) {
        return command.get();
    }

    private static class ConnectionProviderImpl implements ConnectionProvider {
        AbstractRepository<?, ?> repository;
        JdbcTemplate jdbcTemplate;

        @Override
        public <X> X execute(ConnectionCallback<X> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

        @Override
        public <X> X executeInTransaction(ConnectionCallback<X> action) {
            return repository.doInTransaction(() -> execute(action));
        }
    }

    protected static NextEntityFactory jdbc(JdbcTemplate jdbcTemplate) {
        return DefaultNextEntityFactory.jdbc(jdbcTemplate);
    }

    protected static NextEntityFactory jpa(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        return DefaultNextEntityFactory.jpa(entityManager, jdbcTemplate);
    }

}
