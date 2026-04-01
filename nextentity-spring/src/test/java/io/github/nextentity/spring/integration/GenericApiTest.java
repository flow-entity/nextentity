package io.github.nextentity.spring.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.nextentity.api.*;
import io.github.nextentity.api.ExpressionBuilder.Conjunction;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.core.Tuples;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.spring.integration.db.UserQueryProvider;
import io.github.nextentity.spring.integration.db.UserRepository;
import io.github.nextentity.spring.integration.entity.User;
import io.github.nextentity.spring.integration.projection.UserInterface;
import io.github.nextentity.spring.integration.projection.UserModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.nextentity.core.util.Predicates.*;
import static org.junit.jupiter.api.Assertions.*;

public class GenericApiTest {

    protected static final String username = "Jeremy Keynes";
    private static final Logger log = LoggerFactory.getLogger(GenericApiTest.class);


    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testIfNull(UserRepository userQuery) {
        List<User> list = userQuery
                .where(User::getId).eqIfNotNull(null)
                .where(User::getId).geIfNotNull(null)
                .where(User::getId).eq(10)
                .getList();

        NumberPath<User, Integer> id = Path.of(User::getId);

        log.info("{}", list.size());
        ImmutableList<Predicate<User>> predicates = ImmutableList.of(id.geIfNotNull(null), id.eqIfNotNull(null));
        Predicate<User> predicate = id.eqIfNotNull(null)
                .and(predicates);
        List<User> list1 = userQuery.where(predicate).getList();
        log.info("{}", list1.size());
        list1 = userQuery.where(predicate.and(User::getId).eq(1)).getList();
        log.info("{}", list1.size());
        list1 = userQuery.where(predicate.and(id.eq(1))).getList();
        log.info("{}", list1.size());

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAndOr(UserRepository userQuery) {
        User single = userQuery
                .where(User::getId).eq(0)
                .orderBy(User::getId).asc()
                .getSingle(10);
        log.info("{}", single);
        List<User> dbList = userQuery
                .where(User::getRandomNumber).ne(1)
                .where(User::getRandomNumber).gt(100)
                .where(User::getRandomNumber).ne(125)
                .where(User::getRandomNumber).le(666)
                .where(Path.of(User::getRandomNumber).lt(106)
                        .or(User::getRandomNumber).gt(120)
                        .or(User::getRandomNumber).eq(109)
                )
                .where(User::getRandomNumber).ne(128)
                .getList();

        List<User> ftList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 1
                                && user.getRandomNumber() > 100
                                && user.getRandomNumber() != 125
                                && user.getRandomNumber() <= 666
                                && (user.getRandomNumber() < 106
                                    || user.getRandomNumber() > 120
                                    || user.getRandomNumber() == 109)
                                && user.getRandomNumber() != 128
                )
                .collect(Collectors.toList());

        assertEquals(dbList, ftList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAndOrChain(UserRepository userQuery) {
        User single = userQuery
                .where(User::getId).eq(0)
                .getSingle();
        log.info("{}", single);
        List<User> dbList = userQuery
                .where(User::getRandomNumber).ne(1)
                .where(User::getRandomNumber).gt(100)
                .where(User::getRandomNumber).ne(125)
                .where(User::getRandomNumber).le(666)
                .where(Path.of(User::getRandomNumber).lt(106)
                        .or(User::getRandomNumber).gt(120)
                        .or(User::getRandomNumber).eq(109)
                )
                .where(User::getRandomNumber).ne(128)
                .getList();

        List<User> ftList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 1
                                && user.getRandomNumber() > 100
                                && user.getRandomNumber() != 125
                                && user.getRandomNumber() <= 666
                                && (user.getRandomNumber() < 106
                                    || user.getRandomNumber() > 120
                                    || user.getRandomNumber() == 109)
                                && user.getRandomNumber() != 128
                )
                .collect(Collectors.toList());

        assertEquals(dbList, ftList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAndOrChan(UserRepository userQuery) {
        User single = userQuery
                .where(User::getId).eq(0)
                .getSingle();
        log.info("{}", single);
        List<User> dbList = userQuery
                .where(User::getRandomNumber).ne(1)
                .where(User::getRandomNumber).gt(100)
                .where(User::getRandomNumber).ne(125)
                .where(User::getRandomNumber).le(666)
                .where(Path.of(User::getRandomNumber).lt(106)
                        .or(User::getRandomNumber).gt(120)
                        .or(User::getRandomNumber).eq(109))
                .where(User::getRandomNumber).ne(128)
                .getList();

        List<User> ftList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 1
                                && user.getRandomNumber() > 100
                                && user.getRandomNumber() != 125
                                && user.getRandomNumber() <= 666
                                && (user.getRandomNumber() < 106
                                    || user.getRandomNumber() > 120
                                    || user.getRandomNumber() == 109)
                                && user.getRandomNumber() != 128
                )
                .collect(Collectors.toList());

        assertEquals(dbList, ftList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAndOr2(UserRepository userQuery) {
        User single = userQuery
                .where(Path.of(User::getId).eq(0))
                .getSingle();
        log.info("{}", single);
        List<User> dbList = userQuery
                .where(and(
                        Path.of(User::getRandomNumber).ne(1),
                        Path.of(User::getRandomNumber).gt(100),
                        Path.of(User::getRandomNumber).ne(125),
                        Path.of(User::getRandomNumber).le(666),
                        or(
                                Path.of(User::getRandomNumber).lt(106),
                                Path.of(User::getRandomNumber).gt(120),
                                Path.of(User::getRandomNumber).eq(109)
                        ),
                        Path.of(User::getRandomNumber).ne(128)
                )).getList();

        List<User> ftList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 1
                                && user.getRandomNumber() > 100
                                && user.getRandomNumber() != 125
                                && user.getRandomNumber() <= 666
                                && (user.getRandomNumber() < 106
                                    || user.getRandomNumber() > 120
                                    || user.getRandomNumber() == 109)
                                && user.getRandomNumber() != 128
                )
                .collect(Collectors.toList());

        assertEquals(dbList, ftList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testComparablePredicateTesterGt(UserRepository userQuery) {

        List<User> qgt80 = userQuery
                .where(Path.of(User::getRandomNumber).gt(80))
                .orderBy(Path.of(User::getId).asc())
                .getList();
        List<User> fgt80 = userQuery.users().stream()
                .filter(it -> it.getRandomNumber() > 80)
                .collect(Collectors.toList());
        assertEquals(qgt80, fgt80);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void te(UserRepository userQuery) {

        userQuery.fetch(EntityPath.of(User::getParentUser).get(User::getId))
                .where(User::getId).eq(0)
                .orderBy(User::getId)
                .getList();

        // UserRepository userQuery = DbConfigs.MYSQL.getJdbc();
        List<User> users = userQuery.fetch(
                        User::getParentUser,
                        User::getRandomUser)
                .where(User::getId).eq(0)
                .orderBy(User::getId)
                .getList();

        for (int i = 0; i < users.size(); i++) {
            User u0 = users.get(i);
            User u1 = userQuery.users().get(i);
            if (!Objects.equals(u0.getParentUser(), u1.getParentUser())) {
                log.info("{}", u0);
                log.info("{}", u1);
            }
            assertEquals(u0.getParentUser(), u1.getParentUser());
            assertEquals(u0.getRandomUser(), u1.getRandomUser());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testPredicateTesterEq(UserRepository userQuery) {
        int userId = 20;
        User user = userQuery
                .fetch(Arrays.asList(
                        Path.of(User::getParentUser),
                        EntityPath.of(User::getParentUser).get(User::getParentUser)
                ))
                .where(Path.of(User::getId).eq(userId))
                .getSingle();
        assertNotNull(user);
        assertEquals(user.getId(), userId);
        User u = userQuery.users().stream()
                .filter(it -> it.getId() == userId)
                .findAny()
                .orElseThrow();

        if (user.getPid() != null) {
            User parentUser = user.getParentUser();
            assertNotNull(parentUser);
            assertEquals(user.getPid(), parentUser.getId());
            assertEquals(u.getParentUser(), parentUser);
            assertEquals(u.getParentUser().getParentUser(), parentUser.getParentUser());

        }

        List<User> users = userQuery.fetch(
                        User::getParentUser,
                        User::getRandomUser)
                .orderBy(User::getId)
                .getList();

        for (int i = 0; i < users.size(); i++) {
            User u0 = users.get(i);
            User u1 = userQuery.users().get(i);
            assertEquals(u0.getParentUser(), u1.getParentUser());
            assertEquals(u0.getRandomUser(), u1.getRandomUser());
        }


        users = userQuery.fetch(
                        User::getParentUser,
                        User::getRandomUser,
                        User::getTestUser)
                .orderBy(User::getId)
                .getList();

        for (int i = 0; i < users.size(); i++) {
            User u0 = users.get(i);
            User u1 = userQuery.users().get(i);
            assertEquals(u0.getParentUser(), u1.getParentUser());
            assertEquals(u0.getRandomUser(), u1.getRandomUser());
            assertEquals(u0.getTestUser(), u1.getTestUser());
        }

        users = userQuery.fetch(ImmutableList.of(
                        User::getParentUser,
                        User::getRandomUser,
                        User::getTestUser))
                .orderBy(User::getId)
                .getList();

        for (int i = 0; i < users.size(); i++) {
            User u0 = users.get(i);
            User u1 = userQuery.users().get(i);
            assertEquals(u0.getParentUser(), u1.getParentUser());
            assertEquals(u0.getRandomUser(), u1.getRandomUser());
            assertEquals(u0.getTestUser(), u1.getTestUser());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAggregateFunction(UserRepository userQuery) {

        List<Expression<User, ?>> selected = Arrays.asList(
                Path.of(User::getRandomNumber).min(),
                Path.of(User::getRandomNumber).max(),
                Path.of(User::getRandomNumber).count(),
                Path.of(User::getRandomNumber).avg(),
                Path.of(User::getRandomNumber).sum()
        );
        Tuple aggregated = userQuery
                .select(selected)
                .requireSingle();

        assertNotNull(aggregated);
        assertEquals(getUserIdStream(userQuery).min().orElseThrow(), aggregated.<Integer>get(0));
        assertEquals(getUserIdStream(userQuery).max().orElseThrow(), aggregated.<Integer>get(1));
        assertEquals(getUserIdStream(userQuery).count(), aggregated.<Long>get(2));
        OptionalDouble average = getUserIdStream(userQuery).average();
        assertEquals(average.orElse(0), aggregated.<Number>get(3).doubleValue(), 1);
        assertEquals(getUserIdStream(userQuery).sum(), aggregated.<Number>get(4).intValue());

        List<Tuple> resultList = userQuery
                .select(Arrays.asList(Path.of(User::getId).min(), Path.of(User::getRandomNumber)))
                .where(Path.of(User::isValid).eq(true))
                .groupBy(User::getRandomNumber)
                .getList();

        Map<Integer, Optional<User>> map = userQuery.users().stream()
                .filter(User::isValid)
                .collect(Collectors.groupingBy(User::getRandomNumber, Collectors.minBy(Comparator.comparingInt(User::getId))));

        List<Tuple> fObjects = map.values().stream()
                .map(user -> {
                    Integer userId = user.map(User::getId).orElse(null);
                    Integer randomNumber = user.map(User::getRandomNumber).orElse(null);
                    return Tuples.of(userId, randomNumber);
                })
                .sorted(Comparator.comparing(a -> a.<Integer>get(0)))
                .collect(Collectors.toList());
        assertEquals(new HashSet<>(resultList), new HashSet<>(fObjects));

        Tuple one = userQuery
                .select(Collections.singletonList(Path.of(User::getId).sum()))
                .where(Path.of(User::isValid).eq(true))
                .requireSingle();

        int userId = userQuery.users().stream()
                .filter(User::isValid)
                .mapToInt(User::getId)
                .sum();
        assertEquals(one.<Number>get(0).intValue(), userId);

        Integer first = userQuery
                .select(User::getId)
                .orderBy(Path.of(User::getId).desc())
                .getFirst();
        assertEquals(first, userQuery.users().get(userQuery.users().size() - 1).getId());

        Long count = userQuery
                .select(Path.of(User::getRandomNumber).countDistinct())
                .getSingle();
        long count1 = userQuery.users()
                .stream().mapToInt(User::getRandomNumber)
                .distinct()
                .count();
        assertEquals(count1, count);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testSelect(UserRepository userQuery) throws JsonProcessingException {
        List<Tuple2<Integer, String>> qList = userQuery
                .select(User::getRandomNumber, User::getUsername)
                .orderBy(User::getUsername)
                .orderBy(User::getRandomNumber)
                .orderBy(User::getId)
                .getList();

        Comparator<User> comparator = Comparator.comparing(User::getUsername)
                .thenComparing(User::getRandomNumber)
                .thenComparing(User::getId);

        List<Tuple2<Integer, String>> fList = userQuery.users().stream()
                .sorted(comparator)
                .map(it -> Tuples.of(it.getRandomNumber(), it.getUsername()))
                .collect(Collectors.toList());

        JsonMapper jsonMapper = new JsonMapper();
        System.out.println(jsonMapper.writeValueAsString(qList.stream().map(Tuple2::get1)
                .distinct()
                .collect(Collectors.toList())));

        assertEquals(qList, fList);

        qList = userQuery
                .selectDistinct(User::getRandomNumber, User::getUsername)
                .getList();
        fList = fList.stream().distinct().collect(Collectors.toList());
        assertEquals(qList.size(), fList.size());
        HashSet<Tuple2<Integer, String>> set = new HashSet<>(qList);
        assertEquals(set.size(), fList.size());
        assertEquals(set, new HashSet<>(fList));
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testTime(UserRepository userQuery) {
        long start = System.currentTimeMillis();
        userQuery
                .orderBy(Arrays.asList(
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()
                ))
                .getList();
        log.info("{}", System.currentTimeMillis() - start);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testOrderBy(UserRepository userQuery) {
        List<User> list = userQuery
                .orderBy(Arrays.asList(
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()
                ))

                .getList();
        ArrayList<User> sorted = new ArrayList<>(userQuery.users());
        sorted.sort((a, b) -> Integer.compare(b.getRandomNumber(), a.getRandomNumber()));
        Iterator<User> ia = list.iterator();
        Iterator<User> ib = sorted.iterator();
        while (ia.hasNext()) {
            User ua = ia.next();
            User ub = ib.next();
            if (!Objects.equals(ua, ub)) {
                boolean equals = ua.equals(ub);
                log.info("{}", equals);
            }
        }
        assertEquals(list, sorted);

        list = userQuery
                .orderBy(Arrays.asList(Path.of(User::getUsername).asc(),
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()))
                .getList();
        Comparator<User> comparator = Comparator.comparing(User::getUsername)
                .thenComparing(Comparator.comparing(User::getRandomNumber).reversed())
                .thenComparing(User::getId);
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(User::getUsername)
                .orderBy(Arrays.asList(
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()
                ))
                .getList();
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(Path.of(User::getTime).asc())
                .getList();
        checkOrder(list, Comparator.comparing(User::getTime));
    }

    public <T> void checkOrder(Iterable<T> list, Comparator<T> comparator) {
        Iterator<T> iterator = list.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        T pre = iterator.next();
        while (iterator.hasNext()) {
            T next = iterator.next();
            int compare = comparator.compare(pre, next);
            if (compare > 0) {
                log.info("{}", "");
            }
            assertTrue(compare <= 0);
            pre = next;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testOrderBy2(UserRepository userQuery) {
        List<User> list = userQuery
                .orderBy(
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()
                )
                .getList();
        Comparator<User> comparator = Comparator
                .comparing(User::getRandomNumber)
                .reversed()
                .thenComparing(User::getId);
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(
                        Path.of(User::getUsername).asc(),
                        Path.of(User::getRandomNumber).desc(),
                        Path.of(User::getId).asc()
                )
                .getList();

        comparator = Comparator
                .comparing(User::getUsername)
                .thenComparing(Comparator.comparing(User::getRandomNumber).reversed())
                .thenComparing(User::getId);
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(User::getUsername)
                .orderBy(User::getRandomNumber).desc()
                .orderBy(User::getId).asc()
                .getList();
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(User::getUsername, User::getRandomNumber, User::getId).asc()
                .getList();

        comparator = Comparator
                .comparing(User::getUsername)
                .thenComparing(User::getRandomNumber)
                .thenComparing(User::getId);
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(User::getUsername, User::getRandomNumber, User::getId).desc()
                .getList();

        comparator = Comparator
                .comparing(User::getUsername)
                .thenComparing(User::getRandomNumber)
                .thenComparing(User::getId)
                .reversed();
        checkOrder(list, comparator);

        list = userQuery
                .orderBy(User::getTime)
                .getList();
        comparator = Comparator
                .comparing(User::getTime);
        checkOrder(list, comparator);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testPredicate(UserRepository userQuery) {
        List<User> qList = userQuery
                .where(not(Path.of(User::getRandomNumber).ge(10)
                        .or(User::getRandomNumber).lt(5)))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery
                .where(Path.of(User::getUsername).ne("Jeremy Keynes").not())
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> (it.getUsername().equalsIgnoreCase("Jeremy Keynes")))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).eq("Jeremy Keynes"))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, fList);

        qList = userQuery.where(
                        not(Path.of(User::getUsername).eq("Jeremy Keynes")
                                .or(Path.of(User::getId).eq(3)))
                )
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
                                || it.getId() == 3))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(not(Path.of(User::getUsername).eq("Jeremy Keynes")
                        .and(Path.of(User::getId).eq(3))
                ))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
                                && it.getId() == 3))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testPredicate2(UserRepository userQuery) {
        List<User> qList = userQuery
                .where(or(
                        Path.of(User::getRandomNumber).ge(10),
                        Path.of(User::getRandomNumber).lt(5)
                ).not())
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery
                .where(Path.of(User::getUsername).eq("Jeremy Keynes").not())
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).eq("Jeremy Keynes")
                        .not()
                )
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, fList);

        qList = userQuery.where(not(Path.of(User::getUsername).eq("Jeremy Keynes")
                        .or(Path.of(User::getId).eq(3))
                ))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
                                || it.getId() == 3))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(and(
                        Path.of(User::getUsername).eq("Jeremy Keynes"),
                        Path.of(User::getId).eq(3)
                ).not())
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
                                && it.getId() == 3))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testGroupBy1(UserRepository userQuery) {
        List<Tuple3<Boolean, Integer, Integer>> resultList = userQuery
                .select(User::isValid, User::getRandomNumber, User::getPid)
                .groupBy(User::getRandomNumber, User::getPid, User::isValid)
                .getList();

        List<Tuple3<Boolean, Integer, Integer>> resultList2 = userQuery
                .select(User::isValid, User::getRandomNumber, User::getPid)
                .groupBy(User::getRandomNumber, User::getPid, User::isValid)
                .getList();
        assertEquals(resultList, resultList2);
        List<Tuple3<Boolean, Integer, Integer>> list = userQuery.users().stream()
                .map(it -> Tuples.of(it.isValid(), it.getRandomNumber(), it.getPid()))
                .distinct()
                .collect(Collectors.toList());
        assertEquals(sort(resultList), sort(list));


    }

    private static List<Tuple3<Boolean, Integer, Integer>> sort(List<Tuple3<Boolean, Integer, Integer>> resultList) {
        return resultList.stream()
                .sorted(Comparator.comparing(Object::toString))
                .collect(Collectors.toList());
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testIsNull(UserRepository userQuery) {

        List<User> qList = userQuery.where(Path.of(User::getPid).isNotNull())
                .orderBy(User::getId)
                .getList();

        List<User> fList = userQuery.users().stream()
                .filter(it -> it.getPid() != null)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getPid).add(2).multiply(3).isNull())
                .orderBy(User::getId)
                .getList();

        fList = userQuery.users().stream()
                .filter(it -> it.getPid() == null)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testOperatorIfNotNull(UserRepository userQuery) {
        List<User> qList = userQuery.where(User::getRandomNumber).eq(10)
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);


        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getRandomNumber).gtIfNotNull(null)
                .where(User::getRandomNumber).geIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());


        qList = userQuery.where(Path.of(User::getRandomNumber).eqIfNotNull(null))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());
        qList = userQuery.where(Path.of(User::getRandomNumber).gtIfNotNull(null))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());
        qList = userQuery.where(Path.of(User::getRandomNumber).geIfNotNull(null))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());
        qList = userQuery.where(Path.of(User::getRandomNumber).ltIfNotNull(null))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());
        qList = userQuery.where(Path.of(User::getRandomNumber).leIfNotNull(null))
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, userQuery.users());


        qList = userQuery.where(Path.of(User::getRandomNumber).eqIfNotNull(20)).getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 20)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        Conjunction<User> predicate = Path.of(User::getRandomNumber).eq(20).and(User::getUsername).eqIfNotNull(null);
        qList = userQuery.where(predicate).getList();

        assertEquals(qList, fList);
        qList = userQuery.where(Path.of(User::getRandomNumber).gtIfNotNull(20)).getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() > 20)
                .collect(Collectors.toList());
        assertEquals(qList, fList);
        qList = userQuery.where(Path.of(User::getRandomNumber).geIfNotNull(20)).getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() >= 20)
                .collect(Collectors.toList());
        assertEquals(qList, fList);
        qList = userQuery.where(Path.of(User::getRandomNumber).ltIfNotNull(20))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 20)
                .collect(Collectors.toList());
        assertEquals(qList, fList);
        qList = userQuery.where(Path.of(User::getRandomNumber).leIfNotNull(20))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() <= 20)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).eq(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).eqIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).geIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() >= 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).gtIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() > 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);


        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).leIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() <= 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);


        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).ltIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).addIfNotNull(null).ltIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);
        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).addIfNotNull(3).ltIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() + 3 < 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);
        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).addIfNotNull(3).ltIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).multiplyIfNotNull(3).ltIfNotNull(50)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() * 3 < 50)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery
                .where(User::getRandomUser).eqIfNotNull(null)
                .where(User::getId).addIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).subtractIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).multiplyIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).divideIfNotNull(null).eqIfNotNull(null)
                .where(User::getId).modIfNotNull(null).eqIfNotNull(null)
                .where(User::getUsername).eqIfNotNull(null)
                .where(User::getRandomNumber).divideIfNotNull(3).ltIfNotNull(10)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() / 3.0 < 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testOperator2(UserRepository userQuery) {
        Predicate<User> isValid = Path.of(User::isValid);
        userQuery.where(isValid
                        .and(User::getRandomNumber).notBetween(10, 15)
                        .and(User::getId).mod(3).eq(0)
                )
                .getList();
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testOperator(UserRepository userQuery) {

        Predicate<User> isValid = Path.of(User::isValid);
        List<User> qList = userQuery.where(isValid)
                .orderBy(User::getId)
                .getList();
        List<User> validUsers = userQuery.users().stream().filter(User::isValid)
                .collect(Collectors.toList());
        List<User> fList = validUsers;
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).eq(2))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() == 2)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getPid).ne(2))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getPid() != 2)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).in(1, 2, 3))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> Arrays.asList(1, 2, 3).contains(user.getRandomNumber()))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).notIn(1, 2, 3))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> !Arrays.asList(1, 2, 3).contains(user.getRandomNumber()))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getPid).isNull())
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() == null)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).ge(10))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() >= 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).gt(10))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() > 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).le(10))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() <= 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).lt(10))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() < 10)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).between(10, 15))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() >= 10 && user.getRandomNumber() <= 15)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).notBetween(10, 15))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getRandomNumber() < 10 || user.getRandomNumber() > 15)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid
                        .and(User::getRandomNumber).notBetween(10, 15)
                        .and(User::getId).mod(3).eq(0)
                )
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user ->
                        !(user.getRandomNumber() >= 10 && user.getRandomNumber() <= 15)
                        && user.getId() % 3 == 0)
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).ge(Path.of(User::getPid)))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() >= user.getPid())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).gt(Path.of(User::getPid)))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() > user.getPid())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).le(Path.of(User::getPid)))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() <= user.getPid())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber).lt(Path.of(User::getPid)))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() < user.getPid())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(isValid.and(User::getRandomNumber)
                        .between(Path.of(User::getRandomNumber), Path.of(User::getPid)))
                .orderBy(User::getId)
                .getList();
        fList = validUsers.stream()
                .filter(user -> user.getPid() != null && user.getRandomNumber() >= user.getRandomNumber() && user.getRandomNumber() <= user.getPid())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testPredicateAssembler(UserRepository userQuery) {

        List<User> qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .and(User::getParentUser).get(User::getUsername).eq(username))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                && user.getParentUser() != null
                                && Objects.equals(user.getParentUser().getUsername(), username))
                .collect(Collectors.toList());

        assertEq(qList, fList);
        qList = userQuery.where(User::isValid).eq(true)
                .where(User::getParentUser).get(User::getParentUser).get(User::getUsername).eq(username)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                && user.getParentUser() != null
                                && user.getParentUser().getParentUser() != null
                                && Objects.equals(user.getParentUser().getParentUser().getUsername(), username))
                .collect(Collectors.toList());
        assertEq(qList, fList);

        PathRef<User, Number> getUsername = User::getRandomNumber;
        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .and(getUsername).eq(10))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                && Objects.equals(user.getRandomNumber(), 10))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .or(getUsername).eq(10))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                || Objects.equals(user.getRandomNumber(), 10))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .and(getUsername).ne(10))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                && !Objects.equals(user.getRandomNumber(), 10))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .or(getUsername).ne(10))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                || !Objects.equals(user.getRandomNumber(), 10))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        Date time = userQuery.users().get(20).getTime();

        Expression<User, Boolean> or = Path.of(User::isValid).eq(true)
                .or(
                        EntityPath.of(User::getParentUser)
                                .get(User::getUsername)
                                .eq(username)
                                .and(User::getTime)
                                .ge(time));
        qList = userQuery.where(or)
                .orderBy(User::getId)
                .getList();

        List<User> jeremy_keynes = userQuery
                .fetch(User::getParentUser)
                .where(Path.of(User::isValid).eq(true)
                        .or(EntityPath.of(User::getParentUser)
                                .get(User::getUsername).eq(username)
                                .and(User::getTime).ge(time)
                        ))
                .orderBy(User::getId)
                .getList();

        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                || (user.getParentUser() != null
                                    && Objects.equals(user.getParentUser().getUsername(), username)
                                    && user.getTime().getTime() >= time.getTime()))
                .collect(Collectors.toList());

        assertEquals(qList, fList);
        assertEquals(qList, jeremy_keynes);

        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .and(User::getRandomNumber).ne(5))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                && user.getRandomNumber() != 5)
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::isValid).eq(true)
                        .or(User::getRandomNumber).eq(5))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.isValid()
                                || user.getRandomNumber() == 5)
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getRandomNumber).ne(6)
                        .or(User::isValid).eq(false))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 6
                                || !user.isValid())
                .collect(Collectors.toList());

        assertEquals((qList), (fList));

        qList = userQuery.where(Path.of(User::getRandomNumber).ne(6)
                        .and(User::getParentUser).get(User::isValid).eq(true))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 6
                                && (user.getParentUser() != null && user.getParentUser().isValid()))
                .collect(Collectors.toList());

        assertEquals((qList), (fList));

        qList = userQuery.where(Path.of(User::getRandomNumber).ne(6)
                        .and(User::getParentUser).get(User::isValid).ne(true))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 6
                                && (user.getParentUser() != null && !user.getParentUser().isValid()))
                .collect(Collectors.toList());

        assertEquals((qList), (fList));

        qList = userQuery.where(Path.of(User::getRandomNumber).ne(6)
                        .or(User::getParentUser).get(User::isValid).ne(true))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() != 6
                                || (user.getParentUser() != null && !user.getParentUser().isValid()))
                .collect(Collectors.toList());

        assertEquals((qList), (fList));

        qList = userQuery.where(not(Path.of(User::getRandomNumber).ge(10)
                        .or(User::getRandomNumber).lt(5)
                ))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(not(Path.of(User::getRandomNumber).ge(10)
                                .and(User::getRandomNumber).le(15)
                        )
                )
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 && it.getRandomNumber() <= 15))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(not(
                        Path.of(User::getRandomNumber).ge(10)
                                .and(User::getUsername).eq(username)
                ))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 && it.getUsername().equals(username)))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(not(Path.of(User::getRandomNumber).ge(10)
                                .or(User::getUsername).eq(username)
                        )
                )
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> !(it.getRandomNumber() >= 10 || it.getUsername().equals(username)))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(not(Path.of(User::getRandomNumber).ge(10)
                        .and(User::getUsername).eq(username))
                        .not()
                )
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> (it.getRandomNumber() >= 10 && it.getUsername().equals(username)))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(not(Path.of(User::getRandomNumber).ge(10)
                        .or(User::getUsername).eq(username))
                        .not()
                )
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(it -> it.getRandomNumber() >= 10 || it.getUsername().equals(username))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

    }

    private static void assertEq(List<User> qList, List<User> fList) {
        assertEquals(qList.size(), fList.size());
        for (int i = 0; i < qList.size(); i++) {
            if (!qList.get(i).equals(fList.get(i))) {
                throw new RuntimeException();
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void testSubQuery(UserRepository userQuery) {
        Date time = userQuery.users().get(20).getTime();

        userQuery
                .fetch(User::getParentUser)
                .where(Path.of(User::isValid).eq(true)
                        .or(EntityPath.of(User::getParentUser)
                                .get(User::getUsername).eq(username)
                                .and(User::getTime).ge(time)
                        ))
                .count();
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testNumberPredicateTester(UserRepository userQuery) {
        List<User> list = userQuery
                .where(Path.of(User::getRandomNumber).add(2).ge(4))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() + 2 >= 4)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).subtract(2).ge(4))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() - 2 >= 4)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).multiply(2).ge(4))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() * 2 >= 4)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).divide(2).ge(4))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() / 2 >= 4)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).mod(2).ge(1))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() % 2 == 1)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        ///
        list = userQuery
                .where(Path.of(User::getRandomNumber).add(Path.of(User::getId)).ge(40))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() + user.getId() >= 40)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).subtract(Path.of(User::getId)).ge(40))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() - user.getId() >= 40)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).multiply(Path.of(User::getId)).ge(40))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getRandomNumber() * user.getId() >= 40)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).divide(Path.of(User::getId)).ge(40))
                .where(User::getId).ne(0)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getId() != 0 && user.getRandomNumber() / user.getId() >= 40)
                .collect(Collectors.toList());

        assertEquals(list, fList);

        list = userQuery
                .where(Path.of(User::getRandomNumber).mod(Path.of(User::getId)).ge(10))
                .where(User::getId).ne(0)
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getId() != 0 && user.getRandomNumber() % user.getId() >= 10)
                .collect(Collectors.toList());

        assertEquals(list, fList);

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testStringPredicateTester(UserRepository userQuery) {
        String username = "Roy Sawyer";

        List<User> qList = userQuery.where(Path.of(User::getUsername).substring(2).eq("eremy Keynes"))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> user.getUsername().substring(1).equals("eremy Keynes"))
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).substring(1, 1).eq("M"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().charAt(0) == 'M')
                .collect(Collectors.toList());

        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).trim().like(username))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().trim().startsWith(username))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).trim().lower().notContains("i"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> !user.getUsername().toLowerCase().contains("i"))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).length().eq(username.length()))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().length() == username.length())
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).startsWith("M"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().startsWith("M"))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).endsWith("s"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().endsWith("s"))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).lower().contains("s"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().toLowerCase().contains("s"))
                .collect(Collectors.toList());
        assertEquals(qList, fList);

        qList = userQuery.where(Path.of(User::getUsername).upper().contains("S"))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getUsername().toUpperCase().contains("S"))
                .collect(Collectors.toList());
        assertEquals(qList, fList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testResultBuilder(UserRepository userQuery) {
        List<User> resultList = userQuery
                .orderBy(User::getId)
                .getList(5, 10);
        List<User> subList = userQuery.users().subList(5, 5 + 10);
        assertEquals(resultList, subList);

        List<Integer> userIds = userQuery.select(User::getId)
                .orderBy(User::getId)
                .getList(5, 10);
        List<Integer> subUserIds = userQuery.users().subList(5, 5 + 10)
                .stream().map(User::getId)
                .collect(Collectors.toList());

        assertEquals(userIds, subUserIds);

        resultList = userQuery.where(Path.of(User::getId).in())
                .orderBy(User::getId)
                .getList();
        assertEquals(resultList.size(), 0);

        resultList = userQuery.where(Path.of(User::getId).notIn())
                .orderBy(User::getId)
                .getList();
        assertEquals(resultList, userQuery.users());

        long count = userQuery.count();
        assertEquals(count, userQuery.users().size());

        User first = userQuery
                .orderBy(User::getId)
                .getFirst();
        assertEquals(first, userQuery.users().get(0));

        first = userQuery.where(Path.of(User::getId).eq(0)).requireSingle();
        assertEquals(first, userQuery.users().get(0));

        first = userQuery
                .orderBy(User::getId)
                .getFirst(10);
        assertEquals(first, userQuery.users().get(10));

        assertThrowsExactly(IllegalStateException.class, userQuery::requireSingle);
        assertThrowsExactly(NullPointerException.class, () -> userQuery.where(Path.of(User::getId).eq(-1)).requireSingle());

        assertTrue(userQuery.exist());
        assertTrue(userQuery.exist(userQuery.users().size() - 1));
        assertFalse(userQuery.exist(userQuery.users().size()));

        List<UserModel> userModels = userQuery.select(UserModel.class)
                .orderBy(User::getId)
                .getList();

        List<Map<String, Object>> l0 = userQuery.users().stream()
                .map(UserModel::new)
                .map(UserInterface::asMap)
                .collect(Collectors.toList());

        List<Map<String, Object>> l1 = userQuery.select(UserInterface.class)
                .orderBy(User::getId)
                .getList()
                .stream()
                .map(UserInterface::asMap)
                .collect(Collectors.toList());

        List<Map<String, Object>> l2 = userModels.stream()
                .map(UserInterface::asMap)
                .collect(Collectors.toList());

        assertEquals(l0, l1);
        assertEquals(l0, l2);

        User user = userQuery.users().stream()
                .filter(it -> it.getParentUser() != null)
                .findAny()
                .orElse(userQuery.users().get(0));
        UserInterface userInterface = userQuery.select(UserInterface.class)
                .where(User::getId).eq(user.getId())
                .getSingle();

        assertEquals(userInterface.getId(), user.getId());
        assertEquals(userInterface.getRandomNumber(), user.getRandomNumber());
        assertEquals(userInterface.getUsername(), user.getUsername());
        assertEquals(userInterface.getPid(), user.getPid());
        assertEquals(userInterface.isValid(), user.isValid());
        assertEquals(userInterface.getParentUsername(), user.getParentUser().getUsername());

    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testSlice(UserRepository userQuery) {
        Slice<String> slice = userQuery.select(User::getUsername)
                .where(User::getParentUser).get(User::getRandomNumber).eq(10)
                .groupBy(User::getUsername)
                .orderBy(User::getUsername)
                .slice(2, 10);
        log.info("{}", slice);
        long count = userQuery.users().stream()
                .filter(user -> user.getParentUser() != null && user.getParentUser().getRandomNumber() == 10)
                .map(User::getUsername)
                .distinct()
                .count();

        List<String> names = userQuery.users().stream()
                .filter(user -> user.getParentUser() != null && user.getParentUser().getRandomNumber() == 10)
                .map(User::getUsername)
                .sorted()
                .distinct()
                .skip(2)
                .limit(10)
                .toList();
        assertEquals(slice.total(), count);
        assertEquals(new HashSet<>(slice.data()), new HashSet<>(names));
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void projection(UserRepository userQuery) throws JsonProcessingException {
        List<UserInterface> list0 = userQuery.select(UserInterface.class)
                .getList();
        List<UserInterface> list1 = userQuery.select(UserInterface.class)
                .getList();

        log.info("{}", JsonSerializablePredicateValueTest.mapper.writeValueAsString(list0.get(0)));

        assertEquals(list0, list1);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void testInterfaceSelect(UserRepository userQuery) {
        UserInterface list = userQuery.select(UserInterface.class)
                .getFirst();
        String string = list.toString();
        log.info("{}", string);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testAttr(UserRepository userQuery) {
        User first = userQuery.orderBy(Path.of(User::getId).desc()).getFirst();
        ArrayList<User> users = new ArrayList<>(userQuery.users());
        users.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        User f = users.stream().findFirst().orElse(null);
        assertEquals(first, f);

        first = userQuery.orderBy(Path.of(User::getUsername).desc(), Path.of(User::getId).asc())
                .getFirst();

        users = new ArrayList<>(userQuery.users());
        users.sort((a, b) -> b.getUsername().compareTo(a.getUsername()));
        f = users.stream().findFirst().orElse(null);
        assertEquals(first, f);

        first = userQuery.orderBy(Path.of(User::isValid).desc(), Path.of(User::getId).asc()).getFirst();
        users = new ArrayList<>(userQuery.users());
        users.sort((a, b) -> Boolean.compare(b.isValid(), a.isValid()));
        f = users.stream().findFirst().orElse(null);
        assertEquals(first, f);

        first = userQuery
                .where(Path.of(User::isValid).eq(true))
                .orderBy(User::getId)
                .getFirst();

        f = userQuery.users().stream()
                .filter(User::isValid)
                .findFirst()
                .orElse(null);
        assertEquals(first, f);

        List<User> resultList = userQuery
                .where(EntityPath.of(User::getParentUser).get(User::isValid)
                        .eq(true))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> user.getParentUser() != null && user.getParentUser().isValid())
                .collect(Collectors.toList());

        assertEquals(resultList, fList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testWhere(UserRepository userQuery) {
        List<User> resultList = userQuery
                .where(EntityPath.of(User::getParentUser).get(User::getUsername).eq(username))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> user.getParentUser() != null && username.equals(user.getParentUser().getUsername()))
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery
                .where(EntityPath.of(User::getParentUser).get(User::getUsername).ne(username))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> user.getParentUser() != null && !username.equals(user.getParentUser().getUsername()))
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery
                .where(Path.of(User::getUsername).ne(username))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> !username.equals(user.getUsername()))
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery
                .where(Path.of(User::getUsername).ne(username))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> !username.equals(user.getUsername()))
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery
                .where(Path.of(User::getUsername).ne(username))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> !username.equals(user.getUsername()))
                .collect(Collectors.toList());
        assertEquals(resultList, fList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testPathBuilder(UserRepository userQuery) {
        List<User> resultList = userQuery.where(EntityPath.of(User::getParentUser)
                        .get(User::getParentUser).get(User::getUsername).eq(username))
                .orderBy(User::getId)
                .getList();
        List<User> fList = userQuery.users().stream()
                .filter(user -> {
                    User p = user.getParentUser();
                    return p != null && p.getParentUser() != null && username.equals(p.getParentUser().getUsername());
                })
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery.where(EntityPath.of(User::getParentUser)
                        .get(User::getRandomNumber).eq(5))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> {
                    User p = user.getParentUser();
                    return p != null && p.getRandomNumber() == 5;
                })
                .collect(Collectors.toList());
        assertEquals(resultList, fList);

        resultList = userQuery.where(EntityPath.of(User::getParentUser)
                        .get(User::getRandomNumber).eq(5))
                .orderBy(User::getId)
                .getList();
        fList = userQuery.users().stream()
                .filter(user -> {
                    User p = user.getParentUser();
                    return p != null && p.getRandomNumber() == 5;
                })
                .collect(Collectors.toList());
        assertEquals(resultList, fList);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void testBigNum(UserRepository userQuery) {
        List<User> users = userQuery.where(Path.of(User::getTimestamp).eq(Double.MAX_VALUE))
                .getList();
        log.info("{}", users);
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    public void subQueryTest(UserRepository userQuery) {
        Expression<User, List<Integer>> ids = userQuery
                .select(User::getId).where(User::getId)
                .in(1, 2, 3)
                .asSubQuery();

        List<User> result = userQuery.where(User::getId).in(ids).getList();
        log.info("{}", result);
    }

    private IntStream getUserIdStream(UserRepository userQuery) {
        return userQuery.users().stream().mapToInt(User::getRandomNumber);
    }

    // @Test
    // void test() {
    //     test(UserQueryProvider.jpaQuery());
    //     test(UserQueryProvider.jdbcQuery());
    // }

    // private static void test(Query query) {
    //     Select<UserSummary> from = query.from(UserSummary.class);
    //     List<UserSummary> list = from.where(UserSummary::getMaxRandomNumber).le(33).getList();
    //     Map<String, List<User>> map = userQuery.users().stream().collect(Collectors.groupingBy(User::getUsername));
    //     Map<String, UserSummary> summaryMap = new HashMap<>();
    //     map.forEach((k, v) -> {
    //         UserSummary summary = new UserSummary();
    //         summary.setCount((long) v.size());
    //         summary.setUsername(k);
    //         int maxRandomNumber = Integer.MIN_VALUE;
    //         for (User user : v) {
    //             maxRandomNumber = Math.max(maxRandomNumber, user.getRandomNumber());
    //         }
    //         summary.setMaxRandomNumber(maxRandomNumber);
    //         summaryMap.put(k, summary);
    //     });
    //     for (UserSummary summary : list) {
    //         UserSummary s = summaryMap.get(summary.getUsername());
    //         assertEquals(s, summary);
    //         assertTrue(summary.getMaxRandomNumber() <= 33);
    //     }
    // }
}
