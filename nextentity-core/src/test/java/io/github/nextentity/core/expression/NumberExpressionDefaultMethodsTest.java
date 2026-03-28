package io.github.nextentity.core.expression;

import io.github.nextentity.api.NumberExpression;
import io.github.nextentity.api.NumberPath;
import io.github.nextentity.core.util.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NumberExpression interface default methods.
 * <p>
 * These tests verify that the default methods in NumberExpression interface
 * correctly handle null values by returning the same expression instance.
 * <p>
 * Uses Paths.get() to obtain NumberPath instances which implement NumberExpression.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.NumberExpression
 */
@DisplayName("NumberExpression Default Methods Unit Tests")
class NumberExpressionDefaultMethodsTest {

    // Test entity for path references
    static class TestEntity {
        private Double amount;

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    // ==================== addIfNotNull default method ====================

    @Nested
    @DisplayName("addIfNotNull default method")
    class AddIfNotNullTests {

        @Test
        @DisplayName("addIfNotNull with non-null value should return new expression with ADD operation")
        void addIfNotNull_withNonNull_shouldReturnNewExpression() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with non-null value
            NumberExpression<TestEntity, Double> result = path.addIfNotNull(100.0);

            // Then - should return a new expression (not the same instance)
            assertThat(result).isNotNull();
            assertThat(result).isNotSameAs(path);
        }

        @Test
        @DisplayName("addIfNotNull with null value should return same expression instance")
        void addIfNotNull_withNull_shouldReturnSameInstance() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with null value
            NumberExpression<TestEntity, Double> result = path.addIfNotNull(null);

            // Then - should return the same instance (this)
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(path);
        }
    }

    // ==================== subtractIfNotNull default method ====================

    @Nested
    @DisplayName("subtractIfNotNull default method")
    class SubtractIfNotNullTests {

        @Test
        @DisplayName("subtractIfNotNull with non-null value should return new expression with SUBTRACT operation")
        void subtractIfNotNull_withNonNull_shouldReturnNewExpression() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with non-null value
            NumberExpression<TestEntity, Double> result = path.subtractIfNotNull(50.0);

            // Then - should return a new expression (not the same instance)
            assertThat(result).isNotNull();
            assertThat(result).isNotSameAs(path);
        }

        @Test
        @DisplayName("subtractIfNotNull with null value should return same expression instance")
        void subtractIfNotNull_withNull_shouldReturnSameInstance() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with null value
            NumberExpression<TestEntity, Double> result = path.subtractIfNotNull(null);

            // Then - should return the same instance (this)
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(path);
        }
    }

    // ==================== multiplyIfNotNull default method ====================

    @Nested
    @DisplayName("multiplyIfNotNull default method")
    class MultiplyIfNotNullTests {

        @Test
        @DisplayName("multiplyIfNotNull with non-null value should return new expression with MULTIPLY operation")
        void multiplyIfNotNull_withNonNull_shouldReturnNewExpression() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with non-null value
            NumberExpression<TestEntity, Double> result = path.multiplyIfNotNull(2.0);

            // Then - should return a new expression (not the same instance)
            assertThat(result).isNotNull();
            assertThat(result).isNotSameAs(path);
        }

        @Test
        @DisplayName("multiplyIfNotNull with null value should return same expression instance")
        void multiplyIfNotNull_withNull_shouldReturnSameInstance() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with null value
            NumberExpression<TestEntity, Double> result = path.multiplyIfNotNull(null);

            // Then - should return the same instance (this)
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(path);
        }
    }

    // ==================== divideIfNotNull default method ====================

    @Nested
    @DisplayName("divideIfNotNull default method")
    class DivideIfNotNullTests {

        @Test
        @DisplayName("divideIfNotNull with non-null value should return new expression with DIVIDE operation")
        void divideIfNotNull_withNonNull_shouldReturnNewExpression() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with non-null value
            NumberExpression<TestEntity, Double> result = path.divideIfNotNull(10.0);

            // Then - should return a new expression (not the same instance)
            assertThat(result).isNotNull();
            assertThat(result).isNotSameAs(path);
        }

        @Test
        @DisplayName("divideIfNotNull with null value should return same expression instance")
        void divideIfNotNull_withNull_shouldReturnSameInstance() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with null value
            NumberExpression<TestEntity, Double> result = path.divideIfNotNull(null);

            // Then - should return the same instance (this)
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(path);
        }
    }

    // ==================== modIfNotNull default method ====================

    @Nested
    @DisplayName("modIfNotNull default method")
    class ModIfNotNullTests {

        @Test
        @DisplayName("modIfNotNull with non-null value should return new expression with MOD operation")
        void modIfNotNull_withNonNull_shouldReturnNewExpression() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with non-null value
            NumberExpression<TestEntity, Double> result = path.modIfNotNull(3.0);

            // Then - should return a new expression (not the same instance)
            assertThat(result).isNotNull();
            assertThat(result).isNotSameAs(path);
        }

        @Test
        @DisplayName("modIfNotNull with null value should return same expression instance")
        void modIfNotNull_withNull_shouldReturnSameInstance() {
            // Given
            NumberPath<TestEntity, Double> path = Paths.get(TestEntity::getAmount);

            // When - call default method with null value
            NumberExpression<TestEntity, Double> result = path.modIfNotNull(null);

            // Then - should return the same instance (this)
            assertThat(result).isNotNull();
            assertThat(result).isSameAs(path);
        }
    }
}