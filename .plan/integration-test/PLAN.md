# 集成测试用例编写计划

## 进度更新 (2026-03-21)

### 已完成的工作

**第一阶段：测试基础设施搭建** ✅
- 创建了 `DbConfig.java` - 数据库配置封装
- 创建了 `DbConfigProvider.java` - 数据库配置提供者接口
- 创建了 `DbConfigs.java` - 统一数据库配置管理
- 创建了 `IntegrationTestProvider.java` - 参数化测试参数提供者
- 创建了 `Mysql.java` - MySQL Testcontainers 配置
- 创建了 `Postgresql.java` - PostgreSQL Testcontainers 配置
- 创建了 `SingleConnectionProvider.java` - 简单连接提供者
- 更新了 `HibernateUnitInfo.java` - 添加测试实体类
- 更新了 `pom.xml` - 添加 `--add-opens` 配置

**1. 查询功能测试（Query Operations）** ✅
- 创建了 `QueryOperationsIntegrationTest.java`，包含以下测试用例：
  - ✅ 单表全字段查询 (`select * from table`)
  - ✅ 单表部分字段查询 (投影查询)
  - ✅ 主键查询 (`findById`)
  - ✅ 查询所有记录 (`findAll`)
  - ✅ 等值条件 (`eq`)
  - ✅ 不等值条件 (`ne`)
  - ✅ 范围条件 (`gt`, `ge`, `lt`, `le`)
  - ✅ IN 条件 (`in`, `notIn`)
  - ✅ NULL 判断 (`isNull`, `isNotNull`)
  - ✅ LIKE 条件 (`like`)
  - ✅ 布尔条件 (`eq(true/false)`)
  - ✅ AND 条件组合
  - ✅ 单字段升序排序
  - ✅ 单字段降序排序
  - ✅ 多字段组合排序
  - ✅ 简单分页 (`limit`, `offset`)
  - ✅ COUNT 计数
  - ✅ 存在性检查 (`exist()`)
  - ✅ 第一条记录查询 (`first()`)

**2. CRUD 操作测试（CRUD Operations）** ✅
- 创建了 `CrudOperationsIntegrationTest.java`，包含以下测试用例：
  - ✅ 单条插入 (`insert`)
  - ✅ 批量插入 (`insertAll`)
  - ✅ 单条更新 (`update`)
  - ✅ 批量更新 (`updateAll`)
  - ✅ 单条删除 (`delete`)
  - ✅ 批量删除 (`deleteAll`)
  - ✅ 条件删除
  - ✅ 插入重复 ID 异常处理
  - ✅ 更新不存在记录异常处理
  - ✅ 删除不存在记录异常处理
  - ✅ 插入 null 字段处理
  - ✅ 空批量插入处理
  - ✅ 枚举类型更新测试

**3. 聚合函数测试（Aggregate Functions）** ✅
- 创建了 `AggregateFunctionsIntegrationTest.java`，包含以下测试用例：
  - ✅ COUNT 计数
  - ✅ COUNT DISTINCT
  - ✅ SUM 求和
  - ✅ AVG 平均值
  - ✅ MAX 最大值
  - ✅ MIN 最小值
  - ✅ GROUP BY 单列
  - ✅ GROUP BY 多列
  - ✅ GROUP BY + COUNT
  - ✅ GROUP BY + SUM
  - ✅ GROUP BY + AVG
  - ✅ 带 WHERE 条件的聚合
  - ✅ GROUP BY 枚举类型

### 待完成的工作

继续按照原计划完成剩余的测试用例。

---

## 背景

NextEntity 项目的核心模块 `nextentity-core` 已有一定的集成测试基础。现有的集成测试采用 Testcontainers 进行多数据库（MySQL、PostgreSQL、SQL Server）测试，使用参数化测试实现跨数据库验证。

**本计划旨在系统地扩展 `nextentity-core` 模块的集成测试覆盖范围**，确保 NextEntity 框架的核心功能在多种数据库环境下都能正确运行。

> **注意：本计划仅针对 `nextentity-core` 模块，不涉及 `nextentity-spring` 模块的测试用例。**

## 测试模块位置

所有新增集成测试用例应**统一**放置在：
```
nextentity-core/src/test/java/io/github/nextentity/integration/
```

> **注意：不需要参考或关注 `io.github.nextentity.integration` 包以外的已有测试用例（如 `io.github.nextentity.jdbc.*`、`io.github.nextentity.jpa.*` 等包下的测试）。**

## 现有测试架构分析

### 测试基础设施

| 组件 | 位置 | 说明 |
|------|------|------|
| 测试基类 | `AbstractIntegrationTest.java` | H2 内存数据库基础测试类（供 `io.github.nextentity.integration` 包使用） |
| 数据库配置 | `DbConfig.java` | 数据库配置封装 |
| 测试数据工厂 | `TestDataFactory.java` | 创建标准测试数据 |
| 测试实体 | `Department.java`, `Employee.java` | 测试用实体类 |

### 现有集成测试用例

**本计划仅关注 `io.github.nextentity.integration` 包下的测试用例**，其他包（如 `io.github.nextentity.jdbc.*`、`io.github.nextentity.jpa.*`）下的测试用例不在本计划范围内。

### 参数化测试模式

```java
// nextentity-core 中的标准参数化测试模式
@ParameterizedTest
@ArgumentsSource(JdbcArgumentsTest.class)
void testMethod(Arguments arguments) throws Exception {
    DbConfig config = (DbConfig) arguments.get()[0];
    // 测试逻辑
}
```

## 待扩展的集成测试范围

### 1. 查询功能测试（Query Operations）

#### 1.1 基础查询测试
- [ ] 单表全字段查询 (`select * from table`)
- [ ] 单表部分字段查询 (投影查询)
- [ ] 主键查询 (`findById`)
- [ ] 查询所有记录 (`findAll`)

#### 1.2 条件查询测试（WHERE 子句）
- [ ] 等值条件 (`eq`)
- [ ] 不等值条件 (`ne`)
- [ ] 范围条件 (`gt`, `ge`, `lt`, `le`)
- [ ] IN 条件 (`in`, `notIn`)
- [ ] NULL 判断 (`isNull`, `isNotNull`)
- [ ] LIKE 条件 (`like`, `notLike`)
- [ ] 布尔条件 (`isTrue`, `isFalse`)

#### 1.3 逻辑运算符测试
- [ ] AND 条件组合
- [ ] OR 条件组合
- [ ] NOT 条件取反
- [ ] 嵌套条件组合

#### 1.4 排序测试（ORDER BY）
- [ ] 单字段升序排序
- [ ] 单字段降序排序
- [ ] 多字段组合排序
- [ ] 动态条件排序

#### 1.5 分页测试（LIMIT/OFFSET）
- [ ] 简单分页 (`limit`, `offset`)
- [ ] Pageable 分页 (`slice`)
- [ ] 分页与排序组合
- [ ] 边界情况测试（超出总记录数的分页）

#### 1.6 聚合函数测试
- [ ] COUNT 计数
- [ ] SUM 求和
- [ ] AVG 平均值
- [ ] MAX 最大值
- [ ] MIN 最小值
- [ ] 分组聚合 (`groupBy`, `having`)

### 2. 关联查询测试（JOIN Operations）

#### 2.1 基础关联测试
- [ ] 一对一关联查询
- [ ] 一对多关联查询
- [ ] 多对一关联查询
- [ ] 自关联查询

#### 2.2 关联类型测试
- [ ] INNER JOIN
- [ ] LEFT JOIN
- [ ] RIGHT JOIN
- [ ] CROSS JOIN

#### 2.3 关联条件测试
- [ ] 关联条件过滤
- [ ] 多层级关联（级联查询）
- [ ] 关联排序

### 3. 更新操作测试（CRUD Operations）

#### 3.1 插入测试
- [ ] 单条插入 (`insert`)
- [ ] 批量插入 (`insertAll`)
- [ ] 插入返回主键
- [ ] 插入默认值处理
- [ ] 插入 NULL 值处理

#### 3.2 更新测试
- [ ] 按主键更新 (`updateById`)
- [ ] 条件更新 (`updateWhere`)
- [ ] 批量更新
- [ ] 乐观锁更新（版本号检查）
- [ ] 部分字段更新

#### 3.3 删除测试
- [ ] 按主键删除 (`deleteById`)
- [ ] 条件删除 (`deleteWhere`)
- [ ] 批量删除
- [ ] 软删除（逻辑删除）

### 4. 子查询测试（Subquery Operations）

- [ ] WHERE 子句中的子查询
- [ ] SELECT 子句中的子查询
- [ ] FROM 子句中的子查询（派生表）
- [ ] EXISTS/NOT EXISTS 子查询
- [ ] IN/NOT IN 子查询
- [ ] 相关子查询

### 5. 类型转换测试（Type Handling）

#### 5.1 基本类型测试
- [ ] 整数类型（Integer, Long）
- [ ] 浮点类型（Float, Double）
- [ ] 布尔类型（Boolean）
- [ ] 字符类型（Character）

#### 5.2 日期时间类型测试
- [ ] Date
- [ ] Time
- [ ] Timestamp
- [ ] LocalDate
- [ ] LocalTime
- [ ] LocalDateTime
- [ ] Instant
- [ ] Duration

#### 5.3 其他类型测试
- [ ] 字符串类型（String）
- [ ] 枚举类型（Enum）
- [ ] 大对象类型（Blob, Clob）
- [ ] JSON 类型（如数据库支持）

### 6. 事务测试（Transaction Tests）

- [ ] 事务提交
- [ ] 事务回滚
- [ ] 事务隔离级别
- [ ] 嵌套事务
- [ ] 只读事务
- [ ] 事务超时

### 7. 异常处理测试（Exception Handling）

- [ ] 约束违反异常（主键冲突、外键约束）
- [ ] 数据类型不匹配异常
- [ ] SQL 语法错误异常
- [ ] 连接异常
- [ ] 超时异常
- [ ] 自定义异常处理

### 8. 数据库方言兼容性测试

#### 8.1 MySQL 特性测试
- [ ] 反引号标识符
- [ ] LIMIT 语法
- [ ] 自增主键
- [ ] 字符集处理

#### 8.2 PostgreSQL 特性测试
- [ ] 双引号标识符
- [ ] 序列主键
- [ ] 布尔类型处理
- [ ] 数组类型（如适用）

#### 8.3 SQL Server 特性测试
- [ ] 方括号标识符
- [ ] TOP 语法
- [ ] 身份列主键
- [ ] 大小写敏感性

### 9. 性能相关测试

- [ ] 批量操作性能
- [ ] 大数据量查询
- [ ] 索引使用验证
- [ ] N+1 查询问题检测

## 测试实现规范

### 测试类结构

```java
package io.github.nextentity.integration;

import io.github.nextentity.integration.db.DbConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <功能描述> 集成测试
 *
 * 覆盖的测试场景：
 * 1. ...
 * 2. ...
 */
public class <Feature>IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(<Feature>IntegrationTest.class);

    @ParameterizedTest
    @ArgumentsSource(<Feature>Provider.class)
    void testScenario(DbConfig config) throws Exception {
        // 1. 准备测试数据
        // 2. 执行测试逻辑
        // 3. 验证结果
        // 4. 清理数据（如需要）
    }
}
```

### 测试数据管理

1. **使用现有的 TestDataFactory** 创建标准测试数据
2. **每个测试用例独立数据**，避免相互影响
3. **使用 @BeforeEach** 初始化共享测试数据
4. **使用 @AfterEach** 清理测试数据

### 断言规范

```java
// 基础断言
assertNotNull(result);
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);

// 集合断言
assertThat(list).hasSize(5);
assertThat(list).extracting("name").contains("Alice", "Bob");

// 异常断言
assertThrows(ExpectedException.class, () -> {
    // 预期抛出异常的代码
});

// SQL 断言（验证生成的 SQL）
assertThat(sql).contains("SELECT");
assertThat(sql).contains("WHERE");
```

## 实施优先级

### 第一阶段：核心功能测试（高优先级）
1. 基础查询测试完善
2. 条件查询测试完善
3. 更新操作测试完善
4. 事务测试

### 第二阶段：高级功能测试（中优先级）
1. 关联查询测试
2. 子查询测试
3. 聚合函数测试
4. 类型转换测试

### 第三阶段：边界和异常测试（低优先级）
1. 异常处理测试
2. 数据库方言兼容性测试
3. 性能相关测试

## 验证方法

1. **本地运行**：`mvn test -pl nextentity-basic/nextentity-core -Dtest=TestClass`
2. **多数据库验证**：确保测试在 MySQL 和 PostgreSQL 上都能通过
3. **CI/CD 集成**：提交前运行完整测试套件
4. **测试覆盖率**：使用 JaCoCo 监控覆盖率（目标：核心模块>80%）

## 参考资料

- 测试包路径：`nextentity-core/src/test/java/io/github/nextentity/integration/`
- 测试基类：`AbstractIntegrationTest.java`
- 测试数据工厂：`TestDataFactory.java`
- 测试实体：`nextentity-core/src/test/java/io/github/nextentity/test/entity/`
