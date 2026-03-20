package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify PathNode correctly represents property paths
 * <p>
 * Test scenarios:
 * 1. Creation with single path
 * 2. Creation with path array
 * 3. Path navigation (get, append)
 * 4. ImmutableArray interface methods
 * 5. Equality and hashCode
 */
class PathNodeTest {

    @Nested
    class Creation {

        /**
         * Test objective: Verify creation with single path element
         * Test scenario: Create PathNode with single string
         * Expected result: Path contains one element
         */
        @Test
        void constructor_SinglePath_HasOneElement() {
            // given
            String path = "name";

            // when
            PathNode node = new PathNode(path);

            // then
            assertThat(node.size()).isEqualTo(1);
            assertThat(node.get(0)).isEqualTo(path);
        }

        /**
         * Test objective: Verify creation with path array
         * Test scenario: Create PathNode with string array
         * Expected result: Path contains all elements
         */
        @Test
        void constructor_PathArray_HasAllElements() {
            // given
            String[] paths = {"user", "address", "city"};

            // when
            PathNode node = new PathNode(paths);

            // then
            assertThat(node.size()).isEqualTo(3);
            assertThat(node.get(0)).isEqualTo("user");
            assertThat(node.get(1)).isEqualTo("address");
            assertThat(node.get(2)).isEqualTo("city");
        }
    }

    @Nested
    class PathNavigation {

        /**
         * Test objective: Verify get() adds path element
         * Test scenario: Call get() on existing PathNode
         * Expected result: New PathNode with extended path
         */
        @Test
        void get_AddsPathElement() {
            // given
            PathNode node = new PathNode("user");

            // when
            PathNode result = node.get("name");

            // then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0)).isEqualTo("user");
            assertThat(result.get(1)).isEqualTo("name");
        }

        /**
         * Test objective: Verify get() with PathNode combines paths
         * Test scenario: Call get() with another PathNode
         * Expected result: Combined path
         */
        @Test
        void get_WithPathNode_CombinesPaths() {
            // given
            PathNode node1 = new PathNode(new String[]{"user", "address"});
            PathNode node2 = new PathNode(new String[]{"city", "zipCode"});

            // when
            PathNode result = node1.get(node2);

            // then
            assertThat(result.size()).isEqualTo(4);
            assertThat(result.get(0)).isEqualTo("user");
            assertThat(result.get(1)).isEqualTo("address");
            assertThat(result.get(2)).isEqualTo("city");
            assertThat(result.get(3)).isEqualTo("zipCode");
        }

        /**
         * Test objective: Verify original path is not modified
         * Test scenario: Call get() and check original
         * Expected result: Original unchanged
         */
        @Test
        void get_DoesNotModifyOriginal() {
            // given
            PathNode original = new PathNode("user");

            // when
            original.get("name");

            // then
            assertThat(original.size()).isEqualTo(1);
        }
    }

    @Nested
    class ImmutableArrayInterface {

        /**
         * Test objective: Verify stream() returns elements
         * Test scenario: Call stream() on PathNode
         * Expected result: Stream with all path elements
         */
        @Test
        void stream_ReturnsAllElements() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b", "c"});

            // when & then
            assertThat(node.stream()).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify iterator() works correctly
         * Test scenario: Iterate over PathNode
         * Expected result: All elements in order
         */
        @Test
        void iterator_ReturnsAllElements() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when & then
            assertThat(node.iterator()).toIterable().containsExactly("a", "b");
        }

        /**
         * Test objective: Verify deep() returns path depth
         * Test scenario: Call deep() on PathNode
         * Expected result: Same as size()
         */
        @Test
        void deep_ReturnsPathDepth() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b", "c"});

            // when
            int deep = node.deep();

            // then
            assertThat(deep).isEqualTo(3);
        }
    }

    @Nested
    class SubLength {

        /**
         * Test objective: Verify subLength returns prefix
         * Test scenario: Call subLength(2) on 3-element path
         * Expected result: First 2 elements
         */
        @Test
        void subLength_ReturnsPrefix() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b", "c"});

            // when
            PathNode result = node.subLength(2);

            // then
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0)).isEqualTo("a");
            assertThat(result.get(1)).isEqualTo("b");
        }

        /**
         * Test objective: Verify subLength(0) returns null
         * Test scenario: Call subLength(0)
         * Expected result: null
         */
        @Test
        void subLength_Zero_ReturnsNull() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            PathNode result = node.subLength(0);

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify subLength with negative returns null
         * Test scenario: Call subLength(-1)
         * Expected result: null
         */
        @Test
        void subLength_Negative_ReturnsNull() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            PathNode result = node.subLength(-1);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class Equality {

        /**
         * Test objective: Verify equal paths are equal
         * Test scenario: Compare two PathNodes with same paths
         * Expected result: Are equal
         */
        @Test
        void equals_SamePaths_AreEqual() {
            // given
            PathNode node1 = new PathNode(new String[]{"a", "b"});
            PathNode node2 = new PathNode(new String[]{"a", "b"});

            // when & then
            assertThat(node1).isEqualTo(node2);
            assertThat(node1.hashCode()).isEqualTo(node2.hashCode());
        }

        /**
         * Test objective: Verify different paths are not equal
         * Test scenario: Compare two PathNodes with different paths
         * Expected result: Are not equal
         */
        @Test
        void equals_DifferentPaths_AreNotEqual() {
            // given
            PathNode node1 = new PathNode(new String[]{"a", "b"});
            PathNode node2 = new PathNode(new String[]{"a", "c"});

            // when & then
            assertThat(node1).isNotEqualTo(node2);
        }

        /**
         * Test objective: Verify equals with null
         * Test scenario: Compare PathNode with null
         * Expected result: Are not equal
         */
        @Test
        void equals_Null_ReturnsFalse() {
            // given
            PathNode node = new PathNode("test");

            // when & then
            assertThat(node.equals(null)).isFalse();
        }

        /**
         * Test objective: Verify equals with different type
         * Test scenario: Compare PathNode with string
         * Expected result: Are not equal
         */
        @Test
        void equals_DifferentType_ReturnsFalse() {
            // given
            PathNode node = new PathNode("test");

            // when & then
            assertThat(node.equals("test")).isFalse();
        }
    }

    @Nested
    class JoinMethod {

        /**
         * Test objective: Verify join creates new array with added element
         * Test scenario: Call join() on PathNode
         * Expected result: New array with added element
         */
        @Test
        void join_AddsElement() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            String[] result = node.join("c");

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify join does not modify original
         * Test scenario: Call join() and check original
         * Expected result: Original unchanged
         */
        @Test
        void join_DoesNotModifyOriginal() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            node.join("c");

            // then
            assertThat(node.size()).isEqualTo(2);
        }
    }
}
