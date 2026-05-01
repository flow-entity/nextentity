package io.github.nextentity.core.constructor;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.TestEntities;
import io.github.nextentity.jdbc.Arguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 移植自 feature/embedded-support-d：EntityConstructorBuilder 对 {@code @Embedded} 属性的结果构造支持。
 * <p>
 * 目标：验证 EntityConstructorBuilder 能正确处理含嵌入属性的实体构造。
 */
@DisplayName("EntityConstructorBuilder - 嵌入属性结果构造")
class EmbeddedEntityConstructorTest {

    private DefaultMetamodel metamodel;

    @BeforeEach
    void setUp() {
        metamodel = DefaultMetamodel.of();
    }

    @Nested
    @DisplayName("基本嵌入构造")
    class BasicEmbeddedConstructionTests {

        @Test
        @DisplayName("含嵌入属性的实体，构造器返回 ObjectConstructor")
        void shouldBuildObjectConstructorForEmbeddedEntity() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            // 实体类不是接口也不是 Record → ObjectConstructor
            assertThat(constructor).isInstanceOf(ObjectConstructor.class);
        }

        @Test
        @DisplayName("build() 返回的构造器包含嵌入子属性对应的列")
        void shouldBuildConstructorWithEmbeddedSubColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            List<SelectItem> columns = constructor.columns();
            // id, name, street, city, zipCode = 5 columns
            assertThat(columns).hasSize(5);
        }

        @Test
        @DisplayName("从 Arguments 构造嵌入对象")
        void shouldConstructEmbeddedObjectFromArguments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            Arguments args = new TestArguments(
                    1L,          // id
                    "John",      // name
                    "5th Ave",   // street
                    "NYC",       // city
                    "10001"      // zipCode
            );

            Object result = constructor.construct(args);
            assertThat(result).isInstanceOf(TestEntities.EntityWithEmbedded.class);

            TestEntities.EntityWithEmbedded entity = (TestEntities.EntityWithEmbedded) result;
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getName()).isEqualTo("John");
            assertThat(entity.getAddress()).isNotNull();
            assertThat(entity.getAddress().getStreet()).isEqualTo("5th Ave");
            assertThat(entity.getAddress().getCity()).isEqualTo("NYC");
            assertThat(entity.getAddress().getZipCode()).isEqualTo("10001");
        }

        @Test
        @DisplayName("所有嵌入子属性为 null 时嵌入对象为 null")
        void shouldReturnNullEmbeddedWhenAllSubAttributesNull() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            Arguments args = new TestArguments(
                    1L, "John", null, null, null
            );

            Object result = constructor.construct(args);
            TestEntities.EntityWithEmbedded entity = (TestEntities.EntityWithEmbedded) result;
            assertThat(entity.getAddress()).isNull();
        }
    }

    @Nested
    @DisplayName("嵌套嵌入构造")
    class NestedEmbeddedConstructionTests {

        @Test
        @DisplayName("嵌套嵌入构造器包含所有子列")
        void shouldNestedEmbeddedIncludeAllSubColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            // id, contactInfo.email, contactInfo.phone,
            // contactInfo.address.street, contactInfo.address.city, contactInfo.address.zipCode = 6 columns
            List<SelectItem> columns = constructor.columns();
            assertThat(columns).hasSize(6);
        }

        @Test
        @DisplayName("从 Arguments 构造嵌套嵌入对象")
        void shouldConstructNestedEmbeddedObjectFromArguments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            Arguments args = new TestArguments(
                    1L,          // id
                    "a@b.com",   // email
                    "123456",    // phone
                    "5th Ave",   // street
                    "NYC",       // city
                    "10001"      // zipCode
            );

            Object result = constructor.construct(args);
            assertThat(result).isInstanceOf(TestEntities.EntityWithNestedEmbedded.class);

            TestEntities.EntityWithNestedEmbedded entity =
                    (TestEntities.EntityWithNestedEmbedded) result;
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getContactInfo()).isNotNull();
            assertThat(entity.getContactInfo().getEmail()).isEqualTo("a@b.com");
            assertThat(entity.getContactInfo().getPhone()).isEqualTo("123456");
            assertThat(entity.getContactInfo().getAddress()).isNotNull();
            assertThat(entity.getContactInfo().getAddress().getStreet()).isEqualTo("5th Ave");
            assertThat(entity.getContactInfo().getAddress().getCity()).isEqualTo("NYC");
            assertThat(entity.getContactInfo().getAddress().getZipCode()).isEqualTo("10001");
        }

        @Test
        @DisplayName("嵌套嵌入中间层为 null 时深层也为 null")
        void shouldReturnNullNestedWhenMiddleNull() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            Arguments args = new TestArguments(
                    1L, null, null, null, null, null
            );

            Object result = constructor.construct(args);
            TestEntities.EntityWithNestedEmbedded entity =
                    (TestEntities.EntityWithNestedEmbedded) result;
            assertThat(entity.getContactInfo()).isNull();
        }
    }

    @Nested
    @DisplayName("错层嵌入构造")
    class CrossLayerEmbeddedConstructionTests {

        @Test
        @DisplayName("错层嵌入构造器包含所有子列")
        void shouldCrossLayerEmbeddedIncludeAllSubColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            // id, address.city, address.zip.code, secondaryZip.code = 4 columns
            List<SelectItem> columns = constructor.columns();
            assertThat(columns).hasSize(4);
        }

        @Test
        @DisplayName("从 Arguments 构造错层嵌套对象")
        void shouldConstructCrossLayerEmbeddedObjectFromArguments() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            Arguments args = new TestArguments(
                    1L,              // id
                    "NYC",           // address.city
                    "10001",         // address.zip.code
                    "90210"          // secondaryZip.code
            );

            Object result = constructor.construct(args);
            assertThat(result).isInstanceOf(TestEntities.EntityWithCrossLayerEmbedded.class);

            TestEntities.EntityWithCrossLayerEmbedded entity =
                    (TestEntities.EntityWithCrossLayerEmbedded) result;
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getAddress()).isNotNull();
            assertThat(entity.getAddress().getCity()).isEqualTo("NYC");
            assertThat(entity.getAddress().getZip()).isNotNull();
            assertThat(entity.getAddress().getZip().getCode()).isEqualTo("10001");
            assertThat(entity.getSecondaryZip()).isNotNull();
            assertThat(entity.getSecondaryZip().getCode()).isEqualTo("90210");
        }

        @Test
        @DisplayName("错层嵌入中层为 null 时深层也为 null，实体层级独立")
        void shouldCrossLayerEmbeddedPartialNull() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerEmbedded.class);
            EntityConstructorBuilder builder = new EntityConstructorBuilder(entityType, SchemaAttributePaths.empty());
            ValueConstructor constructor = builder.build();

            // 只有 id 和 secondaryZip 非空，address 全 null
            Arguments args = new TestArguments(
                    1L,           // id
                    null,         // address.city
                    null,         // address.zip.code
                    "90210"       // secondaryZip.code
            );

            Object result = constructor.construct(args);
            TestEntities.EntityWithCrossLayerEmbedded entity =
                    (TestEntities.EntityWithCrossLayerEmbedded) result;
            assertThat(entity.getAddress()).isNull();
            assertThat(entity.getSecondaryZip()).isNotNull();
            assertThat(entity.getSecondaryZip().getCode()).isEqualTo("90210");
        }
    }

    /// 简单的测试用 Arguments 实现
    static class TestArguments implements Arguments {
        private final Object[] values;
        private int index = 0;

        TestArguments(Object... values) {
            this.values = values;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object get(int index, ValueConverter<?, ?> convertor) {
            Object value = values[index];
            if (convertor == null || value == null) {
                return value;
            }
            return ((ValueConverter<Object, Object>) convertor).convertToEntityAttribute(value);
        }

        @Override
        public Object next(ValueConverter<?, ?> convertor) {
            return get(index++, convertor);
        }
    }
}
