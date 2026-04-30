package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.expression.Selected;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// InterceptorSelector 单元测试
///
/// 覆盖场景：
/// 1. 构造时按 order 排序
/// 2. select 返回第一个 supports=true 的拦截器
/// 3. 全部不匹配返回 null
/// 4. empty() 创建空选择器
/// 5. isEmpty() 空和非空
/// 6. all() 返回已排序的拦截器列表
/// 7. 传入无序列表时自动排序
@DisplayName("InterceptorSelector")
class InterceptorSelectorTest {

    /// 简单的 Interceptor stub，用于测试选择逻辑
    private static class StubInterceptor implements Interceptor<QueryContext> {

        private final int order;
        private final String name;
        private final boolean supports;

        StubInterceptor(int order, String name, boolean supports) {
            this.order = order;
            this.name = name;
            this.supports = supports;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public boolean supports(QueryContext context, Selected select) {
            return supports;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @Nested
    @DisplayName("构造与排序")
    class ConstructorTests {

        @Test
        @DisplayName("构造时按 order 升序排序")
        void shouldSortByOrderOnConstruction() {
            StubInterceptor low = new StubInterceptor(1, "low", true);
            StubInterceptor mid = new StubInterceptor(5, "mid", true);
            StubInterceptor high = new StubInterceptor(10, "high", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(high, low, mid));

            assertThat(selector.all()).containsExactly(low, mid, high);
        }

        @Test
        @DisplayName("传入无序列表时自动排序")
        void shouldAutoSortUnorderedList() {
            StubInterceptor i3 = new StubInterceptor(30, "third", true);
            StubInterceptor i1 = new StubInterceptor(10, "first", true);
            StubInterceptor i2 = new StubInterceptor(20, "second", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(i3, i1, i2));

            assertThat(selector.all()).containsExactly(i1, i2, i3);
        }

        @Test
        @DisplayName("order 相同时保持相对顺序（稳定排序）")
        void shouldKeepRelativeOrderForSameOrder() {
            StubInterceptor a = new StubInterceptor(0, "a", true);
            StubInterceptor b = new StubInterceptor(0, "b", true);
            StubInterceptor c = new StubInterceptor(0, "c", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(a, b, c));

            assertThat(selector.all()).containsExactly(a, b, c);
        }
    }

    @Nested
    @DisplayName("select()")
    class SelectTests {

        @Test
        @DisplayName("返回第一个 supports=true 的拦截器")
        void shouldReturnFirstSupportedInterceptor() {
            StubInterceptor first = new StubInterceptor(1, "first", false);
            StubInterceptor second = new StubInterceptor(2, "second", true);
            StubInterceptor third = new StubInterceptor(3, "third", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(first, second, third));

            StubInterceptor result = selector.select(null, null);

            assertThat(result).isSameAs(second);
        }

        @Test
        @DisplayName("全部不匹配时返回 null")
        void shouldReturnNullWhenNoneMatch() {
            StubInterceptor a = new StubInterceptor(1, "a", false);
            StubInterceptor b = new StubInterceptor(2, "b", false);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(a, b));

            assertThat(selector.select(null, null)).isNull();
        }

        @Test
        @DisplayName("第一个拦截器匹配时直接返回")
        void shouldReturnImmediatelyWhenFirstMatches() {
            StubInterceptor first = new StubInterceptor(1, "first", true);
            StubInterceptor second = new StubInterceptor(2, "second", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(first, second));

            assertThat(selector.select(null, null)).isSameAs(first);
        }

        @Test
        @DisplayName("空选择器 select 返回 null")
        void shouldReturnNullForEmptySelector() {
            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of());

            assertThat(selector.select(null, null)).isNull();
        }
    }

    @Nested
    @DisplayName("empty()")
    class EmptyTests {

        @Test
        @DisplayName("empty() 创建空选择器")
        void shouldCreateEmptySelector() {
            InterceptorSelector<ConstructInterceptor> selector = InterceptorSelector.empty();

            assertThat(selector.isEmpty()).isTrue();
            assertThat(selector.all()).isEmpty();
        }

        @Test
        @DisplayName("empty() 创建的选择器 select 返回 null")
        void shouldReturnNullOnSelectFromEmpty() {
            InterceptorSelector<ConstructInterceptor> selector = InterceptorSelector.empty();

            assertThat(selector.select(null, null)).isNull();
        }
    }

    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTests {

        @Test
        @DisplayName("空选择器 isEmpty 返回 true")
        void shouldReturnTrueForEmpty() {
            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of());

            assertThat(selector.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("非空选择器 isEmpty 返回 false")
        void shouldReturnFalseForNonEmpty() {
            StubInterceptor interceptor = new StubInterceptor(0, "test", true);
            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(interceptor));

            assertThat(selector.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("all()")
    class AllTests {

        @Test
        @DisplayName("返回已排序的拦截器列表")
        void shouldReturnSortedList() {
            StubInterceptor i2 = new StubInterceptor(20, "second", true);
            StubInterceptor i1 = new StubInterceptor(10, "first", true);
            StubInterceptor i3 = new StubInterceptor(30, "third", true);

            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of(i2, i1, i3));

            assertThat(selector.all()).containsExactly(i1, i2, i3);
        }

        @Test
        @DisplayName("空选择器返回空列表")
        void shouldReturnEmptyListForEmptySelector() {
            InterceptorSelector<StubInterceptor> selector =
                    new InterceptorSelector<>(List.of());

            assertThat(selector.all()).isEmpty();
        }
    }
}
