package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SimpleProjectionJoinAttribute.
 */
@ExtendWith(MockitoExtension.class)
class SimpleProjectionJoinAttributeTest {

    @Mock
    private JoinAttribute sourceJoinAttribute;

    @Mock
    private Attributes mockAttributes;

    private SimpleProjectionJoinAttribute attribute;

    @Nested
    class SourceMethod {

        /**
         * Test objective: Verify source() returns the source join attribute.
         * Test scenario: Create SimpleProjectionJoinAttribute with source and call source().
         * Expected result: Returns the source attribute passed to constructor.
         */
        @Test
        void source_ShouldReturnConstructorValue() {
            // given
            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);

            // when
            JoinAttribute result = attribute.source();

            // then
            assertThat(result).isSameAs(sourceJoinAttribute);
        }
    }

    @Nested
    class BuildAttributesMethod {

        /**
         * Test objective: Verify buildAttributes() is called via attributes().
         * Test scenario: Call attributes() which triggers buildAttributes().
         * Expected result: Returns attributes from the attributeBuilder function.
         */
        @Test
        void attributes_ShouldCallBuildAttributes() {
            // given
            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);

            // when
            Attributes result = attribute.attributes();

            // then
            assertThat(result).isSameAs(mockAttributes);
        }

        /**
         * Test objective: Verify attributeBuilder receives the correct attribute.
         * Test scenario: Create attribute with builder that captures the argument.
         * Expected result: Builder receives the SimpleProjectionJoinAttribute instance.
         */
        @Test
        void buildAttributes_ShouldReceiveThisAttribute() {
            // given
            SimpleProjectionJoinAttribute[] capturedAttr = new SimpleProjectionJoinAttribute[1];
            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> {
                capturedAttr[0] = attr;
                return mockAttributes;
            });

            // when
            attribute.attributes();

            // then
            assertThat(capturedAttr[0]).isSameAs(attribute);
        }
    }

    @Nested
    class Inheritance {

        /**
         * Test objective: Verify SimpleProjectionJoinAttribute implements ProjectionJoinAttribute.
         * Test scenario: Check if instance is ProjectionJoinAttribute.
         * Expected result: Is instance of ProjectionJoinAttribute.
         */
        @Test
        void shouldImplementProjectionJoinAttribute() {
            // given
            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);

            // when & then
            assertThat(attribute).isInstanceOf(ProjectionJoinAttribute.class);
        }
    }
}
