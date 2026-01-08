package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.Collection;
import java.util.List;

/**
 * 查询操作接口，提供多种查询方法。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Select<T> extends SelectFetchStep<T> {

    /**
     * 选择指定类型的投影结果。
     *
     * @param projectionType 投影类型
     * @param <R> 投影结果类型
     * @return 查询条件构建步骤
     */
    <R> SelectWhereStep<T, R> select(Class<R> projectionType);

    /**
     * 选择多个表达式的结果。
     *
     * @param paths 表达式列表
     * @return 多行查询条件构建步骤
     */
    RowsSelectWhereStep<T, Tuple> select(List<? extends TypedExpression<T, ?>> paths);

    /**
     * 选择单个表达式的结果。
     *
     * @param expression 表达式
     * @param <R> 表达式结果类型
     * @return 多行查询条件构建步骤
     */
    <R> RowsSelectWhereStep<T, R> select(TypedExpression<T, R> expression);

    /**
     * 选择单个路径的结果。
     *
     * @param path 路径
     * @param <R> 路径结果类型
     * @return 多行查询条件构建步骤
     */
    <R> RowsSelectWhereStep<T, R> select(Path<T, ? extends R> path);

    /**
     * 选择多个路径的结果。
     *
     * @param paths 路径集合
     * @return 多行查询条件构建步骤
     */
    RowsSelectWhereStep<T, Tuple> select(Collection<Path<T, ?>> paths);

    /**
     * 选择两个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B> RowsSelectWhereStep<T, Tuple2<A, B>> select(Path<T, A> a, Path<T, B> b);

    /**
     * 选择三个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C> RowsSelectWhereStep<T, Tuple3<A, B, C>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c);

    /**
     * 选择四个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D> RowsSelectWhereStep<T, Tuple4<A, B, C, D>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d);

    /**
     * 选择五个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E> RowsSelectWhereStep<T, Tuple5<A, B, C, D, E>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e);

    /**
     * 选择六个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F> RowsSelectWhereStep<T, Tuple6<A, B, C, D, E, F>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f);

    /**
     * 选择七个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G> RowsSelectWhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g);

    /**
     * 选择八个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H> RowsSelectWhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h);

    /**
     * 选择九个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param i 第九个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @param <I> 第九个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I> RowsSelectWhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i);

    /**
     * 选择十个路径的结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param i 第九个路径
     * @param j 第十个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @param <I> 第九个路径类型
     * @param <J> 第十个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I, J> RowsSelectWhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i, Path<T, J> j);

    /**
     * 选择两个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B> RowsSelectWhereStep<T, Tuple2<A, B>> select(TypedExpression<T, A> a, TypedExpression<T, B> b);

    /**
     * 选择三个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C> RowsSelectWhereStep<T, Tuple3<A, B, C>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c);

    /**
     * 选择四个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D> RowsSelectWhereStep<T, Tuple4<A, B, C, D>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d);

    /**
     * 选择五个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E> RowsSelectWhereStep<T, Tuple5<A, B, C, D, E>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e);

    /**
     * 选择六个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F> RowsSelectWhereStep<T, Tuple6<A, B, C, D, E, F>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f);

    /**
     * 选择七个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G> RowsSelectWhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g);

    /**
     * 选择八个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H> RowsSelectWhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h);

    /**
     * 选择九个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param i 第九个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @param <I> 第九个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I> RowsSelectWhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i);

    /**
     * 选择十个表达式的结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param i 第九个表达式
     * @param j 第十个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @param <I> 第九个表达式类型
     * @param <J> 第十个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I, J> RowsSelectWhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i, TypedExpression<T, J> j);

    /**
     * 选择指定类型的去重投影结果。
     *
     * @param projectionType 投影类型
     * @param <R> 投影结果类型
     * @return 查询条件构建步骤
     */
    <R> SelectWhereStep<T, R> selectDistinct(Class<R> projectionType);

    /**
     * 选择多个表达式的去重结果。
     *
     * @param paths 表达式列表
     * @return 多行查询条件构建步骤
     */
    RowsSelectWhereStep<T, Tuple> selectDistinct(List<? extends TypedExpression<T, ?>> paths);

    /**
     * 选择单个表达式的去重结果。
     *
     * @param expression 表达式
     * @param <R> 表达式结果类型
     * @return 多行查询条件构建步骤
     */
    <R> RowsSelectWhereStep<T, R> selectDistinct(TypedExpression<T, R> expression);

    /**
     * 选择单个路径的去重结果。
     *
     * @param path 路径
     * @param <R> 路径结果类型
     * @return 多行查询条件构建步骤
     */
    <R> RowsSelectWhereStep<T, R> selectDistinct(Path<T, ? extends R> path);

    /**
     * 选择多个路径的去重结果。
     *
     * @param paths 路径集合
     * @return 多行查询条件构建步骤
     */
    RowsSelectWhereStep<T, Tuple> selectDistinct(Collection<Path<T, ?>> paths);

    /**
     * 选择两个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B> RowsSelectWhereStep<T, Tuple2<A, B>> selectDistinct(Path<T, A> a, Path<T, B> b);

    /**
     * 选择三个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C> RowsSelectWhereStep<T, Tuple3<A, B, C>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c);

    /**
     * 选择四个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D> RowsSelectWhereStep<T, Tuple4<A, B, C, D>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d);

    /**
     * 选择五个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E> RowsSelectWhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e);

    /**
     * 选择六个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F> RowsSelectWhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f);

    /**
     * 选择七个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G> RowsSelectWhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g);

    /**
     * 选择八个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H> RowsSelectWhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h);

    /**
     * 选择九个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param i 第九个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @param <I> 第九个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I> RowsSelectWhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i);

    /**
     * 选择十个路径的去重结果。
     *
     * @param a 第一个路径
     * @param b 第二个路径
     * @param c 第三个路径
     * @param d 第四个路径
     * @param e 第五个路径
     * @param f 第六个路径
     * @param g 第七个路径
     * @param h 第八个路径
     * @param i 第九个路径
     * @param j 第十个路径
     * @param <A> 第一个路径类型
     * @param <B> 第二个路径类型
     * @param <C> 第三个路径类型
     * @param <D> 第四个路径类型
     * @param <E> 第五个路径类型
     * @param <F> 第六个路径类型
     * @param <G> 第七个路径类型
     * @param <H> 第八个路径类型
     * @param <I> 第九个路径类型
     * @param <J> 第十个路径类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I, J> RowsSelectWhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i, Path<T, J> j);

    /**
     * 选择两个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B> RowsSelectWhereStep<T, Tuple2<A, B>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b);

    /**
     * 选择三个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C> RowsSelectWhereStep<T, Tuple3<A, B, C>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c);

    /**
     * 选择四个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D> RowsSelectWhereStep<T, Tuple4<A, B, C, D>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d);

    /**
     * 选择五个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E> RowsSelectWhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e);

    /**
     * 选择六个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F> RowsSelectWhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f);

    /**
     * 选择七个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G> RowsSelectWhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g);

    /**
     * 选择八个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H> RowsSelectWhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h);

    /**
     * 选择九个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param i 第九个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @param <I> 第九个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I> RowsSelectWhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i);

    /**
     * 选择十个表达式的去重结果。
     *
     * @param a 第一个表达式
     * @param b 第二个表达式
     * @param c 第三个表达式
     * @param d 第四个表达式
     * @param e 第五个表达式
     * @param f 第六个表达式
     * @param g 第七个表达式
     * @param h 第八个表达式
     * @param i 第九个表达式
     * @param j 第十个表达式
     * @param <A> 第一个表达式类型
     * @param <B> 第二个表达式类型
     * @param <C> 第三个表达式类型
     * @param <D> 第四个表达式类型
     * @param <E> 第五个表达式类型
     * @param <F> 第六个表达式类型
     * @param <G> 第七个表达式类型
     * @param <H> 第八个表达式类型
     * @param <I> 第九个表达式类型
     * @param <J> 第十个表达式类型
     * @return 多行查询条件构建步骤
     */
    <A, B, C, D, E, F, G, H, I, J> RowsSelectWhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i, TypedExpression<T, J> j);

}
