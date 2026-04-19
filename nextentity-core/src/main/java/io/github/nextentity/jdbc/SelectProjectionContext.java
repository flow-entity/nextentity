package io.github.nextentity.jdbc;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.ResultMap;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.FetchType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SelectProjectionContext extends QueryContext {
    private final ProjectionSchema projection;
    private final ImmutableArray<SelectItem> expressions;
    private final SchemaAttributePaths schemaAttributePaths;

    /// 批量加载上下文（延迟初始化）
    private final Map<ProjectionSchemaAttribute, BatchLoaderContext> batchLoaderContexts = new ConcurrentHashMap<>();

    /// 存储懒加载属性元数据，供二次查询批量加载使用
    ///
    /// @param attribute         投影属性定义
    /// @param sourceAttribute   父实体中的外键属性（用于获取关联值）
    /// @param targetIdAttribute 目标实体的主键属性（用于 WHERE 条件）
    public record LazyAttributeInfo(
            ProjectionSchemaAttribute attribute,
            EntityBasicAttribute sourceAttribute,
            EntityBasicAttribute targetIdAttribute
    ) {
    }

    public SelectProjectionContext(QueryExecutor executor,
                                   QueryStructure structure,
                                   Metamodel metamodel,
                                   boolean expandObjectAttribute,
                                   SelectProjection select) {
        super(executor, structure, metamodel, expandObjectAttribute);
        this.projection = entityType.getProjection(select.type());
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);

        // 分离 EAGER 和 LAZY 属性
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
    public Schema getSchema() {
        return projection;
    }

    @Override
    public Object construct(Arguments arguments) {
        if (projection.type().isInterface()) {
            return constructInterfaceSchemaWithLazy(projection, arguments, schemaAttributePaths);
        }
        return constructSchema(projection, arguments, schemaAttributePaths);
    }

    /// 构建支持懒加载属性的 Interface 代理对象
    private Object constructInterfaceSchemaWithLazy(ProjectionSchema schema,
                                                    Arguments arguments,
                                                    SchemaAttributePaths paths) {
        // 直接使用父类方法构建 EAGER 属性数据
        ResultMap data = new ResultMap();
        for (Attribute attr : schema.getAttributes()) {
            // 检查是否是 LAZY 属性，如果是则跳过
            if (attr instanceof ProjectionSchemaAttribute schemaAttr
                    && schemaAttr.fetchType() == FetchType.LAZY) {
                Object foreignKey = getSimpleAttributeValue(arguments, attr);
                data.put(attr.getter(), createLazyLoader(schemaAttr, foreignKey));
                continue;  // LAZY 属性不处理，由 lazyMap 处理
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

        return ReflectUtil.newProxyInstance(schema.type(), data);
    }

    /// 创建懒加载器（首次 load 时遍历 results 批量加载）
    private AttributeLoader createLazyLoader(ProjectionSchemaAttribute attribute, Object foreignKey) {
        BatchLoaderContext batchLoaderContext = getBatchLoaderContext(attribute);
        return batchLoaderContext.addForeignKey(foreignKey);
    }

    /// 获取批量加载上下文（延迟初始化）
    ///
    /// 由于 queryExecutor 在 QueryContext 构造后才设置，
    /// 因此在首次需要时创建 BatchLoaderContext。
    ///
    /// @return 批量加载上下文
    private BatchLoaderContext getBatchLoaderContext(ProjectionSchemaAttribute attribute) {
        return batchLoaderContexts.computeIfAbsent(attribute, k -> new BatchLoaderContext(k, this));
    }

    /// 设置查询结果列表
    ///
    /// 存储结果供后续批量加载使用。
    /// 批量加载在首次访问 LAZY 属性时触发。
    ///
    /// @param results 查询结果列表
    @Override
    public void setResults(List<?> results) {
        super.setResults(results);
    }

    /// 分离 EAGER 和 LAZY 属性
    private ImmutableArray<SelectItem> separateAttributes(ProjectionSchema schema, SchemaAttributePaths paths) {
        List<SelectItem> eagerList = new ArrayList<>();

        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionBasicAttribute basicAttr) {
                // 基础属性总是 EAGER
                eagerList.add(basicAttr.source());
            } else if (attr instanceof ProjectionSchemaAttribute schemaAttr) {
                // 嵌套 Schema 属性，根据 fetchType 决定
                FetchType fetchType = schemaAttr.fetchType();
                if (fetchType == FetchType.LAZY) {
                    // LAZY: 添加到 lazyList，子属性不遍历（父 LAZY → 全跳过）
                    EntityBasicAttribute source = schemaAttr.source().sourceAttribute();
                    eagerList.add(source);
                } else {
                    // EAGER: 递归添加到 expressions
                    SchemaAttributePaths subPaths = paths.get(attr.name());
                    if (subPaths != null) {
                        streamProjectionSchema(schemaAttr, subPaths).forEach(eagerList::add);
                    }
                }
            }
        }

        return ImmutableList.ofCollection(eagerList);
    }

    /// 递归展开 ProjectionSchema 属性
    private Stream<SelectItem> streamProjectionSchema(ProjectionSchemaAttribute schemaAttr, SchemaAttributePaths paths) {
        // ProjectionSchemaAttribute 继承 SchemaAttribute，后者继承 Schema
        return schemaAttr.getAttributes().stream()
                .flatMap(attr -> {
                    if (attr instanceof ProjectionBasicAttribute basicAttr) {
                        return Stream.of(basicAttr.source());
                    } else if (attr instanceof ProjectionSchemaAttribute nestedSchemaAttr) {
                        // 嵌套属性同样检查 fetchType
                        FetchType fetchType = nestedSchemaAttr.fetchType();
                        if (fetchType == FetchType.LAZY) {
                            // LAZY: 不添加到 expressions
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

}
