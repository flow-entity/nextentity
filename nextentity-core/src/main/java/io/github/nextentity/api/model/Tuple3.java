package io.github.nextentity.api.model;

/// 3元组接口，表示包含 3 个元素的元组。
///
/// 提供类型安全的方法获取 3 个元素。
///
/// ## 使用示例
///
/// ```java
/// // 查询返回 3 元组
/// List<Tuple3<String, Integer, String>> results = repository.query()
///     .select(User::getName, User::getAge, User::getDepartment)
///     .getList();
///
/// // 访问元素
/// for (Tuple3<String, Integer, String> tuple : results) {
///     String name = tuple.get0();       // 姓名
///     Integer age = tuple.get1();       // 年龄
///     String dept = tuple.get2();       // 部门
/// }
/// ```
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple3<A, B, C> extends Tuple2<A, B> {
    /// 获取第三个元素。
    ///
    /// @return 第三个元素
    default C get2() {
        return get(2);
    }
}