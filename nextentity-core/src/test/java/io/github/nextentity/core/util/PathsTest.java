package io.github.nextentity.core.util;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Path.BooleanRef;
import io.github.nextentity.api.Path.NumberRef;
import io.github.nextentity.api.Path.StringRef;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.PredicateImpl;
import io.github.nextentity.core.expression.SimpleExpressionImpl;
import io.github.nextentity.core.expression.StringExpressionImpl;
import io.github.nextentity.core.expression.NumberExpressionImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Paths utility class and RootImpl.
 */
class PathsTest {

    @Nested
    class StaticFactoryMethods {

        /**
         * Test objective: Verify root() creates EntityRoot instance.
         * Test scenario: Call Paths.root().
         * Expected result: Returns a RootImpl instance.
         */
        @Test
        void root_ShouldReturnEntityRoot() {
            // when
            EntityRoot<String> result = Paths.root();

            // then
            assertThat(result).isNotNull();
        }

        /**
         * Test objective: Verify get(Path) returns EntityPath.
         * Test scenario: Call Paths.get() with a Path reference.
         * Expected result: Returns EntityPath with correct PathNode.
         */
        @Test
        void get_WithPath_ShouldReturnEntityPath() {
            // when
            var result = Paths.get(TestEntity::getId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify get(BooleanRef) returns BooleanPath.
         * Test scenario: Call Paths.get() with a BooleanRef.
         * Expected result: Returns BooleanPath (PredicateImpl).
         */
        @Test
        void get_WithBooleanRef_ShouldReturnBooleanPath() {
            // when
            var result = Paths.get(TestEntity::getActive);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(PredicateImpl.class);
        }

        /**
         * Test objective: Verify get(StringRef) returns StringPath.
         * Test scenario: Call Paths.get() with a StringRef.
         * Expected result: Returns StringPath.
         */
        @Test
        void get_WithStringRef_ShouldReturnStringPath() {
            // when
            var result = Paths.get(TestEntity::getName);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(StringExpressionImpl.class);
        }

        /**
         * Test objective: Verify get(NumberRef) returns NumberPath.
         * Test scenario: Call Paths.get() with a NumberRef.
         * Expected result: Returns NumberPath.
         */
        @Test
        void get_WithNumberRef_ShouldReturnNumberPath() {
            // when
            var result = Paths.get(TestEntity::getCount);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(NumberExpressionImpl.class);
        }
    }

    @Nested
    class TypedPathMethods {

        /**
         * Test objective: Verify path() returns PathExpression.
         * Test scenario: Call Paths.path() with a Path reference.
         * Expected result: Returns PathExpression.
         */
        @Test
        void path_ShouldReturnPathExpression() {
            // when
            var result = Paths.path(TestEntity::getId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify entity() returns EntityPath.
         * Test scenario: Call Paths.entity() with a Path reference.
         * Expected result: Returns EntityPath.
         */
        @Test
        void entity_ShouldReturnEntityPath() {
            // when
            var result = Paths.entity(TestEntity::getParent);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify string() returns StringPath.
         * Test scenario: Call Paths.string() with a Path reference.
         * Expected result: Returns StringPath.
         */
        @Test
        void string_ShouldReturnStringPath() {
            // when
            var result = Paths.string(TestEntity::getName);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(StringExpressionImpl.class);
        }

        /**
         * Test objective: Verify number() returns NumberPath.
         * Test scenario: Call Paths.number() with a Path reference.
         * Expected result: Returns NumberPath.
         */
        @Test
        void number_ShouldReturnNumberPath() {
            // when
            var result = Paths.number(TestEntity::getCount);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(NumberExpressionImpl.class);
        }

        /**
         * Test objective: Verify bool() returns BooleanPath.
         * Test scenario: Call Paths.bool() with a Path reference.
         * Expected result: Returns BooleanPath.
         */
        @Test
        void bool_ShouldReturnBooleanPath() {
            // when
            var result = Paths.bool(TestEntity::getActive);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(PredicateImpl.class);
        }
    }

    @Nested
    class StringBasedPathMethods {

        /**
         * Test objective: Verify path(String) creates path from field name.
         * Test scenario: Call Paths.path() with string field name.
         * Expected result: Returns PathExpression with correct path.
         */
        @Test
        void path_WithString_ShouldCreatePathExpression() {
            // when
            var result = Paths.<TestEntity, Long>path("id");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify entityPath(String) creates entity path from field name.
         * Test scenario: Call Paths.entityPath() with string field name.
         * Expected result: Returns EntityPath.
         */
        @Test
        void entityPath_WithString_ShouldCreateEntityPath() {
            // when
            var result = Paths.<TestEntity, TestEntity>entityPath("parent");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify stringPath(String) creates string path from field name.
         * Test scenario: Call Paths.stringPath() with string field name.
         * Expected result: Returns StringPath.
         */
        @Test
        void stringPath_WithString_ShouldCreateStringPath() {
            // when
            var result = Paths.<TestEntity>stringPath("name");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(StringExpressionImpl.class);
        }

        /**
         * Test objective: Verify numberPath(String) creates number path from field name.
         * Test scenario: Call Paths.numberPath() with string field name.
         * Expected result: Returns NumberPath.
         */
        @Test
        void numberPath_WithString_ShouldCreateNumberPath() {
            // when
            var result = Paths.<TestEntity, Integer>numberPath("count");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(NumberExpressionImpl.class);
        }

        /**
         * Test objective: Verify booleanPath(String) creates boolean path from field name.
         * Test scenario: Call Paths.booleanPath() with string field name.
         * Expected result: Returns BooleanPath.
         */
        @Test
        void booleanPath_WithString_ShouldCreateBooleanPath() {
            // when
            var result = Paths.<TestEntity>booleanPath("active");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(PredicateImpl.class);
        }
    }

    @Nested
    class RootImplTests {

        /**
         * Test objective: Verify RootImpl.literal() creates literal expression.
         * Test scenario: Call literal() with a value.
         * Expected result: Returns SimpleExpressionImpl with LiteralNode.
         */
        @Test
        void literal_ShouldCreateLiteralExpression() {
            // given
            Paths.RootImpl<String> root = new Paths.RootImpl<>();

            // when
            var result = root.literal("test_value");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(SimpleExpressionImpl.class);
        }

        /**
         * Test objective: Verify RootImpl.of() returns same instance.
         * Test scenario: Call of() multiple times.
         * Expected result: Returns the same singleton instance.
         */
        @Test
        void of_ShouldReturnSameInstance() {
            // when
            EntityRoot<String> root1 = Paths.RootImpl.of();
            EntityRoot<Integer> root2 = Paths.RootImpl.of();

            // then
            assertThat(root1).isSameAs(root2);
        }
    }

    /**
     * Test entity class for path references.
     */
    static class TestEntity {
        private Long id;
        private String name;
        private Integer count;
        private Boolean active;
        private TestEntity parent;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
        public TestEntity getParent() { return parent; }
        public void setParent(TestEntity parent) { this.parent = parent; }
    }
}
