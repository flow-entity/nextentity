package io.github.nextentity.jdbc;

///
/// 空值参数记录类
///
/// 该类用于表示一个具有特定类型的空值参数，在SQL构建过程中用于处理NULL值，
/// 同时保留参数的类型信息以便正确设置参数类型。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public record NullParameter(Class<?> type) implements TypedParameter {

    /// 获取参数的字符串表示形式
    ///
    /// @return 包含类型名称的字符串表示，格式为"(类型名)null"
    @Override
    public String toString() {
        return "(" + type.getSimpleName() + ")null";
    }

    /// 获取参数值
    ///
    /// @return 总是返回null，因为这是一个空值参数
    @Override
    public Object value() {
        return null;
    }
}
