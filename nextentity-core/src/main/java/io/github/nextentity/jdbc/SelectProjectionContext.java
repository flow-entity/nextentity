package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.NullableConcurrentMap;
import jakarta.persistence.FetchType;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SelectProjectionContext extends QueryContext {
    private SelectProjection selectProjection;

    private ProjectionSchema projection;
    private ImmutableArray<SelectItem> expressions;
    private SchemaAttributePaths schemaAttributePaths;

    /// 批量加载上下文（延迟初始化）
    private final Map<ProjectionSchemaAttribute, BatchAttributeLoader> batchLoaderContexts = new ConcurrentHashMap<>();

    /// 存储懒加载属性元数据供批量加载使用
    public record LazyAttributeInfo(
            ProjectionSchemaAttribute attribute,
            EntityBasicAttribute sourceAttribute,
            EntityBasicAttribute targetIdAttribute
    ) {
    }

    public SelectProjectionContext(QueryConfig descriptor) {
        super(descriptor);
        enableLazyloading = true;
    }

    public void setSelectProjection(SelectProjection selectProjection) {
        this.selectProjection = selectProjection;
    }

    @Override
    public void init() {
        super.init();
        this.projection = getEntityType().getProjection(selectProjection.type());
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
        this.expressions = separateAttributes(projection, schemaAttributePaths);
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    /// 获取当前构造的 Schema
    ///
    /// @return 投影 Schema
    @Override
    @Nullable
    public MetamodelSchema<?> getSchema() {
        return projection;
    }

    @Override
    public Object doConstruct(Arguments arguments) {
        if (projection.hasLazyAttribute()) {
            var selector = InterceptorSelector.selectConstructor(this);
            if (selector != null && selector.supports(this)) {
                return selector.intercept(this, arguments);
            }
            throw new UnsupportedOperationException(
                    "Lazy loading is not supported for projection type: " + projection.type().getName());
        }
        return constructSchema(projection, arguments, schemaAttributePaths);
    }

    public Map<Method, Object> collectResultMap(Arguments arguments) {
        ProjectionSchema schema = projection;
        SchemaAttributePaths paths = schemaAttributePaths;
        Map<Method, Object> data = new NullableConcurrentMap<>();
        for (Attribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionSchemaAttribute schemaAttr
                && schemaAttr.getFetchType() == FetchType.LAZY) {
                Object foreignKey = getSimpleAttributeValue(arguments, attr);
                data.put(attr.getter(), createLazyLoader(schemaAttr, foreignKey));
                continue;
            }
            SchemaAttributePaths subPaths = paths.get(attr.name());
            if (subPaths != null) {
                if (attr instanceof Schema nestedSchema) {
                    Object value = constructSchema(nestedSchema, arguments, subPaths);
                    data.put(attr.getter(), value);
                } else {
                    Object value = getSimpleAttributeValue(arguments, attr);
                    data.put(attr.getter(), value);
                }
            }
        }
        return data;
    }

    private AttributeLoader createLazyLoader(ProjectionSchemaAttribute attribute, Object foreignKey) {
        return getBatchLoaderContext(attribute).getAttributeLoader(foreignKey);
    }

    private BatchAttributeLoader getBatchLoaderContext(ProjectionSchemaAttribute attribute) {
        return batchLoaderContexts.computeIfAbsent(attribute, k -> new BatchAttributeLoader(k, this));
    }

    @Override
    public void setResults(List<?> results) {
        super.setResults(results);
    }

    private ImmutableArray<SelectItem> separateAttributes(ProjectionSchema schema, SchemaAttributePaths paths) {
        List<SelectItem> eagerList = new ArrayList<>();
        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionBasicAttribute basicAttr) {
                eagerList.add(basicAttr.getEntityAttribute());
            } else if (attr instanceof ProjectionSchemaAttribute schemaAttr) {
                FetchType fetchType = schemaAttr.getFetchType();
                if (fetchType == FetchType.LAZY) {
                    eagerList.add(schemaAttr.getEntityAttribute().getSourceAttribute());
                } else {
                    SchemaAttributePaths subPaths = paths.get(attr.name());
                    if (subPaths != null) {
                        streamProjectionSchema(schemaAttr, subPaths).forEach(eagerList::add);
                    }
                }
            }
        }
        return ImmutableList.ofCollection(eagerList);
    }

    private Stream<SelectItem> streamProjectionSchema(ProjectionSchemaAttribute schemaAttr, SchemaAttributePaths paths) {
        return schemaAttr.getAttributes().stream()
                .flatMap(attr -> {
                    if (attr instanceof ProjectionBasicAttribute basicAttr) {
                        return Stream.of(basicAttr.getEntityAttribute());
                    } else if (attr instanceof ProjectionSchemaAttribute nestedSchemaAttr) {
                        if (nestedSchemaAttr.getFetchType() == FetchType.LAZY) {
                            return Stream.empty();
                        }
                        SchemaAttributePaths subPaths = paths.get(attr.name());
                        if (subPaths != null) {
                            return streamProjectionSchema(nestedSchemaAttr, subPaths);
                        }
                    }
                    return Stream.empty();
                });
    }

    @Override
    public SchemaAttributePaths getSchemaAttributePaths() {
        return schemaAttributePaths;
    }
}
