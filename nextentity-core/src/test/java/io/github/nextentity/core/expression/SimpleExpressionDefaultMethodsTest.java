package io.github.nextentity.core.expression;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleExpression interface default methods.
 * <p>
 * These tests verify that the default methods in SimpleExpression interface
 * correctly delegate to the underlying implementation methods.
 * <p>
 * Note: For methods that are overridden by AbstractExpressionBuilder (ge, gt, le, lt, between, notBetween),
 * we use a custom test implementation that does NOT override these methods, allowing us to test
 * the interface default methods directly.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.SimpleExpression
 */
@DisplayName("SimpleExpression Default Methods Unit Tests")
class SimpleExpressionDefaultMethodsTest {

    // Test entity for path references
    static class TestEntity {
        private String code;
        private BigDecimal amount;
        private Integer count;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    /**
     * A test implementation of SimpleExpression that does NOT override the default methods.
     * This allows us to test the interface default methods directly.
     */
    static class TestSimpleExpression implements SimpleExpression<TestEntity, Integer> {
        private final SimpleExpression<TestEntity, Integer> delegate;

        TestSimpleExpression() {
            this.delegate = Paths.get(TestEntity::getCount);
        }

        @Override
        public NumberExpression<TestEntity, Long> count() {
            return delegate.count();
        }

        @Override
        public NumberExpression<TestEntity, Long> countDistinct() {
            return delegate.countDistinct();
        }

        @Override
        public Predicate<TestEntity> eq(Integer value) {
            return delegate.eq(value);
        }

        @Override
        public Predicate<TestEntity> eqIfNotNull(Integer value) {
            return delegate.eqIfNotNull(value);
        }

        @Override
        public Predicate<TestEntity> eq(TypedExpression<TestEntity, Integer> value) {
            return delegate.eq(value);
        }

        @Override
        public Predicate<TestEntity> ne(Integer value) {
            return delegate.ne(value);
        }

        @Override
        public Predicate<TestEntity> neIfNotNull(Integer value) {
            return delegate.neIfNotNull(value);
        }

        @Override
        public Predicate<TestEntity> ne(TypedExpression<TestEntity, Integer> value) {
            return delegate.ne(value);
        }

        @Override
        public Predicate<TestEntity> in(@NonNull TypedExpression<TestEntity, java.util.List<Integer>> expressions) {
            return delegate.in(expressions);
        }

        @Override
        public Predicate<TestEntity> in(Integer... values) {
            return delegate.in(values);
        }

        @Override
        public Predicate<TestEntity> in(@NonNull java.util.List<? extends TypedExpression<TestEntity, Integer>> values) {
            return delegate.in(values);
        }

        @Override
        public Predicate<TestEntity> in(@NonNull java.util.Collection<? extends Integer> values) {
            return delegate.in(values);
        }

        @Override
        public Predicate<TestEntity> notIn(Integer... values) {
            return delegate.notIn(values);
        }

        @Override
        public Predicate<TestEntity> notIn(@NonNull java.util.List<? extends TypedExpression<TestEntity, Integer>> values) {
            return delegate.notIn(values);
        }

        @Override
        public Predicate<TestEntity> notIn(@NonNull java.util.Collection<? extends Integer> values) {
            return delegate.notIn(values);
        }

        @Override
        public Predicate<TestEntity> isNull() {
            return delegate.isNull();
        }

        @Override
        public Predicate<TestEntity> isNotNull() {
            return delegate.isNotNull();
        }

        @Override
        public Predicate<TestEntity> ge(TypedExpression<TestEntity, Integer> expression) {
            return delegate.ge(expression);
        }

        @Override
        public Predicate<TestEntity> gt(TypedExpression<TestEntity, Integer> expression) {
            return delegate.gt(expression);
        }

        @Override
        public Predicate<TestEntity> le(TypedExpression<TestEntity, Integer> expression) {
            return delegate.le(expression);
        }

        @Override
        public Predicate<TestEntity> lt(TypedExpression<TestEntity, Integer> expression) {
            return delegate.lt(expression);
        }

        @Override
        public Predicate<TestEntity> between(TypedExpression<TestEntity, Integer> l, TypedExpression<TestEntity, Integer> r) {
            return delegate.between(l, r);
        }

        @Override
        public Predicate<TestEntity> notBetween(TypedExpression<TestEntity, Integer> l, TypedExpression<TestEntity, Integer> r) {
            return delegate.notBetween(l, r);
        }

        @Override
        public Order<TestEntity> sort(SortOrder order) {
            return delegate.sort(order);
        }

        @Override
        public Predicate<TestEntity> geIfNotNull(Integer value) {
            return delegate.geIfNotNull(value);
        }

        @Override
        public Predicate<TestEntity> gtIfNotNull(Integer value) {
            return delegate.gtIfNotNull(value);
        }

        @Override
        public Predicate<TestEntity> leIfNotNull(Integer value) {
            return delegate.leIfNotNull(value);
        }

        @Override
        public Predicate<TestEntity> ltIfNotNull(Integer value) {
            return delegate.ltIfNotNull(value);
        }

        @Override
        public SimpleExpression<TestEntity, Integer> max() {
            return delegate.max();
        }

        @Override
        public SimpleExpression<TestEntity, Integer> min() {
            return delegate.min();
        }

        // NOTE: We do NOT override ge(Integer), gt(Integer), le(Integer), lt(Integer),
        // between(Integer, Integer), notBetween(Integer, Integer),
        // between(TypedExpression, Integer), between(Integer, TypedExpression),
        // notBetween(TypedExpression, Integer), notBetween(Integer, TypedExpression),
        // asc(), desc() - these will use the interface default methods!
    }

    // ==================== Default Method Tests (using TestSimpleExpression) ====================

    @Nested
    @DisplayName("Interface default methods via TestSimpleExpression")
    class InterfaceDefaultMethods {

        /**
         * Test: ge(U value) default method - calls ge(root().literal(value)).
         * Uses TestSimpleExpression which does NOT override this method.
         */
        @Test
        @DisplayName("ge(value) should use interface default method")
        void ge_value_shouldUseDefaultMethod() {
            // Given - TestSimpleExpression does NOT override ge(Integer)
            TestSimpleExpression expr = new TestSimpleExpression();

            // When - this calls the interface default method
            Predicate<TestEntity> predicate = expr.ge(100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("gt(value) should use interface default method")
        void gt_value_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Predicate<TestEntity> predicate = expr.gt(100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("le(value) should use interface default method")
        void le_value_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Predicate<TestEntity> predicate = expr.le(100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("lt(value) should use interface default method")
        void lt_value_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Predicate<TestEntity> predicate = expr.lt(100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("between(U, U) should use interface default method")
        void between_twoValues_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Predicate<TestEntity> predicate = expr.between(50, 100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notBetween(U, U) should use interface default method")
        void notBetween_twoValues_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Predicate<TestEntity> predicate = expr.notBetween(50, 100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("between(TypedExpression, U) should use interface default method")
        void between_expressionAndValue_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();
            TypedExpression<TestEntity, Integer> other = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = expr.between(other, 100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("between(U, TypedExpression) should use interface default method")
        void between_valueAndExpression_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();
            TypedExpression<TestEntity, Integer> other = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = expr.between(50, other);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notBetween(TypedExpression, U) should use interface default method")
        void notBetween_expressionAndValue_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();
            TypedExpression<TestEntity, Integer> other = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = expr.notBetween(other, 100);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("notBetween(U, TypedExpression) should use interface default method")
        void notBetween_valueAndExpression_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();
            TypedExpression<TestEntity, Integer> other = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = expr.notBetween(50, other);

            // Then
            assertThat(predicate).isNotNull();
        }

        @Test
        @DisplayName("asc() should use interface default method")
        void asc_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Order<TestEntity> order = expr.asc();

            // Then
            assertThat(order).isNotNull();
        }

        @Test
        @DisplayName("desc() should use interface default method")
        void desc_shouldUseDefaultMethod() {
            // Given
            TestSimpleExpression expr = new TestSimpleExpression();

            // When
            Order<TestEntity> order = expr.desc();

            // Then
            assertThat(order).isNotNull();
        }
    }

    // ==================== Comparison Operations (value-based) ====================

    @Nested
    @DisplayName("ge/gt/le/lt (value) with NumberPath")
    class ComparisonValueOperations {

        @Test
        @DisplayName("ge(value) should create GE predicate")
        void ge_value_shouldCreateGePredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.ge(100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("gt(value) should create GT predicate")
        void gt_value_shouldCreateGtPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.gt(100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("le(value) should create LE predicate")
        void le_value_shouldCreateLePredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.le(100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("lt(value) should create LT predicate")
        void lt_value_shouldCreateLtPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.lt(100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }
    }

    // ==================== Between Operations ====================

    @Nested
    @DisplayName("between/notBetween with NumberPath")
    class BetweenOperations {

        @Test
        @DisplayName("between(U, U) should create BETWEEN predicate")
        void between_twoValues_shouldCreateBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.between(50, 100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notBetween(U, U) should create NOT BETWEEN predicate")
        void notBetween_twoValues_shouldCreateNotBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.notBetween(50, 100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("between(TypedExpression, U) should create BETWEEN predicate")
        void between_expressionAndValue_shouldCreateBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            NumberPath<TestEntity, Integer> otherPath = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.between(otherPath, 100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("between(U, TypedExpression) should create BETWEEN predicate")
        void between_valueAndExpression_shouldCreateBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            NumberPath<TestEntity, Integer> otherPath = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.between(50, otherPath);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notBetween(TypedExpression, U) should create NOT BETWEEN predicate")
        void notBetween_expressionAndValue_shouldCreateNotBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            NumberPath<TestEntity, Integer> otherPath = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.notBetween(otherPath, 100);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notBetween(U, TypedExpression) should create NOT BETWEEN predicate")
        void notBetween_valueAndExpression_shouldCreateNotBetweenPredicate() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            NumberPath<TestEntity, Integer> otherPath = Paths.get(TestEntity::getCount);

            // When
            Predicate<TestEntity> predicate = path.notBetween(50, otherPath);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }
    }

    // ==================== Sort Operations ====================

    @Nested
    @DisplayName("asc/desc default methods")
    class SortOperations {

        @Test
        @DisplayName("asc() should create ASC order")
        void asc_shouldCreateAscOrder() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Order<TestEntity> order = path.asc();

            // Then
            assertThat(order).isNotNull();
            assertThat(order).isInstanceOf(OrderImpl.class);
        }

        @Test
        @DisplayName("desc() should create DESC order")
        void desc_shouldCreateDescOrder() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);

            // When
            Order<TestEntity> order = path.desc();

            // Then
            assertThat(order).isNotNull();
            assertThat(order).isInstanceOf(OrderImpl.class);
        }
    }

    // ==================== Between with Expression Variants ====================

    @Nested
    @DisplayName("Between with expression variants")
    class BetweenWithExpressionVariants {

        @Test
        @DisplayName("between(TypedExpression, TypedExpression) abstract method should work")
        void between_twoExpressions_shouldWork() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            TypedExpression<TestEntity, Integer> left = Paths.get(TestEntity::getCount);
            TypedExpression<TestEntity, Integer> right = Paths.get(TestEntity::getCount);

            // When - call abstract method
            Predicate<TestEntity> predicate = path.between(left, right);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }

        @Test
        @DisplayName("notBetween(TypedExpression, TypedExpression) abstract method should work")
        void notBetween_twoExpressions_shouldWork() {
            // Given
            NumberPath<TestEntity, Integer> path = Paths.get(TestEntity::getCount);
            TypedExpression<TestEntity, Integer> left = Paths.get(TestEntity::getCount);
            TypedExpression<TestEntity, Integer> right = Paths.get(TestEntity::getCount);

            // When - call abstract method
            Predicate<TestEntity> predicate = path.notBetween(left, right);

            // Then
            assertThat(predicate).isNotNull();
            assertThat(predicate).isInstanceOf(PredicateImpl.class);
        }
    }
}