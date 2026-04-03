package io.github.nextentity.jdbc;

///
/// 类型化参数接口
///
/// 该接口定义了带有类型信息的参数，用于在数据库操作中明确指定参数的类型和值，
/// 以便进行正确的类型转换和SQL语句构建。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface TypedParameter {

    /// 获取参数类型
    ///
    /// @return 参数的数据类型
    Class<?> type();

    /// 获取参数值
    ///
    /// @return 参数的实际值
    Object value();
}
