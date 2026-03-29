package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AbstractExpressionBuilder.
 */
class AbstractExpressionBuilderTest {

    private TestExpressionBuilder builder;
    private ExpressionNode capturedNode;

    /**
     * Concrete implementation for testing.
     */
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

        /**
         * Test objective: Verify getRoot() returns the root node.
         * Test scenario: Call getRoot() after construction.
         * Expected result: Returns the root node passed to constructor.
         */
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

        /**
         * Test objective: Verify eq() creates EQ operator node.
         * Test scenario: Call eq() with a value.
         * Expected result: Creates operator node with EQ operator.
         */
        @Test
        void eq_ShouldCreateEqOperator() {
            // when
            builder.eq("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        /**
         * Test objective: Verify eqIfNotNull() with null returns EmptyNode.
         * Test scenario: Call eqIfNotNull() with null value.
         * Expected result: Returns EmptyNode.
         */
        @Test
        void eqIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.eqIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

        /**
         * Test objective: Verify eqIfNotNull() with value creates EQ operator.
         * Test scenario: Call eqIfNotNull() with non-null value.
         * Expected result: Creates operator node with EQ operator.
         */
        @Test
        void eqIfNotNull_WhenNotNull_ShouldCreateEqOperator() {
            // when
            builder.eqIfNotNull("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        /**
         * Test objective: Verify ne() creates NE operator node.
         * Test scenario: Call ne() with a value.
         * Expected result: Creates operator node with NE operator.
         */
        @Test
        void ne_ShouldCreateNeOperator() {
            // when
            builder.ne("value");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NE);
        }

        /**
         * Test objective: Verify neIfNotNull() with null returns EmptyNode.
         * Test scenario: Call neIfNotNull() with null value.
         * Expected result: Returns EmptyNode.
         */
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

        /**
         * Test objective: Verify gt() creates GT operator node.
         * Test scenario: Call gt() with a value.
         * Expected result: Creates operator node with GT operator.
         */
        @Test
        void gt_ShouldCreateGtOperator() {
            // when
            builder.gt(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.GT);
        }

        /**
         * Test objective: Verify ge() creates GE operator node.
         * Test scenario: Call ge() with a value.
         * Expected result: Creates operator node with GE operator.
         */
        @Test
        void ge_ShouldCreateGeOperator() {
            // when
            builder.ge(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.GE);
        }

        /**
         * Test objective: Verify lt() creates LT operator node.
         * Test scenario: Call lt() with a value.
         * Expected result: Creates operator node with LT operator.
         */
        @Test
        void lt_ShouldCreateLtOperator() {
            // when
            builder.lt(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LT);
        }

        /**
         * Test objective: Verify le() creates LE operator node.
         * Test scenario: Call le() with a value.
         * Expected result: Creates operator node with LE operator.
         */
        @Test
        void le_ShouldCreateLeOperator() {
            // when
            builder.le(10);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LE);
        }

        /**
         * Test objective: Verify gtIfNotNull() with null returns EmptyNode.
         * Test scenario: Call gtIfNotNull() with null value.
         * Expected result: Returns EmptyNode.
         */
        @Test
        void gtIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.gtIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

        /**
         * Test objective: Verify geIfNotNull() with value creates GE operator.
         * Test scenario: Call geIfNotNull() with non-null value.
         * Expected result: Creates operator node with GE operator.
         */
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

        /**
         * Test objective: Verify isNull() creates IS_NULL operator node.
         * Test scenario: Call isNull().
         * Expected result: Creates operator node with IS_NULL operator.
         */
        @Test
        void isNull_ShouldCreateIsNullOperator() {
            // when
            builder.isNull();

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IS_NULL);
        }

        /**
         * Test objective: Verify isNotNull() creates IS_NOT_NULL operator node.
         * Test scenario: Call isNotNull().
         * Expected result: Creates operator node with IS_NOT_NULL operator.
         */
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

        /**
         * Test objective: Verify in() with varargs creates IN operator node.
         * Test scenario: Call in() with multiple values.
         * Expected result: Creates operator node with IN operator.
         */
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

        /**
         * Test objective: Verify in() with Collection creates IN operator node.
         * Test scenario: Call in() with a collection.
         * Expected result: Creates operator node with IN operator.
         */
        @Test
        void in_WithCollection_ShouldCreateInOperator() {
            // when
            builder.in(List.of("a", "b"));

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
        }

        /**
         * Test objective: Verify notIn() creates IN operator wrapped with NOT.
         * Test scenario: Call notIn() with values.
         * Expected result: Creates NOT(IN) operator structure.
         */
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

        /**
         * Test objective: Verify between() creates BETWEEN operator node.
         * Test scenario: Call between() with two values.
         * Expected result: Creates operator node with BETWEEN operator.
         */
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

        /**
         * Test objective: Verify notBetween() creates NOT(BETWEEN) structure.
         * Test scenario: Call notBetween() with two values.
         * Expected result: Creates NOT(BETWEEN) operator structure.
         */
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

        /**
         * Test objective: Verify like() creates LIKE operator node.
         * Test scenario: Call like() with a pattern.
         * Expected result: Creates operator node with LIKE operator.
         */
        @Test
        void like_ShouldCreateLikeOperator() {
            // when
            builder.like("%test%");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.LIKE);
        }

        /**
         * Test objective: Verify notLike() creates NOT(LIKE) structure.
         * Test scenario: Call notLike() with a pattern.
         * Expected result: Creates NOT(LIKE) operator structure.
         */
        @Test
        void notLike_ShouldCreateNotLikeOperator() {
            // when
            builder.notLike("%test%");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }

        /**
         * Test objective: Verify likeIfNotNull() with null returns EmptyNode.
         * Test scenario: Call likeIfNotNull() with null value.
         * Expected result: Returns EmptyNode.
         */
        @Test
        void likeIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            // when
            builder.likeIfNotNull(null);

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

        /**
         * Test objective: Verify likeIfNotEmpty() with empty string returns EmptyNode.
         * Test scenario: Call likeIfNotEmpty() with empty string.
         * Expected result: Returns EmptyNode.
         */
        @Test
        void likeIfNotEmpty_WhenEmpty_ShouldReturnEmptyNode() {
            // when
            builder.likeIfNotEmpty("");

            // then
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }

        /**
         * Test objective: Verify likeIfNotEmpty() with non-empty creates LIKE.
         * Test scenario: Call likeIfNotEmpty() with non-empty string.
         * Expected result: Creates LIKE operator.
         */
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

        /**
         * Test objective: Verify not() creates NOT operator node.
         * Test scenario: Call not().
         * Expected result: Creates operator node with NOT operator.
         */
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
