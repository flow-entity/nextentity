package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 单元测试 AbstractExpressionBuilder.
class AbstractExpressionBuilderTest {

    private PathNode rootNode;
    private TestExpressionBuilder builder;

    /// Concrete implementation for testing.
    /// 返回新构建器，保存操作后的 root 节点。
    private class TestExpressionBuilder extends AbstractExpressionBuilder<String, Object, TestExpressionBuilder> {

        public TestExpressionBuilder(ExpressionNode root) {
            super(root);
        }

        @Override
        protected TestExpressionBuilder next(ExpressionNode operate) {
            return new TestExpressionBuilder(operate);
        }
    }

    @BeforeEach
    void setUp() {
        rootNode = new PathNode("test");
        builder = new TestExpressionBuilder(rootNode);
    }

    @Nested
    class GetRoot {

        @Test
        void getRoot_ShouldReturnConstructorValue() {
            assertThat(builder.getRoot()).isEqualTo(rootNode);
        }
    }

    @Nested
    class EqualityOperators {

        @Test
        void eq_ShouldCreateEqOperator() {
            TestExpressionBuilder result = builder.eq("value");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        @Test
        void eqIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.eqIfNotNull(null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void eqIfNotNull_WhenNotNull_ShouldCreateEqOperator() {
            TestExpressionBuilder result = builder.eqIfNotNull("value");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        @Test
        void ne_ShouldCreateNeOperator() {
            TestExpressionBuilder result = builder.ne("value");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NE);
        }

        @Test
        void neIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.neIfNotNull(null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }
    }

    @Nested
    class ComparisonOperators {

        @Test
        void gt_ShouldCreateGtOperator() {
            TestExpressionBuilder result = builder.gt(10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.GT);
        }

        @Test
        void ge_ShouldCreateGeOperator() {
            TestExpressionBuilder result = builder.ge(10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.GE);
        }

        @Test
        void lt_ShouldCreateLtOperator() {
            TestExpressionBuilder result = builder.lt(10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.LT);
        }

        @Test
        void le_ShouldCreateLeOperator() {
            TestExpressionBuilder result = builder.le(10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.LE);
        }

        @Test
        void gtIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.gtIfNotNull(null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void geIfNotNull_WhenNotNull_ShouldCreateGeOperator() {
            TestExpressionBuilder result = builder.geIfNotNull(10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.GE);
        }
    }

    @Nested
    class NullOperators {

        @Test
        void isNull_ShouldCreateIsNullOperator() {
            TestExpressionBuilder result = builder.isNull();

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.IS_NULL);
        }

        @Test
        void isNotNull_ShouldCreateIsNotNullOperator() {
            TestExpressionBuilder result = builder.isNotNull();

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.IS_NOT_NULL);
        }
    }

    @Nested
    class InOperators {

        @Test
        void in_WithVarargs_ShouldCreateInOperator() {
            TestExpressionBuilder result = builder.in("a", "b", "c");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
            assertThat(opNode.operands()).hasSize(4);
        }

        @Test
        void in_WithCollection_ShouldCreateInOperator() {
            TestExpressionBuilder result = builder.in(List.of("a", "b"));

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
        }

        @Test
        void notIn_ShouldCreateNotInOperator() {
            TestExpressionBuilder result = builder.notIn("a", "b");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
            assertThat(opNode.operands().getFirst()).isInstanceOf(OperatorNode.class);
            OperatorNode innerOp = (OperatorNode) opNode.operands().getFirst();
            assertThat(innerOp.operator()).isEqualTo(Operator.IN);
        }

        @Test
        void inIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.inIfNotNull((List<String>) null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void inIfNotNull_WhenNonNull_ShouldCreateInOperator() {
            TestExpressionBuilder result = builder.inIfNotNull(List.of("a", "b"));

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.IN);
        }

        @Test
        void notInIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.notInIfNotNull((List<String>) null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void notInIfNotNull_WhenNonNull_ShouldCreateNotInOperator() {
            TestExpressionBuilder result = builder.notInIfNotNull(List.of("a", "b"));

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
            assertThat(opNode.operands().getFirst()).isInstanceOf(OperatorNode.class);
            OperatorNode innerOp = (OperatorNode) opNode.operands().getFirst();
            assertThat(innerOp.operator()).isEqualTo(Operator.IN);
        }
    }

    @Nested
    class BetweenOperators {

        @Test
        void between_ShouldCreateBetweenOperator() {
            TestExpressionBuilder result = builder.between(1, 10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.BETWEEN);
            assertThat(opNode.operands()).hasSize(3);
        }

        @Test
        void notBetween_ShouldCreateNotBetweenOperator() {
            TestExpressionBuilder result = builder.notBetween(1, 10);

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }
    }

    @Nested
    class LikeOperators {

        @Test
        void like_ShouldCreateLikeOperator() {
            TestExpressionBuilder result = builder.like("%test%");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.LIKE);
        }

        @Test
        void notLike_ShouldCreateNotLikeOperator() {
            TestExpressionBuilder result = builder.notLike("%test%");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }

        @Test
        void likeIfNotNull_WhenNull_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.likeIfNotNull(null);

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void likeIfNotEmpty_WhenEmpty_ShouldReturnEmptyNode() {
            TestExpressionBuilder result = builder.likeIfNotEmpty("");

            assertThat(result.getRoot()).isInstanceOf(EmptyNode.class);
        }

        @Test
        void likeIfNotEmpty_WhenNonEmpty_ShouldCreateLikeOperator() {
            TestExpressionBuilder result = builder.likeIfNotEmpty("test");

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.LIKE);
        }
    }

    @Nested
    class LogicalOperators {

        @Test
        void not_ShouldCreateNotOperator() {
            TestExpressionBuilder result = builder.not();

            assertThat(result.getRoot()).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result.getRoot();
            assertThat(opNode.operator()).isEqualTo(Operator.NOT);
        }
    }
}