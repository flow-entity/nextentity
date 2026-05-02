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
        return build(paths, root, true);
    }

    private ValueConstructor build(SchemaAttributePaths paths, EntitySchema schema, boolean root) {
        List<PropertyBinding> bindings = new ArrayList<>();
        for (EntityAttribute attr : schema.getAttributes()) {
            if (attr instanceof EntitySchemaAttribute schemaAttribute) {
                if (paths.get(schemaAttribute.name()) != null) {
                    SchemaAttributePaths sub = paths.get(schemaAttribute.name());
                    ValueConstructor constructor = build(sub, schemaAttribute.schema(), false);
                    bindings.add(new PropertyBinding(attr, constructor));
                }
            } else if (attr instanceof EntityEmbeddedAttribute embeddedAttribute) {
                SchemaAttributePaths sub = DeepLimitSchemaAttributePaths.of(1);
                ValueConstructor constructor = build(sub, embeddedAttribute.schema(), false);
                bindings.add(new PropertyBinding(attr, constructor));
            } else if (attr instanceof EntityBasicAttribute basicAttribute) {
                SelectItem column = SelectItem.of(basicAttribute);
                bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
            }
        }
        if (schema.type().isInterface()) {
            return new JdkProxyConstructor(schema.type(), bindings, root);
        } else if (schema.type().isRecord()) {
            throw new UnsupportedOperationException(
                    "Record type '" + schema.type() + "' is not supported in entity constructor, use RecordConstructor instead");
        } else {
            return new ObjectConstructor(schema.type(), bindings, root);
        }

    }


}
