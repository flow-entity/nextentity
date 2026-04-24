package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.meta.*;
import jakarta.persistence.FetchType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectionConstructorBuilder {

    private final Map<JoinAttribute, JoinInfo> joins = new ConcurrentHashMap<>();

    private final QueryConfig queryConfig;
    private final ProjectionSchema root;
    private final SchemaAttributePaths paths;

    public ProjectionConstructorBuilder(QueryConfig queryConfig,
                                        ProjectionSchema root,
                                        SchemaAttributePaths paths) {
        this.queryConfig = queryConfig;
        this.root = root;
        this.paths = paths;
    }

    /// 处理 SelectEntity，创建 ObjectConstructor
    ///
    /// @return ObjectConstructor 实例
    public ValueConstructor build() {
        return build(paths, root, 0);
    }

    private ValueConstructor build(SchemaAttributePaths paths, ProjectionSchema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();
        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionSchemaAttribute schemaAttribute) {
                SchemaAttributePaths sub;
                if (schemaAttribute.getFetchType() == FetchType.LAZY) {
                    JoinInfo joinInfo = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    EntityBasicAttribute entityAttribute = schemaAttribute.getSourceAttribute();
                    ValueConverter<?, ?> converter = entityAttribute.valueConvertor();
                    Column column = new Column(entityAttribute.path(), converter, joinInfo.rightTableIndex());
                    ValueConstructor constructor = new LazyValueConstructor(queryConfig, schemaAttribute, column);
                    bindings.add(new PropertyBinding(attr, constructor));
                } else if ((sub = paths.get(schemaAttribute.name())) != null) {
                    JoinInfo joinInfo = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    ValueConstructor constructor = build(sub, schemaAttribute.schema(), joinInfo.rightTableIndex());
                    bindings.add(new PropertyBinding(attr, constructor));
                }
            } else if (attr instanceof ProjectionBasicAttribute basicAttribute) {
                EntityBasicAttribute entityAttribute = basicAttribute.getEntityAttribute();
                ValueConverter<?, ?> converter = entityAttribute.valueConvertor();
                Column column = new Column(entityAttribute.path(), converter, tableIndex);
                bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
            }
        }
        if (schema.type().isInterface()) {
            return getInterfaceConstructor(schema, bindings);
        } else if (schema.type().isRecord()) {
            return getRecordConstructor(schema, bindings);
        } else {
            return getObjectConstructor(schema, bindings);
        }
    }

    protected @NonNull ValueConstructor getObjectConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        return new ObjectConstructor(schema.type(), bindings);
    }

    protected ValueConstructor getRecordConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        return new RecordConstructor(schema.type(), bindings);
    }

    protected @NonNull ValueConstructor getInterfaceConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        return new JdkProxyConstructor(schema.type(), bindings);
    }

    private JoinInfo newJoinInfo(JoinAttribute joinAttribute, int leftTableIndex) {
        return new JoinInfo(JoinType.LEFT,
                leftTableIndex,
                joins.size() + 1,
                joinAttribute.getTargetEntityType(),
                joinAttribute.getSourceAttribute(),
                joinAttribute.getTargetAttribute());
    }

}
