package io.github.nextentity.api;

/// 字段类型描述符构建器。
///
/// 用于创建 FieldTypeDescriptor 实例。
///
/// @author HuangChengwei
/// @since 2.2.0
public class FieldTypeDescriptorBuilder {

    private Class<?> fieldType;
    private Class<?> targetType;
    private Class<?> idType;
    private String idSourcePath = "";
    private boolean isNested = false;
    private boolean requiresJoin = false;

    /// 设置字段类型。
    ///
    /// @param fieldType 字段的 Java 类型
    /// @return 构建器
    public FieldTypeDescriptorBuilder fieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    /// 设置目标实体类型。
    ///
    /// @param targetType 引用的实体类型
    /// @return 构建器
    public FieldTypeDescriptorBuilder targetType(Class<?> targetType) {
        this.targetType = targetType;
        return this;
    }

    /// 设置 ID 类型。
    ///
    /// @param idType ID 的类型
    /// @return 构建器
    public FieldTypeDescriptorBuilder idType(Class<?> idType) {
        this.idType = idType;
        return this;
    }

    /// 设置 ID 来源路径。
    ///
    /// @param idSourcePath ID 来源路径表达式
    /// @return 构建器
    public FieldTypeDescriptorBuilder idSourcePath(String idSourcePath) {
        this.idSourcePath = idSourcePath;
        return this;
    }

    /// 设置是否嵌套。
    ///
    /// @param isNested 是否嵌套 EntityReference
    /// @return 构建器
    public FieldTypeDescriptorBuilder isNested(boolean isNested) {
        this.isNested = isNested;
        return this;
    }

    /// 设置是否需要 JOIN。
    ///
    /// @param requiresJoin 是否需要 JOIN 查询
    /// @return 构建器
    public FieldTypeDescriptorBuilder requiresJoin(boolean requiresJoin) {
        this.requiresJoin = requiresJoin;
        return this;
    }

    /// 构建 FieldTypeDescriptor 实例。
    ///
    /// @return 字段类型描述符
    public FieldTypeDescriptor build() {
        return new DefaultFieldTypeDescriptor(
                fieldType, targetType, idType, idSourcePath, isNested, requiresJoin);
    }

    /// 默认实现。
    private static class DefaultFieldTypeDescriptor implements FieldTypeDescriptor {
        private final Class<?> fieldType;
        private final Class<?> targetType;
        private final Class<?> idType;
        private final String idSourcePath;
        private final boolean isNested;
        private final boolean requiresJoin;

        DefaultFieldTypeDescriptor(Class<?> fieldType, Class<?> targetType,
                                    Class<?> idType, String idSourcePath,
                                    boolean isNested, boolean requiresJoin) {
            this.fieldType = fieldType;
            this.targetType = targetType;
            this.idType = idType;
            this.idSourcePath = idSourcePath;
            this.isNested = isNested;
            this.requiresJoin = requiresJoin;
        }

        @Override
        public Class<?> fieldType() {
            return fieldType;
        }

        @Override
        public Class<?> targetType() {
            return targetType;
        }

        @Override
        public Class<?> idType() {
            return idType;
        }

        @Override
        public String idSourcePath() {
            return idSourcePath;
        }

        @Override
        public boolean isNested() {
            return isNested;
        }

        @Override
        public boolean requiresJoin() {
            return requiresJoin;
        }
    }
}