package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.expression.SelectEntity;
import io.github.nextentity.core.expression.SelectExpression;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.ProjectionSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JdkProxyInterceptor")
class JdkProxyInterceptorTest {

    private interface SampleInterface {}
    private static class SampleClass {}

    private final JdkProxyInterceptor interceptor = JdkProxyInterceptor.of();

    // ==================== Singleton / Factory ====================

    @Nested
    @DisplayName("Factory and constructor")
    class FactoryAndConstructor {

        @Test
        @DisplayName("of() returns the same singleton instance")
        void ofReturnsSingleton() {
            JdkProxyInterceptor a = JdkProxyInterceptor.of();
            JdkProxyInterceptor b = JdkProxyInterceptor.of();

            assertThat(a).isSameAs(b);
        }

        @Test
        @DisplayName("default constructor creates instance with order 0")
        void defaultConstructorOrderIsZero() {
            JdkProxyInterceptor instance = new JdkProxyInterceptor();

            assertThat(instance.order()).isEqualTo(0);
        }

        @Test
        @DisplayName("constructor with order creates instance with specified order")
        void customOrderConstructor() {
            JdkProxyInterceptor instance = new JdkProxyInterceptor(10);

            assertThat(instance.order()).isEqualTo(10);
        }
    }

    // ==================== name() ====================

    @Nested
    @DisplayName("name()")
    class NameTest {

        @Test
        @DisplayName("returns 'jdk-proxy'")
        void returnsJdkProxy() {
            assertThat(interceptor.name()).isEqualTo("jdk-proxy");
        }
    }

    // ==================== order() ====================

    @Nested
    @DisplayName("order()")
    class OrderTest {

        @Test
        @DisplayName("default order is 0")
        void defaultOrderIsZero() {
            assertThat(JdkProxyInterceptor.of().order()).isEqualTo(0);
        }

        @Test
        @DisplayName("custom order is returned correctly")
        void customOrderReturned() {
            assertThat(new JdkProxyInterceptor(42).order()).isEqualTo(42);
        }
    }

    // ==================== supports() ====================

    @Nested
    @DisplayName("supports()")
    class SupportsTest {

        @Test
        @DisplayName("returns true for SelectProjection with interface type and lazy attributes")
        void returnsTrueForInterfaceWithLazyAttributes() {
            QueryContext context = mock(QueryContext.class);
            EntityType entityType = mock(EntityType.class);
            ProjectionSchema projection = mock(ProjectionSchema.class);

            when(context.getEntityType()).thenReturn(entityType);
            when(entityType.getProjection(SampleInterface.class)).thenReturn(projection);
            when(projection.hasLazyAttribute()).thenReturn(true);

            Selected select = new SelectProjection(SampleInterface.class, false);

            assertThat(interceptor.supports(context, select)).isTrue();
        }

        @Test
        @DisplayName("returns false for SelectProjection with non-interface type")
        void returnsFalseForNonInterfaceType() {
            QueryContext context = mock(QueryContext.class);
            Selected select = new SelectProjection(SampleClass.class, false);

            assertThat(interceptor.supports(context, select)).isFalse();
        }

        @Test
        @DisplayName("returns false for SelectProjection with interface but no lazy attributes")
        void returnsFalseForInterfaceWithoutLazyAttributes() {
            QueryContext context = mock(QueryContext.class);
            EntityType entityType = mock(EntityType.class);
            ProjectionSchema projection = mock(ProjectionSchema.class);

            when(context.getEntityType()).thenReturn(entityType);
            when(entityType.getProjection(SampleInterface.class)).thenReturn(projection);
            when(projection.hasLazyAttribute()).thenReturn(false);

            Selected select = new SelectProjection(SampleInterface.class, false);

            assertThat(interceptor.supports(context, select)).isFalse();
        }

        @Test
        @DisplayName("returns false for SelectEntity (not SelectProjection)")
        void returnsFalseForSelectEntity() {
            QueryContext context = mock(QueryContext.class);
            Selected select = new SelectEntity(io.github.nextentity.core.util.ImmutableList.of(), false);

            assertThat(interceptor.supports(context, select)).isFalse();
        }

        @Test
        @DisplayName("returns false for SelectExpression (not SelectProjection)")
        void returnsFalseForSelectExpression() {
            QueryContext context = mock(QueryContext.class);
            Selected select = mock(SelectExpression.class);

            assertThat(interceptor.supports(context, select)).isFalse();
        }
    }

    // ==================== intercept() ====================

    @Nested
    @DisplayName("intercept()")
    class InterceptTest {

        @Test
        @DisplayName("returns null for non-SelectProjection")
        void returnsNullForNonSelectProjection() {
            QueryContext context = mock(QueryContext.class);
            Selected select = new SelectEntity(io.github.nextentity.core.util.ImmutableList.of(), false);

            assertThat(interceptor.intercept(context, select)).isNull();
        }

        @Test
        @DisplayName("returns null for non-interface type")
        void returnsNullForNonInterfaceType() {
            QueryContext context = mock(QueryContext.class);
            Selected select = new SelectProjection(SampleClass.class, false);

            assertThat(interceptor.intercept(context, select)).isNull();
        }

        @Test
        @DisplayName("returns null for interface without lazy attributes")
        void returnsNullForInterfaceWithoutLazyAttributes() {
            QueryContext context = mock(QueryContext.class);
            EntityType entityType = mock(EntityType.class);
            ProjectionSchema projection = mock(ProjectionSchema.class);

            when(context.getEntityType()).thenReturn(entityType);
            when(entityType.getProjection(SampleInterface.class)).thenReturn(projection);
            when(projection.hasLazyAttribute()).thenReturn(false);

            Selected select = new SelectProjection(SampleInterface.class, false);

            assertThat(interceptor.intercept(context, select)).isNull();
        }
    }
}
