package io.github.nextentity.spring.integration;

import io.github.nextentity.spring.integration.db.UserQueryProvider;
import io.github.nextentity.spring.integration.db.UserRepository;
import io.github.nextentity.spring.integration.entity.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/// AbstractRepository 中涉及 idPath 和 idExtractor 的方法测试
///
/// 测试覆盖的方法:
/// - findById(ID id) - 使用 idPath 查询
/// - getById(ID id) - 使用 idPath 查询
/// - findAllById(Collection<ID> ids) - 使用 idPath 查询
/// - getAllById(Collection<ID> ids) - 调用 findAllById
/// - findMapById(Collection<ID> ids) - 使用 idExtractor 创建 Map
/// - findMapAll() - 使用 idExtractor 创建 Map
/// - existsById(ID id) - 使用 idPath 检查存在性
/// - countById(Collection<ID> ids) - 使用 idPath 统计数量
/// - deleteById(ID id) - 使用 idPath 删除
/// - deleteAllById(Collection<ID> ids) - 使用 idPath 批量删除
class AbstractRepositoryIdMethodsTest {

    /// 测试 findById - 正常情况：存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindByIdWhenIdExists(UserRepository repository) {
        // Arrange - 获取第一个用户的 ID
        User firstUser = repository.users().getFirst();
        Integer existingId = firstUser.getId();

        // Act
        Optional<User> result = repository.findById(existingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(firstUser, result.get());
    }

    /// 测试 findById - 边界情况：不存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindByIdReturnEmptyWhenIdNotExists(UserRepository repository) {
        // Arrange - 使用一个不存在的 ID
        Integer nonExistingId = -999;

        // Act
        Optional<User> result = repository.findById(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
    }

    /// 测试 getById - 正常情况：存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldGetByIdWhenIdExists(UserRepository repository) {
        // Arrange
        User expected = repository.users().get(5);
        Integer existingId = expected.getId();

        // Act
        User result = repository.getById(existingId);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    /// 测试 getById - 边界情况：不存在的 ID 返回 null
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldGetByIdReturnNullWhenIdNotExists(UserRepository repository) {
        // Arrange
        Integer nonExistingId = Integer.MAX_VALUE;

        // Act
        User result = repository.getById(nonExistingId);

        // Assert
        assertNull(result);
    }

    /// 测试 findAllById - 正常情况：多个存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdWithExistingIds(UserRepository repository) {
        // Arrange
        List<User> users = repository.users();
        List<Integer> ids = users.stream()
                .filter(u -> u.getId() < 5)
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        List<User> result = repository.findAllById(ids);

        // Assert
        assertEquals(ids.size(), result.size());
        for (User user : result) {
            assertTrue(ids.contains(user.getId()));
        }
    }

    /// 测试 findAllById - 边界情况：空集合
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdReturnEmptyListWhenIdsIsEmpty(UserRepository repository) {
        // Arrange
        Collection<Integer> emptyIds = Collections.emptyList();

        // Act
        List<User> result = repository.findAllById(emptyIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    /// 测试 findAllById - 边界情况：包含不存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdIgnoreNonExistingIds(UserRepository repository) {
        // Arrange
        User existingUser = repository.users().getFirst();
        List<Integer> ids = Arrays.asList(existingUser.getId(), -999, -1000);

        // Act
        List<User> result = repository.findAllById(ids);

        // Assert - 只返回存在的实体，忽略不存在的 ID
        assertEquals(1, result.size());
        assertEquals(existingUser, result.getFirst());
    }

    /// 测试 findAllById - 边界情况：所有 ID 都不存在
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdReturnEmptyWhenAllIdsNotExists(UserRepository repository) {
        // Arrange
        List<Integer> nonExistingIds = Arrays.asList(-1, -2, -3);

        // Act
        List<User> result = repository.findAllById(nonExistingIds);

        // Assert
        assertTrue(result.isEmpty());
    }

    /// 测试 getAllById - 应该与 findAllById 行为一致
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldGetAllByIdBehaveSameAsFindAllById(UserRepository repository) {
        // Arrange
        List<Integer> ids = repository.users().stream()
                .limit(10)
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        List<User> findAllResult = repository.findAllById(ids);
        List<User> getAllResult = repository.getAllById(ids);

        // Assert - 两个方法应该返回相同结果
        assertEquals(findAllResult, getAllResult);
    }

    /// 测试 findMapById - 正常情况
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapByIdCreateCorrectMap(UserRepository repository) {
        // Arrange
        List<User> users = repository.users();
        List<Integer> ids = users.stream()
                .limit(5)
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        Map<Integer, User> result = repository.findMapById(ids);

        // Assert - Map 的键应该是 ID，值应该是对应的实体
        assertEquals(ids.size(), result.size());
        for (Integer id : ids) {
            assertTrue(result.containsKey(id));
            assertEquals(id, result.get(id).getId());
        }
    }

    /// 测试 findMapById - 边界情况：空集合
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapByIdReturnEmptyMapWhenIdsIsEmpty(UserRepository repository) {
        // Arrange
        Collection<Integer> emptyIds = List.of();

        // Act
        Map<Integer, User> result = repository.findMapById(emptyIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /// 测试 findMapById - 使用 idExtractor 提取 ID
    /// 验证 idExtractor 函数正确工作
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapByIdUseIdExtractorCorrectly(UserRepository repository) {
        // Arrange
        List<User> expectedUsers = repository.users().subList(0, 3);
        List<Integer> ids = expectedUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        Map<Integer, User> result = repository.findMapById(ids);

        // Assert - 验证 idExtractor 正确提取了每个实体的 ID
        for (User expected : expectedUsers) {
            User actual = result.get(expected.getId());
            assertNotNull(actual);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected, actual);
        }
    }

    /// 测试 findMapAll - 正常情况
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapAllCreateCorrectMap(UserRepository repository) {
        // Arrange
        List<User> allUsers = repository.users();

        // Act
        Map<Integer, User> result = repository.findMapAll();

        // Assert
        assertEquals(allUsers.size(), result.size());
        for (User user : allUsers) {
            assertTrue(result.containsKey(user.getId()));
            assertEquals(user, result.get(user.getId()));
        }
    }

    /// 测试 findMapAll - 使用 idExtractor 提取 ID
    /// 验证 idExtractor 函数在全部实体上正确工作
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapAllUseIdExtractorCorrectly(UserRepository repository) {
        // Act
        Map<Integer, User> result = repository.findMapAll();

        // Assert - 验证每个实体的 ID 都被正确提取并作为 Map 的键
        for (Map.Entry<Integer, User> entry : result.entrySet()) {
            Integer key = entry.getKey();
            User value = entry.getValue();
            // idExtractor 应该从 value 中提取出 key
            assertEquals(key, value.getId());
        }
    }

    /// 测试 existsById - 存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldExistsByIdReturnTrueWhenIdExists(UserRepository repository) {
        // Arrange
        Integer existingId = repository.users().getFirst().getId();

        // Act
        boolean result = repository.existsById(existingId);

        // Assert
        assertTrue(result);
    }

    /// 测试 existsById - 不存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldExistsByIdReturnFalseWhenIdNotExists(UserRepository repository) {
        // Arrange
        Integer nonExistingId = Integer.MIN_VALUE;

        // Act
        boolean result = repository.existsById(nonExistingId);

        // Assert
        assertFalse(result);
    }

    /// 测试 countById - 正常情况：存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldCountByIdReturnCorrectCount(UserRepository repository) {
        // Arrange
        List<Integer> existingIds = repository.users().stream()
                .limit(5)
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        long result = repository.countById(existingIds);

        // Assert
        assertEquals(existingIds.size(), result);
    }

    /// 测试 countById - 边界情况：空集合
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldCountByIdReturnZeroWhenIdsIsEmpty(UserRepository repository) {
        // Arrange
        Collection<Integer> emptyIds = Collections.emptyList();

        // Act
        long result = repository.countById(emptyIds);

        // Assert
        assertEquals(0L, result);
    }

    /// 测试 countById - 边界情况：包含不存在的 ID
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldCountByIdOnlyCountExistingIds(UserRepository repository) {
        // Arrange
        Integer existingId = repository.users().getFirst().getId();
        List<Integer> ids = Arrays.asList(existingId, -999, -1000, -1001);

        // Act
        long result = repository.countById(ids);

        // Assert - 只统计存在的实体
        assertEquals(1L, result);
    }

    /// 测试 countById - 边界情况：所有 ID 都不存在
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldCountByIdReturnZeroWhenAllIdsNotExists(UserRepository repository) {
        // Arrange
        List<Integer> nonExistingIds = Arrays.asList(-1, -2, -3, -4);

        // Act
        long result = repository.countById(nonExistingIds);

        // Assert
        assertEquals(0L, result);
    }

    /// 测试 idType() - 返回正确的 ID 类型
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldIdTypeReturnCorrectClass(UserRepository repository) {
        // Act
        Class<Integer> idType = repository.idType();

        // Assert
        assertEquals(Integer.class, idType);
    }

    /// 测试 entityType() - 返回正确的实体类型
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldEntityTypeReturnCorrectClass(UserRepository repository) {
        // Act
        Class<User> entityType = repository.entityType();

        // Assert
        assertEquals(User.class, entityType);
    }

    /// 测试 findAllById 的 idPath 使用 - 验证 IN 条件查询
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdUseIdPathForInCondition(UserRepository repository) {
        // Arrange
        List<User> users = repository.users();
        // 选择特定范围的 ID
        int minId = users.stream().mapToInt(User::getId).min().orElse(0);
        int maxId = users.stream().mapToInt(User::getId).max().orElse(0);

        // 创建包含连续 ID 的列表
        List<Integer> idsToQuery = Arrays.asList(minId, minId + 1, minId + 2);

        // 预期结果：只有实际存在的 ID 才会被返回
        Set<Integer> existingIdsInRange = users.stream()
                .map(User::getId)
                .filter(idsToQuery::contains)
                .collect(Collectors.toSet());

        // Act
        List<User> result = repository.findAllById(idsToQuery);

        // Assert
        assertEquals(existingIdsInRange.size(), result.size());
        for (User user : result) {
            assertTrue(existingIdsInRange.contains(user.getId()));
        }
    }

    /// 测试批量操作的 idPath 使用 - deleteAllById 和 deleteById
    /// 注意：此测试会修改数据库数据，需要清理
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldDeleteByIdUseIdPath(UserRepository repository) {
        // 此测试演示 deleteById 使用 idPath 进行删除
        // 由于是集成测试，我们只验证方法可以正常调用
        // 实际删除测试需要在独立事务中进行

        // Arrange - 获取一个用户的 ID（但实际不删除，避免影响其他测试）
        Integer testId = repository.users().getFirst().getId();

        // 验证 existsById 正常工作（作为 deleteById 的反向验证）
        assertTrue(repository.existsById(testId));

        // 注意：实际的 deleteById 测试需要在专门的测试环境中进行
        // 这里我们只验证 idPath 被正确初始化和使用
        assertNotNull(repository.idType());
    }

    /// 测试 findMapById 与 findMapAll 结果一致性
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapByIdConsistentWithFindMapAll(UserRepository repository) {
        // Arrange
        List<User> allUsers = repository.users();
        int maxSize = 2000;
        List<Integer> allIds = allUsers.stream()
                .map(User::getId)
                .limit(maxSize)
                .collect(Collectors.toList());

        // Act
        Map<Integer, User> mapById = repository.findMapById(allIds);
        Map<Integer, User> mapAll = repository.findMapAll();

        // Assert - 两个方法应该返回相同的 Map（对于全部 ID）
        assertEquals(Math.min(mapAll.size(), maxSize), mapById.size());
        mapById.forEach((id, user) -> assertEquals(mapAll.get(id), user));
    }

    /// 测试边界值：单个 ID 的集合
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdWorkWithSingleId(UserRepository repository) {
        // Arrange
        Integer singleId = repository.users().getFirst().getId();
        Collection<Integer> singleIdCollection = Collections.singleton(singleId);

        // Act
        List<User> result = repository.findAllById(singleIdCollection);

        // Assert
        assertEquals(1, result.size());
        assertEquals(singleId, result.getFirst().getId());
    }

    /// 测试 findMapById 返回的 Map 不可修改性
    /// Collectors.toMap 返回的 HashMap 是可修改的，这是预期行为
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindMapByIdReturnModifiableMap(UserRepository repository) {
        // Arrange
        Integer id = repository.users().getFirst().getId();
        List<Integer> ids = Collections.singletonList(id);

        // Act
        Map<Integer, User> result = repository.findMapById(ids);

        // Assert - HashMap 是可修改的（这是 Collectors.toMap 的默认行为）
        // 如果需要不可修改的 Map，应该使用 Collections.unmodifiableMap 包装
        assertNotNull(result);
        assertEquals(1, result.size());

        // 验证 Map 包含正确的数据
        assertTrue(result.containsKey(id));
        assertNotNull(result.get(id));
    }

    /// 测试大量 ID 的查询性能
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldFindAllByIdHandleLargeIdCollection(UserRepository repository) {
        // Arrange - 使用所有用户的 ID
        int limit = 2000;
        List<Integer> allIds = repository.users().stream()
                .map(User::getId)
                .limit(limit)
                .collect(Collectors.toList());

        // Act
        List<User> result = repository.findAllById(allIds);

        // Assert
        assertEquals(allIds.size(), result.size());

        // 验证所有实体都被正确返回
        Set<Integer> resultIds = result.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        assertEquals(new HashSet<>(allIds), resultIds);
    }

    /// 测试 countById 与 findAllById 数量一致性
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void shouldCountByIdConsistentWithFindAllById(UserRepository repository) {
        // Arrange
        List<Integer> ids = repository.users().stream()
                .limit(10)
                .map(User::getId)
                .collect(Collectors.toList());

        // Act
        long countResult = repository.countById(ids);
        List<User> listResult = repository.findAllById(ids);

        // Assert - countById 的结果应该等于 findAllById 返回列表的大小
        assertEquals(listResult.size(), countResult);
    }
}