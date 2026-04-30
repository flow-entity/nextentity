package io.github.nextentity.core.constructor;

import io.github.nextentity.core.meta.*;

import java.util.ArrayList;
import java.util.List;

/// 实体构造器构建器
///
/// 根据 EntityType 及 SchemaAttributePaths 构建实体类型的 ValueConstructor。
/// 支持接口类型（JDK 代理）和普通类（构造函数 + setter），
/// 不支持 Record 类型（应使用 RecordConstructor）。
///
/// @author HuangChengwei
/// @since 2.2.2
public final class EntityConstructorBuilder {

    private final EntityType root;
    private final SchemaAttributePaths paths;

    public EntityConstructorBuilder(EntityType root, SchemaAttributePaths paths) {
        this.root = root;
        this.paths = paths;
    }


    /// 处理 SelectEntity，根据类型创建对应的 ValueConstructor
    ///
    /// @return ValueConstructor 实例
    public ValueConstructor build() {
        return build(paths, root);
    }

    private ValueConstructor build(SchemaAttributePaths paths, EntitySchema schema) {
        List<PropertyBinding> bindings = new ArrayList<>();
        for (EntityAttribute attr : schema.getAttributes()) {
            if (attr instanceof EntitySchemaAttribute schemaAttribute) {
                boolean isEmbedded = schemaAttribute.schema().isEmbedded();
                // 嵌入属性与主表共享同一张表，必须始终构造（不受 fetch 路径限制）
                if (isEmbedded || paths.get(schemaAttribute.name()) != null) {
                    SchemaAttributePaths sub = isEmbedded
                            ? DeepLimitSchemaAttributePaths.of(1)
                            : paths.get(schemaAttribute.name());
                    ValueConstructor constructor = build(sub, schemaAttribute.schema());
                    bindings.add(new PropertyBinding(attr, constructor));
                }
            } else if (attr instanceof EntityBasicAttribute basicAttribute) {
                SelectItem column = SelectItem.of(basicAttribute);
                bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
            }
        }
        if (schema.type().isInterface()) {
            return new JdkProxyConstructor(schema.type(), bindings);
        } else if (schema.type().isRecord()) {
            throw new UnsupportedOperationException(
                    "Record type '" + schema.type() + "' is not supported in entity constructor, use RecordConstructor instead");
        } else {
            return new ObjectConstructor(schema.type(), bindings);
        }

    }


}
