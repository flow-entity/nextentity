package io.github.nextentity.api.model;

/// 4元组接口，表示包含 4 个元素的元组。
///
/// 提供类型安全的方法获取 4 个元素。
///
/// ## 使用示例
///
/// ```java
/// // 查询返回 4 元组
/// List<Tuple4<String, Integer, String, BigDecimal>> results = repository.query()
///     .select(User::getName, User::getAge, User::getDepartment, User::getSalary)
///     .getList();
///
/// // 访问元素
/// for (Tuple4<String, Integer, String, BigDecimal> tuple : results) {
///     String name = tuple.get0();           // 姓名
///     Integer age = tuple.get1();           // 年龄
///     String dept = tuple.get2();           // 部门
///     BigDecimal salary = tuple.get3();     // 薪资
/// }
/// ```
///
/// @param <A> 第一个元素类型
/// @param <B> 第二个元素类型
/// @param <C> 第三个元素类型
/// @param <D> 第四个元素类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Tuple4<A, B, C, D> extends Tuple3<A, B, C> {

    /// 获取第四个元素。
    ///
    /// @return 第四个元素
    default D get3() {
        return get(3);
    }

}