package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y PathNode 正确 represents property paths
 /// <p>
 /// 测试场景s:
 /// 1. Creation with single path
 /// 2. Creation with path array
 /// 3. Path navigation (get, append)
 /// 4. ImmutableArray interface 方法
 /// 5. Equality and hashCode
class PathNodeTest {

    @Nested
    class Creation {

///
         /// 测试目标: 验证y creation with single path element
         /// 测试场景: Create PathNode with single string
         /// 预期结果: Path contains one element
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

///
         /// 测试目标: 验证y creation with path array
         /// 测试场景: Create PathNode with string array
         /// 预期结果: Path contains all elements
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

///
         /// 测试目标: 验证y get() adds path element
         /// 测试场景: Call get() on existing PathNode
         /// 预期结果: New PathNode with extended path
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

///
         /// 测试目标: 验证y get() with PathNode combines paths
         /// 测试场景: Call get() with another PathNode
         /// 预期结果: Combined path
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

///
         /// 测试目标: 验证y original path is not modified
         /// 测试场景: Call get() and check original
         /// 预期结果: Original unchanged
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

///
         /// 测试目标: 验证y stream() returns elements
         /// 测试场景: Call stream() on PathNode
         /// 预期结果: Stream with all path elements
        @Test
        void stream_ReturnsAllElements() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b", "c"});

            // when & then
            assertThat(node.stream()).containsExactly("a", "b", "c");
        }

///
         /// 测试目标: 验证y iterator() works 正确
         /// 测试场景: Iterate over PathNode
         /// 预期结果: All elements in order
        @Test
        void iterator_ReturnsAllElements() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when & then
            assertThat(node.iterator()).toIterable().containsExactly("a", "b");
        }

///
         /// 测试目标: 验证y deep() returns path depth
         /// 测试场景: Call deep() on PathNode
         /// 预期结果: Same as size()
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

///
         /// 测试目标: 验证y subLength returns prefix
         /// 测试场景: Call subLength(2) on 3-element path
         /// 预期结果: First 2 elements
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

///
         /// 测试目标: 验证y subLength(0) returns null
         /// 测试场景: Call subLength(0)
         /// 预期结果: null
        @Test
        void subLength_Zero_ReturnsNull() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            PathNode result = node.subLength(0);

            // then
            assertThat(result).isNull();
        }

///
         /// 测试目标: 验证y subLength with negative returns null
         /// 测试场景: Call subLength(-1)
         /// 预期结果: null
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

///
         /// 测试目标: 验证y equal paths are equal
         /// 测试场景: Compare two PathNodes with same paths
         /// 预期结果: Are equal
        @Test
        void equals_SamePaths_AreEqual() {
            // given
            PathNode node1 = new PathNode(new String[]{"a", "b"});
            PathNode node2 = new PathNode(new String[]{"a", "b"});

            // when & then
            assertThat(node1).isEqualTo(node2);
            assertThat(node1.hashCode()).isEqualTo(node2.hashCode());
        }

///
         /// 测试目标: 验证y different paths are not equal
         /// 测试场景: Compare two PathNodes with different paths
         /// 预期结果: Are not equal
        @Test
        void equals_DifferentPaths_AreNotEqual() {
            // given
            PathNode node1 = new PathNode(new String[]{"a", "b"});
            PathNode node2 = new PathNode(new String[]{"a", "c"});

            // when & then
            assertThat(node1).isNotEqualTo(node2);
        }

///
         /// 测试目标: 验证y equals with null
         /// 测试场景: Compare PathNode with null
         /// 预期结果: Are not equal
        @Test
        void equals_Null_ReturnsFalse() {
            // given
            PathNode node = new PathNode("test");

            // when & then
            assertThat(node.equals(null)).isFalse();
        }

///
         /// 测试目标: 验证y equals with different type
         /// 测试场景: Compare PathNode with string
         /// 预期结果: Are not equal
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

///
         /// 测试目标: 验证y join creates new array with added element
         /// 测试场景: Call join() on PathNode
         /// 预期结果: New array with added element
        @Test
        void join_AddsElement() {
            // given
            PathNode node = new PathNode(new String[]{"a", "b"});

            // when
            String[] result = node.join("c");

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }

///
         /// 测试目标: 验证y join does not modify original
         /// 测试场景: Call join() and check original
         /// 预期结果: Original unchanged
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
