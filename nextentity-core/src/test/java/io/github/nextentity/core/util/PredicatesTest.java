package io.github.nextentity.core.util;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y Predicates utility class provides predicate 操作s
/// <p>
/// 测试场景s:
/// 1. of() creates Predicate from TypedExpression
/// 2. and() combines predicates with AND operator
/// 3. or() combines predicates with OR operator
/// 4. not() negates a predicate
/// <p>
/// 预期结果: Predicates can be combined and negated 正确
class PredicatesTest {

    @Nested
    class OfMethod {

        ///
        /// 测试目标: 验证y of() creates Predicate wrapping the expression
        /// 测试场景: Create predicate from true expression
        /// 预期结果: Predicate with TRUE literal node
        @Test
        void of_WithTrueExpression_ShouldCreatePredicateWithTrueNode() {
            // given
            var trueExpr = Predicate.ofTrue();

            // when
            Predicate<Object> predicate = Predicates.of(trueExpr);

            // then
            assertThat(predicate).isNotNull();
            assertThat(ExpressionNodes.getNode(predicate)).isSameAs(LiteralNode.TRUE);
        }

        ///
        /// 测试目标: 验证y of() creates Predicate from false expression
        /// 测试场景: Create predicate from false expression
        /// 预期结果: Predicate with FALSE literal node
        @Test
        void of_WithFalseExpression_ShouldCreatePredicateWithFalseNode() {
            // given
            var falseExpr = Predicate.ofFalse();

            // when
            Predicate<Object> predicate = Predicates.of(falseExpr);

            // then
            assertThat(predicate).isNotNull();
            assertThat(ExpressionNodes.getNode(predicate)).isSameAs(LiteralNode.FALSE);
        }
    }

    @Nested
    class AndMethod {

        ///
        /// 测试目标: 验证y and() combines predicates with AND operator
        /// 测试场景: Combine false and true predicates
        /// 预期结果: FALSE (boolean algebra optimization: FALSE AND anything = FALSE)
        @Test
        void and_WithFalseAndTrue_ShouldReturnFalse() {
            // given
            var falsePred = Predicate.ofFalse();
            var truePred = Predicate.ofTrue();

            // when
            Predicate<Object> result = Predicates.and(falsePred, truePred);

            // then
            assertThat(result).isNotNull();
            // AND with FALSE results in FALSE (boolean algebra optimization)
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.FALSE);
        }

        ///
        /// 测试目标: 验证y and() with two false predicates
        /// 测试场景: Combine two false predicates
        /// 预期结果: FALSE (optimization)
        @Test
        void and_WithTwoFalsePredicates_ShouldReturnFalse() {
            // given
            var falsePred1 = Predicate.ofFalse();
            var falsePred2 = Predicate.ofFalse();

            // when
            Predicate<Object> result = Predicates.and(falsePred1, falsePred2);

            // then
            assertThat(result).isNotNull();
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.FALSE);
        }
    }

    @Nested
    class OrMethod {

        ///
        /// 测试目标: 验证y or() combines predicates with OR operator
        /// 测试场景: Combine true and false predicates
        /// 预期结果: TRUE (boolean algebra optimization: TRUE OR anything = TRUE)
        @Test
        void or_WithTrueAndFalse_ShouldReturnTrue() {
            // given
            var truePred = Predicate.ofTrue();
            var falsePred = Predicate.ofFalse();

            // when
            Predicate<Object> result = Predicates.or(truePred, falsePred);

            // then
            assertThat(result).isNotNull();
            // OR with TRUE results in TRUE (boolean algebra optimization)
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.TRUE);
        }

        ///
        /// 测试目标: 验证y or() with two false predicates creates an operator node
        /// 测试场景: Combine two false predicates
        /// 预期结果: OperatorNode is created (no optimization for false OR false)
        @Test
        void or_WithTwoFalsePredicates_ShouldCreateOperatorNode() {
            // given
            var falsePred1 = Predicate.ofFalse();
            var falsePred2 = Predicate.ofFalse();

            // when
            Predicate<Object> result = Predicates.or(falsePred1, falsePred2);

            // then
            assertThat(result).isNotNull();
            var node = ExpressionNodes.getNode(result);
            // OR on FALSE creates an operator node with the operands
            assertThat(node).isInstanceOf(OperatorNode.class);
        }
    }

}
