package io.github.nextentity.core.constructor;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.jdbc.Arguments;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ObjectConstructor")
class ObjectConstructorTest {

    private static final Class<?> SIMPLE_BEAN = TestBeans.SimpleBean.class;
    private static final Class<?> FULL_BEAN = TestBeans.FullBean.class;
    private static final Class<?> SINGLE_FIELD_BEAN = TestBeans.SingleFieldBean.class;
    private static final Class<?> NO_SETTER_BEAN = TestBeans.NoSetterBean.class;

    // ==================== Helpers ====================

    /**
     * Creates a mock EntityBasicAttribute that delegates set() to the real setter method.
     * This is essential because ObjectConstructor uses attribute.set(instance, value)
     * to populate the bean via setter invocation.
     */
    private EntityBasicAttribute mockAttributeWithSetter(Class<?> beanClass, String setterName, Class<?> paramType) {
        EntityBasicAttribute attr = mock(EntityBasicAttribute.class);
        try {
            Method setter = beanClass.getMethod(setterName, paramType);
            doAnswer(inv -> {
                Object entity = inv.getArgument(0);
                Object value = inv.getArgument(1);
                setter.invoke(entity, value);
                return null;
            }).when(attr).set(any(), any());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return attr;
    }

    /**
     * Creates a mock EntityBasicAttribute that delegates set() to direct field access.
     * Used for beans without setter methods.
     */
    private EntityBasicAttribute mockAttributeWithFieldAccess(Class<?> beanClass, String fieldName) {
        EntityBasicAttribute attr = mock(EntityBasicAttribute.class);
        try {
            Field field = beanClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            doAnswer(inv -> {
                Object entity = inv.getArgument(0);
                Object value = inv.getArgument(1);
                field.set(entity, value);
                return null;
            }).when(attr).set(any(), any());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return attr;
    }

    /**
     * Creates a mock EntityBasicAttribute with stubbed accessor() (ordinal only).
     * Used for constructor-level tests where set() behavior is not needed.
     */
    private EntityBasicAttribute mockAttribute(int ordinal) {
        EntityBasicAttribute attr = mock(EntityBasicAttribute.class);
        Accessor accessor = mock(Accessor.class);
        when(accessor.ordinal()).thenReturn(ordinal);
        when(attr.accessor()).thenReturn(accessor);
        return attr;
    }

    /**
     * Creates a PropertyBinding that returns the given value from construct().
     */
    private PropertyBinding mockPropertyBinding(EntityBasicAttribute attribute, Object value) {
        ValueConstructor vc = mock(ValueConstructor.class);
        SelectItem column = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
        when(vc.columns()).thenReturn(List.of(column));
        when(vc.construct(any(Arguments.class))).thenReturn(value);
        return new PropertyBinding(attribute, vc);
    }

    /**
     * Creates a PropertyBinding that returns the given value and has the given columns.
     */
    private PropertyBinding mockPropertyBinding(EntityBasicAttribute attribute, Object value, List<SelectItem> columns) {
        ValueConstructor vc = mock(ValueConstructor.class);
        when(vc.columns()).thenReturn(columns);
        when(vc.construct(any(Arguments.class))).thenReturn(value);
        return new PropertyBinding(attribute, vc);
    }

    private Arguments mockArguments() {
        return mock(Arguments.class);
    }

    // ==================== Constructor tests ====================

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("constructs successfully with a valid bean type")
        void constructsSuccessfullyWithValidBeanType() {
            EntityBasicAttribute attr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);

            assertThat(oc).isNotNull();
            assertThat(oc.getResultType()).isEqualTo(SIMPLE_BEAN);
        }

        @Test
        @DisplayName("throws ReflectiveException for interface types")
        void throwsForInterfaceTypes() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            assertThatThrownBy(() -> new ObjectConstructor(Runnable.class, props))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("Cannot create ObjectConstructor for interface types");
        }

        @Test
        @DisplayName("getResultType returns the correct type")
        void getResultTypeReturnsCorrectType() {
            EntityBasicAttribute attr = mockAttributeWithSetter(FULL_BEAN, "setName", String.class);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "Alice"));

            ObjectConstructor oc = new ObjectConstructor(FULL_BEAN, props);

            assertThat(oc.getResultType()).isEqualTo(FULL_BEAN);
        }

        @Test
        @DisplayName("columns() returns flattened columns from all PropertyBindings")
        void columnsReturnsFlattenedColumns() {
            EntityBasicAttribute attr1 = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            EntityBasicAttribute attr2 = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);

            SelectItem col1 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
            SelectItem col2 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
            SelectItem col3 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));

            PropertyBinding pb1 = mockPropertyBinding(attr1, "Alice", List.of(col1, col2));
            PropertyBinding pb2 = mockPropertyBinding(attr2, 30, List.of(col3));

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, List.of(pb1, pb2));

            assertThat(oc.columns()).containsExactly(col1, col2, col3);
        }
    }

    // ==================== construct() tests ====================

    @Nested
    @DisplayName("construct()")
    class ConstructTests {

        @Test
        @DisplayName("constructs a bean with non-null values via setter")
        void constructsBeanWithNonNullValues() {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            EntityBasicAttribute ageAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(nameAttr, "Alice"),
                    mockPropertyBinding(ageAttr, 30)
            );

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(SIMPLE_BEAN);
            TestBeans.SimpleBean bean = (TestBeans.SimpleBean) result;
            assertThat(bean.getName()).isEqualTo("Alice");
            assertThat(bean.getAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("returns null when all property values are null")
        void returnsNullWhenAllValuesNull() {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            EntityBasicAttribute ageAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(nameAttr, null),
                    mockPropertyBinding(ageAttr, null)
            );

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("constructs with partial nulls (some values set, others null)")
        void constructsBeanWithPartialNulls() {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(FULL_BEAN, "setName", String.class);
            EntityBasicAttribute countAttr = mockAttributeWithSetter(FULL_BEAN, "setCount", Integer.class);
            EntityBasicAttribute activeAttr = mockAttributeWithSetter(FULL_BEAN, "setActive", Boolean.class);

            // name is non-null, count is null (Integer wrapper), active is non-null
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(nameAttr, "Bob"),
                    mockPropertyBinding(countAttr, null),
                    mockPropertyBinding(activeAttr, true)
            );

            ObjectConstructor oc = new ObjectConstructor(FULL_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(FULL_BEAN);
            TestBeans.FullBean bean = (TestBeans.FullBean) result;
            assertThat(bean.getName()).isEqualTo("Bob");
            assertThat(bean.getCount()).isNull();
            assertThat(bean.getActive()).isTrue();
        }

        @Test
        @DisplayName("instance is only created on first non-null value (lazy instantiation)")
        void instanceCreatedLazilyOnFirstNonNull() {
            // First property returns null, second returns non-null
            // The no-arg constructor should only be invoked when the second property is processed
            EntityBasicAttribute nameAttr = mock(EntityBasicAttribute.class);
            EntityBasicAttribute ageAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);

            ValueConstructor nullVc = mock(ValueConstructor.class);
            when(nullVc.columns()).thenReturn(List.of());
            when(nullVc.construct(any(Arguments.class))).thenReturn(null);

            ValueConstructor ageVc = mock(ValueConstructor.class);
            when(ageVc.columns()).thenReturn(List.of());
            when(ageVc.construct(any(Arguments.class))).thenReturn(25);

            List<PropertyBinding> props = List.of(
                    new PropertyBinding(nameAttr, nullVc),
                    new PropertyBinding(ageAttr, ageVc)
            );

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(SIMPLE_BEAN);
            TestBeans.SimpleBean bean = (TestBeans.SimpleBean) result;
            assertThat(bean.getAge()).isEqualTo(25);
            // name was null, so setName was never called
            verify(nameAttr, org.mockito.Mockito.never()).set(any(), any());
        }

        @Test
        @DisplayName("multiple setter calls work correctly")
        void multipleSetterCallsWorkCorrectly() {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(FULL_BEAN, "setName", String.class);
            EntityBasicAttribute countAttr = mockAttributeWithSetter(FULL_BEAN, "setCount", Integer.class);
            EntityBasicAttribute activeAttr = mockAttributeWithSetter(FULL_BEAN, "setActive", Boolean.class);

            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(nameAttr, "Carol"),
                    mockPropertyBinding(countAttr, 42),
                    mockPropertyBinding(activeAttr, false)
            );

            ObjectConstructor oc = new ObjectConstructor(FULL_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(FULL_BEAN);
            TestBeans.FullBean bean = (TestBeans.FullBean) result;
            assertThat(bean.getName()).isEqualTo("Carol");
            assertThat(bean.getCount()).isEqualTo(42);
            assertThat(bean.getActive()).isFalse();
        }
    }

    // ==================== Edge cases ====================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("single field bean")
        void singleFieldBean() {
            EntityBasicAttribute attr = mockAttributeWithSetter(SINGLE_FIELD_BEAN, "setValue", String.class);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "hello"));

            ObjectConstructor oc = new ObjectConstructor(SINGLE_FIELD_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(SINGLE_FIELD_BEAN);
            TestBeans.SingleFieldBean bean = (TestBeans.SingleFieldBean) result;
            assertThat(bean.getValue()).isEqualTo("hello");
        }

        @Test
        @DisplayName("single field with null returns null")
        void singleFieldReturnsNullWhenValueNull() {
            EntityBasicAttribute attr = mockAttributeWithSetter(SINGLE_FIELD_BEAN, "setValue", String.class);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, null));

            ObjectConstructor oc = new ObjectConstructor(SINGLE_FIELD_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("empty properties collection returns null")
        void emptyPropertiesReturnsNull() {
            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, List.of());
            Object result = oc.construct(mockArguments());

            // No properties => no non-null values => returns null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null resultType throws NullPointerException")
        void nullResultTypeThrowsNPE() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            assertThatThrownBy(() -> new ObjectConstructor(null, props))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("null properties causes NullPointerException on construct")
        void nullPropertiesCausesNPEOnConstruct() {
            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, null);

            assertThatThrownBy(() -> oc.construct(mockArguments()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("RuntimeException from ValueConstructor.construct() propagates directly")
        void valueConstructorRuntimeExceptionPropagates() {
            EntityBasicAttribute attr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            ValueConstructor failingVc = mock(ValueConstructor.class);
            when(failingVc.columns()).thenReturn(List.of());
            when(failingVc.construct(any(Arguments.class))).thenThrow(new RuntimeException("conversion failed"));

            List<PropertyBinding> props = List.of(new PropertyBinding(attr, failingVc));
            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);

            assertThatThrownBy(() -> oc.construct(mockArguments()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("conversion failed");
        }

        @Test
        @DisplayName("constructor.newInstance() throwing causes ReflectiveException wrapping InvocationTargetException")
        void constructorNewInstanceThrowingCausesReflectiveException() {
            EntityBasicAttribute attr = mockAttributeWithSetter(
                    TestBeans.ThrowingConstructorBean.class, "setName", String.class);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            ObjectConstructor oc = new ObjectConstructor(TestBeans.ThrowingConstructorBean.class, props);

            // ThrowingConstructorBean's no-arg constructor throws RuntimeException
            // Constructor.newInstance wraps it as InvocationTargetException
            // AbstractObjectConstructor.construct catches ReflectiveOperationException -> wraps as ReflectiveException
            assertThatThrownBy(() -> oc.construct(mockArguments()))
                    .isInstanceOf(ReflectiveException.class)
                    .hasCauseInstanceOf(InvocationTargetException.class);
        }

        @Test
        @DisplayName("ReflectiveException from attribute.set() propagates directly without double-wrapping")
        void reflectiveExceptionFromSetPropagatesDirectly() {
            EntityBasicAttribute attr = mock(EntityBasicAttribute.class);
            doAnswer(inv -> {
                throw new ReflectiveException("setter exploded");
            }).when(attr).set(any(), any());

            ValueConstructor vc = mock(ValueConstructor.class);
            when(vc.columns()).thenReturn(List.of());
            when(vc.construct(any(Arguments.class))).thenReturn("value");

            List<PropertyBinding> props = List.of(new PropertyBinding(attr, vc));
            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);

            // ReflectiveException is a RuntimeException, not a ReflectiveOperationException
            // It should propagate directly through AbstractObjectConstructor.construct() without double-wrapping
            assertThatThrownBy(() -> oc.construct(mockArguments()))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessage("setter exploded");
        }

        @Test
        @DisplayName("construct() is thread-safe under concurrent access")
        void constructIsThreadSafe() throws Exception {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            EntityBasicAttribute ageAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(nameAttr, "Alice"),
                    mockPropertyBinding(ageAttr, 30)
            );

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Object>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> oc.construct(mockArguments())));
            }

            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            List<Object> results = new ArrayList<>();
            for (Future<Object> f : futures) {
                Object result = f.get();
                assertThat(result).isInstanceOf(SIMPLE_BEAN);
                TestBeans.SimpleBean bean = (TestBeans.SimpleBean) result;
                assertThat(bean.getName()).isEqualTo("Alice");
                assertThat(bean.getAge()).isEqualTo(30);
                results.add(result);
            }
            // Each thread gets a distinct instance
            assertThat(results).doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("ValueConstructor returns value that is set on the instance via attribute.set()")
        void valueConstructorReturnValueIsSetOnInstance() {
            EntityBasicAttribute nameAttr = mock(EntityBasicAttribute.class);
            doAnswer(inv -> {
                Object entity = inv.getArgument(0);
                Object value = inv.getArgument(1);
                ((TestBeans.SimpleBean) entity).setName((String) value);
                return null;
            }).when(nameAttr).set(any(), any());

            ValueConstructor nameVc = mock(ValueConstructor.class);
            when(nameVc.columns()).thenReturn(List.of());
            when(nameVc.construct(any(Arguments.class))).thenReturn("David");

            List<PropertyBinding> props = List.of(new PropertyBinding(nameAttr, nameVc));
            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(SIMPLE_BEAN);
            TestBeans.SimpleBean bean = (TestBeans.SimpleBean) result;
            assertThat(bean.getName()).isEqualTo("David");

            // Verify that attribute.set() was called with the instance and the value
            verify(nameAttr).set(bean, "David");
        }

        @Test
        @DisplayName("bean without setter uses field access via attribute.set()")
        void beanWithoutSetterUsesFieldAccess() {
            EntityBasicAttribute attr = mockAttributeWithFieldAccess(NO_SETTER_BEAN, "name");
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "field-value"));

            ObjectConstructor oc = new ObjectConstructor(NO_SETTER_BEAN, props);
            Object result = oc.construct(mockArguments());

            assertThat(result).isInstanceOf(NO_SETTER_BEAN);
            TestBeans.NoSetterBean bean = (TestBeans.NoSetterBean) result;
            assertThat(bean.getName()).isEqualTo("field-value");
        }

        @Test
        @DisplayName("columns() returns empty list when properties have no columns")
        void columnsReturnsEmptyWhenNoColumns() {
            ValueConstructor emptyVc = mock(ValueConstructor.class);
            when(emptyVc.columns()).thenReturn(List.of());

            EntityBasicAttribute attr = mockAttributeWithSetter(SINGLE_FIELD_BEAN, "setValue", String.class);
            PropertyBinding pb = new PropertyBinding(attr, emptyVc);

            ObjectConstructor oc = new ObjectConstructor(SINGLE_FIELD_BEAN, List.of(pb));

            assertThat(oc.columns()).isEmpty();
        }

        @Test
        @DisplayName("construct() is called multiple times independently")
        void multipleIndependentConstructCalls() {
            EntityBasicAttribute nameAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setName", String.class);
            EntityBasicAttribute ageAttr = mockAttributeWithSetter(SIMPLE_BEAN, "setAge", int.class);

            // Create a ValueConstructor that returns different values on each call
            List<String> names = new ArrayList<>();
            ValueConstructor nameVc = mock(ValueConstructor.class);
            when(nameVc.columns()).thenReturn(List.of());
            when(nameVc.construct(any(Arguments.class))).thenAnswer(inv -> {
                String name = "Name-" + (names.size() + 1);
                names.add(name);
                return name;
            });

            ValueConstructor ageVc = mock(ValueConstructor.class);
            when(ageVc.columns()).thenReturn(List.of());
            when(ageVc.construct(any(Arguments.class))).thenReturn(25);

            List<PropertyBinding> props = List.of(
                    new PropertyBinding(nameAttr, nameVc),
                    new PropertyBinding(ageAttr, ageVc)
            );

            ObjectConstructor oc = new ObjectConstructor(SIMPLE_BEAN, props);

            Object result1 = oc.construct(mockArguments());
            Object result2 = oc.construct(mockArguments());

            assertThat(result1).isInstanceOf(SIMPLE_BEAN);
            assertThat(result2).isInstanceOf(SIMPLE_BEAN);
            // Each call should produce a distinct instance
            assertThat(result1).isNotSameAs(result2);
            // Verify the dynamic name assignment works
            TestBeans.SimpleBean b1 = (TestBeans.SimpleBean) result1;
            TestBeans.SimpleBean b2 = (TestBeans.SimpleBean) result2;
            assertThat(b1.getName()).isEqualTo("Name-1");
            assertThat(b2.getName()).isEqualTo("Name-2");
        }

        @Test
        @DisplayName("class without public no-arg constructor throws NullPointerException")
        void classWithoutPublicNoArgConstructorThrows() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            // Integer has no public no-arg constructor
            assertThatThrownBy(() -> new ObjectConstructor(Integer.class, props))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("abstract class as resultType throws NullPointerException (no public no-arg constructor)")
        void abstractClassThrowsNPE() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            // Abstract classes can't be instantiated; DefaultSchema returns null constructor
            assertThatThrownBy(() -> new ObjectConstructor(AbstractList.class, props))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
