# NextEntity 未完成测试计划

## 概述

本文档从 PLAN.md 提取所有尚未完成的测试任务。整体测试进度约 65%，需继续完成以下文件。

---

## 核心原则

**测试的最终目标是找出潜在的漏洞和错误，而不是单纯追求代码覆盖率。**

### 测试重点

1. **边界条件**：null 值、空集合、极值、越界等
2. **异常处理**：非法参数、异常状态恢复
3. **并发安全**：多线程环境下的正确性（如适用）
4. **业务逻辑**：核心业务流程的正确性
5. **集成点**：模块间交互的正确性

### 测试设计思路

- **等价类划分**：有效输入、无效输入
- **边界值分析**：最小值、最大值、临界值
- **错误推测**：根据经验推测可能的错误点
- **状态转换**：对象状态变化的正确性

### 覆盖率参考

覆盖率是辅助指标，不是目标：
- 核心类（QueryBuilder, ExpressionBuilder）：建议 80%+
- 工具类：建议 90%+
- **重点：覆盖关键路径和易错点**

---

## 重要要求

### 数据库环境要求

**使用 Testcontainers 作为集成测试数据库环境**，参考 `nextentity-spring` 模块下的测试用例结构：

- 参考 `AbstractTestcontainersDbConfigProvider.java` 创建数据库配置基类
- 参考 `Mysql.java`, `Postgresql.java`, `SqlServer.java` 创建具体的数据库配置
- 使用 singleton 模式启动容器，共享跨测试

### 测试用例设计要求

| 要求 | 说明 |
|------|------|
| **参考范围** | `nextentity-spring` 测试用例仅供参考，不作为覆盖率评估范围 |
| **禁止照搬** | 不能照搬 `nextentity-spring` 的测试用例代码 |
| **Entity 设计** | 不能使用 `nextentity-spring` 里的 `User` 类，需要自己设计 Entity 类 |
| **原创性** | 根据被测试类的实际功能设计独立的测试场景和数据 |

### Entity 设计建议

根据测试需求设计 Entity 类，建议放在 `nextentity-core/src/test/java/io/github/nextentity/test/` 目录下：

**基础实体示例：**
```java
// 简单实体 - 用于基础 CRUD 测试
@Entity
public class Customer {
    @Id
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    // getter/setter...
}

// 带关联的实体 - 用于关联查询测试
@Entity
public class Order {
    @Id
    private Long id;
    private String orderNo;
    @ManyToOne
    private Customer customer;
    private BigDecimal amount;
    private OrderStatus status;
    // getter/setter...
}
```

**建议设计的 Entity 类型：**

| Entity 类型 | 用途 | 包含字段 |
|-------------|------|----------|
| 简单实体 | 基础 CRUD 测试 | id, name, createdAt |
| 带枚举实体 | 枚举转换测试 | id, status (enum) |
| 带关联实体 | 关联查询测试 | id, parent, children |
| 乐观锁实体 | 乐观锁测试 | id, version, name |
| 自定义ID实体 | ID生成测试 | id (各种类型), name |

---

## 未完成测试清单（按包分类）

### core/ (核心模块)

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| AbstractCollector.java | 9 | 5 | 0% | 抽象结果收集器 |
| OrderOperatorImpl.java | 3 | 3 | 0% | 排序操作符实现 |
| QueryBuilder.java | 56 | 45 | 0% | **主查询构建器实现（核心）** |
| UpdateExecutor.java | 3 | 3 | 0% | 更新执行器接口 |
| Updaters.java | 9 | 8 | 0% | 更新操作工具 |
| WhereImpl.java | 31 | 25 | 0% | **WHERE 子句实现（核心）** |

### core/expression/ (表达式系统)

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| AbstractExpressionBuilder.java | 62 | 55 | 0% | **抽象表达式构建器（核心）** |
| ExpressionBuilderImpl.java | 2 | 2 | 0% | 表达式构建器实现 |
| NumberOperatorImpl.java | 12 | 12 | 0% | 数值操作符实现 |
| OrderOperatorImpl.java | 1 | 1 | 0% | 排序操作符实现 |
| PathOperatorImpl.java | 3 | 3 | 0% | 路径操作符实现 |
| QueryStructure.java | 14 | 12 | 0% | 查询结构 |
| SelectExpressions.java | 1 | 1 | 0% | 选择表达式工具 |
| StringOperatorImpl.java | 5 | 5 | 0% | 字符串操作符实现 |

### core/meta/ (元模型系统)

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| AbstractMetamodel.java | 32 | 28 | 0% | **抽象元模型（核心）** |
| SimpleEntity.java | 8 | 8 | 0% | 简单实体 |
| SimpleEntityAttribute.java | 13 | 12 | 0% | 简单实体属性 |
| SimpleJoinAttribute.java | 9 | 8 | 0% | 简单关联属性 |
| SimpleProjection.java | 4 | 4 | 0% | 简单投影 |
| SimpleProjectionAttribute.java | 2 | 2 | 0% | 简单投影属性 |
| SimpleProjectionJoinAttribute.java | 2 | 2 | 0% | 简单投影关联属性 |
| SubQueryEntity.java | 1 | 1 | 0% | 子查询实体 |

### core/reflect/schema/

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| AbstractSchemaAttribute.java | 2 | 2 | 0% | 抽象模式属性 |
| SimpleAttributes.java | 2 | 2 | 0% | 简单属性集合 |

### core/util/

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| ImmutableList.java | 47 | 40 | 95% | 不可变列表实现 (1个bug待修复) |
| Paths.java | 31 | 25 | 0% | 路径工具 (RootImpl已测试) |

### jdbc/ (JDBC实现)

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| AbstractArguments.java | 1 | 1 | 0% | 抽象查询参数 |
| AbstractQuerySqlBuilder.java | 56 | 45 | 0% | **抽象查询 SQL 构建器（核心）** |
| AbstractUpdateSqlBuilder.java | 9 | 8 | 0% | 抽象更新 SQL 构建器 |
| JdbcArguments.java | 1 | 1 | 0% | JDBC 参数 |
| JdbcQueryExecutor.java | 1 | 1 | 0% | JDBC 查询执行器（集成测试） |
| JdbcResultCollector.java | 1 | 1 | 0% | JDBC 结果收集器 |
| JdbcUpdateExecutor.java | 10 | 8 | 0% | JDBC 更新执行器（集成测试） |
| QueryContext.java | 21 | 18 | 0% | 查询上下文 |
| SelectArrayContext.java | 2 | 2 | 0% | 数组选择上下文 |
| SelectEntityContext.java | 2 | 2 | 0% | 实体选择上下文 |
| SelectPrimitiveContext.java | 2 | 2 | 0% | 基本类型选择上下文 |
| SelectProjectionContext.java | 2 | 2 | 0% | 投影选择上下文 |
| SelectSimpleEntityContext.java | 2 | 2 | 0% | 简单实体选择上下文 |

### jpa/ (JPA实现)

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| JpaExpressionBuilder.java | 10 | 9 | 0% | JPA 表达式构建器 |
| JpaNativeQueryExecutor.java | 4 | 4 | 0% | JPA 原生查询执行器（集成测试） |
| JpaQueryExecutor.java | 20 | 16 | 0% | **JPA 查询执行器（集成测试）** |
| JpaUpdateExecutor.java | 5 | 5 | 0% | JPA 更新执行器（集成测试） |

### meta/jpa/

| 文件 | 方法数 | 需测试方法数 | 当前状态 | 说明 |
|------|--------|--------------|----------|------|
| JpaMetamodel.java | 20 | 18 | 0% | **JPA 元模型实现（集成测试）** |

---

## 无需测试的文件

以下文件为接口、注解、枚举或异常类，通常无需编写测试：

### api/ (接口定义)
- BaseWhereStep.java, BooleanExpression.java, BooleanPath.java
- Collector.java, EntityPath.java, EntityRootProvider.java
- Expression.java, ExpressionBuilder.java, FetchStep.java
- GroupByStep.java, HavingStep.java, NumberExpression.java
- NumberPath.java, OrderByStep.java, OrderOperator.java
- Path.java, PathExpression.java, Predicate.java
- Select.java, SimpleExpression.java, SortOrder.java
- StringExpression.java, StringPath.java, SubQueryBuilder.java
- TypedExpression.java, Update.java, WhereStep.java

### api/model/ (模型接口)
- EntityRoot.java, LockModeType.java, Order.java
- Page.java, PageCollector.java, Pageable.java
- Slice.java, Sliceable.java, Tuple.java
- Tuple2~Tuple10.java

### core/annotation/ (注解)
- EntityAttribute.java, SubSelect.java

### core/exception/ (异常类)
- BeanReflectiveException.java, OptimisticLockException.java
- RepositoryConfigurationException.java, TransactionRequiredException.java
- UncheckedReflectiveException.java, UncheckedSQLException.java

### core/ (接口/标记)
- Persistable.java, QueryConfig.java, QueryExecutor.java
- SelectItem.java

### core/converter/ (接口)
- TypeConverter.java

### core/expression/ (接口)
- ExpressionNode.java, ExpressionTree.java, From.java
- Operator.java, OrderImpl.java, Selected.java

### core/meta/ (接口)
- DatabaseColumnAttribute.java, DatabaseType.java
- EntityAttribute.java, EntitySchema.java, EntityType.java
- JoinAttribute.java, Metamodel.java, ProjectionAttribute.java
- ProjectionJoinAttribute.java, ProjectionType.java
- SubQueryEntityType.java, ValueConverter.java

### core/reflect/schema/ (接口)
- Attribute.java, Attributes.java, ReflectType.java
- Schema.java, SchemaAttribute.java

### core/util/ (常量/接口)
- EmptyArrays.java, Sizeable.java

### jdbc/ (接口)
- Arguments.java, ConnectionProvider.java
- JdbcUpdateSqlBuilder.java, SqlStatement.java, TypedParameter.java

---

## 统计汇总

| 项目 | 数值 |
|------|------|
| **需测试文件数（未完成）** | 38 个 |
| **需测试方法数（未完成）** | ~350 个 |
| **无需测试文件数** | ~70 个 |
| **已完成测试进度** | ~65% |

---

## 测试编写顺序建议

### Phase 1: 测试基础设施搭建

**目标：** 创建集成测试所需的 Testcontainers 环境和 Entity 类

**任务：**
1. 创建测试目录结构：
   ```
   nextentity-core/src/test/java/io/github/nextentity/test/
   ├── entity/                    # 测试用 Entity 类
   │   ├── Customer.java          # 简单实体
   │   ├── Order.java             # 带关联实体
   │   ├── Product.java           # 带枚举实体
   │   └── Category.java          # 自引用关联实体
   ├── db/                        # 数据库配置
   │   ├── AbstractTestDbConfig.java
   │   ├── MySqlTestDb.java
   │   ├── PostgresqlTestDb.java
   │   └── SqlServerTestDb.java
   └── RepositoryTestBase.java    # 测试基类
   ```

2. 添加 Testcontainers 依赖到 `nextentity-core/pom.xml`

### Phase 2: 核心查询构建器（高优先级）

**单元测试：**
1. `OrderOperatorImpl.java` (core/) - 3 方法 - 简单，无依赖
2. `AbstractCollector.java` - 5 方法 - 基础收集器
3. `WhereImpl.java` - 25 方法 - WHERE 子句实现
4. `QueryBuilder.java` - 45 方法 - **核心查询构建器**
5. `Updaters.java` - 8 方法 - 更新操作工具
6. `UpdateExecutor.java` - 3 方法 - 更新执行器接口

### Phase 3: 表达式构建器（高优先级）

**单元测试：**
1. `ExpressionBuilderImpl.java` - 2 方法 - 表达式构建入口
2. `OrderOperatorImpl.java` (expression/) - 1 方法 - 排序操作符
3. `PathOperatorImpl.java` - 3 方法 - 路径操作符
4. `StringOperatorImpl.java` - 5 方法 - 字符串操作符
5. `NumberOperatorImpl.java` - 12 方法 - 数值操作符
6. `SelectExpressions.java` - 1 方法 - 选择表达式
7. `QueryStructure.java` - 12 方法 - 查询结构
8. `AbstractExpressionBuilder.java` - 55 方法 - **核心表达式构建器**

### Phase 4: 元模型系统（中优先级）

**单元测试：**
1. `SimpleEntity.java` - 8 方法 - 简单实体
2. `SimpleEntityAttribute.java` - 12 方法 - 实体属性
3. `SimpleJoinAttribute.java` - 8 方法 - 关联属性
4. `SimpleProjection.java` - 4 方法 - 投影
5. `SimpleProjectionAttribute.java` - 2 方法 - 投影属性
6. `SimpleProjectionJoinAttribute.java` - 2 方法 - 投影关联
7. `SubQueryEntity.java` - 1 方法 - 子查询实体
8. `AbstractMetamodel.java` - 28 方法 - **元模型基类**

### Phase 5: JDBC 实现（中优先级）

**单元测试：**
1. `AbstractArguments.java` - 1 方法 - 抽象参数
2. `JdbcArguments.java` - 1 方法 - JDBC 参数
3. `SelectArrayContext.java` - 2 方法 - 数组选择上下文
4. `SelectEntityContext.java` - 2 方法 - 实体选择上下文
5. `SelectPrimitiveContext.java` - 2 方法 - 基本类型选择上下文
6. `SelectProjectionContext.java` - 2 方法 - 投影选择上下文
7. `SelectSimpleEntityContext.java` - 2 方法 - 简单实体选择上下文
8. `QueryContext.java` - 18 方法 - 查询上下文
9. `JdbcResultCollector.java` - 1 方法 - 结果收集器
10. `AbstractQuerySqlBuilder.java` - 45 方法 - **查询 SQL 构建器**
11. `AbstractUpdateSqlBuilder.java` - 8 方法 - 更新 SQL 构建器

**集成测试（使用 Testcontainers）：**
12. `JdbcQueryExecutor.java` - 1 方法 - JDBC 查询执行器
13. `JdbcUpdateExecutor.java` - 8 方法 - JDBC 更新执行器

### Phase 6: JPA 实现（中优先级）

**单元测试：**
1. `JpaExpressionBuilder.java` - 9 方法 - JPA 表达式构建器

**集成测试（使用 Testcontainers）：**
2. `JpaNativeQueryExecutor.java` - 4 方法 - 原生查询执行器
3. `JpaQueryExecutor.java` - 16 方法 - **JPA 查询执行器**
4. `JpaUpdateExecutor.java` - 5 方法 - JPA 更新执行器
5. `JpaMetamodel.java` - 18 方法 - **JPA 元模型**

### Phase 7: 工具类补充（低优先级）

**单元测试：**
1. `Paths.java` - 25 方法 - 路径工具（完成 RootImpl 以外的测试）
2. `AbstractSchemaAttribute.java` - 2 方法 - 抽象模式属性
3. `SimpleAttributes.java` - 2 方法 - 简单属性集合
4. `ImmutableList.java` - 补充剩余测试（待 bug 修复）

---

## Testcontainers 配置参考

### Maven 依赖

```xml
<!-- nextentity-core/pom.xml -->
<dependencies>
    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.x</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <version>1.19.x</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.19.x</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mssqlserver</artifactId>
        <version>1.19.x</version>
        <scope>test</scope>
    </dependency>

    <!-- 数据库驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Hibernate for JPA tests -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 数据库配置基类示例

```java
// 参考 nextentity-spring 的 AbstractTestcontainersDbConfigProvider
public abstract class AbstractTestDbConfig {
    protected abstract JdbcDatabaseContainer<?> getContainer();

    public DataSource getDataSource() {
        JdbcDatabaseContainer<?> container = getContainer();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(container.getJdbcUrl());
        dataSource.setUsername(container.getUsername());
        dataSource.setPassword(container.getPassword());
        dataSource.setDriverClassName(container.getDriverClassName());
        return dataSource;
    }
}
```

---

## 已知问题

详见 `.plan/test/ISSUES.md`:
1. `ImmutableList.of()` 未创建防御性副本
2. `NumberConverter` 精度丢失时返回原值
3. `ReflectUtil.getEnum(String)` 方法调用错误

---

## 测试规范

### Javadoc 格式
```java
/**
 * 测试目标：[简述测试目的]
 * 测试场景：[具体测试场景]
 * 预期结果：[期望的行为或输出]
 */
@Test
void testMethodName_Scenario() {
    // given
    // when
    // then
}
```

### 测试类型判定
- **单元测试**：无外部依赖，毫秒级执行，位于 `src/test/java/`
- **集成测试**：需要数据库，秒级执行，使用 Testcontainers

### 断言规范
使用 AssertJ 链式断言：
```java
assertThat(result)
    .isNotNull()
    .hasSize(3)
    .contains("expected");
```

### 集成测试命名
- 集成测试类命名：`类名 + IntegrationTest`（如 `JdbcQueryExecutorIntegrationTest`）
- 位于 `nextentity-core/src/test/java/io/github/nextentity/jdbc/` 目录
