package io.github.nextentity.jdbc;

import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.constructor.*;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// 值构造器选择器
///
/// 根据 Select 类型选择/创建对应的 ValueConstructor。
/// 这是 ResultConstructor 体系的核心工厂类，负责将 Select 定义转换为可执行的构造器。
///
/// 选择规则：
/// - SelectEntity → ObjectConstructor with PropertyBinding[]
/// - SelectExpression → SingleValueConstructor
/// - SelectExpressions → ArrayConstructor
/// - SelectProjection → ObjectConstructor with PropertyBinding[]
/// - SelectNested → ArrayConstructor
///
/// @author HuangChengwei
/// @since 2.2.2
public final class ConstructorSelector {

    private final EntityType entityType;

    /// 创建新的 ConstructorSelector
    ///
    /// @param entityType 实体类型
    public ConstructorSelector(EntityType entityType) {
        this.entityType = entityType;
    }

    /// 根据 Select 类型创建对应的 ValueConstructor
    ///
    /// @param select Select 定义
    /// @return 对应的 ValueConstructor 实例
    public ValueConstructor select(Selected select) {
        return select(select, 0, false);
    }

    /// 根据 Select 类型创建对应的 ValueConstructor
    ///
    /// @param select              Select 定义
    /// @param expandReferencePath 是否展开关联路径
    /// @return 对应的 ValueConstructor 实例
    public ValueConstructor select(Selected select, boolean expandReferencePath) {
        return select(select, 0, expandReferencePath);
    }

    /// 根据 Select 类型创建对应的 ValueConstructor（带表索引）
    ///
    /// @param select              Select 定义
    /// @param tableIndex          表索引（0=主表，>0=join表）
    /// @param expandReferencePath 是否展开关联路径
    /// @return 对应的 ValueConstructor 实例
    public ValueConstructor select(Selected select, int tableIndex, boolean expandReferencePath) {
        if (select instanceof SelectEntity selectEntity) {
            return selectEntity(selectEntity, tableIndex, expandReferencePath);
        } else if (select instanceof SelectExpression selectExpression) {
            return selectExpression(selectExpression, tableIndex);
        } else if (select instanceof SelectExpressions selectExpressions) {
            return selectExpressions(selectExpressions, tableIndex);
        } else if (select instanceof SelectProjection selectProjection) {
            return selectProjection(selectProjection, tableIndex);
        } else if (select instanceof SelectNested selectNested) {
            return selectNested(selectNested, tableIndex, expandReferencePath);
        }
        throw new IllegalArgumentException("Unknown select type: " + select.getClass().getName());
    }

    /// 处理 SelectEntity，创建 ObjectConstructor
    ///
    /// @param selectEntity        SelectEntity 定义
    /// @param tableIndex          表索引
    /// @param expandReferencePath 是否展开关联路径
    /// @return ObjectConstructor 实例
    private ValueConstructor selectEntity(SelectEntity selectEntity, int tableIndex, boolean expandReferencePath) {
        ImmutableList<PathNode> fetchNodes = selectEntity.fetch();
        if (fetchNodes == null || fetchNodes.isEmpty() || !expandReferencePath) {
            // 无 fetch 或不展开关联路径：只选择基本属性列
            return createPrimitiveConstructor(entityType, tableIndex);
        }

        // 有 fetch 且展开关联路径：遍历所有属性，Schema 类型属性按 fetch 路径展开
        List<PropertyBinding> bindings = new ArrayList<>();
        for (Attribute attr : entityType.getAttributes()) {
            if (attr instanceof Schema nestedSchema) {
                // 关联属性：检查是否在 fetch 路径中
                PathNode matchingPath = findMatchingFetchPath(fetchNodes, attr);
                if (matchingPath != null) {
                    ValueConstructor vc = createNestedSchemaConstructor(nestedSchema, tableIndex);
                    bindings.add(new PropertyBinding(attr, vc));
                }
                // 不在 fetch 路径中的关联属性跳过
            } else {
                // 基本属性：始终包含
                PathNode pathNode = getPathNodeForAttribute(attr);
                ValueConverter<?, ?> converter = getConverter(attr);
                Column column = Column.ofPath(pathNode, converter, tableIndex);
                bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
            }
        }

        return new ObjectConstructor(entityType.type(), bindings);
    }

    /// 在 fetch 路径中查找匹配指定属性的路径
    private @Nullable PathNode findMatchingFetchPath(ImmutableList<PathNode> fetchNodes, Attribute attr) {
        for (PathNode path : fetchNodes) {
            Attribute pathAttr = path.getAttribute(entityType);
            if (pathAttr == attr) {
                return path;
            }
        }
        return null;
    }

    /// 处理 SelectExpression，创建 SingleValueConstructor
    ///
    /// @param selectExpression SelectExpression 定义
    /// @param tableIndex       表索引
    /// @return SingleValueConstructor 实例
    private ValueConstructor selectExpression(SelectExpression selectExpression, int tableIndex) {
        ExpressionNode expression = selectExpression.expression();
        Column column = createColumn(expression, tableIndex);
        return new SingleValueConstructor(column);
    }

    /// 处理 SelectExpressions，创建 ArrayConstructor
    ///
    /// @param selectExpressions SelectExpressions 定义
    /// @param tableIndex        表索引
    /// @return ArrayConstructor 实例
    private ValueConstructor selectExpressions(SelectExpressions selectExpressions, int tableIndex) {
        ImmutableArray<ExpressionNode> items = selectExpressions.items();
        List<ValueConstructor> constructors = new ArrayList<>(items.size());

        for (ExpressionNode item : items) {
            Column column = createColumn(item, tableIndex);
            constructors.add(new SingleValueConstructor(column));
        }

        return new ArrayConstructor(constructors);
    }

    /// 处理 SelectProjection，创建 ObjectConstructor
    ///
    /// @param selectProjection SelectProjection 定义
    /// @param tableIndex       表索引
    /// @return ObjectConstructor 实例
    private ValueConstructor selectProjection(SelectProjection selectProjection, int tableIndex) {
        Class<?> projectionType = selectProjection.type();
        ProjectionSchema projection = entityType.getProjection(projectionType);

        List<PropertyBinding> bindings = new ArrayList<>();
        for (Attribute attribute : projection.getAttributes()) {
            PropertyBinding binding = createProjectionBinding(attribute, tableIndex);
            bindings.add(binding);
        }
        if (projectionType.isInterface()) {
            return new JdkProxyConstructor(projectionType, bindings);
        } else {
            return new ObjectConstructor(projectionType, bindings);
        }
    }

    /// 处理 SelectNested，创建 ArrayConstructor
    ///
    /// @param selectNested        SelectNested 定义
    /// @param tableIndex          表索引
    /// @param expandReferencePath 是否展开关联路径
    /// @return ArrayConstructor 实例
    private ValueConstructor selectNested(SelectNested selectNested, int tableIndex, boolean expandReferencePath) {
        ImmutableList<Selected> items = selectNested.items();
        List<ValueConstructor> constructors = new ArrayList<>(items.size());

        for (Selected item : items) {
            ValueConstructor constructor = select(item, tableIndex, expandReferencePath);
            constructors.add(constructor);
        }

        return new ArrayConstructor(constructors);
    }

    /// 为实体属性创建 PropertyBinding
    ///
    /// @param path       属性路径
    /// @param tableIndex 表索引
    /// @return PropertyBinding 实例，如果路径无效则返回 null
    private @Nullable PropertyBinding createPropertyBinding(PathNode path, int tableIndex) {
        Attribute attribute = path.getAttribute(entityType);
        if (attribute == null) {
            return null;
        }

        ValueConstructor valueConstructor = createAttributeConstructor(path, attribute, tableIndex);

        return new PropertyBinding(attribute, valueConstructor);
    }

    /// 为投影属性创建 PropertyBinding
    ///
    /// @param attribute  属性
    /// @param tableIndex 表索引
    /// @return PropertyBinding 实例
    private PropertyBinding createProjectionBinding(Attribute attribute, int tableIndex) {
        ValueConstructor valueConstructor = createProjectionConstructor(attribute, tableIndex);
        return new PropertyBinding(attribute, valueConstructor);
    }

    /// 为属性创建 ValueConstructor
    ///
    /// @param path       属性路径
    /// @param attribute  属性元数据
    /// @param tableIndex 表索引
    /// @return ValueConstructor 实例
    private ValueConstructor createAttributeConstructor(PathNode path, Attribute attribute, int tableIndex) {
        // 检查是否为嵌套对象
        if (attribute instanceof Schema nestedSchema) {
            return createNestedSchemaConstructor(nestedSchema, tableIndex);
        }

        // 基本属性：创建 SingleValueConstructor
        ValueConverter<?, ?> converter = getConverter(attribute);
        Column column = Column.ofPath(path, converter, tableIndex);
        return new SingleValueConstructor(column);
    }

    /// 为投影属性创建 ValueConstructor
    ///
    /// @param attribute  属性
    /// @param tableIndex 表索引
    /// @return ValueConstructor 实例
    private ValueConstructor createProjectionConstructor(Attribute attribute, int tableIndex) {
        // 检查是否为嵌套对象
        if (attribute instanceof Schema nestedSchema) {
            return createNestedSchemaConstructor(nestedSchema, tableIndex);
        }

        // 基本属性：从源实体属性获取转换器
        ValueConverter<?, ?> converter = null;
        if (attribute instanceof EntityBasicAttribute entityAttr) {
            converter = entityAttr.valueConvertor();
        } else if (attribute instanceof ProjectionBasicAttribute projAttr) {
            converter = projAttr.getEntityAttribute().valueConvertor();
        }

        // 创建列（投影属性的路径从 entity 获取）
        PathNode pathNode = getPathNodeForAttribute(attribute);
        Column column = Column.ofPath(pathNode, converter, tableIndex);
        return new SingleValueConstructor(column);
    }

    /// 为嵌套 Schema 创建 ObjectConstructor
    ///
    /// @param schema     嵌套 Schema
    /// @param tableIndex 表索引
    /// @return ObjectConstructor 实例
    private ValueConstructor createNestedSchemaConstructor(Schema schema, int tableIndex) {
        return createPrimitiveConstructor(schema, tableIndex);
    }

    /// 为 Schema 创建只包含基本属性的 ObjectConstructor
    ///
    /// @param schema     实体 Schema
    /// @param tableIndex 表索引
    /// @return ObjectConstructor 实例
    private ValueConstructor createPrimitiveConstructor(Schema schema, int tableIndex) {
        List<PropertyBinding> bindings = new ArrayList<>();

        for (Attribute attr : schema.getPrimitives()) {
            PathNode pathNode = getPathNodeForAttribute(attr);
            ValueConverter<?, ?> converter = getConverter(attr);
            Column column = Column.ofPath(pathNode, converter, tableIndex);
            bindings.add(new PropertyBinding(attr, new SingleValueConstructor(column)));
        }

        return new ObjectConstructor(schema.type(), bindings);
    }

    /// 为表达式创建 Column
    ///
    /// @param expression 表达式节点
    /// @param tableIndex 表索引
    /// @return Column 实例
    private Column createColumn(ExpressionNode expression, int tableIndex) {
        if (expression instanceof PathNode pathNode) {
            ValueConverter<?, ?> converter = getConverterForPath(pathNode);
            return Column.ofPath(pathNode, converter, tableIndex);
        } else if (expression instanceof OperatorNode operatorNode) {
            ValueConverter<?, ?> converter = getConverterForOperator(operatorNode);
            return Column.ofOperator(operatorNode, converter);
        } else if (expression instanceof LiteralNode literalNode) {
            ValueConverter<?, ?> converter = getConverterForLiteral(literalNode);
            return Column.ofLiteral(literalNode, converter);
        }

        // 未知类型：抛出异常
        throw new IllegalArgumentException("Unsupported expression type: " + expression.getClass().getName());
    }

    /// 为 PathNode 获取 ValueConverter
    ///
    /// @param pathNode 路径节点
    /// @return ValueConverter 实例
    private @Nullable ValueConverter<?, ?> getConverterForPath(PathNode pathNode) {
        Attribute attribute = pathNode.getAttribute(entityType);
        if (attribute instanceof EntityBasicAttribute entityAttr) {
            return entityAttr.valueConvertor();
        }
        return null;
    }

    /// 为 OperatorNode 获取 ValueConverter
    ///
    /// @param operatorNode 运算符节点
    /// @return ValueConverter 实例
    private ValueConverter<?, ?> getConverterForOperator(OperatorNode operatorNode) {
        // 优先从第一个 PathNode 操作数获取属性转换器
        if (!operatorNode.operands().isEmpty()) {
            ExpressionNode first = operatorNode.operands().getFirst();
            if (first instanceof PathNode pathNode) {
                ValueConverter<?, ?> converter = getConverterForPath(pathNode);
                if (converter != null) {
                    return converter;
                }
            }
        }
        // 回退：使用 ExpressionTypeResolver 推断类型
        Class<?> type = ExpressionTypeResolver.getOperationType(operatorNode, entityType);
        return IdentityValueConverter.of(type);
    }

    /// 为 LiteralNode 获取 ValueConverter
    ///
    /// @param literalNode 字面量节点
    /// @return ValueConverter 实例
    private ValueConverter<?, ?> getConverterForLiteral(LiteralNode literalNode) {
        Object value = literalNode.value();
        if (value == null) {
            return IdentityValueConverter.of();
        }
        Class<?> type = value.getClass();
        return IdentityValueConverter.of(type);
    }

    /// 为 Attribute 获取 ValueConverter
    ///
    /// @param attribute 属性
    /// @return ValueConverter 实例
    private @Nullable ValueConverter<?, ?> getConverter(Attribute attribute) {
        if (attribute instanceof EntityBasicAttribute entityAttr) {
            return entityAttr.valueConvertor();
        }
        return null;
    }

    /// 为 Attribute 创建 PathNode
    ///
    /// @param attribute 属性
    /// @return PathNode 实例
    private PathNode getPathNodeForAttribute(Attribute attribute) {
        String[] path = attribute.path().toArray(String[]::new);
        return new PathNode(path, attribute);
    }
}
