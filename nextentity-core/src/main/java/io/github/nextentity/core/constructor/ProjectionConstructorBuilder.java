package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.meta.*;
import jakarta.persistence.FetchType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 投影构造器构建器
///
/// 根据 ProjectionSchema 构建 ValueConstructor，
/// 支持接口类型（JDK 代理）、Record 类型（规范构造函数）和普通类（构造函数 + setter）。
/// 对于 FetchType.LAZY 的嵌套属性，使用 LazyValueConstructor 实现懒加载。
///
/// @author HuangChengwei
/// @since 2.2.2
public class ProjectionConstructorBuilder {

    private final Map<JoinAttribute, JoinIndex> joins = new ConcurrentHashMap<>();

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

    /// 处理 SelectProjection，根据类型创建对应的 ValueConstructor
    ///
    /// @return ValueConstructor 实例
    public ValueConstructor build() {
        return build(paths, root, 0);
    }

    private ValueConstructor build(SchemaAttributePaths paths, ProjectionSchema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();
        boolean supportLazyLoading = isSupportLazyLoading(schema);
        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionSchemaAttribute schemaAttribute) {
                SchemaAttributePaths sub = paths.get(schemaAttribute.name());
                if (schemaAttribute.getFetchType() == FetchType.LAZY || supportLazyLoading && sub == null) {
                    JoinIndex joinIndex = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    EntityBasicAttribute entityAttribute = schemaAttribute.getSourceAttribute();
                    ValueConverter<?, ?> converter = entityAttribute.valueConvertor();
                    Column column = new Column(entityAttribute.path(), converter, joinIndex.rightTableIndex());
                    ValueConstructor constructor = new LazyValueConstructor(queryConfig, schemaAttribute, column);
                    bindings.add(new PropertyBinding(attr, constructor));
                } else {
                    JoinIndex joinIndex = joins.computeIfAbsent(schemaAttribute, _ -> newJoinInfo(schemaAttribute, tableIndex));
                    ValueConstructor constructor = build(sub, schemaAttribute.schema(), joinIndex.rightTableIndex());
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

    private JoinIndex newJoinInfo(JoinAttribute joinAttribute, int leftTableIndex) {
        return new JoinIndex(JoinType.LEFT,
                leftTableIndex,
                joins.size() + 1,
                joinAttribute.getTargetEntityType(),
                joinAttribute.getSourceAttribute(),
                joinAttribute.getTargetAttribute());
    }

    private boolean isSupportLazyLoading(ProjectionSchema schema) {
        if (queryConfig.interfaceLazyEnabled() && schema.type().isInterface()) {
            return true;
        }
        return queryConfig.dtoObjectLazyEnabled() && !schema.type().isRecord() && !schema.type().isInterface();
    }

}
