package io.github.nextentity.core.constructor;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.jdbc.SchemaAttributePaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 值构造器选择器
///
/// @author HuangChengwei
/// @since 2.2.2
public final class EntityConstructorBuilder {

    private final Map<JoinAttribute, JoinInfo> joins = new ConcurrentHashMap<>();

    private final EntityType root;
    private final Metamodel metamodel;
    private final SchemaAttributePaths paths;

    public EntityConstructorBuilder(EntityType root, Metamodel metamodel, SchemaAttributePaths paths) {
        this.root = root;
        this.metamodel = metamodel;
        this.paths = paths;
    }


    /// 处理 SelectEntity，创建 ObjectConstructor
    ///
    /// @return ObjectConstructor 实例
    public ValueConstructor build() {
        return build(paths, root, 0);
    }

    private ValueConstructor build(SchemaAttributePaths paths, EntitySchema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();
        for (EntityAttribute attr : schema.getAttributes()) {
            if (attr instanceof EntitySchemaAttribute schemaAttribute) {
                SchemaAttributePaths sub = paths.get(schemaAttribute.name());
                if (sub != null) {
                    JoinInfo joinInfo = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute));
                    ValueConstructor constructor = build(sub, schemaAttribute.schema(), joinInfo.rightTableIndex());
                    bindings.add(new PropertyBinding(attr, constructor));
                }
            } else if (attr instanceof EntityBasicAttribute basicAttribute) {
                ValueConverter<?, ?> converter = basicAttribute.valueConvertor();
                Column column = Column.ofPath(attr.path(), converter, tableIndex);
                bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
            }
        }

        return new ObjectConstructor(schema.type(), bindings);
    }


    private JoinInfo newJoinInfo(EntitySchemaAttribute joinAttribute) {
        return new JoinInfo(JoinType.LEFT,
                0,
                joins.size() + 1,
                metamodel.getEntity(joinAttribute.getTargetEntityType().type()),
                joinAttribute.getSourceAttribute(),
                joinAttribute.getTargetAttribute());
    }
}
