package io.github.nextentity.core;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Pages utility class creates pages correctly
 * <p>
 * Test scenarios:
 * 1. Create page with items and total
 * 2. Create pageable
 * 3. Page equality
 */
class PagesTest {

    @Nested
    class PageCreation {

        /**
         * Test objective: Verify page() creates Page
         * Test scenario: Create page with items and total
         * Expected result: Page with correct items and total
         */
        @Test
        void page_WithItemsAndTotal_ReturnsPage() {
            // given
            List<String> items = Arrays.asList("a", "b", "c");
            long total = 100L;

            // when
            Page<String> page = Pages.page(items, total);

            // then
            assertThat(page.getItems()).isEqualTo(items);
            assertThat(page.getTotal()).isEqualTo(total);
        }

        /**
         * Test objective: Verify page with empty list
         * Test scenario: Create page with empty items
         * Expected result: Page with empty items
         */
        @Test
        void page_EmptyItems_ReturnsPage() {
            // given
            List<String> items = List.of();
            long total = 0L;

            // when
            Page<String> page = Pages.page(items, total);

            // then
            assertThat(page.getItems()).isEmpty();
            assertThat(page.getTotal()).isEqualTo(0L);
        }
    }

    @Nested
    class PageableCreation {

        /**
         * Test objective: Verify pageable() creates Pageable
         * Test scenario: Create pageable with page and size
         * Expected result: Pageable with correct values
         */
        @Test
        void pageable_WithPageAndSize_ReturnsPageable() {
            // given
            int page = 2;
            int size = 20;

            // when
            Pageable pageable = Pages.pageable(page, size);

            // then
            assertThat(pageable.page()).isEqualTo(page);
            assertThat(pageable.size()).isEqualTo(size);
        }

        /**
         * Test objective: Verify pageable with zero page
         * Test scenario: Create pageable with page 0
         * Expected result: Pageable with page 0
         */
        @Test
        void pageable_FirstPage_ReturnsPageable() {
            // given
            int page = 0;
            int size = 10;

            // when
            Pageable pageable = Pages.pageable(page, size);

            // then
            assertThat(pageable.page()).isEqualTo(0);
            assertThat(pageable.size()).isEqualTo(size);
        }
    }

    @Nested
    class PageEquality {

        /**
         * Test objective: Verify page equality
         * Test scenario: Compare two equal pages
         * Expected result: Are equal
         */
        @Test
        void equals_SameContent_AreEqual() {
            // given
            List<String> items = Arrays.asList("a", "b");
            Page<String> page1 = Pages.page(items, 10L);
            Page<String> page2 = Pages.page(items, 10L);

            // when & then
            assertThat(page1).isEqualTo(page2);
            assertThat(page1.hashCode()).isEqualTo(page2.hashCode());
        }

        /**
         * Test objective: Verify page inequality with different total
         * Test scenario: Compare pages with different totals
         * Expected result: Are not equal
         */
        @Test
        void equals_DifferentTotal_AreNotEqual() {
            // given
            List<String> items = Arrays.asList("a", "b");
            Page<String> page1 = Pages.page(items, 10L);
            Page<String> page2 = Pages.page(items, 20L);

            // when & then
            assertThat(page1).isNotEqualTo(page2);
        }

        /**
         * Test objective: Verify page inequality with different items
         * Test scenario: Compare pages with different items
         * Expected result: Are not equal
         */
        @Test
        void equals_DifferentItems_AreNotEqual() {
            // given
            Page<String> page1 = Pages.page(Arrays.asList("a", "b"), 10L);
            Page<String> page2 = Pages.page(Arrays.asList("c", "d"), 10L);

            // when & then
            assertThat(page1).isNotEqualTo(page2);
        }
    }

    @Nested
    class PageableEquality {

        /**
         * Test objective: Verify pageable equality
         * Test scenario: Compare two equal pageables
         * Expected result: Are equal
         */
        @Test
        void equals_SameContent_AreEqual() {
            // given
            Pageable pageable1 = Pages.pageable(1, 10);
            Pageable pageable2 = Pages.pageable(1, 10);

            // when & then
            assertThat(pageable1).isEqualTo(pageable2);
        }

        /**
         * Test objective: Verify pageable inequality
         * Test scenario: Compare pageables with different values
         * Expected result: Are not equal
         */
        @Test
        void equals_DifferentContent_AreNotEqual() {
            // given
            Pageable pageable1 = Pages.pageable(1, 10);
            Pageable pageable2 = Pages.pageable(2, 10);

            // when & then
            assertThat(pageable1).isNotEqualTo(pageable2);
        }
    }
}
