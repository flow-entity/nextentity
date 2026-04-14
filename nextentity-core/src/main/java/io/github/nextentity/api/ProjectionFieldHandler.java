package io.github.nextentity.api;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 投影字段处理器接口，用于处理投影中的特殊字段类型。
///
/// ## 设计目标
/// 提供统一的扩展点，使不同类型的投影字段能够：
/// - 自定义查询表达式构建
/// - 自定义结果值映射
/// - 支持后处理逻辑（如延迟加载注入）
///
/// ## 典型实现
/// - EntityReferencePlugin - 处理 EntityReference 延迟加载字段
/// - JsonFieldPlugin - 处理 JSON 字段自动解析
/// - EncryptedFieldPlugin - 处理加密字段自动加解密
///
/// ## 使用示例
/// ```java
/// public class EntityReferencePlugin implements ProjectionFieldHandler {
///     @Override
///     public boolean supports(FieldInfo field, ProjectionContext context) {
///         return EntityReference.class.isAssignableFrom(field.type())
///             && !field.hasAnnotation(DirectReference.class);  // 排除立即加载
///     }
///
///     @Override
///     public void postProcess(Object instance, FieldInfo field, Object value, ProjectionContext ctx) {
///         if (value instanceof EntityReference ref) {
///             ref.setLoader(ctx.createLoader(field.targetType(), ref.getId()));
///         }
///     }
/// }
/// ```
///
/// @author HuangChengwei
/// @since 2.2.0
public interface ProjectionFieldHandler<T> {

    /// 判断此处理器是否支持给定的字段。
    ///
    /// 通过 FieldInfo 可以获取：
    /// - 字段类型、名称
    /// - 字段上的注解（@ReferenceId、@DirectReference 等）
    /// - 字段所属的投影类
    ///
    /// @param field 字段信息
    /// @param context 投影上下文
    /// @return 如果支持处理此字段返回 true
    boolean supports(FieldInfo field, ProjectionContext context);

    /// 解析字段的类型描述符。
    ///
    /// 描述符包含字段类型、引用实体类型、ID 类型等元数据。
    ///
    /// @param field 字段信息
    /// @param context 投影上下文
    /// @return 字段类型描述符
    FieldTypeDescriptor resolveFieldType(FieldInfo field, ProjectionContext context);

    /// 构建 SELECT 查询表达式列表。
    ///
    /// 用于从数据库查询中提取必要的数据（如 ID）。
    ///
    /// @param field 字段信息
    /// @param descriptor 字段类型描述符
    /// @param context 投影上下文
    /// @return SELECT 表达式列表（通常只包含 ID 字段）
    default List<ExpressionNode> buildSelectExpressions(FieldInfo field, FieldTypeDescriptor descriptor, ProjectionContext context) {
        return List.of();
    }

    /// 从查询结果映射字段值。
    ///
    /// @param field 字段信息
    /// @param arguments 查询结果参数迭代器
    /// @param context 投影上下文
    /// @param descriptor 字段类型描述符
    /// @return 映射后的字段值（如 EntityReference 实例）
    default Object mapValue(FieldInfo field, Arguments arguments, ProjectionContext context, FieldTypeDescriptor descriptor) {
        return null;
    }

    /// 后处理已构建的投影对象。
    ///
    /// 在所有字段映射完成后调用，用于注入延迟加载器等后处理逻辑。
    ///
    /// @param instance 投影对象实例
    /// @param field 字段信息
    /// @param value 已映射的字段值
    /// @param context 投影上下文
    default void postProcess(Object instance, FieldInfo field, Object value, ProjectionContext context) {
    }

    /// 处理器优先级。
    ///
    /// 数值越小优先级越高，越先被匹配和处理。
    /// 默认优先级为 100，允许高优先级处理器（如 ID 字段）使用较小的值。
    ///
    /// @return 优先级数值
    default int order() {
        return 100;
    }
}