package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.converter.InstantConverter;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DefaultMetamodel")
class DefaultMetamodelTest {

    private DefaultMetamodel metamodel;

    @BeforeEach
    void setUp() {
        metamodel = DefaultMetamodel.of();
    }

    // ── 构造方法测试 ──

    @Nested
    @DisplayName("构造方法")
    class ConstructorTests {

        @Test
        @DisplayName("of() 工厂方法创建实例")
        void shouldCreateInstanceWithOfFactory() {
            DefaultMetamodel instance = DefaultMetamodel.of();

            assertThat(instance).isNotNull();
            assertThat(instance.getResolver()).isNotNull();
        }

        @Test
        @DisplayName("通过 MetamodelConfiguration 构造")
        void shouldCreateInstanceWithConfiguration() {
            DefaultMetamodel instance = new DefaultMetamodel(MetamodelConfiguration.DEFAULT);

            assertThat(instance).isNotNull();
        }

        @Test
        @DisplayName("通过自定义 MetamodelConfiguration 构造")
        void shouldCreateInstanceWithCustomConfiguration() {
            MetamodelConfiguration config = MetamodelConfiguration.of(false, true);
            DefaultMetamodel instance = new DefaultMetamodel(config);

            assertThat(instance).isNotNull();
            DefaultMetamodelResolver resolver = (DefaultMetamodelResolver) instance.getResolver();
            assertThat(resolver.getConfig().interfaceProjectionLazyLoadEnabled()).isFalse();
            assertThat(resolver.getConfig().dtoProjectionLazyLoadEnabled()).isTrue();
        }

        @Test
        @DisplayName("通过 MetamodelResolver 构造")
        void shouldCreateInstanceWithResolver() {
            DefaultMetamodelResolver resolver = DefaultMetamodelResolver.of();
            DefaultMetamodel instance = new DefaultMetamodel(resolver);

            assertThat(instance).isNotNull();
            assertThat(instance.getResolver()).isSameAs(resolver);
        }
    }

    // ── 缓存测试 ──

    @Nested
    @DisplayName("缓存行为")
    class CachingTests {

        @Test
        @DisplayName("相同类型返回相同实例")
        void shouldReturnSameInstanceForSameType() {
            EntityType first = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityType second = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(second).isSameAs(first);
        }

        @Test
        @DisplayName("不同类型返回不同实例")
        void shouldReturnDifferentInstanceForDifferentType() {
            EntityType simple = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityType versioned = metamodel.getEntity(TestEntities.VersionedEntity.class);

            assertThat(versioned).isNotSameAs(simple);
        }

        @Test
        @DisplayName("getEntity 立即解析属性")
        void shouldEagerlyResolveAttributes() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.getAttributes()).isNotEmpty();
        }
    }

    // ── 并发安全测试 ──

    @Nested
    @DisplayName("并发安全")
    class ConcurrencyTests {

        @Test
        @DisplayName("并发调用 getEntity 返回相同实例")
        void shouldHandleConcurrentGetEntity() throws Exception {
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            List<Future<EntityType>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    startLatch.await();
                    return metamodel.getEntity(TestEntities.SimpleEntity.class);
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            EntityType first = futures.getFirst().get();
            for (Future<EntityType> f : futures) {
                assertThat(f.get()).isSameAs(first);
            }
        }

        @Test
        @DisplayName("并发解析多个实体类型结果一致")
        void shouldHandleConcurrentMultipleTypes() throws Exception {
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);

            List<Future<EntityType>> simpleFutures = new ArrayList<>();
            List<Future<EntityType>> deptFutures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                simpleFutures.add(executor.submit(() -> {
                    startLatch.await();
                    return metamodel.getEntity(TestEntities.SimpleEntity.class);
                }));
                deptFutures.add(executor.submit(() -> {
                    startLatch.await();
                    return metamodel.getEntity(TestEntities.DepartmentEntity.class);
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            EntityType firstSimple = simpleFutures.getFirst().get();
            EntityType firstDept = deptFutures.getFirst().get();
            for (int i = 0; i < threadCount; i++) {
                assertThat(simpleFutures.get(i).get()).isSameAs(firstSimple);
                assertThat(deptFutures.get(i).get()).isSameAs(firstDept);
            }
        }
    }

    // ── 实体元数据测试 ──

    @Nested
    @DisplayName("实体元数据")
    class EntityMetadataTests {

        @Test
        @DisplayName("默认表名（驼峰转下划线）")
        void shouldResolveDefaultTableName() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.tableName()).isEqualTo("simple_entity");
        }

        @Test
        @DisplayName("@Table 注解覆盖表名")
        void shouldResolveTableAnnotationName() {
            EntityType entityType = metamodel.getEntity(TestEntities.TableOverrideEntity.class);

            assertThat(entityType.tableName()).isEqualTo("custom_tbl");
        }

        @Test
        @DisplayName("@Entity(name=...) 作为表名回退")
        void shouldEntityNameUsedAsTableNameWhenNoTable() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityNameOverrideEntity.class);

            assertThat(entityType.tableName()).isEqualTo("CustomName");
        }

        @Test
        @DisplayName("@Table 优先于 @Entity(name=...) 作为表名")
        void shouldTableAnnotationTakePrecedenceOverEntityName() {
            EntityType entityType = metamodel.getEntity(TestEntities.BothTableAndEntityNameEntity.class);

            assertThat(entityType.tableName()).isEqualTo("table_name_value");
            assertThat(entityType.entityName()).isEqualTo("EntityNameValue");
        }

        @Test
        @DisplayName("默认实体名称为类简单名")
        void shouldResolveDefaultEntityName() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.entityName()).isEqualTo("SimpleEntity");
        }

        @Test
        @DisplayName("@Entity(name=...) 覆盖实体名称")
        void shouldResolveEntityNameAnnotation() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityNameOverrideEntity.class);

            assertThat(entityType.entityName()).isEqualTo("CustomName");
        }
    }

    // ── ID 属性测试 ──

    @Nested
    @DisplayName("ID 属性")
    class IdAttributeTests {

        @Test
        @DisplayName("@Id 注解检测")
        void shouldDetectIdAnnotatedField() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityBasicAttribute id = entityType.id();

            assertThat(id).isNotNull();
            assertThat(id.isId()).isTrue();
            assertThat(id.name()).isEqualTo("id");
            assertThat(id.type()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("无 @Id 但有 id 字段时回退")
        void shouldFallbackToIdNamedField() {
            EntityType entityType = metamodel.getEntity(TestEntities.NoIdAnnotationEntity.class);
            EntityBasicAttribute id = entityType.id();

            assertThat(id).isNotNull();
            assertThat(id.name()).isEqualTo("id");
        }

        @Test
        @DisplayName("@GeneratedValue 不干扰 ID 检测")
        void shouldGeneratedValueNotInterfereWithId() {
            EntityType entityType = metamodel.getEntity(TestEntities.AutoIdEntity.class);
            EntityBasicAttribute id = entityType.id();

            assertThat(id).isNotNull();
            assertThat(id.isId()).isTrue();
        }

        @Test
        @DisplayName("无 id 字段且无 @Id 注解抛 ConfigurationException")
        void shouldThrowForEntityWithoutId() {
            assertThatThrownBy(() -> metamodel.getEntity(TestEntities.NoIdAtAllEntity.class))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("No ID attribute found");
        }
    }

    // ── Version 属性测试 ──

    @Nested
    @DisplayName("Version 属性")
    class VersionAttributeTests {

        @Test
        @DisplayName("无 @Version 时返回 null")
        void shouldReturnNullVersionWhenNoVersionField() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.version()).isNull();
        }

        @Test
        @DisplayName("@Version 检测")
        void shouldDetectVersionField() {
            EntityType entityType = metamodel.getEntity(TestEntities.VersionedEntity.class);
            EntityBasicAttribute version = entityType.version();

            assertThat(version).isNotNull();
            assertThat(version.isVersion()).isTrue();
            assertThat(version.name()).isEqualTo("version");
            assertThat(version.type()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("id 和 version 是不同属性")
        void shouldVersionAndIdBeDistinct() {
            EntityType entityType = metamodel.getEntity(TestEntities.VersionedEntity.class);

            assertThat(entityType.id()).isNotSameAs(entityType.version());
            assertThat(entityType.id().name()).isEqualTo("id");
            assertThat(entityType.version().name()).isEqualTo("version");
        }

        @Test
        @DisplayName("不支持的 Version 类型抛 ConfigurationException")
        void shouldThrowForUnsupportedVersionType() {
            assertThatThrownBy(() -> metamodel.getEntity(TestEntities.UnsupportedVersionTypeEntity.class))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("not support version type");
        }
    }

    // ── 基本属性测试 ──

    @Nested
    @DisplayName("基本属性")
    class BasicAttributeTests {

        @Test
        @DisplayName("解析所有基本属性")
        void shouldResolveAllBasicAttributes() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.getAttributes()).hasSize(3);
            assertThat(entityType.getAttribute("id")).isNotNull();
            assertThat(entityType.getAttribute("name")).isNotNull();
            assertThat(entityType.getAttribute("age")).isNotNull();
        }

        @Test
        @DisplayName("@Column(name=...) 覆盖列名")
        void shouldResolveColumnNameFromAnnotation() {
            EntityType entityType = metamodel.getEntity(TestEntities.ColumnAnnotatedEntity.class);

            EntityBasicAttribute nameAttr = (EntityBasicAttribute) entityType.getAttribute("name");
            assertThat(nameAttr.columnName()).isEqualTo("full_name");

            EntityBasicAttribute emailAttr = (EntityBasicAttribute) entityType.getAttribute("email");
            assertThat(emailAttr.columnName()).isEqualTo("email_addr");
        }

        @Test
        @DisplayName("字段名自动转下划线列名")
        void shouldDeriveColumnNameFromFieldName() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityBasicAttribute nameAttr = (EntityBasicAttribute) entityType.getAttribute("name");

            assertThat(nameAttr.columnName()).isEqualTo("name");
        }

        @Test
        @DisplayName("@Column(updatable=false) 检测")
        void shouldRespectUpdatableFalse() {
            EntityType entityType = metamodel.getEntity(TestEntities.ColumnAnnotatedEntity.class);

            EntityBasicAttribute nameAttr = (EntityBasicAttribute) entityType.getAttribute("name");
            assertThat(nameAttr.isUpdatable()).isFalse();

            EntityBasicAttribute emailAttr = (EntityBasicAttribute) entityType.getAttribute("email");
            assertThat(emailAttr.isUpdatable()).isTrue();
        }

        @Test
        @DisplayName("ID 默认不可更新")
        void shouldIdBeNonUpdatable() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.id().isUpdatable()).isFalse();
        }

        @Test
        @DisplayName("@Transient 和 transient 字段排除")
        void shouldExcludeTransientFields() {
            EntityType entityType = metamodel.getEntity(TestEntities.TransientFieldEntity.class);

            assertThat(entityType.getAttribute("computedField")).isNull();
            assertThat(entityType.getAttribute("keywordTransientField")).isNull();
            assertThat(entityType.getAttributes()).hasSize(2); // id + name
        }

        @Test
        @DisplayName("getPrimitives 不含关联属性")
        void shouldPrimitivesNotContainAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("id"))
                    .anyMatch(a -> a.name().equals("name"))
                    .anyMatch(a -> a.name().equals("salary"))
                    .anyMatch(a -> a.name().equals("departmentId"));
            assertThat(entityType.getPrimitives())
                    .allSatisfy(a -> assertThat(a).isNotInstanceOf(EntitySchemaAttribute.class));

            assertThat(entityType.getAttributes())
                    .anyMatch(a -> a.name().equals("department"))
                    .hasSize(entityType.getPrimitives().size() + 1);
        }

        @Test
        @DisplayName("enum 字段使用 EnumValueConverter")
        void shouldEnumFieldHaveEnumValueConverter() {
            EntityType entityType = metamodel.getEntity(TestEntities.EnumFieldEntity.class);
            EntityBasicAttribute statusAttr = (EntityBasicAttribute) entityType.getAttribute("status");

            assertThat(statusAttr.valueConvertor()).isInstanceOf(EnumValueConverter.class);
            @SuppressWarnings("unchecked")
            EnumValueConverter<TestEntities.TestStatus> converter =
                    (EnumValueConverter<TestEntities.TestStatus>) statusAttr.valueConvertor();
            assertThat(converter.convertToDatabaseColumn(TestEntities.TestStatus.ACTIVE)).isEqualTo(0);
            assertThat(converter.convertToDatabaseColumn(TestEntities.TestStatus.INACTIVE)).isEqualTo(1);
        }

        @Test
        @DisplayName("LocalDate 字段使用 LocalDateValueConverter")
        void shouldLocalDateFieldHaveLocalDateValueConverter() {
            EntityType entityType = metamodel.getEntity(TestEntities.DateTimeFieldEntity.class);
            EntityBasicAttribute attr = (EntityBasicAttribute) entityType.getAttribute("localDate");

            assertThat(attr.valueConvertor()).isInstanceOf(LocalDateValueConverter.class);
        }

        @Test
        @DisplayName("LocalDateTime 字段使用 LocalDateTimeValueConverter")
        void shouldLocalDateTimeFieldHaveLocalDateTimeValueConverter() {
            EntityType entityType = metamodel.getEntity(TestEntities.DateTimeFieldEntity.class);
            EntityBasicAttribute attr = (EntityBasicAttribute) entityType.getAttribute("localDateTime");

            assertThat(attr.valueConvertor()).isInstanceOf(LocalDateTimeValueConverter.class);
        }

        @Test
        @DisplayName("LocalTime 字段使用 LocalTimeValueConverter")
        void shouldLocalTimeFieldHaveLocalTimeValueConverter() {
            EntityType entityType = metamodel.getEntity(TestEntities.DateTimeFieldEntity.class);
            EntityBasicAttribute attr = (EntityBasicAttribute) entityType.getAttribute("localTime");

            assertThat(attr.valueConvertor()).isInstanceOf(LocalTimeValueConverter.class);
        }

        @Test
        @DisplayName("Instant 字段使用 InstantConverter")
        void shouldInstantFieldHaveInstantConverter() {
            EntityType entityType = metamodel.getEntity(TestEntities.DateTimeFieldEntity.class);
            EntityBasicAttribute attr = (EntityBasicAttribute) entityType.getAttribute("instant");

            assertThat(attr.valueConvertor()).isInstanceOf(InstantConverter.class);
        }

        @Test
        @DisplayName("getAttribute(name) 查找属性")
        void shouldGetAttributeByName() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityAttribute attr = entityType.getAttribute("name");

            assertThat(attr).isNotNull();
            assertThat(attr.name()).isEqualTo("name");
            assertThat(attr.type()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("getAttribute 不存在的属性返回 null")
        void shouldReturnNullForNonExistentAttribute() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.getAttribute("nonExistent")).isNull();
        }
    }

    // ── 关联属性测试 ──

    @Nested
    @DisplayName("关联属性")
    class AssociationAttributeTests {

        @Test
        @DisplayName("@ManyToOne 产生 EntitySchemaAttribute")
        void shouldDetectManyToOneAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute dept = entityType.getAttribute("department");

            assertThat(dept).isInstanceOf(EntitySchemaAttribute.class);
        }

        @Test
        @DisplayName("关联属性 type() 返回目标实体类")
        void shouldAssociationHaveCorrectType() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute dept = entityType.getAttribute("department");

            assertThat(dept.type()).isEqualTo(TestEntities.DepartmentEntity.class);
        }

        @Test
        @DisplayName("LAZY 关联的 getFetchType 返回 LAZY")
        void shouldLazyAssociationReturnLazyFetchType() {
            MetamodelConfiguration config = MetamodelConfiguration.of(true, true);
            DefaultMetamodel metamodel = new DefaultMetamodel(config);
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            assertThat(dept.getFetchType()).isEqualTo(FetchType.LAZY);
        }

        @Test
        @DisplayName("EAGER 关联的 getFetchType 返回 EAGER")
        void shouldEagerAssociationReturnEagerFetchType() {
            EntityType entityType = metamodel.getEntity(TestEntities.EagerAssocEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            assertThat(dept.getFetchType()).isEqualTo(FetchType.EAGER);
        }

        @Test
        @DisplayName("hasLazyAttribute 对 LAZY 实体返回 true")
        void shouldHasLazyAttributeBeTrueForLazyEntity() {
            MetamodelConfiguration config = MetamodelConfiguration.of(true, true);
            DefaultMetamodel metamodel = new DefaultMetamodel(config);
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);

            assertThat(entityType.hasLazyAttribute()).isTrue();
        }

        @Test
        @DisplayName("hasLazyAttribute 对 EAGER 实体返回 false")
        void shouldHasLazyAttributeBeFalseForEagerEntity() {
            EntityType entityType = metamodel.getEntity(TestEntities.EagerAssocEntity.class);

            assertThat(entityType.hasLazyAttribute()).isFalse();
        }

        @Test
        @DisplayName("hasLazyAttribute 对无关联实体返回 false")
        void shouldHasLazyAttributeBeFalseForNoAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.hasLazyAttribute()).isFalse();
        }

        @Test
        @DisplayName("关联 sourceAttribute 正确解析")
        void shouldAssociationSourceAttributeResolved() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            assertThat(dept.getSourceAttribute().name()).isEqualTo("departmentId");
        }

        @Test
        @DisplayName("关联 targetAttribute 正确解析")
        void shouldAssociationTargetAttributeResolved() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            assertThat(dept.getTargetAttribute().name()).isEqualTo("id");
        }

        @Test
        @DisplayName("关联 getTargetEntityType 返回正确目标")
        void shouldAssociationTargetEntityTypeResolved() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            assertThat(dept.getTargetEntityType().type()).isEqualTo(TestEntities.DepartmentEntity.class);
        }

        @Test
        @DisplayName("自引用不导致无限递归")
        void shouldSelfReferenceNotCauseInfiniteRecursion() {
            EntityType entityType = metamodel.getEntity(TestEntities.SelfRefEntity.class);

            assertThat(entityType).isNotNull();
            EntitySchemaAttribute parent = (EntitySchemaAttribute) entityType.getAttribute("parent");
            assertThat(parent).isNotNull();
            assertThat(parent.getTargetEntityType().type()).isEqualTo(TestEntities.SelfRefEntity.class);
        }

        @Test
        @DisplayName("关联在 getAttributes 中但不在 getPrimitives 中")
        void shouldAssociationInAttributesButNotPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute dept = entityType.getAttribute("department");

            assertThat(entityType.getAttributes()).anyMatch(a -> a.name().equals("department"));
            assertThat(entityType.getPrimitives()).noneMatch(a -> a.name().equals("department"));
        }

        @Test
        @DisplayName("@OneToOne 产生 EntitySchemaAttribute")
        void shouldDetectOneToOneAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.OneToOneOwnerEntity.class);
            EntityAttribute profile = entityType.getAttribute("profile");

            assertThat(profile).isInstanceOf(EntitySchemaAttribute.class);
            assertThat(profile.type()).isEqualTo(TestEntities.ProfileEntity.class);
        }

        @Test
        @DisplayName("@OneToOne 关联的 getFetchType 返回 LAZY")
        void shouldOneToOneLazyAssociationReturnLazyFetchType() {
            MetamodelConfiguration config = MetamodelConfiguration.of(true, true);
            DefaultMetamodel metamodel = new DefaultMetamodel(config);
            EntityType entityType = metamodel.getEntity(TestEntities.OneToOneOwnerEntity.class);
            EntitySchemaAttribute profile = (EntitySchemaAttribute) entityType.getAttribute("profile");

            assertThat(profile.getFetchType()).isEqualTo(FetchType.LAZY);
        }

        @Test
        @DisplayName("@OneToOne 关联的 sourceAttribute 和 targetAttribute 正确解析")
        void shouldOneToOneSourceAndTargetAttributeResolved() {
            EntityType entityType = metamodel.getEntity(TestEntities.OneToOneOwnerEntity.class);
            EntitySchemaAttribute profile = (EntitySchemaAttribute) entityType.getAttribute("profile");

            assertThat(profile.getSourceAttribute().name()).isEqualTo("profileId");
            assertThat(profile.getTargetAttribute().name()).isEqualTo("id");
        }
    }

    // ── 嵌套路径测试 ──

    @Nested
    @DisplayName("嵌套路径")
    class NestedPathTests {

        @Test
        @DisplayName("穿越关联的嵌套路径")
        void shouldTraverseNestedPath() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute attr = entityType.getAttribute(List.of("department", "name"));

            assertThat(attr).isNotNull();
            assertThat(attr).isInstanceOf(EntityBasicAttribute.class);
            assertThat(attr.name()).isEqualTo("name");
            assertThat(attr.type()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("单层路径等价于 getAttribute(name)")
        void shouldSinglePathEqualToGetName() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityAttribute byList = entityType.getAttribute(List.of("name"));
            EntityAttribute byName = entityType.getAttribute("name");

            assertThat(byList).isSameAs(byName);
        }

        @Test
        @DisplayName("无效嵌套路径返回 null")
        void shouldInvalidNestedPathReturnNull() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute attr = entityType.getAttribute(List.of("department", "nonExistent"));

            assertThat(attr).isNull();
        }

        @Test
        @DisplayName("declareBy() 返回声明属性的实体 Schema")
        void shouldDeclareByReturnDeclaringEntitySchema() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityAttribute nameAttr = entityType.getAttribute("name");

            assertThat(nameAttr.declareBy()).isSameAs(entityType);
        }

        @Test
        @DisplayName("基本属性 path() 包含单段名称")
        void shouldBasicAttributePathContainSingleSegment() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityAttribute nameAttr = entityType.getAttribute("name");

            assertThat(nameAttr.path().size()).isEqualTo(1);
            assertThat(nameAttr.path().get(0)).isEqualTo("name");
        }

        @Test
        @DisplayName("关联属性 path() 包含单段名称")
        void shouldAssociationAttributePathContainSingleSegment() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntityAttribute deptAttr = entityType.getAttribute("department");

            assertThat(deptAttr.path().size()).isEqualTo(1);
            assertThat(deptAttr.path().get(0)).isEqualTo("department");
        }

        // ── 多层嵌套路径 ──

        @Test
        @DisplayName("三层嵌套路径穿越两个关联")
        void shouldTraverseThreeLevelNestedPath() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute attr = entityType.getAttribute(List.of("department", "company", "name"));

            assertThat(attr).isNotNull();
            assertThat(attr).isInstanceOf(EntityBasicAttribute.class);
            assertThat(attr.name()).isEqualTo("name");
            assertThat(attr.type()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("三层嵌套路径中间层属性正确")
        void shouldThreeLevelIntermediateAttributesCorrect() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute dept = entityType.getAttribute(List.of("department", "company"));

            assertThat(dept).isNotNull();
            assertThat(dept).isInstanceOf(EntitySchemaAttribute.class);
            assertThat(dept.name()).isEqualTo("company");
            assertThat(dept.type()).isEqualTo(TestEntities.CompanyEntity.class);
        }

        @Test
        @DisplayName("三层嵌套路径中间层无效路径返回 null")
        void shouldThreeLevelInvalidPathReturnNull() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute attr = entityType.getAttribute(List.of("department", "company", "nonExistent"));

            assertThat(attr).isNull();
        }

        @Test
        @DisplayName("三层嵌套: department 的 path() 为单段")
        void shouldThreeLevelFirstAssociationPathBeSingleSegment() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute deptAttr = entityType.getAttribute("department");

            assertThat(deptAttr.path().size()).isEqualTo(1);
            assertThat(deptAttr.path().get(0)).isEqualTo("department");
        }

        @Test
        @DisplayName("三层嵌套: department.company 的 path() 为两段")
        void shouldThreeLevelSecondAssociationPathBeTwoSegments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute companyAttr = entityType.getAttribute(List.of("department", "company"));

            assertThat(companyAttr.path().size()).isEqualTo(2);
            assertThat(companyAttr.path().get(0)).isEqualTo("department");
            assertThat(companyAttr.path().get(1)).isEqualTo("company");
        }

        @Test
        @DisplayName("三层嵌套: department.company.name 的 path() 为三段")
        void shouldThreeLevelDeepBasicAttributePathBeThreeSegments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute nameAttr = entityType.getAttribute(List.of("department", "company", "name"));

            assertThat(nameAttr.path().size()).isEqualTo(3);
            assertThat(nameAttr.path().get(0)).isEqualTo("department");
            assertThat(nameAttr.path().get(1)).isEqualTo("company");
            assertThat(nameAttr.path().get(2)).isEqualTo("name");
        }

        @Test
        @DisplayName("两层嵌套: department.name 的 path() 为两段")
        void shouldTwoLevelNestedAttributePathBeTwoSegments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute nameAttr = entityType.getAttribute(List.of("department", "name"));

            assertThat(nameAttr.path().size()).isEqualTo(2);
            assertThat(nameAttr.path().get(0)).isEqualTo("department");
            assertThat(nameAttr.path().get(1)).isEqualTo("name");
        }

        @Test
        @DisplayName("三层嵌套: 中间层 declareBy() 返回父关联属性")
        void shouldThreeLevelIntermediateDeclareByReturnParentAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute dept = entityType.getAttribute("department");
            EntityAttribute company = entityType.getAttribute(List.of("department", "company"));

            assertThat(company.declareBy()).isSameAs(dept);
        }

        @Test
        @DisplayName("三层嵌套: 深层基本属性 declareBy() 返回父关联属性")
        void shouldThreeLevelDeepBasicDeclareByReturnParentAssociation() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
            EntityAttribute company = entityType.getAttribute(List.of("department", "company"));
            EntityAttribute name = entityType.getAttribute(List.of("department", "company", "name"));

            assertThat(name.declareBy()).isSameAs(company);
        }
    }

    // ── 投影解析测试 ──

    @Nested
    @DisplayName("投影解析")
    class ProjectionTests {

        @Nested
        @DisplayName("接口投影")
        class InterfaceProjectionTests {

            @Test
            @DisplayName("基本接口投影解析")
            void shouldResolveInterfaceProjection() {
                EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.SimpleProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.type()).isEqualTo(TestProjections.SimpleProjection.class);
                assertThat(projection.getEntitySchema()).isSameAs(entityType);
                assertThat(projection.getAttributes()).hasSize(2);
            }

            @Test
            @DisplayName("@EntityPath 接口投影解析嵌套路径")
            void shouldResolveInterfaceProjectionWithEntityPath() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.EntityPathProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.getAttributes()).hasSize(3);

                ProjectionAttribute deptNameAttr = projection.getAttribute("departmentName");
                assertThat(deptNameAttr).isInstanceOf(ProjectionBasicAttribute.class);
                EntityBasicAttribute entityAttr = ((ProjectionBasicAttribute) deptNameAttr).getEntityAttribute();
                assertThat(entityAttr.name()).isEqualTo("name");
                assertThat(entityAttr.declareBy().type()).isEqualTo(TestEntities.DepartmentEntity.class);
            }

            @Test
            @DisplayName("投影基本属性映射到实体属性")
            void shouldProjectionBasicAttributeMapToEntityAttribute() {
                EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.SimpleProjection.class);
                ProjectionAttribute idAttr = projection.getAttribute("id");

                assertThat(idAttr).isInstanceOf(ProjectionBasicAttribute.class);
                assertThat(((ProjectionBasicAttribute) idAttr).getEntityAttribute().name()).isEqualTo("id");
            }

            @Test
            @DisplayName("投影缓存")
            void shouldProjectionCaching() {
                EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
                ProjectionSchema first = entityType.getProjection(TestProjections.SimpleProjection.class);
                ProjectionSchema second = entityType.getProjection(TestProjections.SimpleProjection.class);

                assertThat(second).isSameAs(first);
            }
        }

        @Nested
        @DisplayName("类投影")
        class ClassProjectionTests {

            @Test
            @DisplayName("JavaBean 类投影解析")
            void shouldResolveClassProjection() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JavaBeanProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.getAttributes()).hasSize(3);
                assertThat(projection.getAttribute("id")).isNotNull();
                assertThat(projection.getAttribute("name")).isNotNull();
                assertThat(projection.getAttribute("departmentName")).isNotNull();
            }

            @Test
            @DisplayName("@EntityPath 嵌套路径映射")
            void shouldEntityPathMapThroughAssociation() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JavaBeanProjection.class);
                ProjectionAttribute deptNameAttr = projection.getAttribute("departmentName");

                assertThat(deptNameAttr).isInstanceOf(ProjectionBasicAttribute.class);
                EntityBasicAttribute entityAttr = ((ProjectionBasicAttribute) deptNameAttr).getEntityAttribute();
                assertThat(entityAttr.name()).isEqualTo("name");
                assertThat(entityAttr.declareBy().type()).isEqualTo(TestEntities.DepartmentEntity.class);
            }
        }

        @Nested
        @DisplayName("Record 投影")
        class RecordProjectionTests {

            @Test
            @DisplayName("record 投影解析")
            void shouldResolveRecordProjection() {
                EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.SimpleRecordProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.getAttributes()).hasSize(2);
            }

            @Test
            @DisplayName("带关联的 record 投影")
            void shouldResolveRecordProjectionWithAssociation() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.RecordProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.getAttributes()).hasSize(3); // id, name, salary
            }
        }

        @Nested
        @DisplayName("@Join 投影")
        class JoinProjectionTests {

            @Test
            @DisplayName("@Join 产生 ProjectionJoinAttribute")
            void shouldResolveJoinProjection() {
                EntityType entityType = metamodel.getEntity(TestEntities.OrderEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JoinProjection.class);
                ProjectionAttribute customerAttr = projection.getAttribute("customer");

                assertThat(customerAttr).isInstanceOf(ProjectionJoinAttribute.class);
            }

            @Test
            @DisplayName("@Join 的 getTargetEntityType")
            void shouldJoinProjectionTargetEntityType() {
                EntityType entityType = metamodel.getEntity(TestEntities.OrderEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JoinProjection.class);
                ProjectionJoinAttribute customerAttr = (ProjectionJoinAttribute) projection.getAttribute("customer");

                assertThat(customerAttr.getTargetEntityType().type()).isEqualTo(TestEntities.CustomerEntity.class);
            }

            @Test
            @DisplayName("@Join 的 sourceAttribute")
            void shouldJoinSourceAttributeResolved() {
                EntityType entityType = metamodel.getEntity(TestEntities.OrderEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JoinProjection.class);
                ProjectionJoinAttribute customerAttr = (ProjectionJoinAttribute) projection.getAttribute("customer");

                assertThat(customerAttr.getSourceAttribute().name()).isEqualTo("customerId");
            }

            @Test
            @DisplayName("@Join 的 targetAttribute")
            void shouldJoinTargetAttributeResolved() {
                EntityType entityType = metamodel.getEntity(TestEntities.OrderEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.JoinProjection.class);
                ProjectionJoinAttribute customerAttr = (ProjectionJoinAttribute) projection.getAttribute("customer");

                assertThat(customerAttr.getTargetAttribute().name()).isEqualTo("id");
            }
        }

        @Nested
        @DisplayName("嵌套投影")
        class NestedProjectionTests {

            @Test
            @DisplayName("嵌套接口投影产生 ProjectionSchemaAttribute")
            void shouldNestedInterfaceProjectionProduceSchemaAttribute() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.NestedInterfaceProjection.class);
                ProjectionAttribute deptAttr = projection.getAttribute("department");

                assertThat(deptAttr).isInstanceOf(ProjectionSchemaAttribute.class);
            }

            @Test
            @DisplayName("嵌套投影属性链接到实体关联属性")
            void shouldNestedProjectionLinkToEntityAssociation() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.NestedInterfaceProjection.class);
                ProjectionSchemaAttribute deptAttr = (ProjectionSchemaAttribute) projection.getAttribute("department");

                assertThat(deptAttr.getEntityAttribute().name()).isEqualTo("department");
                assertThat(deptAttr.getEntityAttribute()).isInstanceOf(EntitySchemaAttribute.class);
            }

            @Test
            @DisplayName("嵌套投影 hasLazyAttribute")
            void shouldNestedProjectionHasLazyAttribute() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.NestedInterfaceProjection.class);

                assertThat(projection.hasLazyAttribute()).isTrue();
            }
        }

        @Nested
        @DisplayName("@Fetch(LAZY) 投影")
        class FetchLazyProjectionTests {

            @Test
            @DisplayName("@Fetch(LAZY) 投影关联")
            void shouldProjectionWithLazyAssociation() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.FetchLazyProjection.class);
                ProjectionAttribute deptAttr = projection.getAttribute("department");

                assertThat(deptAttr).isInstanceOf(ProjectionSchemaAttribute.class);
                assertThat(((ProjectionSchemaAttribute) deptAttr).getFetchType()).isEqualTo(FetchType.LAZY);
                assertThat(projection.hasLazyAttribute()).isTrue();
            }
        }

        @Nested
        @DisplayName("多层嵌套投影")
        class DeepNestedProjectionTests {

            @Test
            @DisplayName("三层嵌套接口投影解析")
            void shouldResolveDeepNestedInterfaceProjection() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);

                assertThat(projection).isNotNull();
                assertThat(projection.getAttributes()).hasSize(3);
                assertThat(projection.getAttribute("id")).isNotNull();
                assertThat(projection.getAttribute("name")).isNotNull();
                assertThat(projection.getAttribute("department")).isNotNull();
            }

            @Test
            @DisplayName("三层嵌套投影第一层产生 ProjectionSchemaAttribute")
            void shouldDeepNestedFirstLevelBeSchemaAttribute() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);
                ProjectionAttribute deptAttr = projection.getAttribute("department");

                assertThat(deptAttr).isInstanceOf(ProjectionSchemaAttribute.class);
            }

            @Test
            @DisplayName("三层嵌套投影第二层产生 ProjectionSchemaAttribute")
            void shouldDeepNestedSecondLevelBeSchemaAttribute() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);
                ProjectionSchemaAttribute deptAttr = (ProjectionSchemaAttribute) projection.getAttribute("department");

                ProjectionAttribute companyAttr = deptAttr.schema().getAttribute("company");
                assertThat(companyAttr).isInstanceOf(ProjectionSchemaAttribute.class);
            }

            @Test
            @DisplayName("三层嵌套投影第三层包含基本属性")
            void shouldDeepNestedThirdLevelContainBasicAttributes() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);
                ProjectionSchemaAttribute deptAttr = (ProjectionSchemaAttribute) projection.getAttribute("department");
                ProjectionSchemaAttribute companyAttr = (ProjectionSchemaAttribute) deptAttr.schema().getAttribute("company");

                ProjectionAttribute nameAttr = companyAttr.schema().getAttribute("name");
                assertThat(nameAttr).isInstanceOf(ProjectionBasicAttribute.class);
                assertThat(nameAttr.name()).isEqualTo("name");
            }

            @Test
            @DisplayName("三层嵌套投影 @EntityPath department.company.name 解析")
            void shouldDeepNestedEntityPathResolveCorrectly() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedEntityPathProjection.class);

                ProjectionAttribute companyNameAttr = projection.getAttribute("companyName");
                assertThat(companyNameAttr).isInstanceOf(ProjectionBasicAttribute.class);
                EntityBasicAttribute entityAttr = ((ProjectionBasicAttribute) companyNameAttr).getEntityAttribute();
                assertThat(entityAttr.name()).isEqualTo("name");
                assertThat(entityAttr.declareBy().type()).isEqualTo(TestEntities.CompanyEntity.class);
            }

            @Test
            @DisplayName("三层嵌套投影 @Fetch(LAZY) 属性的 getFetchType 返回 LAZY")
            void shouldDeepNestedProjectionFetchLazyAttributeReturnLazy() {
                MetamodelConfiguration config = MetamodelConfiguration.of(true, true);
                DefaultMetamodel lazyMetamodel = new DefaultMetamodel(config);
                EntityType entityType = lazyMetamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);

                ProjectionAttribute deptAttr = projection.getAttribute("department");
                assertThat(deptAttr).isInstanceOf(ProjectionSchemaAttribute.class);
                assertThat(((ProjectionSchemaAttribute) deptAttr).getFetchType()).isEqualTo(FetchType.LAZY);
            }

            @Test
            @DisplayName("三层嵌套投影 path() 逐层递增")
            void shouldDeepNestedProjectionPathIncrementPerLevel() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedProjection.class);

                ProjectionSchemaAttribute deptAttr = (ProjectionSchemaAttribute) projection.getAttribute("department");
                assertThat(deptAttr.path().size()).isEqualTo(1);
                assertThat(deptAttr.path().get(0)).isEqualTo("department");

                ProjectionSchemaAttribute companyAttr = (ProjectionSchemaAttribute) deptAttr.schema().getAttribute("company");
                assertThat(companyAttr.path().size()).isEqualTo(2);
                assertThat(companyAttr.path().get(0)).isEqualTo("department");
                assertThat(companyAttr.path().get(1)).isEqualTo("company");

                ProjectionAttribute nameAttr = companyAttr.schema().getAttribute("name");
                assertThat(nameAttr.path().size()).isEqualTo(3);
                assertThat(nameAttr.path().get(0)).isEqualTo("department");
                assertThat(nameAttr.path().get(1)).isEqualTo("company");
                assertThat(nameAttr.path().get(2)).isEqualTo("name");
            }

            @Test
            @DisplayName("三层嵌套投影 @EntityPath 属性的 path() 使用投影属性名")
            void shouldDeepNestedEntityPathAttributePathUseProjectionName() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedEntityPathProjection.class);

                ProjectionAttribute deptNameAttr = projection.getAttribute("departmentName");
                assertThat(deptNameAttr.path().size()).isEqualTo(1);
                assertThat(deptNameAttr.path().get(0)).isEqualTo("departmentName");

                ProjectionAttribute companyNameAttr = projection.getAttribute("companyName");
                assertThat(companyNameAttr.path().size()).isEqualTo(1);
                assertThat(companyNameAttr.path().get(0)).isEqualTo("companyName");
            }

            @Test
            @DisplayName("三层嵌套投影 @EntityPath 映射的实体属性路径正确")
            void shouldDeepNestedEntityPathMapToCorrectEntityPath() {
                EntityType entityType = metamodel.getEntity(TestEntities.EmployeeWithDeptCompanyEntity.class);
                ProjectionSchema projection = entityType.getProjection(TestProjections.DeepNestedEntityPathProjection.class);

                ProjectionBasicAttribute deptNameAttr = (ProjectionBasicAttribute) projection.getAttribute("departmentName");
                EntityBasicAttribute deptNameEntityAttr = deptNameAttr.getEntityAttribute();
                assertThat(deptNameEntityAttr.name()).isEqualTo("name");
                assertThat(deptNameEntityAttr.path().size()).isEqualTo(2);
                assertThat(deptNameEntityAttr.path().get(0)).isEqualTo("department");
                assertThat(deptNameEntityAttr.path().get(1)).isEqualTo("name");

                ProjectionBasicAttribute companyNameAttr = (ProjectionBasicAttribute) projection.getAttribute("companyName");
                EntityBasicAttribute companyNameEntityAttr = companyNameAttr.getEntityAttribute();
                assertThat(companyNameEntityAttr.name()).isEqualTo("name");
                assertThat(companyNameEntityAttr.path().size()).isEqualTo(3);
                assertThat(companyNameEntityAttr.path().get(0)).isEqualTo("department");
                assertThat(companyNameEntityAttr.path().get(1)).isEqualTo("company");
                assertThat(companyNameEntityAttr.path().get(2)).isEqualTo("name");
            }
        }
    }

    @Nested
    @DisplayName("嵌入属性")
    class EmbeddedAttributeTests {

        @Test
        @DisplayName("DefaultMetamodelResolver 能检测到 @Embedded 注解")
        void shouldResolverDetectEmbeddedAnnotation() {
            DefaultMetamodelResolver resolver = DefaultMetamodelResolver.of();
            var accessors = DefaultAccessor.of(TestEntities.EntityWithEmbedded.class);
            Accessor addressAccessor = accessors.stream()
                    .filter(a -> a.name().equals("address"))
                    .findFirst()
                    .orElseThrow();

            assertThat(resolver.isEmbedded(addressAccessor)).isTrue();
        }

        @Test
        @DisplayName("@Embedded 字段应被解析为 EntitySchemaAttribute")
        void shouldEmbeddedFieldBeResolvedAsSchemaAttribute() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityAttribute addressAttr = entityType.getAttribute("address");

            assertThat(addressAttr).isInstanceOf(EntitySchemaAttribute.class);
        }

        @Test
        @DisplayName("@Embedded 字段的 isEmbedded() 应返回 true")
        void shouldEmbeddedFieldIsEmbeddedReturnTrue() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityAttribute addressAttr = entityType.getAttribute("address");

            assertThat(addressAttr).isInstanceOf(MetamodelSchema.class);
            assertThat(((MetamodelSchema<?>) addressAttr).isEmbedded()).isTrue();
        }

        @Test
        @DisplayName("@Embedded 字段的内部子属性应被展开到 getPrimitives")
        void shouldExpandEmbeddedFieldInnerAttributesToPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("street"))
                    .anyMatch(a -> a.name().equals("city"))
                    .anyMatch(a -> a.name().equals("zipCode"));
        }

        @Test
        @DisplayName("@Embedded 字段自身不应作为整体出现在 getPrimitives 中")
        void shouldEmbeddedFieldItselfNotAppearInPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);

            assertThat(entityType.getPrimitives())
                    .noneMatch(a -> a.name().equals("address"));
        }

        @Test
        @DisplayName("嵌套 @Embedded 字段应递归展开内部子属性到 getPrimitives")
        void shouldNestedEmbeddedFieldExpandRecursively() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("email"))
                    .anyMatch(a -> a.name().equals("phone"))
                    .anyMatch(a -> a.name().equals("street"))
                    .anyMatch(a -> a.name().equals("city"))
                    .anyMatch(a -> a.name().equals("zipCode"))
                    .noneMatch(a -> a.name().equals("contactInfo"))
                    .noneMatch(a -> a.name().equals("address"));
        }
    }

    // ── @AttributeOverride / @AttributeOverrides 测试 ──

    @Nested
    @DisplayName("@AttributeOverride / @AttributeOverrides")
    class AttributeOverrideTests {

        @Test
        @DisplayName("@AttributeOverride 覆盖嵌入字段的列名")
        void shouldAttributeOverrideChangeColumnNameOfEmbeddedField() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);

            // 嵌入子属性通过 getPrimitives() 获取
            EntityBasicAttribute firstName = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("firstName"))
                    .findFirst().orElse(null);
            assertThat(firstName).isNotNull();
            // 当前缺陷: @AttributeOverride 未处理，列名仍为默认的 "first_name"
            assertThat(firstName.columnName()).isEqualTo("first_name_ov");
        }

        @Test
        @DisplayName("@AttributeOverride 未覆盖的嵌入字段保持默认列名")
        void shouldNonOverriddenFieldKeepDefaultColumnName() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);

            EntityBasicAttribute lastName = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("lastName"))
                    .findFirst().orElse(null);
            assertThat(lastName).isNotNull();
            assertThat(lastName.columnName()).isEqualTo("last_name");
        }

        @Test
        @DisplayName("@AttributeOverrides 覆盖多个嵌入字段的列名")
        void shouldAttributeOverridesChangeMultipleColumnNames() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverrides.class);

            EntityBasicAttribute street = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("street"))
                    .findFirst().orElse(null);
            assertThat(street).isNotNull();
            // 当前缺陷: @AttributeOverrides 未处理，列名仍为默认的 "street"
            assertThat(street.columnName()).isEqualTo("addr_street");

            EntityBasicAttribute city = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("city"))
                    .findFirst().orElse(null);
            assertThat(city).isNotNull();
            // 当前缺陷: @AttributeOverrides 未处理，列名仍为默认的 "city"
            assertThat(city.columnName()).isEqualTo("addr_city");
        }

        @Test
        @DisplayName("@AttributeOverrides 未覆盖的字段保持默认列名")
        void shouldNonOverriddenFieldKeepDefaultColumnNameInOverridesEntity() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverrides.class);

            EntityBasicAttribute zipCode = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("zipCode"))
                    .findFirst().orElse(null);
            assertThat(zipCode).isNotNull();
            assertThat(zipCode.columnName()).isEqualTo("zip_code");
        }

        @Test
        @DisplayName("@AttributeOverride 嵌入字段出现在 getPrimitives 中")
        void shouldOverriddenEmbeddedFieldsAppearInPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("firstName"))
                    .anyMatch(a -> a.name().equals("lastName"));
            assertThat(entityType.getPrimitives())
                    .noneMatch(a -> a.name().equals("fullName"));
        }

        @Test
        @DisplayName("@AttributeOverrides 嵌入字段出现在 getPrimitives 中")
        void shouldAttributeOverridesEmbeddedFieldsAppearInPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverrides.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("street"))
                    .anyMatch(a -> a.name().equals("city"))
                    .anyMatch(a -> a.name().equals("zipCode"));
            assertThat(entityType.getPrimitives())
                    .noneMatch(a -> a.name().equals("address"));
        }

        @Test
        @DisplayName("@AttributeOverride 嵌入属性也有正确的 getAttribute(name)")
        void shouldOverriddenEmbeddedFieldResolvedByGetAttribute() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);

            EntityAttribute fullName = entityType.getAttribute("fullName");
            assertThat(fullName).isInstanceOf(EntitySchemaAttribute.class);
            assertThat(((MetamodelSchema<?>) fullName).isEmbedded()).isTrue();
        }

        @Test
        @DisplayName("嵌入子属性（非 id 字段）不是 id 也不是 version")
        void shouldEmbeddedFieldPrimitivesNotBeIdOrVersion() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);

            assertThat(entityType.getPrimitives())
                    .filteredOn(a -> !a.name().equals("id") && !a.name().equals("name"))
                    .allSatisfy(a -> {
                        EntityBasicAttribute basic = (EntityBasicAttribute) a;
                        assertThat(basic.isId()).isFalse();
                        assertThat(basic.isVersion()).isFalse();
                    });
        }

        // ── 多层嵌套 @AttributeOverride 测试 ──

        @Test
        @DisplayName("多层嵌套: 一层深度 @AttributeOverride(email) 覆盖列名")
        void shouldNestedAttributeOverrideOneLevelDeep() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedAttributeOverride.class);

            // email 在 ContactInfo 中，被 @AttributeOverride(name="email", column=@Column(name="contact_email")) 覆盖
            EntityBasicAttribute email = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("email"))
                    .findFirst().orElse(null);
            assertThat(email).isNotNull();
            // 当前缺陷: 多层嵌套 @AttributeOverride 未处理，列名仍为默认的 "email"
            assertThat(email.columnName()).isEqualTo("contact_email");
        }

        @Test
        @DisplayName("多层嵌套: 两层深度 @AttributeOverride(address.street) 覆盖列名")
        void shouldNestedAttributeOverrideTwoLevelsDeep() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedAttributeOverride.class);

            // street 在 ContactInfo.address 中，被 @AttributeOverride(name="address.street", column=@Column(name="deep_street")) 覆盖
            EntityBasicAttribute street = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("street"))
                    .findFirst().orElse(null);
            assertThat(street).isNotNull();
            // 当前缺陷: 多层嵌套 @AttributeOverride 未处理，列名仍为默认的 "street"
            assertThat(street.columnName()).isEqualTo("deep_street");
        }

        @Test
        @DisplayName("多层嵌套: 未覆盖字段保持默认列名")
        void shouldNestedAttributeOverrideKeepDefaultForNonOverridden() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedAttributeOverride.class);

            EntityBasicAttribute phone = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("phone"))
                    .findFirst().orElse(null);
            assertThat(phone).isNotNull();
            assertThat(phone.columnName()).isEqualTo("phone");

            EntityBasicAttribute city = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("city"))
                    .findFirst().orElse(null);
            assertThat(city).isNotNull();
            assertThat(city.columnName()).isEqualTo("city");

            EntityBasicAttribute zipCode = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("zipCode"))
                    .findFirst().orElse(null);
            assertThat(zipCode).isNotNull();
            assertThat(zipCode.columnName()).isEqualTo("zip_code");
        }

        @Test
        @DisplayName("多层嵌套: 嵌入字段展开到 getPrimitives 中")
        void shouldNestedAttributeOverrideExpandPrimitives() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedAttributeOverride.class);

            assertThat(entityType.getPrimitives())
                    .anyMatch(a -> a.name().equals("email"))
                    .anyMatch(a -> a.name().equals("phone"))
                    .anyMatch(a -> a.name().equals("street"))
                    .anyMatch(a -> a.name().equals("city"))
                    .anyMatch(a -> a.name().equals("zipCode"));
            assertThat(entityType.getPrimitives())
                    .noneMatch(a -> a.name().equals("contactInfo"))
                    .noneMatch(a -> a.name().equals("address"));
        }

        // ── 错层嵌套 @AttributeOverride 隔离测试 ──

        @Test
        @DisplayName("错层: address.city 覆盖生效")
        void shouldCrossLayerCityOverride() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerOverride.class);

            EntityBasicAttribute city = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("city"))
                    .findFirst().orElse(null);
            assertThat(city).isNotNull();
            assertThat(city.columnName()).isEqualTo("addr_city");
        }

        @Test
        @DisplayName("错层: address.zip.code 覆盖生效")
        void shouldCrossLayerNestedZipCodeOverride() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerOverride.class);

            EntityBasicAttribute code = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("code"))
                    .findFirst().orElse(null);
            assertThat(code).isNotNull();
            assertThat(code.columnName()).isEqualTo("addr_zip_code");
        }

        @Test
        @DisplayName("错层: secondaryZip.code 独立覆盖，不受 address 影响")
        void shouldCrossLayerSecondaryZipBeIndependent() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerOverride.class);

            // 有两个 name="code" 的 primitive（address.zip.code 和 secondaryZip.code）
            // 其中一个应是 addr_zip_code（address.zip.code），另一个是 sec_zip_code（secondaryZip.code）
            assertThat(entityType.getPrimitives())
                    .filteredOn(a -> a.name().equals("code"))
                    .hasSize(2)
                    .extracting(a -> ((EntityBasicAttribute) a).columnName())
                    .containsExactlyInAnyOrder("addr_zip_code", "sec_zip_code");
        }

        @Test
        @DisplayName("错层: 无覆盖时默认列名")
        void shouldCrossLayerNoOverrideReturnDefault() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerEmbedded.class);

            EntityBasicAttribute city = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("city"))
                    .findFirst().orElse(null);
            assertThat(city).isNotNull();
            assertThat(city.columnName()).isEqualTo("city");

            EntityBasicAttribute code = (EntityBasicAttribute) entityType.getPrimitives().stream()
                    .filter(a -> a.name().equals("code"))
                    .findFirst().orElse(null);
            assertThat(code).isNotNull();
            assertThat(code.columnName()).isEqualTo("code");
        }

        // ── Resolver 直接测试 ──

        @Test
        @DisplayName("resolver: 有 @AttributeOverride 时返回覆盖映射")
        void shouldResolverReturnOverrides() {
            DefaultMetamodelResolver resolver = DefaultMetamodelResolver.of();
            var accessors = io.github.nextentity.core.reflect.schema.impl.DefaultAccessor
                    .of(TestEntities.EntityWithAttributeOverride.class);
            var fullNameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("fullName"))
                    .findFirst().orElseThrow();

            Map<String, String> overrides = resolver.getAttributeOverrides(fullNameAccessor);

            assertThat(overrides).containsEntry("firstName", "first_name_ov");
        }

        @Test
        @DisplayName("resolver: 无 @AttributeOverride 时返回空映射")
        void shouldResolverReturnEmptyWhenNoOverride() {
            DefaultMetamodelResolver resolver = DefaultMetamodelResolver.of();
            var accessors = io.github.nextentity.core.reflect.schema.impl.DefaultAccessor
                    .of(TestEntities.EntityWithEmbedded.class);
            var addressAccessor = accessors.stream()
                    .filter(a -> a.name().equals("address"))
                    .findFirst().orElseThrow();

            Map<String, String> overrides = resolver.getAttributeOverrides(addressAccessor);

            assertThat(overrides).isEmpty();
        }
    }

    @Nested
    @DisplayName("边界情况")
    class EdgeCaseTests {

        @Test
        @DisplayName("type() 返回正确 Class")
        void shouldTypeReturnCorrectClass() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);

            assertThat(entityType.type()).isEqualTo(TestEntities.SimpleEntity.class);
        }

        @Test
        @DisplayName("简单类型的 ValueConverter 为 IdentityValueConverter")
        void shouldValueConverterResolvedForSimpleTypes() {
            EntityType entityType = metamodel.getEntity(TestEntities.SimpleEntity.class);
            EntityBasicAttribute nameAttr = (EntityBasicAttribute) entityType.getAttribute("name");

            assertThat(nameAttr.valueConvertor()).isInstanceOf(IdentityValueConverter.class);
        }

        @Test
        @DisplayName("EntitySchemaAttribute 的 schema() 返回目标实体 Schema")
        void shouldSchemaAttributeSchemaReturnTargetSchema() {
            EntityType entityType = metamodel.getEntity(TestEntities.EmployeeEntity.class);
            EntitySchemaAttribute dept = (EntitySchemaAttribute) entityType.getAttribute("department");

            EntitySchema targetSchema = dept.schema();
            assertThat(targetSchema.type()).isEqualTo(TestEntities.DepartmentEntity.class);
            assertThat(targetSchema.tableName()).isEqualTo("department_entity");
        }

        @Test
        @DisplayName("配置禁用 lazy 时 @Fetch(LAZY) 投影不产生懒加载属性")
        void shouldConfigDisableLazySuppressFetchLazy() {
            MetamodelConfiguration config = MetamodelConfiguration.of(false, false);
            DefaultMetamodel strictMetamodel = new DefaultMetamodel(config);
            EntityType entityType = strictMetamodel.getEntity(TestEntities.EmployeeEntity.class);
            ProjectionSchema projection = entityType.getProjection(TestProjections.FetchLazyProjection.class);

            assertThat(projection.hasLazyAttribute()).isFalse();
        }
    }
}
