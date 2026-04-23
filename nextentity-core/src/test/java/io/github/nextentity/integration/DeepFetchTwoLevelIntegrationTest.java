package io.github.nextentity.integration;

import io.github.nextentity.api.EntityPath;
import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Category;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeepFetchTwoLevelIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelSelfReference(IntegrationTestContext context) {
        // Find a category whose parent has a parent (two levels deep)
        List<Category> all = context.queryCategories().list();
        Category deep = null;
        for (Category c : all) {
            if (c.getParentId() != null) {
                Category parent = context.queryCategories()
                        .where(Category::getId).eq(c.getParentId())
                        .first();
                if (parent != null && parent.getParentId() != null) {
                    deep = c;
                    break;
                }
            }
        }
        assertThat(deep).isNotNull();

        // Fetch two levels: parentCategory.parentCategory
        Category result = context.queryCategories()
                .fetch(Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .where(Category::getId).eq(deep.getId())
                .single();

        assertThat(result).isNotNull();
        assertThat(result.getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getParentCategory().getId())
                .isEqualTo(deep.getParentId() == null ? null :
                        context.queryCategories()
                        .where(Category::getId).eq(deep.getParentId())
                        .first().getParentId());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchOneLevelAndTwoLevelTogether(IntegrationTestContext context) {
        // Fetch both single-level and two-level paths in one call
        Category result = context.queryCategories()
                .fetch(Category::getParentCategory,
                        Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .where(Category::getId).eq(6L)
                .single();

        assertThat(result).isNotNull();
        assertThat(result.getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getName()).isEqualTo("Computers");
        assertThat(result.getParentCategory().getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getParentCategory().getName()).isEqualTo("Electronics");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelWithWhereCondition(IntegrationTestContext context) {
        // Fetch two levels with a where condition
        List<Category> results = context.queryCategories()
                .fetch(Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .where(Category::getName).like("Lap%")
                .list();

        assertThat(results).isNotEmpty();
        for (Category c : results) {
            assertThat(c.getParentCategory()).isNotNull();
            assertThat(c.getParentCategory().getParentCategory()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelWithNestedPathWhere(IntegrationTestContext context) {
        // Use nested path in where clause combined with two-level fetch
        List<Category> results = context.queryCategories()
                .fetch(Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .where(Category::getParentCategory).get(Category::getName).eq("Computers")
                .list();

        assertThat(results).isNotEmpty();
        for (Category c : results) {
            assertThat(c.getParentCategory()).isNotNull();
            assertThat(c.getParentCategory().getName()).isEqualTo("Computers");
            assertThat(c.getParentCategory().getParentCategory()).isNotNull();
            assertThat(c.getParentCategory().getParentCategory().getName()).isEqualTo("Electronics");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelWithOrderBy(IntegrationTestContext context) {
        List<Category> results = context.queryCategories()
                .fetch(Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .orderBy(Category::getId).asc()
                .list();

        assertThat(results).isNotEmpty();
        // Verify deep categories have both levels loaded
        Category laptops = results.stream()
                .filter(c -> "Laptops".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(laptops).isNotNull();
        assertThat(laptops.getParentCategory()).isNotNull();
        assertThat(laptops.getParentCategory().getParentCategory()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelWithEntityPath(IntegrationTestContext context) {
        // Use EntityPath.of() instead of Path.of() for the two-level fetch
        EntityPath<Category, Category> parentPath = EntityPath.of(Category::getParentCategory);

        Category result = context.queryCategories()
                .fetch(parentPath.get(Category::getParentCategory))
                .where(Category::getId).eq(7L)
                .single();

        assertThat(result).isNotNull();
        assertThat(result.getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getName()).isEqualTo("Computers");
        assertThat(result.getParentCategory().getParentCategory()).isNotNull();
        assertThat(result.getParentCategory().getParentCategory().getName()).isEqualTo("Electronics");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void fetchTwoLevelRootCategory(IntegrationTestContext context) {
        // Root category has no parent — fetch should return null for parentCategory
        Category result = context.queryCategories()
                .fetch(Path.of(Category::getParentCategory).get(Category::getParentCategory))
                .where(Category::getId).eq(1L)
                .single();

        assertThat(result).isNotNull();
        assertThat(result.getParentCategory()).isNull();
    }
}
