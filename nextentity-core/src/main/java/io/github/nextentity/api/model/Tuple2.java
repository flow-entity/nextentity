package io.github.nextentity.api.model;

/// 2元组接口，表示包含 2 个元素的元组。
///
/// 提供类型安全的方法获取 2 个元素。
///
/// ## 使用示例
///
/// ```java
/// // 查询返回 2 元组
/// List<Tuple2<String, Integer>> results = repository.query()
///     .select(User::getName, User::getAge)
///     .getList();
///
/// // 访问元素
/// for (Tuple2<String, Integer> tuple : results) {
///     String name = tuple.get0();  // 第一个元素（姓名）
///     Integer age = tuple.get1();  // 第二个元素（年龄）
///     System.out.println(name + ": " + age);
/// }
/// ```
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple2<A, B> extends Tuple {

    /// 获取第一个元素。
    ///
    /// @return 第一个元素
    default A get0() {
        return get(0);
    }

    /// 获取第二个元素。
    ///
    /// @return 第二个元素
    default B get1() {
        return get(1);
    }

}