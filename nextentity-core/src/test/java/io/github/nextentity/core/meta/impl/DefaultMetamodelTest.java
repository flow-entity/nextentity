package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.converter.InstantConverter;
import io.github.nextentity.core.meta.*;
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
    }

    // ── 边界情况测试 ──

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
