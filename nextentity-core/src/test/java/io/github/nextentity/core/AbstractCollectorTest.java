package io.github.nextentity.core;

import io.github.nextentity.api.Collector;
import io.github.nextentity.api.SubQueryBuilder;
import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Sliceable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AbstractCollector interface default methods.
 */
class AbstractCollectorTest {

    private TestCollectorImpl<String> collector;

    @BeforeEach
    void setUp() {
        collector = new TestCollectorImpl<>();
    }

    /**
     * Test objective: Verify that slice returns correct slice when count > offset.
     * Test scenario: Call slice with offset and limit when there are more items.
     * Expected result: Returns a Slice containing the items.
     */
    @Test
    void slice_WhenCountGreaterThanOffset_ShouldReturnSliceWithContent() {
        // given
        List<String> items = Arrays.asList("a", "b", "c");
        collector.setItems(items);
        collector.setCount(10L);

        // when
        Slice<String> slice = collector.slice(0, 3);

        // then
        assertThat(slice.data()).hasSize(3);
        assertThat(slice.total()).isEqualTo(10L);
        assertThat(slice.offset()).isEqualTo(0);
        assertThat(slice.limit()).isEqualTo(3);
    }

    /**
     * Test objective: Verify that slice returns empty slice when count <= offset.
     * Test scenario: Call slice with offset that exceeds total count.
     * Expected result: Returns an empty Slice.
     */
    @Test
    void slice_WhenCountLessThanOrEqualToOffset_ShouldReturnEmptySlice() {
        // given
        collector.setCount(5L);

        // when
        Slice<String> slice = collector.slice(10, 3);

        // then
        assertThat(slice.data()).isEmpty();
        assertThat(slice.total()).isEqualTo(5L);
        assertThat(slice.offset()).isEqualTo(10);
        assertThat(slice.limit()).isEqualTo(3);
    }

    /**
     * Test objective: Verify that slice(PageCollector) returns page with content when count > offset.
     * Test scenario: Call slice with pageable when there are items.
     * Expected result: Returns a Page containing the items.
     */
    @Test
    void sliceCollector_WhenCountGreaterThanOffset_ShouldReturnPageWithContent() {
        // given
        Pageable<String> pageable = Pages.pageable(1, 5);
        List<String> items = Arrays.asList("a", "b", "c");
        collector.setItems(items);
        collector.setCount(10L);

        // when
        Page<String> page = collector.slice(pageable);

        // then
        assertThat(page.getItems()).hasSize(3);
        assertThat(page.getTotal()).isEqualTo(10L);
    }

    /**
     * Test objective: Verify that slice(PageCollector) returns empty page when count <= offset.
     * Test scenario: Call slice with pageable where offset exceeds count.
     * Expected result: Returns an empty Page.
     */
    @Test
    void sliceCollector_WhenCountLessThanOrEqualToOffset_ShouldReturnEmptyPage() {
        // given
        Pageable<String> pageable = Pages.pageable(3, 5); // offset = (3-1) * 5 = 10
        collector.setCount(5L);

        // when
        Page<String> page = collector.slice(pageable);

        // then
        assertThat(page.getItems()).isEmpty();
        assertThat(page.getTotal()).isEqualTo(5L);
    }

    /**
     * Test collector implementation for testing AbstractCollector default methods.
     */
    static class TestCollectorImpl<T> implements Collector<T> {
        private List<T> items = Collections.emptyList();
        private long count = 0;
        private boolean existResult = false;

        public void setItems(List<T> items) {
            this.items = items;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public void setExistResult(boolean existResult) {
            this.existResult = existResult;
        }

        @Override
        public long count() {
            return count;
        }

        @Override
        public boolean exists() {
            return existResult;
        }

        @Override
        public List<T> list() {
            return items;
        }

        @Override
        public List<T> window(int offset, int limit) {
            int effectiveOffset = Math.max(offset, 0);
            int effectiveLimit = Math.max(limit, 0);
            if (effectiveOffset >= items.size() || effectiveLimit == 0) {
                return Collections.emptyList();
            }
            int end = Math.min(effectiveOffset + effectiveLimit, items.size());
            return items.subList(effectiveOffset, end);
        }

        @Override
        public <X> SubQueryBuilder<X, T> toSubQuery() {
            throw new UnsupportedOperationException();
        }
    }
}
