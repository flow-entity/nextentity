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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RecordConstructor")
class RecordConstructorTest {

    // Use top-level public records so DefaultSchema can find public constructors
    private static final Class<?> SIMPLE_RECORD = TestRecords.SimpleRecord.class;
    private static final Class<?> SINGLE_FIELD_RECORD = TestRecords.SingleFieldRecord.class;
    private static final Class<?> ALL_TYPES_RECORD = TestRecords.AllTypesRecord.class;
    private static final Class<?> EMPTY_ARGS_RECORD = TestRecords.EmptyArgsRecord.class;

    // A non-record class for negative constructor tests
    static class NotARecord {
        private final String name;
        NotARecord(String name) { this.name = name; }
    }

    // ==================== Helpers ====================

    /**
     * Creates a mock EntityBasicAttribute (non-sealed, implements EntityAttribute
     * which is a permitted subclass of the sealed MetamodelAttribute).
     * Only stubs accessor() with ordinal, which is what RecordConstructor actually uses.
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
        @DisplayName("constructs successfully with a valid record type")
        void constructsSuccessfullyWithValidRecordType() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, props);

            assertThat(rc).isNotNull();
            assertThat(rc.getResultType()).isEqualTo(SINGLE_FIELD_RECORD);
        }

        @Test
        @DisplayName("throws ReflectiveException when resultType is not a record")
        void throwsWhenNotRecordType() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            assertThatThrownBy(() -> new RecordConstructor(NotARecord.class, props))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("is not a record type");
        }

        @Test
        @DisplayName("getResultType returns the correct type")
        void getResultTypeReturnsCorrectType() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Alice"),
                    mockPropertyBinding(attr2, 30)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);

            assertThat(rc.getResultType()).isEqualTo(SIMPLE_RECORD);
        }

        @Test
        @DisplayName("columns() returns flattened columns from all PropertyBindings")
        void columnsReturnsFlattenedColumns() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);

            SelectItem col1 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
            SelectItem col2 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
            SelectItem col3 = SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));

            PropertyBinding pb1 = mockPropertyBinding(attr1, "Alice", List.of(col1, col2));
            PropertyBinding pb2 = mockPropertyBinding(attr2, 30, List.of(col3));

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, List.of(pb1, pb2));

            assertThat(rc.columns()).containsExactly(col1, col2, col3);
        }
    }

    // ==================== construct() tests ====================

    @Nested
    @DisplayName("construct()")
    class ConstructTests {

        @Test
        @DisplayName("constructs a record with non-null values correctly")
        void constructsRecordWithNonNullValues() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Alice"),
                    mockPropertyBinding(attr2, 30)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(SIMPLE_RECORD);
            TestRecords.SimpleRecord record = (TestRecords.SimpleRecord) result;
            assertThat(record.name()).isEqualTo("Alice");
            assertThat(record.age()).isEqualTo(30);
        }

        @Test
        @DisplayName("returns null when all property values are null")
        void returnsNullWhenAllValuesNull() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            // Both value constructors return null
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, null),
                    mockPropertyBinding(attr2, null)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("constructs a record when some values are null (partial nulls with wrapper types)")
        void constructsRecordWithPartialNulls() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            EntityBasicAttribute attr3 = mockAttribute(2);
            // name is non-null, count is null (Integer wrapper), active is non-null
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Bob"),
                    mockPropertyBinding(attr2, null),
                    mockPropertyBinding(attr3, true)
            );

            RecordConstructor rc = new RecordConstructor(ALL_TYPES_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(ALL_TYPES_RECORD);
            TestRecords.AllTypesRecord record = (TestRecords.AllTypesRecord) result;
            assertThat(record.name()).isEqualTo("Bob");
            // Integer wrapper type accepts null
            assertThat(record.count()).isNull();
            assertThat(record.active()).isTrue();
        }

        @Test
        @DisplayName("throws when primitive field receives null (cannot unbox null to int)")
        void throwsWhenPrimitiveFieldReceivesNull() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            // name is non-null so hasNonnull becomes true, but age (int) is null
            // This causes IllegalArgumentException from Constructor.newInstance when unboxing null to int
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Bob"),
                    mockPropertyBinding(attr2, null)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);

            // IllegalArgumentException propagates because it's a RuntimeException, not wrapped
            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("property values are placed at correct ordinal positions")
        void valuesPlacedAtCorrectOrdinalPositions() {
            // Define attributes with non-sequential ordinals to verify positioning
            EntityBasicAttribute attr2 = mockAttribute(2);
            EntityBasicAttribute attr0 = mockAttribute(0);
            EntityBasicAttribute attr1 = mockAttribute(1);

            // Properties are provided in non-ordinal order
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr2, true),
                    mockPropertyBinding(attr0, "test"),
                    mockPropertyBinding(attr1, 42)
            );

            RecordConstructor rc = new RecordConstructor(ALL_TYPES_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(ALL_TYPES_RECORD);
            TestRecords.AllTypesRecord record = (TestRecords.AllTypesRecord) result;
            assertThat(record.name()).isEqualTo("test");
            assertThat(record.count()).isEqualTo(42);
            assertThat(record.active()).isTrue();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when wrong type passed to constructor")
        void throwsWhenWrongTypePassedToConstructor() {
            // Use a ValueConstructor that returns a wrong type for a primitive field
            // e.g., returning a String for an int field will cause IllegalArgumentException
            // from Constructor.newInstance (argument type mismatch)
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);

            ValueConstructor badVc = mock(ValueConstructor.class);
            when(badVc.columns()).thenReturn(List.of());
            // Return a String where int is expected - causes IllegalArgumentException
            when(badVc.construct(any(Arguments.class))).thenReturn("not-an-int");

            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Alice"),
                    new PropertyBinding(attr2, badVc)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);

            // IllegalArgumentException propagates directly (not wrapped) because
            // it is a RuntimeException, not a ReflectiveOperationException
            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ==================== Edge cases ====================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("record with single field")
        void recordWithSingleField() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "hello"));

            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(SINGLE_FIELD_RECORD);
            TestRecords.SingleFieldRecord record = (TestRecords.SingleFieldRecord) result;
            assertThat(record.value()).isEqualTo("hello");
        }

        @Test
        @DisplayName("record with single field returns null when value is null")
        void singleFieldReturnsNullWhenValueNull() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, null));

            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("record with multiple fields")
        void recordWithMultipleFields() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            EntityBasicAttribute attr3 = mockAttribute(2);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "item"),
                    mockPropertyBinding(attr2, 99),
                    mockPropertyBinding(attr3, false)
            );

            RecordConstructor rc = new RecordConstructor(ALL_TYPES_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(ALL_TYPES_RECORD);
            TestRecords.AllTypesRecord record = (TestRecords.AllTypesRecord) result;
            assertThat(record.name()).isEqualTo("item");
            assertThat(record.count()).isEqualTo(99);
            assertThat(record.active()).isFalse();
        }

        @Test
        @DisplayName("record with primitive field: non-null value works correctly")
        void primitiveFieldWithNonNullValue() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Charlie"),
                    mockPropertyBinding(attr2, 25)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(SIMPLE_RECORD);
            TestRecords.SimpleRecord record = (TestRecords.SimpleRecord) result;
            assertThat(record.name()).isEqualTo("Charlie");
            assertThat(record.age()).isEqualTo(25);
        }

        @Test
        @DisplayName("constructs with empty properties collection returns null")
        void constructsWithEmptyProperties() {
            // A record with no properties mapped - all args remain null,
            // hasNonnull stays false, so construct returns null
            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, List.of());
            Object result = rc.construct(mockArguments());

            // No non-null values => hasNonnull is false => returns null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("empty record with no properties returns null (all-null-returns-null rule)")
        void emptyArgsRecordReturnsNull() {
            // EmptyArgsRecord has 0 fields, parameterCount = 0, args = [],
            // hasNonnull stays false => returns null (all-null-returns-null behavior)
            RecordConstructor rc = new RecordConstructor(EMPTY_ARGS_RECORD, List.of());
            Object result = rc.construct(mockArguments());

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("construct() is called multiple times independently")
        void multipleIndependentConstructCalls() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);

            // Create ValueConstructors that return different values on each call
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
                    new PropertyBinding(attr1, nameVc),
                    new PropertyBinding(attr2, ageVc)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);

            Object result1 = rc.construct(mockArguments());
            Object result2 = rc.construct(mockArguments());

            assertThat(result1).isInstanceOf(SIMPLE_RECORD);
            assertThat(result2).isInstanceOf(SIMPLE_RECORD);
            // Each call should produce a distinct instance
            assertThat(result1).isNotSameAs(result2);
            // Verify the dynamic name assignment works
            TestRecords.SimpleRecord r1 = (TestRecords.SimpleRecord) result1;
            TestRecords.SimpleRecord r2 = (TestRecords.SimpleRecord) result2;
            assertThat(r1.name()).isEqualTo("Name-1");
            assertThat(r2.name()).isEqualTo("Name-2");
        }

        @Test
        @DisplayName("columns() returns empty list when properties have no columns")
        void columnsReturnsEmptyWhenNoColumns() {
            ValueConstructor emptyVc = mock(ValueConstructor.class);
            when(emptyVc.columns()).thenReturn(List.of());

            EntityBasicAttribute attr = mockAttribute(0);
            PropertyBinding pb = new PropertyBinding(attr, emptyVc);

            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, List.of(pb));

            assertThat(rc.columns()).isEmpty();
        }

        @Test
        @DisplayName("only first non-null value is needed to trigger record construction")
        void onlyOneNonNullTriggersConstruction() {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            EntityBasicAttribute attr3 = mockAttribute(2);
            // Only name is non-null
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "only-name"),
                    mockPropertyBinding(attr2, null),
                    mockPropertyBinding(attr3, null)
            );

            RecordConstructor rc = new RecordConstructor(ALL_TYPES_RECORD, props);
            Object result = rc.construct(mockArguments());

            assertThat(result).isInstanceOf(ALL_TYPES_RECORD);
            TestRecords.AllTypesRecord record = (TestRecords.AllTypesRecord) result;
            assertThat(record.name()).isEqualTo("only-name");
            assertThat(record.count()).isNull();
            assertThat(record.active()).isNull();
        }

        @Test
        @DisplayName("ordinal out of bounds causes ArrayIndexOutOfBoundsException")
        void ordinalOutOfBoundsThrowsAIOOBE() {
            EntityBasicAttribute attr = mockAttribute(99);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "value"));

            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, props);

            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(ArrayIndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("InvocationTargetException from constructor is wrapped as ReflectiveException")
        void invocationTargetExceptionWrappedAsReflectiveException() {
            EntityBasicAttribute attr = mockAttribute(0);
            // "a" triggers the validation in ValidatingRecord: name.length() < 2
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "a"));

            RecordConstructor rc = new RecordConstructor(TestRecords.ValidatingRecord.class, props);

            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(ReflectiveException.class)
                    .hasCauseInstanceOf(InvocationTargetException.class);
        }

        @Test
        @DisplayName("RuntimeException from ValueConstructor.construct() propagates directly")
        void valueConstructorRuntimeExceptionPropagates() {
            EntityBasicAttribute attr = mockAttribute(0);
            ValueConstructor failingVc = mock(ValueConstructor.class);
            when(failingVc.columns()).thenReturn(List.of());
            when(failingVc.construct(any(Arguments.class))).thenThrow(new RuntimeException("conversion failed"));

            List<PropertyBinding> props = List.of(new PropertyBinding(attr, failingVc));
            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, props);

            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("conversion failed");
        }

        @Test
        @DisplayName("construct() is thread-safe under concurrent access")
        void constructIsThreadSafe() throws Exception {
            EntityBasicAttribute attr1 = mockAttribute(0);
            EntityBasicAttribute attr2 = mockAttribute(1);
            List<PropertyBinding> props = List.of(
                    mockPropertyBinding(attr1, "Alice"),
                    mockPropertyBinding(attr2, 30)
            );

            RecordConstructor rc = new RecordConstructor(SIMPLE_RECORD, props);
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Object>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> rc.construct(mockArguments())));
            }

            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            for (Future<Object> f : futures) {
                Object result = f.get();
                assertThat(result).isInstanceOf(SIMPLE_RECORD);
                TestRecords.SimpleRecord record = (TestRecords.SimpleRecord) result;
                assertThat(record.name()).isEqualTo("Alice");
                assertThat(record.age()).isEqualTo(30);
            }
        }

        @Test
        @DisplayName("null resultType throws NullPointerException")
        void nullResultTypeThrowsNPE() {
            EntityBasicAttribute attr = mockAttribute(0);
            List<PropertyBinding> props = List.of(mockPropertyBinding(attr, "test"));

            assertThatThrownBy(() -> new RecordConstructor(null, props))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("null properties causes NullPointerException on construct")
        void nullPropertiesCausesNPEOnConstruct() {
            RecordConstructor rc = new RecordConstructor(SINGLE_FIELD_RECORD, null);

            assertThatThrownBy(() -> rc.construct(mockArguments()))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
