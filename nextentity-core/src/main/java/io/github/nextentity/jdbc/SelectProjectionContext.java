package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.reflect.InstanceInvocationHandler;
import io.github.nextentity.core.reflect.LazyLoader;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.FetchType;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SelectProjectionContext extends QueryContext {
    private final ProjectionSchema projection;
    private final ImmutableArray<SelectItem> expressions;
    private final ImmutableArray<LazyAttributeInfo> lazyAttributes;
    private final SchemaAttributePaths schemaAttributePaths;

    /// 批量加载上下文（延迟初始化）
    private volatile BatchLoaderContext batchLoaderContext;

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

    public SelectProjectionContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute, SelectProjection select) {
        super(structure, metamodel, expandObjectAttribute);
        this.projection = entityType.getProjection(select.type());
        this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);

        // 分离 EAGER 和 LAZY 属性
        SeparatedAttributes separated = separateAttributes(projection, schemaAttributePaths);
        this.expressions = separated.eager;
        this.lazyAttributes = separated.lazy;
    }

    @Override
    public ImmutableArray<SelectItem> getSelectedExpression() {
        return expressions;
    }

    @Override
    public Object construct(Arguments arguments) {
        if (projection.type().isInterface()) {
            return constructInterfaceSchemaWithLazy(projection, arguments, schemaAttributePaths);
        }
        return constructSchema(projection, arguments, schemaAttributePaths);
    }

    /// 构建支持懒加载属性的 Interface 代理对象
    private Object constructInterfaceSchemaWithLazy(ProjectionSchema schema, Arguments arguments, SchemaAttributePaths paths) {
        // 直接使用父类方法构建 EAGER 属性数据
        Map<Method, Object> data = new HashMap<>();
        for (Attribute attr : schema.getAttributes()) {
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

        // 创建懒加载器（从缓存获取，批量加载由 setResults 触发）
        Map<Method, LazyLoader> lazyMap = new HashMap<>();
        for (LazyAttributeInfo info : lazyAttributes) {
            lazyMap.put(info.attribute().getter(), createLazyLoader(info));
        }

        if (data.isEmpty() && lazyMap.isEmpty()) {
            return null;
        }
        return ReflectUtil.newProxyInstance(schema.type(), data, lazyMap);
    }

    /// 创建懒加载器（首次 load 时遍历 results 批量加载）
    private LazyLoader createLazyLoader(LazyAttributeInfo info) {
        ProjectionSchemaAttribute attribute = info.attribute();
        EntityBasicAttribute sourceAttribute = info.sourceAttribute();
        EntityBasicAttribute targetIdAttribute = info.targetIdAttribute();
        Method foreignKeyGetter = sourceAttribute.getter();

        // 目标实体类型
        EntitySchema targetSchema = attribute.source().target();
        Class<?> targetType = targetSchema.type();

        // 创建外键收集器：遍历 results 提取所有外键值
        Supplier<Set<Object>> foreignKeyCollector = () -> {
            List<?> results = getResults();
            Set<Object> keys = new HashSet<>();
            if (results != null) {
                for (Object result : results) {
                    if (result != null && Proxy.isProxyClass(result.getClass())) {
                        InstanceInvocationHandler handler = (InstanceInvocationHandler)
                                Proxy.getInvocationHandler(result);
                        Map<Method, Object> data = handler.data();
                        Object foreignKey = data.get(foreignKeyGetter);
                        if (foreignKey != null) {
                            keys.add(foreignKey);
                        }
                    }
                }
            }
            return keys;
        };

        return data -> {
            // 从 EAGER 属性数据获取外键值
            Object foreignKey = data.get(foreignKeyGetter);

            // 获取批量加载器（带外键收集器）
            BatchLoaderContext ctx = getBatchLoaderContext();
            BatchLazyLoader loader = ctx.getBatchLoader(targetType, targetIdAttribute, foreignKeyCollector);

            // load() 首次调用时会触发批量加载（遍历 results 收集所有外键）
            return loader.load(foreignKey);
        };
    }

    /// 获取批量加载上下文（延迟初始化）
    ///
    /// 由于 queryExecutor 在 QueryContext 构造后才设置，
    /// 因此在首次需要时创建 BatchLoaderContext。
    ///
    /// @return 批量加载上下文
    private BatchLoaderContext getBatchLoaderContext() {
        if (batchLoaderContext == null) {
            synchronized (this) {
                if (batchLoaderContext == null) {
                    batchLoaderContext = new BatchLoaderContext(
                            getQueryExecutor(),
                            getFetchConfig()
                    );
                }
            }
        }
        return batchLoaderContext;
    }

    /// 获取懒加载属性列表
    public ImmutableArray<LazyAttributeInfo> getLazyAttributes() {
        return lazyAttributes;
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
    private SeparatedAttributes separateAttributes(ProjectionSchema schema, SchemaAttributePaths paths) {
        List<SelectItem> eagerList = new ArrayList<>();
        List<LazyAttributeInfo> lazyList = new ArrayList<>();

        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionBasicAttribute basicAttr) {
                // 基础属性总是 EAGER
                eagerList.add(basicAttr.source());
            } else if (attr instanceof ProjectionSchemaAttribute schemaAttr) {
                // 嵌套 Schema 属性，根据 fetchType 决定
                FetchType fetchType = schemaAttr.fetchType();
                if (fetchType == FetchType.LAZY) {
                    // LAZY: 添加到 lazyList，子属性不遍历（父 LAZY → 全跳过）
                    lazyList.add(createLazyAttributeInfo(schemaAttr, paths));
                } else {
                    // EAGER: 递归添加到 expressions
                    SchemaAttributePaths subPaths = paths.get(attr.name());
                    if (subPaths != null) {
                        streamProjectionSchema(schemaAttr, subPaths).forEach(eagerList::add);
                    }
                }
            }
        }

        return new SeparatedAttributes(
                ImmutableList.ofCollection(eagerList),
                ImmutableList.ofCollection(lazyList)
        );
    }

    /// 创建懒加载属性信息
    private LazyAttributeInfo createLazyAttributeInfo(ProjectionSchemaAttribute schemaAttr, SchemaAttributePaths paths) {
        EntitySchemaAttribute source = schemaAttr.source();
        // 外键属性：sourceAttribute 是父实体中指向目标实体的属性
        EntityBasicAttribute sourceAttribute = source.sourceAttribute();
        // 目标主键：targetAttribute 是目标实体中被引用的属性（通常是主键）
        EntityBasicAttribute targetIdAttribute = source.targetAttribute();
        return new LazyAttributeInfo(schemaAttr, sourceAttribute, targetIdAttribute);
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

    /// 属性分离结果
    private record SeparatedAttributes(
            ImmutableArray<SelectItem> eager,
            ImmutableArray<LazyAttributeInfo> lazy
    ) {
    }
}
