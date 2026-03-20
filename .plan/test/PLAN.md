# NextEntity Core 测试计划

## 测试用例编写流程

### 1. 测试环境要求

#### 1.1 开发环境

| 项目 | 要求 | 说明 |
|------|------|------|
| **JDK** | 17+ | 核心模块要求 Java 17+ |
| **Maven** | 3.8+ | 构建工具 |
| **IDE** | IntelliJ IDEA / Eclipse | 推荐 IntelliJ IDEA |
| **内存** | 最低 4GB，推荐 8GB+ | 运行测试和 IDE |
| **磁盘** | 最低 10GB 可用空间 | 包含依赖和测试数据库 |

#### 1.2 依赖版本

| 依赖 | 版本 | 用途 |
|------|------|------|
| JUnit Jupiter | 5.10.0 | 测试框架 |
| Mockito | 5.x | Mock 框架 |
| AssertJ | 3.24.x | 断言库 |
| H2 Database | 2.2.x | 内存数据库（集成测试） |
| Testcontainers | 1.19.x | Docker 容器测试（可选） |
| Spring Boot | 4.0.0 | 集成测试支持 |

#### 1.3 Maven 依赖配置

```xml
<!-- pom.xml -->
<dependencies>
    <!-- 测试框架 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Mock 框架 -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>

    <!-- 断言库 -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>

    <!-- 内存数据库 -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
        <scope>test</scope>
    </dependency>

    <!-- Spring Boot Test（集成测试） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### 1.4 数据库环境

**单元测试**：无需数据库

**集成测试**：

| 方式 | 配置 | 适用场景 |
|------|------|----------|
| H2 内存数据库 | 默认配置 | 快速测试、CI 环境 |
| 本地 MySQL | `application-mysql.yml` | MySQL 特性测试 |
| 本地 PostgreSQL | `application-pg.yml` | PostgreSQL 特性测试 |
| 本地 SQL Server | `application-mssql.yml` | SQL Server 特性测试 |
| Testcontainers | Docker 容器 | 多数据库兼容性测试 |

**H2 默认配置**：
```yaml
# src/test/resources/application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
```

**多数据库配置**：
```yaml
# src/test/resources/application-mysql.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nextentity_test
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root

# src/test/resources/application-pg.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nextentity_test
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: postgres

# src/test/resources/application-mssql.yml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=nextentity_test
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    username: sa
    password: yourStrong(!)Password
```

#### 1.5 IDE 配置

**IntelliJ IDEA**：
1. 安装插件：`JUnit`, `Mockito`, `AssertJ` 代码提示
2. 启用注解处理：`Settings → Build → Compiler → Annotation Processors`
3. 配置 JUnit 5：`Settings → Build, Execution, Deployment → Build Tools → Maven → JUnit`

**Eclipse**：
1. 安装 m2e 插件
2. 确保 JUnit 5 支持

#### 1.6 环境验证

运行以下命令验证环境配置：

```bash
# 验证 Java 版本
java -version

# 验证 Maven 版本
mvn -version

# 验证测试环境（运行现有测试）
mvn test -Dtest=*Test

# 验证依赖下载
mvn dependency:resolve
```

---

### 2. 测试目录结构
```
nextentity-core/src/test/java/io/github/nextentity/
├── core/                    # 核心类测试
│   ├── QueryBuilderTest.java
│   ├── PagesTest.java
│   ├── TuplesTest.java
│   └── ...
├── expression/              # 表达式系统测试
├── util/                    # 工具类测试
└── reflect/                 # 反射工具测试
```

---

### 3. 测试类命名规范
- 测试类名：`原类名 + Test`（如 `QueryBuilderTest`）
- 测试方法名：`test方法名_测试场景`（如 `testSelect_WhereClause`）

### 4. Javadoc 测试目标规范
每个测试方法必须包含 Javadoc，说明测试目标：

```java
/**
 * 测试目标：验证 select 方法能正确构建查询结构
 * <p>
 * 测试场景：
 * 1. 正常情况：传入有效的实体类型
 * 2. 边界条件：传入 null 值
 * 3. 预期结果：返回正确的 Select 对象
 */
@Test
void testSelect_NormalCase() {
    // 测试代码
}
```

**Javadoc 必须包含：**
- **测试目标**：简述本测试要验证什么
- **测试场景**：列出测试的具体场景（可选多场景）
- **预期结果**：期望的行为或输出

### 5. 测试框架
- **JUnit 5** (Jupiter)
- **Mockito** 用于模拟依赖
- **AssertJ** 用于断言

### 6. 测试用例编写步骤
1. **分析源码**：阅读待测试类的源码，理解其职责和边界
2. **识别测试点**：公开方法、边界条件、异常处理
3. **编写测试用例**：
   - 单元测试：测试单个方法的正确性
   - 集成测试：测试多个类之间的协作
4. **执行测试**：`mvn test -Dtest=ClassName`
5. **更新进度**：在本文档中更新测试进度

### 7. 测试目标与原则

**核心原则：测试的最终目标是找出潜在的漏洞和错误，而不是单纯追求代码覆盖率。**

#### 测试重点
1. **边界条件**：null 值、空集合、极值、越界等
2. **异常处理**：非法参数、异常状态恢复
3. **并发安全**：多线程环境下的正确性（如适用）
4. **业务逻辑**：核心业务流程的正确性
5. **集成点**：模块间交互的正确性

#### 测试设计思路
- **等价类划分**：有效输入、无效输入
- **边界值分析**：最小值、最大值、临界值
- **错误推测**：根据经验推测可能的错误点
- **状态转换**：对象状态变化的正确性

#### 覆盖率参考
覆盖率是辅助指标，不是目标：
- 核心类（QueryBuilder, ExpressionBuilder）：建议 80%+
- 工具类：建议 90%+
- 重点：覆盖关键路径和易错点

### 8. 测试失败处理流程

当测试用例不通过时，按以下流程处理：

#### 步骤 1：确认测试用例正确性
- 检查测试逻辑是否符合预期行为
- 检查断言条件是否正确
- 检查测试数据是否合理

#### 步骤 2：问题分类
| 问题类型 | 处理方式           |
|----------|----------------|
| **测试代码错误** | 修复测试用例         |
| **业务代码 Bug** | 修复业务代码(先记录，后期修复) |
| **需求不明确** | 确认需求后决定修改哪方    |
| **设计缺陷** | 评估影响范围，可能需要重构  |

#### 步骤 3：记录问题
在 `.plan/test/ISSUES.md` 中记录：
```markdown
## [日期] 测试失败记录

### 测试方法
`QueryBuilderTest.testSelect_WithNullEntity`

### 失败原因
- 实际行为：抛出 NullPointerException
- 预期行为：返回空结果或抛出 IllegalArgumentException

### 问题分析
[分析根本原因]

### 解决方案
- [ ] 修复业务代码
- [ ] 修复测试用例
- [ ] 其他：______

### 验证结果
[修复后的验证情况]
```

#### 步骤 4：修复与验证
1. 根据问题类型进行修复
2. 重新执行测试：`mvn test -Dtest=ClassName`
3. 更新问题记录状态

#### 原则
- **不要为了通过测试而修改测试**：除非确认测试本身有误
- **每个失败的测试都是发现 Bug 的机会**：认真分析根因
- **保持测试的独立性**：不依赖其他测试的执行顺序

### 9. 测试用例编写顺序

按照 **依赖关系** 和 **重要性** 确定编写顺序：

#### 第一阶段：基础工具类（无依赖）
| 顺序 | 文件 | 原因 |
|------|------|------|
| 1 | `core/util/EmptyArrays.java` | 最基础，无依赖 |
| 2 | `core/util/Sizeable.java` | 接口定义 |
| 3 | `core/util/Lazy.java` | 延迟初始化工具 |
| 4 | `core/util/Exceptions.java` | 异常工具 |
| 5 | `core/util/ImmutableArray.java` | 不可变数组 |
| 6 | `core/util/ImmutableList.java` | 不可变列表 |
| 7 | `core/util/Iterators.java` | 迭代器工具 |
| 8 | `core/util/Maps.java` | Map 工具 |
| 9 | `core/util/Paths.java` | 路径工具 |
| 10 | `core/util/Predicates.java` | 谓词工具 |

#### 第二阶段：反射与元模型基础
| 顺序 | 文件 | 原因 |
|------|------|------|
| 11 | `core/reflect/PrimitiveTypes.java` | 基本类型工具 |
| 12 | `core/reflect/ReflectUtil.java` | 反射工具 |
| 13 | `core/reflect/InstanceInvocationHandler.java` | 动态代理 |
| 14 | `core/reflect/schema/SimpleAttribute.java` | 属性定义 |
| 15 | `core/reflect/schema/SimpleSchema.java` | 模式定义 |
| 16 | `core/meta/ValueConverter.java` | 值转换器 |
| 17 | `core/meta/EnumConverter.java` | 枚举转换器 |

#### 第三阶段：类型转换器
| 顺序 | 文件 | 原因 |
|------|------|------|
| 18 | `core/converter/TypeConverter.java` | 转换器接口 |
| 19 | `core/converter/EnumConverter.java` | 枚举转换 |
| 20 | `core/converter/NumberConverter.java` | 数值转换 |
| 21 | `core/converter/LocalDateTimeConverter.java` | 日期转换 |
| 22 | `core/converter/TypeConverters.java` | 转换器注册表 |

#### 第四阶段：表达式系统（核心）
| 顺序 | 文件 | 原因 |
|------|------|------|
| 23 | `core/expression/EmptyNode.java` | 空节点 |
| 24 | `core/expression/LiteralNode.java` | 字面值节点 |
| 25 | `core/expression/PathNode.java` | 路径节点 |
| 26 | `core/expression/OperatorNode.java` | 操作符节点 |
| 27 | `core/expression/Operator.java` | 操作符定义 |
| 28 | `core/expression/Expressions.java` | 表达式工具 |
| 29 | `core/expression/ExpressionNodes.java` | 节点工具 |
| 30 | `core/expression/SimpleExpressionImpl.java` | 简单表达式 |
| 31 | `core/expression/NumberExpressionImpl.java` | 数值表达式 |
| 32 | `core/expression/StringExpressionImpl.java` | 字符串表达式 |
| 33 | `core/expression/PredicateImpl.java` | 谓词实现 |
| 34 | `core/expression/AbstractExpressionBuilder.java` | 表达式构建器基类 |
| 35 | `core/expression/ExpressionBuilderImpl.java` | 表达式构建器 |
| 36 | `core/expression/QueryStructure.java` | 查询结构 |

#### 第五阶段：元模型系统
| 顺序 | 文件 | 原因 |
|------|------|------|
| 37 | `core/meta/SimpleEntity.java` | 简单实体 |
| 38 | `core/meta/SimpleEntityAttribute.java` | 实体属性 |
| 39 | `core/meta/SimpleJoinAttribute.java` | 关联属性 |
| 40 | `core/meta/AbstractMetamodel.java` | 元模型基类 |
| 41 | `core/meta/SimpleProjection.java` | 投影 |
| 42 | `core/meta/SimpleProjectionAttribute.java` | 投影属性 |

#### 第六阶段：核心查询构建（重点）
| 顺序 | 文件 | 原因 |
|------|------|------|
| 43 | `core/Tuples.java` | 元组工具 |
| 44 | `core/TypeCastUtil.java` | 类型转换 |
| 45 | `core/ExpressionTypeResolver.java` | 表达式类型解析 |
| 46 | `core/PathReference.java` | 路径引用 |
| 47 | `core/WhereImpl.java` | WHERE 实现 |
| 48 | `core/Pages.java` | 分页工具 |
| 49 | `core/QueryBuilder.java` | **查询构建器（核心）** |
| 50 | `core/AbstractCollector.java` | 结果收集器 |
| 51 | `core/SimpleQueryConfig.java` | 查询配置 |
| 52 | `core/SqlLogger.java` | SQL 日志 |

#### 第七阶段：JDBC 实现
| 顺序 | 文件 | 原因 |
|------|------|------|
| 53 | `jdbc/JdbcUtil.java` | JDBC 工具 |
| 54 | `jdbc/AttributeParameter.java` | 属性参数 |
| 55 | `jdbc/NullParameter.java` | 空参数 |
| 56 | `jdbc/QuerySqlStatement.java` | 查询语句 |
| 57 | `jdbc/InsertSqlStatement.java` | 插入语句 |
| 58 | `jdbc/BatchSqlStatement.java` | 批量语句 |
| 59 | `jdbc/AbstractArguments.java` | 抽象参数 |
| 60 | `jdbc/AbstractQuerySqlBuilder.java` | 查询 SQL 构建器 |
| 61 | `jdbc/AbstractUpdateSqlBuilder.java` | 更新 SQL 构建器 |
| 62 | `jdbc/QueryContext.java` | 查询上下文 |
| 63 | `jdbc/JdbcQueryExecutor.java` | JDBC 查询执行器 |
| 64 | `jdbc/JdbcUpdateExecutor.java` | JDBC 更新执行器 |

#### 第八阶段：数据库方言
| 顺序 | 文件 | 原因 |
|------|------|------|
| 65 | `jdbc/MySqlQuerySqlBuilder.java` | MySQL 查询 |
| 66 | `jdbc/MySqlUpdateSqlBuilder.java` | MySQL 更新 |
| 67 | `jdbc/PostgresqlQuerySqlBuilder.java` | PostgreSQL 查询 |
| 68 | `jdbc/PostgreSqlUpdateSqlBuilder.java` | PostgreSQL 更新 |
| 69 | `jdbc/SqlServerQuerySqlBuilder.java` | SQL Server 查询 |
| 70 | `jdbc/SqlServerUpdateSqlBuilder.java` | SQL Server 更新 |
| 71 | `jdbc/SqlDialectSelector.java` | 方言选择器 |

#### 第九阶段：JPA 实现
| 顺序 | 文件 | 原因 |
|------|------|------|
| 72 | `jpa/JpaArguments.java` | JPA 参数 |
| 73 | `jpa/JpaExpressionBuilder.java` | JPA 表达式构建器 |
| 74 | `jpa/JpaQueryExecutor.java` | JPA 查询执行器 |
| 75 | `jpa/JpaNativeQueryExecutor.java` | JPA 原生查询 |
| 76 | `jpa/JpaUpdateExecutor.java` | JPA 更新执行器 |

#### 第十阶段：JPA 元模型
| 顺序 | 文件 | 原因 |
|------|------|------|
| 77 | `meta/jpa/AttributeConverterWrapper.java` | 属性转换包装器 |
| 78 | `meta/jpa/DurationStringConverter.java` | Duration 转换 |
| 79 | `meta/jpa/JpaMetamodel.java` | JPA 元模型 |

### 10. 测试类型划分

#### 单元测试 vs 集成测试判定标准

| 判定维度 | 单元测试 | 集成测试 |
|----------|----------|----------|
| **外部依赖** | 无外部依赖（数据库、网络、文件系统） | 需要外部资源 |
| **测试范围** | 单个类/方法 | 多个组件协作 |
| **执行速度** | 毫秒级 | 秒级 |
| **Mock 使用** | 可使用 Mock 模拟依赖 | 使用真实依赖 |
| **测试目的** | 验证逻辑正确性 | 验证组件集成正确性 |

---

#### 单元测试（Unit Test）

纯逻辑类，无外部依赖，可直接测试：

| 阶段 | 文件 | 测试重点 |
|------|------|----------|
| 工具类 | `core/util/EmptyArrays.java` | 常量正确性 |
| 工具类 | `core/util/Sizeable.java` | 接口定义 |
| 工具类 | `core/util/Lazy.java` | 延迟初始化、线程安全 |
| 工具类 | `core/util/Exceptions.java` | 异常包装 |
| 工具类 | `core/util/ImmutableArray.java` | 不可变性、边界条件 |
| 工具类 | `core/util/ImmutableList.java` | 不可变性、迭代器 |
| 工具类 | `core/util/Iterators.java` | 迭代逻辑 |
| 工具类 | `core/util/Maps.java` | Map 操作 |
| 工具类 | `core/util/Paths.java` | 路径处理 |
| 工具类 | `core/util/Predicates.java` | 谓词逻辑 |
| 反射 | `core/reflect/PrimitiveTypes.java` | 类型判断 |
| 反射 | `core/reflect/ReflectUtil.java` | 反射操作 |
| 反射 | `core/reflect/schema/SimpleAttribute.java` | 属性操作 |
| 反射 | `core/reflect/schema/SimpleSchema.java` | 模式操作 |
| 转换器 | `core/converter/*.java` | 类型转换逻辑 |
| 表达式 | `core/expression/EmptyNode.java` | 空节点行为 |
| 表达式 | `core/expression/LiteralNode.java` | 字面值处理 |
| 表达式 | `core/expression/PathNode.java` | 路径解析 |
| 表达式 | `core/expression/OperatorNode.java` | 操作符处理 |
| 表达式 | `core/expression/Expressions.java` | 表达式构建 |
| 表达式 | `core/expression/SimpleExpressionImpl.java` | 表达式实现 |
| 表达式 | `core/expression/NumberExpressionImpl.java` | 数值表达式 |
| 表达式 | `core/expression/StringExpressionImpl.java` | 字符串表达式 |
| 表达式 | `core/expression/PredicateImpl.java` | 谓词实现 |
| 核心 | `core/Tuples.java` | 元组创建 |
| 核心 | `core/TypeCastUtil.java` | 类型转换 |
| 核心 | `core/ExpressionTypeResolver.java` | 类型解析 |
| 元模型 | `core/meta/SimpleEntity.java` | 实体定义 |
| 元模型 | `core/meta/ValueConverter.java` | 值转换 |
| 元模型 | `core/meta/EnumConverter.java` | 枚举转换 |
| JPA元模型 | `meta/jpa/DurationStringConverter.java` | Duration 转换 |

**单元测试模板：**
```java
class ImmutableListTest {

    /**
     * 测试目标：验证 ImmutableList.of 方法正确创建列表
     * 测试场景：传入多个元素
     * 预期结果：返回包含所有元素的不可变列表
     */
    @Test
    void of_MultipleElements_ReturnsImmutableList() {
        // given
        String[] elements = {"a", "b", "c"};

        // when
        ImmutableList<String> list = ImmutableList.of(elements);

        // then
        assertThat(list).containsExactly("a", "b", "c");
    }
}
```

---

#### 集成测试（Integration Test）

需要数据库或其他外部资源的测试：

| 阶段 | 文件 | 测试重点 | 需要资源 |
|------|------|----------|----------|
| JDBC | `jdbc/JdbcQueryExecutor.java` | 查询执行 | 数据库连接 |
| JDBC | `jdbc/JdbcUpdateExecutor.java` | 更新执行 | 数据库连接 |
| JDBC | `jdbc/AbstractQuerySqlBuilder.java` | SQL 生成 + 执行 | 数据库连接 |
| JDBC | `jdbc/AbstractUpdateSqlBuilder.java` | SQL 生成 + 执行 | 数据库连接 |
| JDBC | `jdbc/QueryContext.java` | 查询上下文 | 数据库连接 |
| MySQL | `jdbc/MySqlQuerySqlBuilder.java` | MySQL 方言 | MySQL 数据库 |
| MySQL | `jdbc/MySqlUpdateSqlBuilder.java` | MySQL 方言 | MySQL 数据库 |
| PostgreSQL | `jdbc/PostgresqlQuerySqlBuilder.java` | PostgreSQL 方言 | PostgreSQL 数据库 |
| PostgreSQL | `jdbc/PostgreSqlUpdateSqlBuilder.java` | PostgreSQL 方言 | PostgreSQL 数据库 |
| SQL Server | `jdbc/SqlServerQuerySqlBuilder.java` | SQL Server 方言 | SQL Server 数据库 |
| SQL Server | `jdbc/SqlServerUpdateSqlBuilder.java` | SQL Server 方言 | SQL Server 数据库 |
| JPA | `jpa/JpaQueryExecutor.java` | JPA 查询 | EntityManager |
| JPA | `jpa/JpaNativeQueryExecutor.java` | 原生查询 | EntityManager |
| JPA | `jpa/JpaUpdateExecutor.java` | JPA 更新 | EntityManager |
| JPA元模型 | `meta/jpa/JpaMetamodel.java` | JPA 元模型解析 | JPA 实体类 |

**集成测试模板：**
```java
@SpringBootTest
class JdbcQueryExecutorIntegrationTest {

    @Autowired
    private JdbcQueryExecutor executor;

    /**
     * 测试目标：验证 JdbcQueryExecutor 能正确执行查询
     * 测试场景：执行简单 SELECT 查询
     * 预期结果：返回正确的实体列表
     */
    @Test
    void execute_SimpleQuery_ReturnsEntities() {
        // given
        QueryStructure<User> query = createTestQuery();

        // when
        List<User> result = executor.execute(query);

        // then
        assertThat(result).isNotEmpty();
    }
}
```

---

#### 可选测试类型（根据实际情况决定）

| 文件 | 单元测试 | 集成测试 | 建议 |
|------|----------|----------|------|
| `core/QueryBuilder.java` | ✅ 构建逻辑 | ✅ 实际执行 | 两者都需要 |
| `core/WhereImpl.java` | ✅ 条件构建 | ✅ SQL 生成 | 两者都需要 |
| `core/Pages.java` | ✅ 分页计算 | - | 单元测试 |
| `core/reflect/InstanceInvocationHandler.java` | ✅ Mock 测试 | - | 单元测试 |
| `jdbc/SqlDialectSelector.java` | ✅ 选择逻辑 | ✅ 方言验证 | 两者都需要 |
| `jpa/JpaExpressionBuilder.java` | ✅ 表达式构建 | ✅ JPQL 验证 | 两者都需要 |

---

#### 测试策略总结

```
┌─────────────────────────────────────────────────────────────┐
│                      测试金字塔                              │
├─────────────────────────────────────────────────────────────┤
│                         /\                                  │
│                        /  \        集成测试（少量）           │
│                       /────\       - JDBC/JPA 执行器         │
│                      /      \      - 数据库方言              │
│                     /────────\                              │
│                    /          \    混合测试（中等）           │
│                   /────────────\   - QueryBuilder            │
│                  /              \  - WhereImpl               │
│                 /────────────────\                           │
│                /                  \ 单元测试（大量）          │
│               /────────────────────\- 工具类                 │
│              /                      - 表达式系统             │
│             /────────────────────────- 转换器                │
└─────────────────────────────────────────────────────────────┘
```

---

### 11. 测试环境配置详细说明

#### 单元测试环境
无需额外配置，直接运行：
```bash
mvn test -Dtest=ClassName
```

#### 集成测试环境

**数据库配置**（`src/test/resources/application-test.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```

**测试 Profile 激活**：
```java
@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest {
    // ...
}
```

**多数据库测试**（可选）：
```yaml
# MySQL
spring.datasource.url: jdbc:tc:mysql:8.0:///testdb

# PostgreSQL
spring.datasource.url: jdbc:tc:postgresql:15:///testdb

# SQL Server
spring.datasource.url: jdbc:tc:mssqlserver:2022:///testdb
```

---

### 12. Mock 使用规范

#### 何时使用 Mock
- 外部依赖（数据库、网络服务）
- 难以构造的复杂对象
- 需要隔离测试的场景

#### Mockito 使用示例

**Mock 对象**：
```java
@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {

    @Mock
    private QueryExecutor queryExecutor;

    @InjectMocks
    private QueryBuilder<User> queryBuilder;

    @Test
    void testExecute_WithMockExecutor() {
        // given
        List<User> expected = List.of(new User(1L, "test"));
        when(queryExecutor.execute(any())).thenReturn(expected);

        // when
        List<User> result = queryBuilder.getList();

        // then
        assertThat(result).isEqualTo(expected);
        verify(queryExecutor).execute(any());
    }
}
```

**Spy 对象**（部分 Mock）：
```java
@Test
void testWithSpy() {
    // given
    ImmutableList<String> list = spy(new ImmutableList<>());
    when(list.size()).thenReturn(100);

    // then
    assertThat(list.size()).isEqualTo(100);
}
```

#### Mock 注意事项
- ❌ 不要过度使用 Mock，优先使用真实对象
- ❌ 不要 Mock 值对象（如 String、Integer）
- ✅ Mock 行为，而不是状态
- ✅ 使用 `@InjectMocks` 自动注入依赖

---

### 13. 参数化测试

#### 使用场景
- 多组输入输出相同的逻辑
- 边界值测试
- 不同数据类型的相同操作

#### JUnit 5 参数化示例

**@ValueSource**（单参数）：
```java
@ParameterizedTest
@ValueSource(strings = {"hello", "world", "test"})
void testStringOperations(String input) {
    assertThat(input).isNotEmpty();
}
```

**@CsvSource**（多参数）：
```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "10, 20, 30",
    "100, 200, 300"
})
void testAdd(int a, int b, int expected) {
    assertThat(a + b).isEqualTo(expected);
}
```

**@MethodSource**（复杂对象）：
```java
@ParameterizedTest
@MethodSource("provideTestData")
void testWithMethodSource(String input, int expected) {
    assertThat(input.length()).isEqualTo(expected);
}

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of("hello", 5),
        Arguments.of("world", 5),
        Arguments.of("", 0)
    );
}
```

**@EnumSource**（枚举测试）：
```java
@ParameterizedTest
@EnumSource(SortOrder.class)
void testSortOrder(SortOrder order) {
    assertThat(order).isNotNull();
}
```

---

### 14. 测试夹具（Fixture）

#### 生命周期注解
```java
class QueryBuilderTest {

    @BeforeAll
    static void beforeAll() {
        // 整个测试类执行前运行一次
        // 用于：初始化共享资源、启动嵌入式数据库
    }

    @BeforeEach
    void setUp() {
        // 每个测试方法前运行
        // 用于：初始化测试数据、重置状态
    }

    @AfterEach
    void tearDown() {
        // 每个测试方法后运行
        // 用于：清理临时数据、关闭连接
    }

    @AfterAll
    static void afterAll() {
        // 整个测试类执行后运行一次
        // 用于：关闭资源、清理环境
    }
}
```

#### 测试数据工厂
```java
class TestFixtures {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        return user;
    }

    public static User createUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    public static List<User> createUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> createUser((long) i, "user" + i))
            .collect(Collectors.toList());
    }
}
```

---

### 15. 断言最佳实践

#### AssertJ 链式断言
```java
// ❌ 多个断言
assertThat(list.size()).isEqualTo(3);
assertThat(list).contains("a");
assertThat(list).doesNotContainNull();

// ✅ 链式断言
assertThat(list)
    .hasSize(3)
    .contains("a")
    .doesNotContainNull()
    .startsWith("a");
```

#### 异常断言
```java
// JUnit 5 方式
@Test
void testException() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> service.process(null)
    );
    assertThat(ex.getMessage()).contains("cannot be null");
}

// AssertJ 方式
@Test
void testExceptionWithAssertJ() {
    assertThatThrownBy(() -> service.process(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot be null");
}
```

#### 软断言（Soft Assertions）
```java
@Test
void testSoftAssertions() {
    User user = new User(1L, "test");

    SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(user.getId()).isEqualTo(1L);
        softly.assertThat(user.getName()).isEqualTo("test");
        softly.assertThat(user.getEmail()).isNotNull(); // 即使失败也继续执行
    });
}
```

---

### 16. CI/CD 集成

#### GitHub Actions 配置
```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: testdb
        ports:
          - 3306:3306

      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: testdb
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run Tests
        run: mvn test

      - name: Generate Coverage Report
        run: mvn jacoco:report

      - name: Upload Coverage
        uses: codecov/codecov-action@v3
```

#### Maven 依赖配置
```xml
<!-- pom.xml -->
<plugins>
    <!-- Surefire: 单元测试 -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.1.2</version>
    </plugin>

    <!-- Failsafe: 集成测试 -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.1.2</version>
        <executions>
            <execution>
                <goals>
                    <goal>integration-test</goal>
                    <goal>verify</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

    <!-- JaCoCo: 覆盖率 -->
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.10</version>
        <executions>
            <execution>
                <goals>
                    <goal>prepare-agent</goal>
                    <goal>report</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

---

### 17. 测试命令速查

| 命令 | 说明 |
|------|------|
| `mvn test` | 运行所有测试 |
| `mvn test -Dtest=ClassName` | 运行指定测试类 |
| `mvn test -Dtest=ClassName#methodName` | 运行指定测试方法 |
| `mvn test -Dgroups=unit` | 运行单元测试（需配置 Tag） |
| `mvn test -Dgroups=integration` | 运行集成测试 |
| `mvn verify` | 运行集成测试 + 验证 |
| `mvn jacoco:report` | 生成覆盖率报告 |
| `mvn test -DskipTests=false` | 强制运行测试 |

---

### 18. 常见问题与解决方案

| 问题 | 解决方案 |
|------|----------|
| 测试间相互影响 | 使用 `@BeforeEach` 重置状态，或使用 `@TestInstance(Lifecycle.PER_METHOD)` |
| 数据库测试数据污染 | 使用 `@Transactional` + `@Rollback`，或使用内存数据库 |
| 测试执行顺序不确定 | 使用 `@Order` 注解（不推荐，应保持测试独立） |
| Mock 不生效 | 检查是否使用 `@ExtendWith(MockitoExtension.class)` |
| 参数化测试中文乱码 | 在 `@CsvSource` 中使用 `encoding = "UTF-8"` |
| 测试超时 | 使用 `@Timeout(value = 1, unit = TimeUnit.SECONDS)` |

---

## 测试进度（nextentity-core/src/main/java/io/github/nextentity）

### api/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| BaseWhereStep.java | 0 | - | - | WHERE 子句构建步骤基类 |
| BooleanExpression.java | 0 | - | - | 布尔类型表达式接口 |
| BooleanPath.java | 0 | - | - | 布尔属性路径接口 |
| Collector.java | 27 | - | - | 结果收集器接口 |
| EntityPath.java | 0 | - | - | 实体路径表达式接口 |
| EntityRootProvider.java | 0 | - | - | 实体根节点提供者接口 |
| Expression.java | 0 | - | - | 表达式接口 |
| ExpressionBuilder.java | 25 | - | - | 表达式构建器（含操作符） |
| FetchStep.java | 7 | - | - | FETCH 子句构建步骤 |
| GroupByStep.java | 5 | - | - | GROUP BY 子句构建步骤 |
| HavingStep.java | 0 | - | - | HAVING 子句构建步骤 |
| NumberExpression.java | 10 | - | - | 数值类型表达式接口 |
| NumberPath.java | 0 | - | - | 数值属性路径接口 |
| OrderByStep.java | 6 | - | - | ORDER BY 子句构建步骤 |
| OrderOperator.java | 12 | - | - | 排序操作符接口 |
| Path.java | 0 | - | - | 属性路径函数式接口 |
| PathExpression.java | 0 | - | - | 路径表达式接口 |
| Predicate.java | 0 | - | - | 布尔谓词接口 |
| Select.java | 0 | - | - | SELECT 查询构建器接口 |
| SimpleExpression.java | 12 | - | - | 简单表达式接口 |
| SortOrder.java | 0 | - | - | 排序方向枚举 |
| StringExpression.java | 19 | - | - | 字符串类型表达式接口 |
| StringPath.java | 0 | - | - | 字符串属性路径接口 |
| SubQueryBuilder.java | 2 | - | - | 子查询构建器接口 |
| TypedExpression.java | 0 | - | - | 类型化表达式接口 |
| Update.java | 0 | - | - | UPDATE/INSERT/DELETE 操作接口 |
| WhereStep.java | 0 | - | - | WHERE 子句构建步骤 |

### api/model/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| EntityRoot.java | 0 | - | - | 实体根节点模型 |
| LockModeType.java | 0 | - | - | 锁模式类型枚举 |
| Order.java | 0 | - | - | 排序规格 |
| Page.java | 0 | - | - | 分页结果容器 |
| PageCollector.java | 1 | 0 | - | 分页收集构建器 |
| Pageable.java | 1 | 0 | - | 分页请求接口 |
| Slice.java | 0 | - | - | 切片结果容器 |
| Sliceable.java | 0 | - | - | 切片请求接口 |
| Tuple.java | 0 | - | - | 通用元组接口 |
| Tuple10.java | 1 | 0 | - | 类型化元组实现（10 元素） |
| Tuple2.java | 2 | 0 | - | 类型化元组实现（2 元素） |
| Tuple3.java | 1 | 0 | - | 类型化元组实现（3 元素） |
| Tuple4.java | 1 | 0 | - | 类型化元组实现（4 元素） |
| Tuple5.java | 1 | 0 | - | 类型化元组实现（5 元素） |
| Tuple6.java | 1 | 0 | - | 类型化元组实现（6 元素） |
| Tuple7.java | 1 | 0 | - | 类型化元组实现（7 元素） |
| Tuple8.java | 1 | 0 | - | 类型化元组实现（8 元素） |
| Tuple9.java | 1 | 0 | - | 类型化元组实现（9 元素） |

### core/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AbstractCollector.java | 9 | 5 | 0% | 抽象结果收集器 |
| ExpressionTypeResolver.java | 7 | 7 | 100% | 表达式类型解析器 |
| OrderOperatorImpl.java | 3 | 3 | 0% | 排序操作符实现 |
| Pages.java | 18 | 15 | 100% | 分页工具类 |
| PathReference.java | 7 | 7 | 100% | 路径引用实现 |
| Persistable.java | 0 | - | - | 可持久化实体标记 |
| QueryBuilder.java | 56 | 45 | 0% | 主查询构建器实现 |
| QueryConfig.java | 0 | - | - | 查询配置 |
| QueryExecutor.java | 0 | - | - | 查询执行器接口 |
| SelectItem.java | 0 | - | - | 选择项包装器 |
| SimpleQueryConfig.java | 4 | 4 | 100% | 简单查询配置实现 |
| SqlLogger.java | 2 | 2 | 100% | SQL 日志工具 |
| Tuples.java | 9 | 9 | 100% | 元组创建工具 |
| TypeCastUtil.java | 6 | 6 | 100% | 类型转换工具 |
| UpdateExecutor.java | 3 | 3 | 0% | 更新执行器接口 |
| Updaters.java | 9 | 8 | 0% | 更新操作工具 |
| WhereImpl.java | 31 | 25 | 0% | WHERE 子句实现 |

### core/annotation/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| EntityAttribute.java | 0 | - | - | 实体属性注解 |
| SubSelect.java | 0 | - | - | 子查询注解 |

### core/converter/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| EnumConverter.java | 3 | 3 | 100% | 枚举类型转换器 |
| LocalDateTimeConverter.java | 3 | 3 | 100% | LocalDateTime 转换器 |
| NumberConverter.java | 6 | 6 | 100% | 数值类型转换器 |
| TypeConverter.java | 3 | - | - | 类型转换器接口 |
| TypeConverters.java | 1 | 1 | 100% | 类型转换器注册表 |

### core/exception/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| BeanReflectiveException.java | 0 | - | - | Bean 反射异常 |
| OptimisticLockException.java | 0 | - | - | 乐观锁异常 |
| RepositoryConfigurationException.java | 0 | - | - | 仓储配置异常 |
| TransactionRequiredException.java | 0 | - | - | 需要事务异常 |
| UncheckedReflectiveException.java | 0 | - | - | 未检查反射异常 |
| UncheckedSQLException.java | 0 | - | - | 未检查 SQL 异常 |

### core/expression/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AbstractExpressionBuilder.java | 62 | 55 | 0% | 抽象表达式构建器 |
| EmptyNode.java | 2 | 2 | 100% | 空表达式节点 |
| ExpressionBuilderImpl.java | 2 | 2 | 0% | 表达式构建器实现 |
| ExpressionNode.java | 5 | - | - | 表达式节点接口 |
| ExpressionNodes.java | 9 | 9 | 100% | 表达式节点工具 |
| ExpressionTree.java | 0 | - | - | 表达式树结构 |
| Expressions.java | 5 | 5 | 100% | 表达式工具 |
| From.java | 0 | - | - | FROM 子句接口 |
| FromEntity.java | 1 | 1 | 0% | FROM 实体实现 |
| FromSubQuery.java | 1 | 1 | 0% | FROM 子查询实现 |
| LiteralNode.java | 5 | 5 | 100% | 字面值节点 |
| NumberExpressionImpl.java | 9 | 9 | 100% | 数值表达式实现 |
| NumberOperatorImpl.java | 12 | 12 | 0% | 数值操作符实现 |
| Operator.java | 5 | - | - | 操作符接口 |
| OperatorNode.java | 6 | 6 | 100% | 操作符节点 |
| OrderImpl.java | 0 | - | - | 排序实现 |
| OrderOperatorImpl.java | 1 | 1 | 0% | 排序操作符实现 |
| PathNode.java | 16 | 15 | 100% | 路径节点 |
| PathOperatorImpl.java | 3 | 3 | 0% | 路径操作符实现 |
| PredicateImpl.java | 9 | 9 | 100% | 谓词实现 |
| QueryStructure.java | 14 | 12 | 0% | 查询结构 |
| SelectEntity.java | 1 | 1 | 0% | 选择实体 |
| SelectExpression.java | 1 | 1 | 0% | 选择表达式 |
| SelectExpressions.java | 1 | 1 | 0% | 选择表达式工具 |
| SelectProjection.java | 1 | 1 | 0% | 选择投影 |
| Selected.java | 0 | - | - | 已选择项 |
| SimpleExpressionImpl.java | 14 | 12 | 100% | 简单表达式实现 |
| SliceImpl.java | 0 | - | - | 切片实现 |
| SortExpression.java | 3 | 3 | 0% | 排序表达式 |
| StringExpressionImpl.java | 5 | 5 | 100% | 字符串表达式实现 |
| StringOperatorImpl.java | 5 | 5 | 0% | 字符串操作符实现 |

### core/meta/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AbstractMetamodel.java | 32 | 28 | 0% | 抽象元模型 |
| DatabaseColumnAttribute.java | 1 | - | - | 数据库列属性 |
| DatabaseType.java | 0 | - | - | 数据库类型 |
| EntityAttribute.java | 2 | - | - | 实体属性 |
| EntitySchema.java | 1 | - | - | 实体模式 |
| EntityType.java | 0 | - | - | 实体类型 |
| EnumConverter.java | 3 | 3 | 100% | 元数据枚举转换器 |
| IdentityValueConverter.java | 5 | 5 | 100% | 标识值转换器 |
| InstantConverter.java | 5 | 5 | 100% | Instant 转换器 |
| JoinAttribute.java | 0 | - | - | 关联属性 |
| Metamodel.java | 0 | - | - | 元模型接口 |
| OrdinalOfEnumType.java | 3 | 3 | 100% | 枚举序号类型 |
| ProjectionAttribute.java | 1 | - | - | 投影属性 |
| ProjectionJoinAttribute.java | 0 | - | - | 投影关联属性 |
| ProjectionType.java | 0 | - | - | 投影类型 |
| SimpleEntity.java | 8 | 8 | 0% | 简单实体 |
| SimpleEntityAttribute.java | 13 | 12 | 0% | 简单实体属性 |
| SimpleJoinAttribute.java | 9 | 8 | 0% | 简单关联属性 |
| SimpleProjection.java | 4 | 4 | 0% | 简单投影 |
| SimpleProjectionAttribute.java | 2 | 2 | 0% | 简单投影属性 |
| SimpleProjectionJoinAttribute.java | 2 | 2 | 0% | 简单投影关联属性 |
| SubQueryEntity.java | 1 | 1 | 0% | 子查询实体 |
| SubQueryEntityType.java | 0 | - | - | 子查询实体类型 |
| ValueConverter.java | 1 | - | - | 值转换器接口 |

### core/reflect/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| InstanceInvocationHandler.java | 6 | 6 | 100% | 实例调用处理器 |
| PrimitiveTypes.java | 4 | 4 | 100% | 基本类型工具 |
| ReflectUtil.java | 11 | 11 | 100% | 反射工具类 |

### core/reflect/schema/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AbstractSchemaAttribute.java | 2 | 2 | 0% | 抽象模式属性 |
| Attribute.java | 3 | - | - | 属性接口 |
| Attributes.java | 0 | - | - | 属性工具 |
| ReflectType.java | 2 | - | - | 反射类型 |
| Schema.java | 4 | - | - | 模式接口 |
| SchemaAttribute.java | 0 | - | - | 模式属性接口 |
| SimpleAttribute.java | 16 | 14 | 100% | 简单属性 |
| SimpleAttributes.java | 2 | 2 | 0% | 简单属性集合 |
| SimpleSchema.java | 4 | 4 | 100% | 简单模式 |

### core/util/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| EmptyArrays.java | 0 | - | - | 空数组常量 |
| Exceptions.java | 2 | 2 | 100% | 异常工具 |
| ImmutableArray.java | 1 | 1 | 100% | 不可变数组实现 |
| ImmutableList.java | 47 | 40 | 95% | 不可变列表实现 (1个bug待修复) |
| Iterators.java | 13 | 13 | 100% | 迭代器工具 |
| Lazy.java | 1 | 1 | 100% | 延迟初始化 |
| Maps.java | 5 | 5 | 100% | Map 工具 |
| Paths.java | 31 | 25 | 0% | 路径工具 |
| Predicates.java | 4 | 4 | 100% | 谓词工具 |
| Sizeable.java | 2 | - | - | 可大小度量接口 |

### jdbc/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AbstractArguments.java | 1 | 1 | 0% | 抽象查询参数 |
| AbstractQuerySqlBuilder.java | 56 | 45 | 0% | 抽象查询 SQL 构建器 |
| AbstractUpdateSqlBuilder.java | 9 | 8 | 0% | 抽象更新 SQL 构建器 |
| Arguments.java | 0 | - | - | 查询参数接口 |
| AttributeParameter.java | 2 | 2 | 0% | 属性参数 |
| BatchSqlStatement.java | 2 | 2 | 0% | 批量 SQL 语句 |
| ConnectionProvider.java | 0 | - | - | 连接提供者 |
| DeepLimitSchemaAttributePaths.java | 3 | 3 | 0% | 深层限制模式属性路径 |
| DefaultSchemaAttributePaths.java | 3 | 3 | 0% | 默认模式属性路径 |
| EmptySchemaAttributePaths.java | 2 | 2 | 0% | 空模式属性路径 |
| InsertSqlStatement.java | 2 | 2 | 0% | 插入 SQL 语句 |
| JdbcArguments.java | 1 | 1 | 0% | JDBC 参数 |
| JdbcQueryExecutor.java | 1 | 1 | 0% | JDBC 查询执行器 |
| JdbcResultCollector.java | 1 | 1 | 0% | JDBC 结果收集器 |
| JdbcUpdateExecutor.java | 10 | 8 | 0% | JDBC 更新执行器 |
| JdbcUpdateSqlBuilder.java | 0 | - | - | JDBC 更新 SQL 构建器 |
| JdbcUtil.java | 5 | 5 | 0% | JDBC 工具类 |
| MySqlQuerySqlBuilder.java | 7 | 6 | 0% | MySQL 查询 SQL 构建器 |
| MySqlUpdateSqlBuilder.java | 2 | 2 | 0% | MySQL 更新 SQL 构建器 |
| NullParameter.java | 3 | 3 | 0% | 空参数 |
| PostgreSqlUpdateSqlBuilder.java | 4 | 4 | 0% | PostgreSQL 更新 SQL 构建器 |
| PostgresqlQuerySqlBuilder.java | 7 | 6 | 0% | PostgreSQL 查询 SQL 构建器 |
| QueryContext.java | 21 | 18 | 0% | 查询上下文 |
| QuerySqlStatement.java | 3 | 3 | 0% | 查询 SQL 语句 |
| SchemaAttributePaths.java | 1 | - | - | 模式属性路径 |
| SelectArrayContext.java | 2 | 2 | 0% | 数组选择上下文 |
| SelectEntityContext.java | 2 | 2 | 0% | 实体选择上下文 |
| SelectPrimitiveContext.java | 2 | 2 | 0% | 基本类型选择上下文 |
| SelectProjectionContext.java | 2 | 2 | 0% | 投影选择上下文 |
| SelectSimpleEntityContext.java | 2 | 2 | 0% | 简单实体选择上下文 |
| SqlDialectSelector.java | 5 | 5 | 0% | SQL 方言选择器 |
| SqlServerQuerySqlBuilder.java | 8 | 7 | 0% | SQL Server 查询 SQL 构建器 |
| SqlServerUpdateSqlBuilder.java | 3 | 3 | 0% | SQL Server 更新 SQL 构建器 |
| SqlStatement.java | 1 | - | - | SQL 语句 |
| TypedParameter.java | 0 | - | - | 类型化参数 |

### jpa/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| JpaArguments.java | 1 | 1 | 0% | JPA 参数 |
| JpaExpressionBuilder.java | 10 | 9 | 0% | JPA 表达式构建器 |
| JpaNativeQueryExecutor.java | 4 | 4 | 0% | JPA 原生查询执行器 |
| JpaQueryExecutor.java | 20 | 16 | 0% | JPA 查询执行器 |
| JpaUpdateExecutor.java | 5 | 5 | 0% | JPA 更新执行器 |
| LockModeTypeAdapter.java | 1 | 1 | 0% | 锁模式类型适配器 |

### meta/jpa/
| 文件 | 方法数 | 需测试方法数 | 测试进度 | 说明 |
|------|--------|--------------|----------|------|
| AttributeConverterWrapper.java | 3 | 3 | 0% | 属性转换器包装器 |
| DurationStringConverter.java | 2 | 2 | 0% | Duration 字符串转换器 |
| JpaMetamodel.java | 20 | 18 | 0% | JPA 元模型实现 |

---

## 统计汇总

| 项目 | 数值 |
|------|------|
| **文件总数** | 185 个 Java 文件 |
| **包结构** | 17 个包 |
| **方法总数** | ~800+ 个 |
| **需测试方法数** | ~650+ 个 |
| **整体测试进度** | ~55% |
| **测试用例总数** | 516 |
| **通过** | 513 |
| **跳过 (Bug相关)** | 3 |
| **失败** | 0 |

### 已完成测试

#### 第一阶段：基础工具类 (core/util)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| EmptyArraysTest | 5 | ✅ 通过 |
| SizeableTest | 5 | ✅ 通过 |
| LazyTest | 8 | ✅ 通过 |
| ExceptionsTest | 5 | ✅ 通过 |
| ImmutableArrayTest | 9 | ✅ 通过 |
| ImmutableListTest | 50 | ⚠️ 1跳过 (Bug #1) |
| IteratorsTest | 21 | ✅ 通过 |
| MapsTest | 11 | ✅ 通过 |
| PredicatesTest | 5 | ✅ 通过 |

#### 第二阶段：反射与元模型基础
| 文件 | 测试数 | 状态 |
|------|--------|------|
| PrimitiveTypesTest | 38 | ✅ 通过 |
| ReflectUtilTest | 11 | ✅ 通过 |
| InstanceInvocationHandlerTest | 8 | ✅ 通过 |
| SimpleAttributeTest | 5 | ✅ 通过 |
| SimpleSchemaTest | 3 | ✅ 通过 |
| EnumConverterTest (meta) | 10 | ✅ 通过 |

#### 第三阶段：类型转换器
| 文件 | 测试数 | 状态 |
|------|--------|------|
| NumberConverterTest | 10 | ⚠️ 1跳过 (Bug #2) |
| EnumConverterTest (converter) | 9 | ⚠️ 1跳过 (Bug #3) |
| LocalDateTimeConverterTest | 8 | ✅ 通过 |
| TypeConvertersTest | 6 | ✅ 通过 |

#### 第四阶段：表达式系统 (core/expression)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| EmptyNodeTest | 4 | ✅ 通过 |
| LiteralNodeTest | 12 | ✅ 通过 |
| PathNodeTest | 17 | ✅ 通过 |
| OperatorNodeTest | 11 | ✅ 通过 |
| OperatorTest | 46 | ✅ 通过 |
| ExpressionsTest | 5 | ✅ 通过 |
| ExpressionNodesTest | 7 | ✅ 通过 |
| SimpleExpressionImplTest | 5 | ✅ 通过 |
| NumberExpressionImplTest | 9 | ✅ 通过 |
| PredicateImplTest | 4 | ✅ 通过 |
| StringExpressionImplTest | 5 | ✅ 通过 |

#### 第五阶段：元模型系统 (core/meta)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| OrdinalOfEnumTypeTest | 8 | ✅ 通过 |
| IdentityValueConverterTest | 8 | ✅ 通过 |
| InstantConverterTest | 7 | ✅ 通过 |

#### 第六阶段：核心查询构建 (core)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| TuplesTest | 8 | ✅ 通过 |
| PagesTest | 9 | ✅ 通过 |
| TypeCastUtilTest | 5 | ✅ 通过 |
| PathReferenceTest | 9 | ✅ 通过 |
| SimpleQueryConfigTest | 3 | ✅ 通过 |
| SqlLoggerTest | 2 | ✅ 通过 |
| ExpressionTypeResolverTest | 9 | ✅ 通过 |

#### 第七阶段：JDBC实现 (jdbc)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| AttributeParameterTest | 3 | ✅ 通过 |
| NullParameterTest | 4 | ✅ 通过 |
| QuerySqlStatementTest | 5 | ✅ 通过 |
| BatchSqlStatementTest | 4 | ✅ 通过 |
| InsertSqlStatementTest | 4 | ✅ 通过 |
| JdbcUtilTest | 5 | ✅ 通过 |
| SchemaAttributePathsTest | 2 | ✅ 通过 |
| DeepLimitSchemaAttributePathsTest | 7 | ✅ 通过 |

#### 第八阶段：数据库方言 (jdbc/dialect)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| MySqlUpdateSqlBuilderTest | 2 | ✅ 通过 |
| PostgreSqlUpdateSqlBuilderTest | 7 | ✅ 通过 |
| SqlServerUpdateSqlBuilderTest | 2 | ✅ 通过 |
| MySqlQuerySqlBuilderTest | 4 | ✅ 通过 |
| PostgresqlQuerySqlBuilderTest | 4 | ✅ 通过 |
| SqlServerQuerySqlBuilderTest | 5 | ✅ 通过 |
| SqlDialectSelectorTest | 4 | ✅ 通过 |

#### 第九阶段：JPA实现 (jpa)
| 文件 | 测试数 | 状态 |
|------|--------|------|
| JpaArgumentsTest | 5 | ✅ 通过 |
| LockModeTypeAdapterTest | 9 | ✅ 通过 |
| DurationStringConverterTest | 7 | ✅ 通过 |
| AttributeConverterWrapperTest | 4 | ✅ 通过 |

### 发现的Bug
详见 `.plan/test/ISSUES.md`
1. ImmutableList.of() 未创建防御性副本
2. NumberConverter 精度丢失时返回原值
3. ReflectUtil.getEnum(String) 方法调用错误

### 测试优先级建议
1. **高优先级**：`QueryBuilder.java`, `AbstractExpressionBuilder.java`, `AbstractQuerySqlBuilder.java` - 核心查询构建逻辑
2. **高优先级**：`Pages.java`, `Tuples.java`, `TypeCastUtil.java` - 工具类
3. **中优先级**：`expression` 包下的实现类 - 表达式系统
4. **中优先级**：`meta` 包下的实现类 - 元模型处理
5. **低优先级**：接口类、注解类、异常类 - 主要为定义，无需测试
