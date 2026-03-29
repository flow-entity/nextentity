package io.github.nextentity.core.expression;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Path.StringRef;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for StringExpression interface default methods.
 * <p>
 * These tests verify that the default methods in StringExpression interface
 * correctly delegate to the underlying implementation methods.
 * <p>
 * Uses Paths.get() to obtain StringPath instances which implement StringExpression.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.StringExpression
 */
@DisplayName("StringExpression Default Methods Unit Tests")
class StringExpressionDefaultMethodsTest {

    // Test entity for path references
    static class TestEntity {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // ==================== Basic String Operations ====================

    @Nested
    @DisplayName("startsWith/endsWith/contains default methods")
    class BasicStringOperations {

        /**
         * Test: startsWith(String) should call like(value + '%').
         * This tests the default method in StringExpression interface.
         */
        @Test
        @DisplayName("startsWith should create LIKE predicate with prefix pattern")
        void startsWith_shouldCallLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method startsWith
            Predicate<TestEntity> predicate = path.startsWith("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        /**
         * Test: endsWith(String) should call like('%' + value).
         * This tests the default method in StringExpression interface.
         */
        @Test
        @DisplayName("endsWith should create LIKE predicate with suffix pattern")
        void endsWith_shouldCallLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method endsWith
            Predicate<TestEntity> predicate = path.endsWith("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        /**
         * Test: contains(String) should call like('%' + value + '%').
         * This tests the default method in StringExpression interface.
         */
        @Test
        @DisplayName("contains should create LIKE predicate with contains pattern")
        void contains_shouldCallLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method contains
            Predicate<TestEntity> predicate = path.contains("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }
    }

    // ==================== Negated String Operations ====================

    @Nested
    @DisplayName("notStartsWith/notEndsWith/notContains default methods")
    class NegatedStringOperations {

        @Test
        @DisplayName("notStartsWith should create NOT LIKE predicate with prefix pattern")
        void notStartsWith_shouldCallNotLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method notStartsWith
            Predicate<TestEntity> predicate = path.notStartsWith("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notEndsWith should create NOT LIKE predicate with suffix pattern")
        void notEndsWith_shouldCallNotLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method notEndsWith
            Predicate<TestEntity> predicate = path.notEndsWith("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notContains should create NOT LIKE predicate with contains pattern")
        void notContains_shouldCallNotLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method notContains
            Predicate<TestEntity> predicate = path.notContains("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }
    }

    // ==================== IfNotNull String Operations ====================

    @Nested
    @DisplayName("IfNotNull variants default methods")
    class IfNotNullStringOperations {

        @Test
        @DisplayName("startsWithIfNotNull with non-null value should create LIKE predicate")
        void startsWithIfNotNull_withNonNull_shouldCallLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.startsWithIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("startsWithIfNotNull with null value should create empty predicate")
        void startsWithIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.startsWithIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("endsWithIfNotNull with non-null value should create LIKE predicate")
        void endsWithIfNotNull_withNonNull_shouldCallLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.endsWithIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("endsWithIfNotNull with null value should create empty predicate")
        void endsWithIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.endsWithIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("containsIfNotNull with non-null value should create LIKE predicate")
        void containsIfNotNull_withNonNull_shouldCallLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.containsIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("containsIfNotNull with null value should create empty predicate")
        void containsIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.containsIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notStartsWithIfNotNull with non-null value should create NOT LIKE predicate")
        void notStartsWithIfNotNull_withNonNull_shouldCallNotLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.notStartsWithIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notStartsWithIfNotNull with null value should create empty predicate")
        void notStartsWithIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.notStartsWithIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notEndsWithIfNotNull with non-null value should create NOT LIKE predicate")
        void notEndsWithIfNotNull_withNonNull_shouldCallNotLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.notEndsWithIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notEndsWithIfNotNull with null value should create empty predicate")
        void notEndsWithIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.notEndsWithIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notContainsIfNotNull with non-null value should create NOT LIKE predicate")
        void notContainsIfNotNull_withNonNull_shouldCallNotLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-null value
            Predicate<TestEntity> predicate = path.notContainsIfNotNull("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notContainsIfNotNull with null value should create empty predicate")
        void notContainsIfNotNull_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.notContainsIfNotNull(null);

            // Then
            assertThat(predicate).isNotNull();
        }
    }

    // ==================== IfNotEmpty String Operations ====================

    @Nested
    @DisplayName("IfNotEmpty variants default methods")
    class IfNotEmptyStringOperations {

        @Test
        @DisplayName("startsWithIfNotEmpty with non-empty value should create LIKE predicate")
        void startsWithIfNotEmpty_withNonEmpty_shouldCallLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.startsWithIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("startsWithIfNotEmpty with empty value should create empty predicate")
        void startsWithIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.startsWithIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("startsWithIfNotEmpty with null value should create empty predicate")
        void startsWithIfNotEmpty_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.startsWithIfNotEmpty(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("endsWithIfNotEmpty with non-empty value should create LIKE predicate")
        void endsWithIfNotEmpty_withNonEmpty_shouldCallLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.endsWithIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("endsWithIfNotEmpty with empty value should create empty predicate")
        void endsWithIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.endsWithIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("endsWithIfNotEmpty with null value should create empty predicate")
        void endsWithIfNotEmpty_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.endsWithIfNotEmpty(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("containsIfNotEmpty with non-empty value should create LIKE predicate")
        void containsIfNotEmpty_withNonEmpty_shouldCallLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.containsIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("containsIfNotEmpty with empty value should create empty predicate")
        void containsIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.containsIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("containsIfNotEmpty with null value should create empty predicate")
        void containsIfNotEmpty_withNull_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with null value
            Predicate<TestEntity> predicate = path.containsIfNotEmpty(null);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notStartsWithIfNotEmpty with non-empty value should create NOT LIKE predicate")
        void notStartsWithIfNotEmpty_withNonEmpty_shouldCallNotLikeWithPrefixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.notStartsWithIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notStartsWithIfNotEmpty with empty value should create empty predicate")
        void notStartsWithIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.notStartsWithIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notEndsWithIfNotEmpty with non-empty value should create NOT LIKE predicate")
        void notEndsWithIfNotEmpty_withNonEmpty_shouldCallNotLikeWithSuffixPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.notEndsWithIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notEndsWithIfNotEmpty with empty value should create empty predicate")
        void notEndsWithIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.notEndsWithIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notContainsIfNotEmpty with non-empty value should create NOT LIKE predicate")
        void notContainsIfNotEmpty_withNonEmpty_shouldCallNotLikeWithContainsPattern() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with non-empty value
            Predicate<TestEntity> predicate = path.notContainsIfNotEmpty("test");

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notContainsIfNotEmpty with empty value should create empty predicate")
        void notContainsIfNotEmpty_withEmpty_shouldReturnEmptyPredicate() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method with empty value
            Predicate<TestEntity> predicate = path.notContainsIfNotEmpty("");

            // Then
            assertThat(predicate).isNotNull();
        }
    }

    // ==================== substring(int) default method ====================

    @Nested
    @DisplayName("substring(int) default method")
    class SubstringDefaultMethod {

        @Test
        @DisplayName("substring(int) should delegate to substring(int, Integer.MAX_VALUE)")
        void substring_withOffsetOnly_shouldDelegateToSubstringWithMaxLength() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method substring(int)
            StringExpression<TestEntity> result = path.substring(5);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(StringExpressionImpl.class);
        }
    }

    // ==================== asc/desc default methods (from SimpleExpression) ====================

    @Nested
    @DisplayName("asc/desc default methods from SimpleExpression")
    class SortOrderDefaultMethods {

        @Test
        @DisplayName("asc() should delegate to sort(SortOrder.ASC)")
        void asc_shouldDelegateToSortAsc() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method asc()
            Order<TestEntity> order = path.asc();

            // Then
            assertThat(order).isNotNull();
            assertThat(order).isInstanceOf(OrderImpl.class);
        }

        @Test
        @DisplayName("desc() should delegate to sort(SortOrder.DESC)")
        void desc_shouldDelegateToSortDesc() {
            // Given
            StringPath<TestEntity> path = Paths.get(TestEntity::getName);

            // When - call default method desc()
            Order<TestEntity> order = path.desc();

            // Then
            assertThat(order).isNotNull();
            assertThat(order).isInstanceOf(OrderImpl.class);
        }
    }

}