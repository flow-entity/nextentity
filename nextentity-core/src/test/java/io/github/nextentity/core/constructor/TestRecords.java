package io.github.nextentity.core.constructor;

/**
 * Test record types for {@link RecordConstructorTest}.
 * Defined as top-level public records so that {@code DefaultSchema.of(type).getConstructor()}
 * can find the public canonical constructor via reflection.
 */
public final class TestRecords {

    private TestRecords() {}

    public record SimpleRecord(String name, int age) {}

    public record SingleFieldRecord(String value) {}

    public record AllTypesRecord(String name, Integer count, Boolean active) {}

    public record EmptyArgsRecord() {}

    public record ValidatingRecord(String name) {
        public ValidatingRecord {
            if (name != null && name.length() < 2) {
                throw new IllegalArgumentException("name too short");
            }
        }
    }
}
