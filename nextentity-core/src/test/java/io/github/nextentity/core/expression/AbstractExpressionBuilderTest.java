package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 单元测试 AbstractExpressionBuilder.
class AbstractExpressionBuilderTest {

    private TestExpressionBuilder builder;
    private ExpressionNode capturedNode;

///
     /// Concrete implementation for testing.
    private class TestExpressionBuilder extends AbstractExpressionBuilder<String, Object, TestExpressionBuilder> {

        public TestExpressionBuilder(ExpressionNode root) {
            super(root);
        }

        @Override
        protected TestExpressionBuilder next(ExpressionNode operate) {
            capturedNode = operate;
            return this;
        }
    }

    @BeforeEach
    void setUp() {
        PathNode rootNode = new PathNode("test");
        builder = new TestExpressionBuilder(rootNode);
        capturedNode = null;
    }

    @Nested
    class GetRoot {

///
         /// 测试目标: 验证y getRoot() returns the root node.
         /// 测试场景: Call getRoot() after construction.
         /// 预期结果: Returns the root node passed to constructor.
        @Test
        void getRoot_ShouldReturnConstructorValue() {
            // when
            ExpressionNode result = builder.getRoot();

            // then
            assertThat(result).isInstanceOf(PathNode.class);
        }
    }

    @Nested
    class EqualityOperators {

///
         /// 测试目标: 验证y eq() creates EQ operator node.
         /// 测试场景: Call eq() with a value.
         /// 预期结果: 创建 operator node with EQ operator.
        @Test
        void eq_ShouldCreateEqOperator() {
            // when
            builder.eq("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

///
         /// 测试目标: 验证y eqIfNotNull() with null returns EmptyNode.
         /// 测试场景: Call eqIfNotNull() with null value.
         /// 预期结果: Returns EmptyNode.
        @Test
        void eqIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.eqIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

///
         /// 测试目标: 验证y eqIfNotNull() with value creates EQ operator.
         /// 测试场景: Call eqIfNotNull() with non-null value.
         /// 预期结果: 创建 operator node with EQ operator.
        @Test
        void eqIfNotNull_WhenNotNull_ShouldCreateEqOperator() {
            // when
            builder.eqIfNotNull("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

///
         /// 测试目标: 验证y ne() creates NE operator node.
         /// 测试场景: Call ne() with a value.
         /// 预期结果: 创建 operator node with NE operator.
        @Test
        void ne_ShouldCreateNeOperator() {
            // when
            builder.ne("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NE);
        }

///
         /// 测试目标: 验证y neIfNotNull() with null returns EmptyNode.
         /// 测试场景: Call neIfNotNull() with null value.
         /// 预期结果: Returns EmptyNode.
        @Test
        void neIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.neIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }
    }

    @Nested
    class ComparisonOperators {

///
         /// 测试目标: 验证y gt() creates GT operator node.
         /// 测试场景: Call gt() with a value.
         /// 预期结果: 创建 operator node with GT operator.
        @Test
        void gt_ShouldCreateGtOperator() {
            // when
            builder.gt(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.GT);
        }

///
         /// 测试目标: 验证y ge() creates GE operator node.
         /// 测试场景: Call ge() with a value.
         /// 预期结果: 创建 operator node with GE operator.
        @Test
        void ge_ShouldCreateGeOperator() {
            // when
            builder.ge(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.GE);
        }

///
         /// 测试目标: 验证y lt() creates LT operator node.
         /// 测试场景: Call lt() with a value.
         /// 预期结果: 创建 operator node with LT operator.
        @Test
        void lt_ShouldCreateLtOperator() {
            // when
            builder.lt(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LT);
        }

///
         /// 测试目标: 验证y le() creates LE operator node.
         /// 测试场景: Call le() with a value.
         /// 预期结果: 创建 operator node with LE operator.
        @Test
        void le_ShouldCreateLeOperator() {
            // when
            builder.le(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LE);
        }

///
         /// 测试目标: 验证y gtIfNotNull() with null returns EmptyNode.
         /// 测试场景: Call gtIfNotNull() with null value.
         /// 预期结果: Returns EmptyNode.
        @Test
        void gtIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.gtIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

///
         /// 测试目标: 验证y geIfNotNull() with value creates GE operator.
         /// 测试场景: Call geIfNotNull() with non-null value.
         /// 预期结果: 创建 operator node with GE operator.
        @Test
        void geIfNotNull_WhenNotNull_ShouldCreateGeOperator() {
            // when
            builder.geIfNotNull(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.GE);
        }
    }

    @Nested
    class NullOperators {

///
         /// 测试目标: 验证y isNull() creates IS_NULL operator node.
         /// 测试场景: Call isNull().
         /// 预期结果: 创建 operator node with IS_NULL operator.
        @Test
        void isNull_ShouldCreateIsNullOperator() {
            // when
            builder.isNull();

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IS_NULL);
        }

///
         /// 测试目标: 验证y isNotNull() creates IS_NOT_NULL operator node.
         /// 测试场景: Call isNotNull().
         /// 预期结果: 创建 operator node with IS_NOT_NULL operator.
        @Test
        void isNotNull_ShouldCreateIsNotNullOperator() {
            // when
            builder.isNotNull();

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IS_NOT_NULL);
        }
    }

    @Nested
    class InOperators {

///
         /// 测试目标: 验证y in() with varargs creates IN operator node.
         /// 测试场景: Call in() with multiple values.
         /// 预期结果: 创建 operator node with IN operator.
        @Test
        void in_WithVarargs_ShouldCreateInOperator() {
            // when
            builder.in("a", "b", "c");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
            assertThat(opNode.operands()).hasSize(4); // root + 3 values
        }

///
         /// 测试目标: 验证y in() with Collection creates IN operator node.
         /// 测试场景: Call in() with a collection.
         /// 预期结果: 创建 operator node with IN operator.
        @Test
        void in_WithCollection_ShouldCreateInOperator() {
            // when
            builder.in(List.of("a", "b"));

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
        }

///
         /// 测试目标: 验证y notIn() creates IN operator wrapped with NOT.
         /// 测试场景: Call notIn() with values.
         /// 预期结果: 创建 NOT(IN) operator structure.
        @Test
        void notIn_ShouldCreateNotInOperator() {
            // when
            builder.notIn("a", "b");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
            assertThat(opNode.operands().getFirst()).isInstanceOf(OperatorNode.class);
            OperatorNode innerOp = (OperatorNode) opNode.operands().getFirst();
            assertThat(innerOp.operator()).isEqualTo(Operator.IN);
        }
    }

    @Nested
    class BetweenOperators {

///
         /// 测试目标: 验证y between() creates BETWEEN operator node.
         /// 测试场景: Call between() with two values.
         /// 预期结果: 创建 operator node with BETWEEN operator.
        @Test
        void between_ShouldCreateBetweenOperator() {
            // when
            builder.between(1, 10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.BETWEEN);
            assertThat(opNode.operands()).hasSize(3); // root + left + right
        }

///
         /// 测试目标: 验证y notBetween() creates NOT(BETWEEN) structure.
         /// 测试场景: Call notBetween() with two values.
         /// 预期结果: 创建 NOT(BETWEEN) operator structure.
        @Test
        void notBetween_ShouldCreateNotBetweenOperator() {
            // when
            builder.notBetween(1, 10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }
    }

    @Nested
    class LikeOperators {

///
         /// 测试目标: 验证y like() creates LIKE operator node.
         /// 测试场景: Call like() with a pattern.
         /// 预期结果: 创建 operator node with LIKE operator.
        @Test
        void like_ShouldCreateLikeOperator() {
            // when
            builder.like("%test%");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LIKE);
        }

///
         /// 测试目标: 验证y notLike() creates NOT(LIKE) structure.
         /// 测试场景: Call notLike() with a pattern.
         /// 预期结果: 创建 NOT(LIKE) operator structure.
        @Test
        void notLike_ShouldCreateNotLikeOperator() {
            // when
            builder.notLike("%test%");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }

///
         /// 测试目标: 验证y likeIfNotNull() with null returns EmptyNode.
         /// 测试场景: Call likeIfNotNull() with null value.
         /// 预期结果: Returns EmptyNode.
        @Test
        void likeIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.likeIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

///
         /// 测试目标: 验证y likeIfNotEmpty() with empty string returns EmptyNode.
         /// 测试场景: Call likeIfNotEmpty() with empty string.
         /// 预期结果: Returns EmptyNode.
        @Test
        void likeIfNotEmpty_WhenEmpty_ShouldReturnEmptyNode() {
            // when
            builder.likeIfNotEmpty("");

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

///
         /// 测试目标: 验证y likeIfNotEmpty() with non-empty creates LIKE.
         /// 测试场景: Call likeIfNotEmpty() with non-empty string.
         /// 预期结果: 创建 LIKE operator.
        @Test
        void likeIfNotEmpty_WhenNonEmpty_ShouldCreateLikeOperator() {
            // when
            builder.likeIfNotEmpty("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LIKE);
        }
    }

    @Nested
    class LogicalOperators {

///
         /// 测试目标: 验证y not() creates NOT operator node.
         /// 测试场景: Call not().
         /// 预期结果: 创建 operator node with NOT operator.
        @Test
        void not_ShouldCreateNotOperator() {
            // when
            builder.not();

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }
    }
}

