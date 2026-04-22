package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y SliceImpl 正确 represents a slice of results
/// <p>
/// 测试场景s:
/// 1. Create with data, total, offset, limit
/// 2. Access all properties
/// 3. Implements Slice interface
class SliceImplTest {

    @Nested
    class Creation {

        @Test
        void sliceImpl_CreatesWithProperties() {
            // given
            List<String> data = List.of("a", "b", "c");
            long total = 100L;
            int offset = 0;
            int limit = 10;

            // when
            SliceImpl<String> slice = new SliceImpl<>(data, total, offset, limit);

            // then
            assertThat(slice.data()).containsExactly("a", "b", "c");
            assertThat(slice.total()).isEqualTo(100L);
            assertThat(slice.offset()).isEqualTo(0);
            assertThat(slice.limit()).isEqualTo(10);
        }

        @Test
        void sliceImpl_CreatesWithEmptyData() {
            // given
            List<String> data = Collections.emptyList();

            // when
            SliceImpl<String> slice = new SliceImpl<>(data, 0L, 0, 10);

            // then
            assertThat(slice.data()).isEmpty();
            assertThat(slice.total()).isZero();
        }

        @Test
        void sliceImpl_ImplementsSlice() {
            // given
            SliceImpl<String> slice = new SliceImpl<>(List.of("x"), 1L, 0, 10);

            // then
            assertThat(slice).isInstanceOf(io.github.nextentity.api.model.Slice.class);
        }

        @Test
        void sliceImpl_WithOffset() {
            // given
            List<String> data = List.of("x", "y");
            int offset = 20;

            // when
            SliceImpl<String> slice = new SliceImpl<>(data, 50L, offset, 10);

            // then
            assertThat(slice.offset()).isEqualTo(20);
        }
    }
}
