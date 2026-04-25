package io.github.nextentity.core.reflect;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.EmployeeWithLazyDepartment;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.fast.FastIntegrationTestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/// LoadObserver 扩展点的集成测试。
///
/// 验证延时加载行为可被观测：
/// - 首次访问触发批量加载
/// - 后续访问缓存命中
/// - 外键集合包含所有待加载值
///
/// @author HuangChengwei
@DisplayName("LoadObserver Integration Tests")
public class LoadObserverTest {

    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class) // TODO use IntegrationTestProvider.class
    @DisplayName("Should observe batch load on first lazy access")
    void shouldObserveBatchLoadOnFirstLazyAccess(IntegrationTestContext context) {
        // Given - 创建观察器，记录事件
        List<LoadObserver.BatchLoadEvent> loadEvents = new ArrayList<>();
        List<LoadObserver.CacheHitEvent> hitEvents = new ArrayList<>();
        List<Long> beforeAccessDeptIds = new ArrayList<>();

        LoadObserver observer = new LoadObserver() {
            @Override
            public void onBeforeLoad(BatchLoadEvent event) {
                loadEvents.add(event);
            }

            @Override
            public void onAfterLoad(BatchLoadEvent event) {
                // 更新最后一个事件的 endTime
                int lastIndex = loadEvents.size() - 1;
                loadEvents.set(lastIndex, event);
            }

            @Override
            public void onCacheHit(CacheHitEvent event) {
                hitEvents.add(event);
            }
        };

        // When - 在观察器范围内执行查询和访问
        LoadObserverRegistry.withObserver(observer, () -> {
            // ===== 触发前：验证初始状态 =====
            List<EmployeeWithLazyDepartment> results = context.queryEmployees()
                    .select(EmployeeWithLazyDepartment.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .orderBy(Employee::getId).asc()
                    .list();

            // 验证：查询返回结果
            assertThat(results).isNotEmpty();

            // 验证：EAGER 属性已加载（无需触发事件）
            for (EmployeeWithLazyDepartment emp : results) {
                assertThat(emp.getId()).isNotNull();
                assertThat(emp.getName()).isNotNull();
                assertThat(emp.getDepartmentId()).isNotNull();
                beforeAccessDeptIds.add(emp.getDepartmentId());
            }

            // 验证：触发前无任何加载/缓存命中事件
            assertThat(loadEvents).isEmpty();
            assertThat(hitEvents).isEmpty();

            // ===== 触发：首次访问 LAZY 属性 =====
            EmployeeWithLazyDepartment first = results.getFirst();
            Department dept = first.getDepartment();

            // ===== 触发后：验证批量加载事件 =====
            assertThat(loadEvents).hasSize(1);
            assertThat(hitEvents).isEmpty();

            LoadObserver.BatchLoadEvent loadEvent = loadEvents.get(0);
            assertThat(loadEvent.entityType()).isEqualTo(Department.class);
            assertThat(loadEvent.startTimeNanos()).isPositive();
            assertThat(loadEvent.endTimeNanos()).isPositive();
            assertThat(loadEvent.endTimeNanos()).isGreaterThanOrEqualTo(loadEvent.startTimeNanos());

            // 验证：外键集合包含所有投影对象的 departmentId（唯一值）
            Set<?> foreignKeys = loadEvent.foreignKeys();
            List<Long> distinctDeptIds = beforeAccessDeptIds.stream().distinct().toList();
            assertThat(foreignKeys.size()).isEqualTo(distinctDeptIds.size());

            // ===== 后续访问：验证缓存命中 =====
            EmployeeWithLazyDepartment second = results.get(1);
            Department dept2 = second.getDepartment();

            assertThat(loadEvents).hasSize(1); // 无新加载事件
            assertThat(hitEvents).hasSize(1);  // 1 次缓存命中

            LoadObserver.CacheHitEvent hitEvent = hitEvents.get(0);
            assertThat(hitEvent.entityType()).isEqualTo(Department.class);
            assertThat(hitEvent.foreignKey()).isEqualTo(second.getDepartmentId());
            assertThat(hitEvent.cachedValue()).isNotNull();
        });

        // 验证：范围外观察器已解绑
        assertThat(LoadObserverRegistry.isBound()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should observe multiple cache hits after batch load")
    void shouldObserveMultipleCacheHitsAfterBatchLoad(IntegrationTestContext context) {
        // Given
        List<LoadObserver.BatchLoadEvent> loadEvents = new ArrayList<>();
        List<LoadObserver.CacheHitEvent> hitEvents = new ArrayList<>();

        LoadObserver observer = new LoadObserver() {
            @Override
            public void onBeforeLoad(BatchLoadEvent event) {
                loadEvents.add(event);
            }

            @Override
            public void onAfterLoad(BatchLoadEvent event) {
                int lastIndex = loadEvents.size() - 1;
                loadEvents.set(lastIndex, event);
            }

            @Override
            public void onCacheHit(CacheHitEvent event) {
                hitEvents.add(event);
            }
        };

        // When
        LoadObserverRegistry.withObserver(observer, () -> {
            // ===== 触发前：验证初始状态 =====
            List<EmployeeWithLazyDepartment> results = context.queryEmployees()
                    .select(EmployeeWithLazyDepartment.class)
                    .where(Employee::getDepartmentId).isNotNull()
                    .orderBy(Employee::getId).asc()
                    .list();

            int totalCount = results.size();

            // 验证：EAGER 属性已加载
            assertThat(results).isNotEmpty();
            assertThat(results).allMatch(emp -> emp.getId() != null);
            assertThat(results).allMatch(emp -> emp.getName() != null);
            assertThat(results).allMatch(emp -> emp.getDepartmentId() != null);

            // 验证：触发前无任何事件
            assertThat(loadEvents).isEmpty();
            assertThat(hitEvents).isEmpty();

            // ===== 触发：首次访问 LAZY 属性 =====
            results.getFirst().getDepartment();

            // 验证：触发后只有 1 次批量加载
            assertThat(loadEvents).hasSize(1);
            assertThat(hitEvents).isEmpty();

            // ===== 后续访问：验证缓存命中 =====
            for (int i = 1; i < results.size(); i++) {
                results.get(i).getDepartment();
            }

            // 验证：无新加载事件，所有后续访问都是缓存命中
            assertThat(loadEvents).hasSize(1);
            assertThat(hitEvents).hasSize(totalCount - 1);
        });
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should not trigger events without observer")
    void shouldNotTriggerEventsWithoutObserver(IntegrationTestContext context) {
        // ===== 触发前：验证观察器未绑定 =====
        assertThat(LoadObserverRegistry.isBound()).isFalse();

        // ===== 触发：执行查询和访问（无观察器） =====
        List<EmployeeWithLazyDepartment> results = context.queryEmployees()
                .select(EmployeeWithLazyDepartment.class)
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getId).asc()
                .list(5);

        // 验证：EAGER 属性已加载
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(emp -> emp.getId() != null);
        assertThat(results).allMatch(emp -> emp.getName() != null);

        // ===== 触发后：访问 LAZY 属性无事件 =====
        for (EmployeeWithLazyDepartment emp : results) {
            Department dept = emp.getDepartment();
            // 正常工作，只是没有事件记录
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
            }
        }

        // ===== 验证：功能正常，无异常 =====
        assertThat(LoadObserverRegistry.isBound()).isFalse();
    }
}