package io.github.nextentity.core.reflect.schema.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultAccessor")
class DefaultAccessorTest {

    // ==================== of() - simple types ====================

    @Nested
    @DisplayName("of() returns empty list for simple types")
    class OfSimpleTypes {

        @Test
        @DisplayName("primitive types return empty list")
        void primitiveTypes() {
            assertThat(DefaultAccessor.of(int.class)).isEmpty();
            assertThat(DefaultAccessor.of(long.class)).isEmpty();
            assertThat(DefaultAccessor.of(boolean.class)).isEmpty();
            assertThat(DefaultAccessor.of(double.class)).isEmpty();
        }

        @Test
        @DisplayName("wrapper types return empty list")
        void wrapperTypes() {
            assertThat(DefaultAccessor.of(Integer.class)).isEmpty();
            assertThat(DefaultAccessor.of(Long.class)).isEmpty();
            assertThat(DefaultAccessor.of(Boolean.class)).isEmpty();
            assertThat(DefaultAccessor.of(Double.class)).isEmpty();
        }

        @Test
        @DisplayName("String and CharSequence return empty list")
        void stringTypes() {
            assertThat(DefaultAccessor.of(String.class)).isEmpty();
            assertThat(DefaultAccessor.of(CharSequence.class)).isEmpty();
        }

        @Test
        @DisplayName("Number subclasses return empty list")
        void numberTypes() {
            assertThat(DefaultAccessor.of(Number.class)).isEmpty();
            assertThat(DefaultAccessor.of(BigDecimal.class)).isEmpty();
        }

        @Test
        @DisplayName("enum types return empty list")
        void enumTypes() {
            assertThat(DefaultAccessor.of(SampleEnum.class)).isEmpty();
        }

        @Test
        @DisplayName("array types return empty list")
        void arrayTypes() {
            assertThat(DefaultAccessor.of(String[].class)).isEmpty();
            assertThat(DefaultAccessor.of(int[].class)).isEmpty();
        }

        @Test
        @DisplayName("Temporal types return empty list")
        void temporalTypes() {
            assertThat(DefaultAccessor.of(LocalDateTime.class)).isEmpty();
            assertThat(DefaultAccessor.of(LocalDate.class)).isEmpty();
        }

        @Test
        @DisplayName("Date returns empty list")
        void dateType() {
            assertThat(DefaultAccessor.of(Date.class)).isEmpty();
        }

        @Test
        @DisplayName("Collection and Map return empty list")
        void collectionAndMapTypes() {
            assertThat(DefaultAccessor.of(Collection.class)).isEmpty();
            assertThat(DefaultAccessor.of(Map.class)).isEmpty();
            assertThat(DefaultAccessor.of(List.class)).isEmpty();
        }
    }

    // ==================== of() - bean types ====================

    @Nested
    @DisplayName("of() returns accessors for bean types")
    class OfBeanTypes {

        @Test
        @DisplayName("returns accessors for simple bean with getter/setter")
        void simpleBean() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SimpleBean.class);

            assertThat(accessors).hasSize(2);
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();
            DefaultAccessor ageAccessor = accessors.stream()
                    .filter(a -> a.name().equals("age")).findFirst().orElseThrow();

            assertThat(nameAccessor.type()).isEqualTo(String.class);
            assertThat(nameAccessor.getter()).isNotNull();
            assertThat(nameAccessor.setter()).isNotNull();
            assertThat(nameAccessor.field()).isNotNull();

            assertThat(ageAccessor.type()).isEqualTo(int.class);
            assertThat(ageAccessor.getter()).isNotNull();
            assertThat(ageAccessor.setter()).isNotNull();
        }

        @Test
        @DisplayName("ordinals are assigned sequentially starting from 0")
        void ordinalsAssignedSequentially() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SimpleBean.class);

            for (int i = 0; i < accessors.size(); i++) {
                assertThat(accessors.get(i).ordinal()).isEqualTo(i);
            }
        }

        @Test
        @DisplayName("caching: of() returns same instance for same type")
        void cachingReturnsSameInstance() {
            List<DefaultAccessor> first = DefaultAccessor.of(SimpleBean.class);
            List<DefaultAccessor> second = DefaultAccessor.of(SimpleBean.class);

            assertThat(first).isSameAs(second);
        }

        @Test
        @DisplayName("field-only properties have getter/setter from introspector")
        void fieldWithPropertyDescriptor() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(BeanWithExtraField.class);

            assertThat(accessors).anyMatch(a -> a.name().equals("name"));
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();

            assertThat(nameAccessor.getter()).isNotNull();
            assertThat(nameAccessor.setter()).isNotNull();
        }

        @Test
        @DisplayName("skips 'class' property from BeanInfo")
        void skipsClassProperty() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SimpleBean.class);

            assertThat(accessors).noneMatch(a -> a.name().equals("class"));
        }
    }

    // ==================== of() - record types ====================

    @Nested
    @DisplayName("of() returns accessors for record types")
    class OfRecordTypes {

        @Test
        @DisplayName("returns accessors for record components")
        void recordAccessors() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SampleRecord.class);

            assertThat(accessors).hasSize(2);
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();
            DefaultAccessor ageAccessor = accessors.stream()
                    .filter(a -> a.name().equals("age")).findFirst().orElseThrow();

            assertThat(nameAccessor.type()).isEqualTo(String.class);
            assertThat(nameAccessor.getter()).isNotNull();
            assertThat(nameAccessor.setter()).isNull();
            assertThat(nameAccessor.field()).isNull();

            assertThat(ageAccessor.type()).isEqualTo(int.class);
            assertThat(ageAccessor.getter()).isNotNull();
            assertThat(ageAccessor.setter()).isNull();
        }

        @Test
        @DisplayName("record accessors have sequential ordinals")
        void recordOrdinals() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SampleRecord.class);

            for (int i = 0; i < accessors.size(); i++) {
                assertThat(accessors.get(i).ordinal()).isEqualTo(i);
            }
        }
    }

    // ==================== of() - interface types ====================

    @Nested
    @DisplayName("of() returns accessors for interface types")
    class OfInterfaceTypes {

        @Test
        @DisplayName("returns accessors from getter methods in interface")
        void interfaceAccessors() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SampleInterface.class);

            assertThat(accessors).isNotEmpty();
            assertThat(accessors).anyMatch(a -> a.name().equals("name"));
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();

            assertThat(nameAccessor.getter()).isNotNull();
            assertThat(nameAccessor.field()).isNull();
        }
    }

    // ==================== of() - inheritance ====================

    @Nested
    @DisplayName("of() handles inheritance")
    class OfInheritance {

        @Test
        @DisplayName("includes fields from superclass")
        void includesSuperclassFields() {
            List<DefaultAccessor> accessors = DefaultAccessor.of(ChildBean.class);

            assertThat(accessors).anyMatch(a -> a.name().equals("name"));
            assertThat(accessors).anyMatch(a -> a.name().equals("extra"));
        }
    }

    // ==================== Accessor.get() / set() ====================

    @Nested
    @DisplayName("Accessor.get() and set()")
    class AccessorGetSet {

        @Test
        @DisplayName("get() reads value via getter method")
        void getReadsViaGetter() throws Exception {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SimpleBean.class);
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();

            SimpleBean bean = new SimpleBean();
            bean.setName("test-value");

            Object value = nameAccessor.get(bean);
            assertThat(value).isEqualTo("test-value");
        }

        @Test
        @DisplayName("set() writes value via setter method")
        void setWritesViaSetter() throws Exception {
            List<DefaultAccessor> accessors = DefaultAccessor.of(SimpleBean.class);
            DefaultAccessor nameAccessor = accessors.stream()
                    .filter(a -> a.name().equals("name")).findFirst().orElseThrow();

            SimpleBean bean = new SimpleBean();
            nameAccessor.set(bean, "new-value");

            assertThat(bean.getName()).isEqualTo("new-value");
        }
    }

    // ==================== Test types ====================

    public record SampleRecord(String name, int age) {}

    public interface SampleInterface {
        String getName();
        int getAge();
    }

    public enum SampleEnum { A, B, C }

    public static class SimpleBean {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class BeanWithExtraField {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ParentBean {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ChildBean extends ParentBean {
        private String extra;
        public String getExtra() { return extra; }
        public void setExtra(String extra) { this.extra = extra; }
    }
}
