package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y SimpleExpressionImpl 正确 implements expression 操作s
 /// <p>
 /// 测试场景s:
 /// 1. Root 方法
 /// 2. Count 操作s
 /// 3. Min/Max 操作s
 /// 4. getRoot returns correct node
class SimpleExpressionImplTest {

    @Nested
    class GetRoot {

///
         /// 测试目标: 验证y getRoot returns the expression node
         /// 测试场景: Create SimpleExpressionImpl and get root
         /// 预期结果: Returns the node passed in constructor
        @Test
        void getRoot_ReturnsNode() {
            // given
            LiteralNode node = new LiteralNode("test");
            SimpleExpressionImpl<Object, String> expression = new SimpleExpressionImpl<>(node);

            // when
            ExpressionNode root = expression.getRoot();

            // then
            assertThat(root).isSameAs(node);
        }
    }

    @Nested
    class CountOperations {

///
         /// 测试目标: 验证y count() creates COUNT operator
         /// 测试场景: Call count() on expression
         /// 预期结果: Expression with COUNT operator
        @Test
        void count_CreatesCountOperator() {
            // given
            PathNode pathNode = new PathNode("field");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.count();

            // then - cast to ExpressionTree to get root
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.COUNT);
        }
    }

    @Nested
    class MinMaxOperations {

///
         /// 测试目标: 验证y max() creates MAX operator
         /// 测试场景: Call max() on expression
         /// 预期结果: Expression with MAX operator
        @Test
        void max_CreatesMaxOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.max();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MAX);
        }

///
         /// 测试目标: 验证y min() creates MIN operator
         /// 测试场景: Call min() on expression
         /// 预期结果: Expression with MIN operator
        @Test
        void min_CreatesMinOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.min();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MIN);
        }
    }
}
