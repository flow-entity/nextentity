# 懒加载属性分离实现方案

## Context

**问题**：当前 `SelectProjectionContext` 将所有属性放入 `expressions`，都会出现在 SELECT 子句中。需要支持懒加载属性不出现在 SELECT 中。

**目标**：将 `expressions` 中的 LAZY 属性分离出来，只在 SELECT 中包含 EAGER 属性，LAZY 属性创建代理对象供首次访问时触发二次查询。

**设计决策**：
- LAZY 属性在 `construct()` 时**创建代理对象**，首次访问触发二次查询
- 嵌套属性处理：**父属性 LAZY → 子属性全跳过**（整棵子树不加载）

---

## 关键文件

| 文件 | 作用 |
|-----|------|
| `nextentity-core/.../jdbc/SelectProjectionContext.java` | **主要修改** - 分离 EAGER/LAZY 属性 |
| `nextentity-core/.../jdbc/QueryContext.java` | 修改 `stream()` 方法支持 fetchType 过滤 |
| `nextentity-core/.../jdbc/SelectEntityContext.java` | 同样需要支持 fetchType 过滤 |
| `nextentity-core/.../core/meta/ProjectionSchemaAttribute.java` | 已有 `fetchType()` 方法 |
| `nextentity-core/.../jdbc/FetchConfig.java` | 配置传递 |
| `nextentity-core/.../core/util/Lazy.java` | 已存在，可复用 |
| `nextentity-core/.../core/reflect/InstanceInvocationHandler.java` | 代理 invocation handler |

---

## 实现步骤

### Step 1: 新增 LazyAttributeInfo 记录类

在 `SelectProjectionContext.java` 中新增：

```java
/// 存储懒加载属性元数据，供二次查询使用
public record LazyAttributeInfo(
    ProjectionSchemaAttribute attribute,
    SchemaAttributePaths path
) {}
```

### Step 2: 修改 SelectProjectionContext 字段

```java
public class SelectProjectionContext extends QueryContext {
    private final ProjectionSchema projection;
    private final ImmutableArray<SelectItem> expressions;           // EAGER 属性
    private final ImmutableArray<LazyAttributeInfo> lazyAttributes; // LAZY 属性（新增）
    private final SchemaAttributePaths schemaAttributePaths;
    private final FetchConfig fetchConfig;                          // 配置（新增）
}
```

### Step 3: 修改属性分离方法（核心逻辑）

```java
private SeparatedAttributes separateAttributes(
        Schema schema,
        SchemaAttributePaths paths,
        FetchConfig config) {

    List<SelectItem> eagerList = new ArrayList<>();
    List<LazyAttributeInfo> lazyList = new ArrayList<>();

    for (Attribute attr : schema.getAttributes()) {
        FetchType fetchType = getFetchType(attr, config);

        if (fetchType == FetchType.LAZY && config.lazyLoadEnabled()) {
            // LAZY: 添加到 lazyList，不遍历子属性（父 LAZY → 全跳过）
            if (attr instanceof ProjectionSchemaAttribute schemaAttr) {
                lazyList.add(new LazyAttributeInfo(schemaAttr, paths));
            }
            // 不调用 stream()，子属性全部跳过
        } else {
            // EAGER: 递归添加到 expressions
            stream(attr, paths).forEach(eagerList::add);
        }
    }

    return new SeparatedAttributes(
        ImmutableList.of(eagerList),
        ImmutableList.of(lazyList)
    );
}
```

### Step 4: 修改 construct() 方法 - 创建代理

```java
@Override
public Object construct(Arguments arguments) {
    // 1. 构造 EAGER 属性
    Object result = constructSchema(projection, arguments, schemaAttributePaths);

    // 2. 为 LAZY 属性创建代理
    for (LazyAttributeInfo lazyInfo : lazyAttributes) {
        Object lazyProxy = createLazyProxy(lazyInfo);
        // 设置到 result 对象
        setLazyAttribute(result, lazyInfo.attribute(), lazyProxy);
    }

    return result;
}

private Object createLazyProxy(LazyAttributeInfo lazyInfo) {
    Class<?> targetType = lazyInfo.attribute().type();
    Supplier<Object> loader = () -> loadLazyAttribute(lazyInfo);

    // 使用 Lazy 工具类创建延迟加载器
    Lazy<Object> lazyValue = new Lazy<>(loader);

    // 创建接口代理
    return ReflectUtil.newProxyInstance(targetType, method -> {
        // getter 方法触发加载
        return lazyValue.get();
    });
}

private Object loadLazyAttribute(LazyAttributeInfo lazyInfo) {
    // 二次查询：根据父实体的 ID 加载关联属性
    // 需要访问 queryExecutor 和父实体 ID
    // TODO: 实现二次查询逻辑
}
```

### Step 5: 配置传递链路

修改 `QueryContext.create()` 接收 `FetchConfig`：

```java
public static QueryContext create(QueryStructure structure,
                                  Metamodel metamodel,
                                  boolean expandObjectAttribute,
                                  FetchConfig fetchConfig) {
    Selected select = structure.select();
    if (select instanceof SelectProjection selectProjection) {
        return new SelectProjectionContext(structure, metamodel,
            expandObjectAttribute, selectProjection, fetchConfig);
    }
    // ...
}
```

### Step 6: 修改 stream() 方法

```java
protected Stream<SelectItem> stream(Attribute attribute, SchemaAttributePaths paths) {
    // 简化版本：只处理 EAGER 属性
    // LAZY 属性在外层已过滤，不会进入此方法
    if (attribute instanceof EntityBasicAttribute expression) {
        return Stream.of(expression);
    } else if (attribute instanceof ProjectionBasicAttribute expression) {
        return Stream.of(expression.source());
    } else if (attribute instanceof Schema schema) {
        SchemaAttributePaths sub = paths.get(attribute.name());
        if (sub != null) {
            return schema.getAttributes().stream()
                    .flatMap(subAttr -> stream(subAttr, sub));
        }
    }
    return Stream.empty();
}
```

---

## 验证计划

1. **单元测试**：
   - 测试 `separateAttributes()` 正确分离 EAGER/LAZY
   - 测试父 LAZY 时子属性不遍历

2. **集成测试**：
   - 验证生成的 SQL 不包含 LAZY 属性列
   - 验证 LAZY 属性代理首次访问触发查询

3. **代理测试**：
   - 验证 `Lazy.get()` 只加载一次
   - 验证多线程安全

---

## 后续实现（二次查询）

`loadLazyAttribute()` 需要：
1. 父实体 ID 值（从 `Arguments` 或已构造的 result 中获取）
2. `QueryExecutor` 引用（可能需要新增参数）
3. 批量加载优化（使用 `FetchConfig.batchMaxSize()`）