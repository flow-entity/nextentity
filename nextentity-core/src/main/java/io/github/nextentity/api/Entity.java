package io.github.nextentity.api;

/// 实体类标记接口。
///
/// 实现此接口的实体类可以被框架识别，用于类型安全的数据库操作，
/// 并支持通过方法引用构建 {@link EntityPath} 访问嵌套属性。
///
/// 对于需要暴露主键的实体，请使用 {@link Persistable} 接口。
///
/// @author HuangChengwei
/// @see Path 路径表达式创建示例
/// @see EntityPath 嵌套属性访问
/// @since 1.0.0
public interface Entity {
}