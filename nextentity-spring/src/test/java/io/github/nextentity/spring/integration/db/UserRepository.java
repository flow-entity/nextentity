package io.github.nextentity.spring.integration.db;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.*;
import io.github.nextentity.core.EntityTemplateFactory;
import io.github.nextentity.spring.AbstractRepository;
import io.github.nextentity.spring.integration.entity.User;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// 用户仓库类
///
/// @author HuangChengwei
public class UserRepository extends AbstractRepository<User, Integer> {

    private List<User> users;
    private String name;

    /// 创建 Repository 实例。
    ///
    /// 通过构造器注入 EntityContext，自动检测实体类型和主键类型，
    /// 并初始化查询构建器和更新执行器。
    ///
    /// @param context EntityContext 实体上下文
    protected UserRepository(EntityTemplateFactory context) {
        super(context);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void delete(@NonNull Iterable<User> entities) {
        super.deleteAll(entities);
    }

    public void insert(@NonNull Iterable<User> entities) {
        super.insertAll(entities);
    }

    public void update(@NonNull Iterable<User> entities) {
        super.updateAll(entities);
    }

    public EntityQuery<User> query() {
        return super.query();
    }

    public EntityQuery<User> getQuery() {
        return super.query();
    }

    public <R> BaseWhereStep<User, R> select(Class<R> projectionType) {
        return getQuery().select(projectionType);
    }

    public User first() {
        return getQuery().first();
    }

    public long count() {
        return getQuery().count();
    }

    public List<User> getList(int offset, int maxResult, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, maxResult);
    }

    public List<User> getList(int offset, int maxResult) {
        return getQuery().list(offset, maxResult);
    }

    public boolean exists() {
        return getQuery().exists();
    }

    public boolean exist() {
        return getQuery().exists();
    }

    public User getSingle(int offset) {
        return getQuery().list(offset, 1).stream().findFirst().orElse(null);
    }

    public User getSingle(int offset, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, 1).stream().findFirst().orElse(null);
    }

    public User getFirst(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).first();
    }

    public User getSingle(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).single();
    }

    public User single() {
        return getQuery().single();
    }

    public User single(int offset) {
        return getQuery().list(offset, 1).stream().findFirst().orElse(null);
    }

    public User first(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).first();
    }

    public List<User> limit(int limit) {
        return getQuery().list(limit);
    }

    public Collector<User> lock(LockModeType lockModeType) {
        return getQuery().lock(lockModeType);
    }

    public User single(int offset, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, 1).stream().findFirst().orElse(null);
    }

    public Slice<User> slice(int offset, int limit) {
        return getQuery().slice(offset, limit);
    }

    public io.github.nextentity.spring.integration.domain.Page<User> getPage(
            io.github.nextentity.spring.integration.domain.Pageable<User> pageable) {
        Slice<User> slice = getQuery().slice(pageable.offset(), pageable.getSize());
        return new io.github.nextentity.spring.integration.domain.Page<>(slice.data(), slice.total(), pageable);
    }

    public boolean exist(int offset) {
        return !getQuery().list(offset, 1).isEmpty();
    }

    public User getFirst(int offset, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, 1).stream().findFirst().orElse(null);
    }

    public List<User> getList() {
        return getQuery().list();
    }

    public List<User> getList(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list();
    }

    public <X> SubQueryBuilder<X, User> asSubQuery() {
        return getQuery().toSubQuery();
    }

    public List<User> offset(int offset, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, Integer.MAX_VALUE);
    }

    public User first(int offset) {
        return getQuery().list(offset, 1).stream().findFirst().orElse(null);
    }

    public User single(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).single();
    }

    public User requireSingle(LockModeType lockModeType) {
        return getQuery().lock(lockModeType).single();
    }

    public List<User> limit(int limit, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(limit);
    }

    public User getFirst() {
        return getQuery().first();
    }

    public User getSingle() {
        return getQuery().single();
    }

    public User first(int offset, LockModeType lockModeType) {
        return getQuery().lock(lockModeType).list(offset, 1).stream().findFirst().orElse(null);
    }

    public User getFirst(int offset) {
        return getQuery().list(offset, 1).stream().findFirst().orElse(null);
    }

    public BaseWhereStep<User, User> where(Expression<User, Boolean> predicate) {
        return getQuery().where(predicate);
    }

    public <N> ExpressionBuilder.PathOperator<User, N, ? extends BaseWhereStep<User, User>> where(PathRef<User, N> path) {
        return getQuery().where(path);
    }

    public <N extends Number> ExpressionBuilder.NumberOperator<User, N, ? extends BaseWhereStep<User, User>> where(PathRef.NumberRef<User, N> path) {
        return getQuery().where(path);
    }

    public ExpressionBuilder.StringOperator<User, ? extends BaseWhereStep<User, User>> where(PathRef.StringRef<User> path) {
        return getQuery().where(path);
    }

    public <N> ExpressionBuilder.PathOperator<User, N, ? extends BaseWhereStep<User, User>> where(Path<User, N> path) {
        return getQuery().where(path);
    }

    public <N extends Number> ExpressionBuilder.NumberOperator<User, N, ? extends BaseWhereStep<User, User>> where(NumberPath<User, N> path) {
        return getQuery().where(path);
    }

    public ExpressionBuilder.StringOperator<User, ? extends BaseWhereStep<User, User>> where(StringPath<User> path) {
        return getQuery().where(path);
    }

    public Collector<User> orderBy(List<? extends Order<User>> orders) {
        return getQuery().orderBy(orders);
    }

    public Collector<User> orderBy(Order<User> order) {
        return getQuery().orderBy(order);
    }

    public Collector<User> orderBy(Order<User> p0, Order<User> p1) {
        return getQuery().orderBy(p0, p1);
    }

    public Collector<User> orderBy(Order<User> order1, Order<User> order2, Order<User> order3) {
        return getQuery().orderBy(order1, order2, order3);
    }

    public OrderOperator<User, User> orderBy(Collection<PathRef<User, ? extends Comparable<?>>> paths) {
        return getQuery().orderBy(paths);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> path) {
        return getQuery().orderBy(path);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> p1, PathRef<User, ? extends Comparable<?>> p2) {
        return getQuery().orderBy(p1, p2);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> p1, PathRef<User, ? extends Comparable<?>> p2, PathRef<User, ? extends Comparable<?>> p3) {
        return getQuery().orderBy(p1, p2, p3);
    }

    public WhereStep<User, Tuple> select(Collection<? extends Expression<User, ?>> paths) {
        return getQuery().select(paths);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g) {
        return getQuery().select(a, b, c, d, e, f, g);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f) {
        return getQuery().selectDistinct(a, b, c, d, e, f);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h) {
        return getQuery().select(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d) {
        return getQuery().selectDistinct(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i, PathRef<User, J> j) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i, Expression<User, J> j) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e) {
        return getQuery().selectDistinct(a, b, c, d, e);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e) {
        return getQuery().selectDistinct(a, b, c, d, e);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c) {
        return getQuery().select(a, b, c);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> selectDistinct(Expression<User, A> a, Expression<User, B> b) {
        return getQuery().selectDistinct(a, b);
    }

    public <R> WhereStep<User, R> select(Expression<User, R> expression) {
        return getQuery().select(expression);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d) {
        return getQuery().select(a, b, c, d);
    }

    public <R> WhereStep<User, R> select(PathRef<User, R> path) {
        return getQuery().select(path);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f) {
        return getQuery().select(a, b, c, d, e, f);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i, Expression<User, J> j) {
        return getQuery().select(a, b, c, d, e, f, g, h, i, j);
    }

    public WhereStep<User, Tuple> selectDistinct(Collection<? extends Expression<User, ?>> paths) {
        return getQuery().selectDistinct(paths);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h) {
        return getQuery().select(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f) {
        return getQuery().selectDistinct(a, b, c, d, e, f);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> select(PathRef<User, A> a, PathRef<User, B> b) {
        return getQuery().select(a, b);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c) {
        return getQuery().select(a, b, c);
    }

    public <R> WhereStep<User, R> selectDistinct(PathRef<User, R> path) {
        return getQuery().selectDistinct(path);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b) {
        return getQuery().selectDistinct(a, b);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i, PathRef<User, J> j) {
        return getQuery().select(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c) {
        return getQuery().selectDistinct(a, b, c);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d) {
        return getQuery().select(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i) {
        return getQuery().select(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d) {
        return getQuery().selectDistinct(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i) {
        return getQuery().select(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e) {
        return getQuery().select(a, b, c, d, e);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> select(Expression<User, A> a, Expression<User, B> b) {
        return getQuery().select(a, b);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f) {
        return getQuery().select(a, b, c, d, e, f);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e) {
        return getQuery().select(a, b, c, d, e);
    }

    public <R> BaseWhereStep<User, R> selectDistinct(Class<R> projectionType) {
        return getQuery().selectDistinct(projectionType);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c) {
        return getQuery().selectDistinct(a, b, c);
    }

    public <R> WhereStep<User, R> selectDistinct(Expression<User, R> expression) {
        return getQuery().selectDistinct(expression);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g) {
        return getQuery().select(a, b, c, d, e, f, g);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> path) {
        return getQuery().fetch(path);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> p0, Path<User, ?> p1) {
        return getQuery().fetch(p0, p1);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> p0, Path<User, ?> p1, Path<User, ?> p2) {
        return getQuery().fetch(p0, p1, p2);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> path) {
        return getQuery().fetch(path);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> p0, PathRef<User, ?> p1) {
        return getQuery().fetch(p0, p1);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> p0, PathRef<User, ?> p1, PathRef<User, ?> p2) {
        return getQuery().fetch(p0, p1, p2);
    }

    public BaseWhereStep<User, User> fetch(Collection<? extends PathRef<User, ?>> pathExpressions) {
        return getQuery().fetch(pathExpressions);
    }

    public List<User> users() {
        if (users == null) {
            List<User> list = query().orderBy(User::getId).asc().list();
            Map<Integer, User> map = list.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            for (User user : list) {
                Integer pid = user.getPid();
                User p = map.get(pid);
                user.setParentUser(p);
                user.setRandomUser(map.get(user.getRandomNumber()));
                user.setTestUser(map.get(user.getTestInteger()));
            }
            users = list;
        }
        return users;
    }

    public void clear() {
        users = null;
    }

    @Transactional
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public String toString() {
        return name;
    }
}


