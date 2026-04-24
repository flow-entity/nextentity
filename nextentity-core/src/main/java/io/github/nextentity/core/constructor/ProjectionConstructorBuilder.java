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
    private final boolean supportInterfaceProxy;
    private final boolean supportObjectProxy;

    public ProjectionConstructorBuilder(QueryConfig queryConfig,
                                        ProjectionSchema root,
                                        SchemaAttributePaths paths,
                                        boolean supportInterfaceProxy,
                                        boolean supportObjectProxy) {
        this.queryConfig = queryConfig;
        this.root = root;
        this.paths = paths;
        this.supportInterfaceProxy = supportInterfaceProxy;
        this.supportObjectProxy = supportObjectProxy;
    }

    /// 处理 SelectEntity，创建 ObjectConstructor
    ///
    /// @return ObjectConstructor 实例
    public ValueConstructor build() {
        return build(paths, root, 0);
    }

    private ValueConstructor build(SchemaAttributePaths paths, ProjectionSchema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();
        boolean supportLazyLoading = isSupportLazyLoading(schema);
        System.out.println(supportLazyLoading);
        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionSchemaAttribute schemaAttribute) {
                SchemaAttributePaths sub;
                if (schemaAttribute.getFetchType() == FetchType.LAZY) {
                    JoinInfo joinInfo = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    EntityBasicAttribute entityAttribute = schemaAttribute.getTargetAttribute();
                    ValueConverter<?, ?> converter = entityAttribute.valueConvertor();
                    Column column = Column.ofPath(entityAttribute.path(), converter, joinInfo.rightTableIndex());
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
                Column column = Column.ofPath(entityAttribute.path(), converter, tableIndex);
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

    protected @NonNull ObjectConstructor getObjectConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        return new ObjectConstructor(schema.type(), bindings);
    }

    protected ValueConstructor getRecordConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected @NonNull JdkProxyConstructor getInterfaceConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
        return new JdkProxyConstructor(schema.type(), bindings);
    }

    private boolean isSupportLazyLoading(ProjectionSchema schema) {
        if (supportInterfaceProxy && schema.type().isInterface()) {
            return true;
        }
        return supportObjectProxy && !schema.type().isRecord() && !schema.type().isRecord();
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
