package io.github.nextentity.spring;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Repository;
import io.github.nextentity.api.TypedExpression.BooleanPathExpression;
import io.github.nextentity.api.TypedExpression.NumberPathExpression;
import io.github.nextentity.api.TypedExpression.StringPathExpression;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.impl.*;
import io.github.nextentity.core.meta.EntityType;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
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

    protected abstract class $String implements StringPathExpression<T> {
    }

    private class $StringImpl extends $String implements AbstractInternalPathExpression, AbstractStringExpression<T> {
        private final String[] paths;

        private $StringImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Boolean implements BooleanPathExpression<T> {
    }

    private class $BooleanImpl extends $Boolean implements AbstractInternalPathExpression, AbstractBooleanExpression<T> {
        private final String[] paths;

        private $BooleanImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Number<X extends Number> implements NumberPathExpression<T, X> {
    }

    private class $NumberImpl<X extends Number> extends $Number<X> implements AbstractInternalPathExpression, AbstractNumberExpression<T, X> {
        private final String[] paths;

        private $NumberImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Long extends $Number<Long> {
    }

    private class $LongImpl extends $Long implements AbstractInternalPathExpression, AbstractNumberExpression<T, Long> {
        private final String[] paths;

        private $LongImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Integer extends $Number<Integer> {
    }

    private class $IntegerImpl extends $Integer implements AbstractInternalPathExpression, AbstractNumberExpression<T, Integer> {
        private final String[] paths;

        private $IntegerImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Short extends $Number<Short> {
    }

    private class $ShortImpl extends $Short implements AbstractInternalPathExpression, AbstractNumberExpression<T, Short> {
        private final String[] paths;

        private $ShortImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Byte extends $Number<Byte> {
    }

    private class $ByteImpl extends $Byte implements AbstractInternalPathExpression, AbstractNumberExpression<T, Byte> {
        private final String[] paths;

        private $ByteImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Double extends $Number<Double> {
    }

    private class $DoubleImpl extends $Double implements AbstractInternalPathExpression, AbstractNumberExpression<T, Double> {
        private final String[] paths;

        private $DoubleImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Float extends $Number<Float> {
    }

    private class $FloatImpl extends $Float implements AbstractInternalPathExpression, AbstractNumberExpression<T, Float> {
        private final String[] paths;

        private $FloatImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $BigDecimal extends $Number<BigDecimal> {
    }

    private class $BigDecimalImpl extends $BigDecimal implements AbstractInternalPathExpression, AbstractNumberExpression<T, BigDecimal> {
        private final String[] paths;

        private $BigDecimalImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Entity<X> implements AbstractEntityPath<T, X> {
    }

    private class $EntityImpl<X> extends $Entity<X> implements AbstractInternalPathExpression {
        private final String[] paths;

        private $EntityImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected abstract class $Path<X> implements AbstractPathExpression<T, X> {
    }

    private class PathImpl<X> extends $Path<X> implements AbstractInternalPathExpression {
        private final String[] paths;

        private PathImpl(String[] paths) {
            this.paths = paths;
        }

        @Override
        public String[] paths() {
            return paths;
        }
    }

    protected $Boolean $(Path.BooleanPath<T> path) {
        return new $BooleanImpl(paths(path));
    }

    protected $String $(Path.StringPath<T> path) {
        return new $StringImpl(paths(path));
    }

    protected <U extends Number> $Number<U> $(Path.NumberPath<T, U> path) {
        return new $NumberImpl<>(paths(path));
    }

    protected $Long $(Path.LongPath<T> path) {
        return new $LongImpl(paths(path));
    }

    protected $Integer $(Path.IntegerPath<T> path) {
        return new $IntegerImpl(paths(path));
    }

    protected $Short $(Path.ShortPath<T> path) {
        return new $ShortImpl(paths(path));
    }

    protected $Byte $(Path.BytePath<T> path) {
        return new $ByteImpl(paths(path));
    }

    protected $Double $(Path.DoublePath<T> path) {
        return new $DoubleImpl(paths(path));
    }

    protected $Float $(Path.FloatPath<T> path) {
        return new $FloatImpl(paths(path));
    }

    protected $BigDecimal $(Path.BigDecimalPath<T> path) {
        return new $BigDecimalImpl(paths(path));
    }

    protected <U> $Path<U> $(Path<T, U> path) {
        return new PathImpl<>(paths(path));
    }

    protected <U> $Entity<U> entity(Path<T, U> path) {
        return new $EntityImpl<>(paths(path));
    }

    private String[] paths(Path<?, ?> path) {
        return new String[]{ExpressionImpls.attributeName(path)};
    }
}
