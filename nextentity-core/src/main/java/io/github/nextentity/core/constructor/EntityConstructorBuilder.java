package io.github.nextentity.core.constructor;

import io.github.nextentity.core.meta.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 实体构造器构建器
///
/// 根据 EntityType 及 SchemaAttributePaths 构建实体类型的 ValueConstructor。
/// 支持接口类型（JDK 代理）和普通类（构造函数 + setter），
/// 不支持 Record 类型（应使用 RecordConstructor）。
///
/// @author HuangChengwei
/// @since 2.2.2
public final class EntityConstructorBuilder {

    private final Map<JoinAttribute, JoinIndex> joins = new ConcurrentHashMap<>();

    private final EntityType root;
    private final Metamodel metamodel;
    private final SchemaAttributePaths paths;

    public EntityConstructorBuilder(EntityType root, Metamodel metamodel, SchemaAttributePaths paths) {
        this.root = root;
        this.metamodel = metamodel;
        this.paths = paths;
    }


    /// 处理 SelectEntity，根据类型创建对应的 ValueConstructor
    ///
    /// @return ValueConstructor 实例
    public ValueConstructor build() {
        return build(paths, root, 0);
    }

    private ValueConstructor build(SchemaAttributePaths paths, EntitySchema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();
        for (EntityAttribute attr : schema.getAttributes()) {
            if (attr instanceof EntitySchemaAttribute schemaAttribute) {
                SchemaAttributePaths sub = paths.get(schemaAttribute.name());
                if (sub != null) {
                    JoinIndex joinIndex = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    ValueConstructor constructor = build(sub, schemaAttribute.schema(), joinIndex.rightTableIndex());
                    bindings.add(new PropertyBinding(attr, constructor));
                }
            } else if (attr instanceof EntityBasicAttribute basicAttribute) {
                ValueConverter<?, ?> converter = basicAttribute.valueConvertor();
                Column column = new Column(attr.path(), converter, tableIndex);
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


    private JoinIndex newJoinInfo(JoinAttribute joinAttribute, int leftTableIndex) {
        return new JoinIndex(JoinType.LEFT,
                leftTableIndex,
                joins.size() + 1,
                metamodel.getEntity(joinAttribute.getTargetEntityType().type()),
                joinAttribute.getSourceAttribute(),
                joinAttribute.getTargetAttribute());
    }
}
