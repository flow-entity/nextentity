package io.github.nextentity.jdbc;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SelectProjectionContext extends QueryContext {
    private final ProjectionSchema projection;
    private final ImmutableArray<SelectItem> expressions;
    private final ImmutableArray<LazyAttributeInfo> lazyAttributes;
    private final SchemaAttributePaths schemaAttributePaths;

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
        Object result = constructSchema(projection, arguments, schemaAttributePaths);
        // TODO: 为 LAZY 属性创建代理对象
        return result;
    }

    /// 获取懒加载属性列表
    public ImmutableArray<LazyAttributeInfo> getLazyAttributes() {
        return lazyAttributes;
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
