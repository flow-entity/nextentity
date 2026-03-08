//package io.github.nextentity.test;
//
//import io.github.nextentity.api.NumberPath;
//import io.github.nextentity.api.Predicate;
//import io.github.nextentity.api.TypedExpression;
//import io.github.nextentity.api.model.Tuple;
//import io.github.nextentity.api.model.Tuple2;
//import io.github.nextentity.core.Tuples;
//import io.github.nextentity.core.util.ImmutableList;
//import io.github.nextentity.test.db.UserRepository;
//import io.github.nextentity.test.entity.User;
//import io.github.nextentity.test.entity.UserMeta;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ArgumentsSource;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static io.github.nextentity.core.util.Predicates.and;
//import static io.github.nextentity.core.util.Predicates.or;
//import static org.junit.jupiter.api.Assertions.*;
//
//@Slf4j
//public class GenericApiTest0 {
//
//    protected static final String username = "Jeremy Keynes";
//
//    protected static final UserMeta<User> user = new UserMeta<>();
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testIfNull(UserRepository userQuery) {
//        List<User> list = userQuery
//                .where(user.id).eqIfNotNull(null)
//                .where(user.id).geIfNotNull(null)
//                .where(user.id).eq(10)
//                .getList();
//        NumberPath<User, Integer> id = user.id;
//
//        log.info("{}", list.size());
//        ImmutableList<Predicate<User>> predicates = ImmutableList.of(id.geIfNotNull(null), id.eqIfNotNull(null));
//        Predicate<User> predicate = id.eqIfNotNull(null)
//                .and(predicates);
//        List<User> list1 = userQuery.where(predicate).getList();
//        log.info("{}", list1.size());
//        list1 = userQuery.where(predicate.and(user.id.eq(1))).getList();
//        log.info("{}", list1.size());
//        list1 = userQuery.where(predicate.and(id.eq(1))).getList();
//        log.info("{}", list1.size());
//
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testAndOr(UserRepository userQuery) {
//        User single = userQuery
//                .where(user.id).eq(0)
//                .orderBy(user.id.asc())
//                .getSingle(10);
//        log.info("{}", single);
//        List<User> dbList = userQuery
//                .where(user.randomNumber).ne(1)
//                .where(user.randomNumber).gt(100)
//                .where(user.randomNumber).ne(125)
//                .where(user.randomNumber).le(666)
//                .where(user.randomNumber.lt(106)
//                        .or(user.randomNumber.gt(120))
//                        .or(user.randomNumber.eq(109))
//                )
//                .where(user.randomNumber).ne(128)
//                .getList();
//
//        List<User> ftList = userQuery.users().stream()
//                .filter(user -> user.getRandomNumber() != 1
//                                && user.getRandomNumber() > 100
//                                && user.getRandomNumber() != 125
//                                && user.getRandomNumber() <= 666
//                                && (user.getRandomNumber() < 106
//                                    || user.getRandomNumber() > 120
//                                    || user.getRandomNumber() == 109)
//                                && user.getRandomNumber() != 128
//                )
//                .collect(Collectors.toList());
//
//        assertEquals(dbList, ftList);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testAndOrChain(UserRepository userQuery) {
//        User single = userQuery
//                .where(user.id).eq(0)
//                .getSingle();
//        log.info("{}", single);
//        List<User> dbList = userQuery
//                .where(user.randomNumber).ne(1)
//                .where(user.randomNumber).gt(100)
//                .where(user.randomNumber).ne(125)
//                .where(user.randomNumber).le(666)
//                .where(user.randomNumber.lt(106)
//                        .or(user.randomNumber.gt(120))
//                        .or(user.randomNumber.eq(109))
//                )
//                .where(user.randomNumber).ne(128)
//                .getList();
//
//        List<User> ftList = userQuery.users().stream()
//                .filter(user -> user.getRandomNumber() != 1
//                                && user.getRandomNumber() > 100
//                                && user.getRandomNumber() != 125
//                                && user.getRandomNumber() <= 666
//                                && (user.getRandomNumber() < 106
//                                    || user.getRandomNumber() > 120
//                                    || user.getRandomNumber() == 109)
//                                && user.getRandomNumber() != 128
//                )
//                .collect(Collectors.toList());
//
//        assertEquals(dbList, ftList);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testAndOrChan(UserRepository userQuery) {
//        User single = userQuery
//                .where(user.id).eq(0)
//                .getSingle();
//        log.info("{}", single);
//        List<User> dbList = userQuery
//                .where(user.randomNumber).ne(1)
//                .where(user.randomNumber).gt(100)
//                .where(user.randomNumber).ne(125)
//                .where(user.randomNumber).le(666)
//                .where(user.randomNumber.lt(106)
//                        .or(user.randomNumber.gt(120))
//                        .or(user.randomNumber.eq(109)))
//                .where(user.randomNumber).ne(128)
//                .getList();
//
//        List<User> ftList = userQuery.users().stream()
//                .filter(user -> user.getRandomNumber() != 1
//                                && user.getRandomNumber() > 100
//                                && user.getRandomNumber() != 125
//                                && user.getRandomNumber() <= 666
//                                && (user.getRandomNumber() < 106
//                                    || user.getRandomNumber() > 120
//                                    || user.getRandomNumber() == 109)
//                                && user.getRandomNumber() != 128
//                )
//                .collect(Collectors.toList());
//
//        assertEquals(dbList, ftList);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testAndOr2(UserRepository userQuery) {
//        User single = userQuery
//                .where(user.id.eq(0))
//                .getSingle();
//        log.info("{}", single);
//        List<User> dbList = userQuery
//                .where(and(
//                        user.randomNumber.ne(1),
//                        user.randomNumber.gt(100),
//                        user.randomNumber.ne(125),
//                        user.randomNumber.le(666),
//                        or(
//                                user.randomNumber.lt(106),
//                                user.randomNumber.gt(120),
//                                user.randomNumber.eq(109)
//                        ),
//                        user.randomNumber.ne(128)
//                )).getList();
//
//        List<User> ftList = userQuery.users().stream()
//                .filter(user -> user.getRandomNumber() != 1
//                                && user.getRandomNumber() > 100
//                                && user.getRandomNumber() != 125
//                                && user.getRandomNumber() <= 666
//                                && (user.getRandomNumber() < 106
//                                    || user.getRandomNumber() > 120
//                                    || user.getRandomNumber() == 109)
//                                && user.getRandomNumber() != 128
//                )
//                .collect(Collectors.toList());
//
//        assertEquals(dbList, ftList);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testComparablePredicateTesterGt(UserRepository userQuery) {
//
//        List<User> qgt80 = userQuery
//                .where(user.randomNumber.gt(80))
//                .orderBy(user.id.asc())
//                .getList();
//        List<User> fgt80 = userQuery.users().stream()
//                .filter(it -> it.getRandomNumber() > 80)
//                .collect(Collectors.toList());
//        assertEquals(qgt80, fgt80);
//
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    void te(UserRepository userQuery) {
//
//        userQuery.fetch(user.parentUser.id)
//                .where(user.id).eq(0)
//                .orderBy(user.id.asc())
//                .getList();
//
//        // UserRepository userQuery = DbConfigs.MYSQL.getJdbc();
//        List<User> users = userQuery.fetch(
//                        user.parentUser,
//                        user.randomUser)
//                .where(user.id).eq(0)
//                .orderBy(user.id.asc())
//                .getList();
//
//        for (int i = 0; i < users.size(); i++) {
//            User u0 = users.get(i);
//            User u1 = userQuery.users().get(i);
//            if (!Objects.equals(u0.getParentUser(), u1.getParentUser())) {
//                log.info("{}", u0);
//                log.info("{}", u1);
//            }
//            assertEquals(u0.getParentUser(), u1.getParentUser());
//            assertEquals(u0.getRandomUser(), u1.getRandomUser());
//        }
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testPredicateTesterEq(UserRepository userQuery) {
//        int userId = 20;
//        User user = userQuery
//                .fetch(GenericApiTest0.user.parentUser,
//                        GenericApiTest0.user.parentUser.parentUser)
//                .where(GenericApiTest0.user.id.eq(userId))
//                .getSingle();
//        assertNotNull(user);
//        assertEquals(user.getId(), userId);
//        User u = userQuery.users().stream()
//                .filter(it -> it.getId() == userId)
//                .findAny()
//                .orElseThrow();
//
//        if (user.getPid() != null) {
//            User parentUser = user.getParentUser();
//            assertNotNull(parentUser);
//            assertEquals(user.getPid(), parentUser.getId());
//            assertEquals(u.getParentUser(), parentUser);
//            assertEquals(u.getParentUser().getParentUser(), parentUser.getParentUser());
//
//        }
//
//        List<User> users = userQuery.fetch(
//                        GenericApiTest0.user.parentUser,
//                        GenericApiTest0.user.randomUser)
//                .orderBy(GenericApiTest0.user.id.asc())
//                .getList();
//
//        for (int i = 0; i < users.size(); i++) {
//            User u0 = users.get(i);
//            User u1 = userQuery.users().get(i);
//            assertEquals(u0.getParentUser(), u1.getParentUser());
//            assertEquals(u0.getRandomUser(), u1.getRandomUser());
//        }
//
//
//        users = userQuery.fetch(
//                        GenericApiTest0.user.parentUser,
//                        GenericApiTest0.user.randomUser,
//                        GenericApiTest0.user.testUser)
//                .orderBy(GenericApiTest0.user.id.asc())
//                .getList();
//
//        for (int i = 0; i < users.size(); i++) {
//            User u0 = users.get(i);
//            User u1 = userQuery.users().get(i);
//
//            assertEquals(u0.getParentUser(), u1.getParentUser());
//            assertEquals(u0.getRandomUser(), u1.getRandomUser());
//            assertEquals(u0.getTestUser(), u1.getTestUser());
//
//        }
//
//        users = userQuery.fetch(
//                        GenericApiTest0.user.parentUser,
//                        GenericApiTest0.user.randomUser,
//                        GenericApiTest0.user.testUser)
//                .orderBy(GenericApiTest0.user.id.asc())
//                .getList();
//
//        for (int i = 0; i < users.size(); i++) {
//            User u0 = users.get(i);
//            User u1 = userQuery.users().get(i);
//            assertEquals(u0.getParentUser(), u1.getParentUser());
//            assertEquals(u0.getRandomUser(), u1.getRandomUser());
//            assertEquals(u0.getTestUser(), u1.getTestUser());
//        }
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testAggregateFunction(UserRepository userQuery) {
//
//        List<TypedExpression<User, ?>> selected = Arrays.asList(
//                user.randomNumber.min(),
//                user.randomNumber.max(),
//                user.randomNumber.count(),
//                user.randomNumber.avg(),
//                user.randomNumber.sum()
//        );
//        Tuple aggregated = userQuery
//                .select(selected)
//                .requireSingle();
//
//        assertNotNull(aggregated);
//        assertEquals(getUserIdStream(userQuery).min().orElseThrow(), aggregated.<Integer>get(0));
//        assertEquals(getUserIdStream(userQuery).max().orElseThrow(), aggregated.<Integer>get(1));
//        assertEquals(getUserIdStream(userQuery).count(), aggregated.<Long>get(2));
//        OptionalDouble average = getUserIdStream(userQuery).average();
//        assertEquals(average.orElse(0), aggregated.<Number>get(3).doubleValue(), 1);
//        assertEquals(getUserIdStream(userQuery).sum(), aggregated.<Number>get(4).intValue());
//
//        List<Tuple> resultList = userQuery
//                .select(Arrays.asList(user.id.min(), user.randomNumber))
//                .where(user.valid.eq(true))
//                .groupBy(user.randomNumber)
//                .getList();
//
//        Map<Integer, Optional<User>> map = userQuery.users().stream()
//                .filter(User::isValid)
//                .collect(Collectors.groupingBy(User::getRandomNumber, Collectors.minBy(Comparator.comparingInt(User::getId))));
//
//        List<Tuple> fObjects = map.values().stream()
//                .map(user -> {
//                    Integer userId = user.map(User::getId).orElse(null);
//                    Integer randomNumber = user.map(User::getRandomNumber).orElse(null);
//                    return Tuples.of(userId, randomNumber);
//                })
//                .sorted(Comparator.comparing(a -> a.<Integer>get(0)))
//                .collect(Collectors.toList());
//        assertEquals(new HashSet<>(resultList), new HashSet<>(fObjects));
//
//        Tuple one = userQuery
//                .select(Collections.singletonList(user.id.sum()))
//                .where(user.valid.eq(true))
//                .requireSingle();
//
//        int userId = userQuery.users().stream()
//                .filter(User::isValid)
//                .mapToInt(User::getId)
//                .sum();
//        assertEquals(one.<Number>get(0).intValue(), userId);
//
//        Integer first = userQuery
//                .select(user.id)
//                .orderBy(user.id.desc())
//                .getFirst();
//        assertEquals(first, userQuery.users().get(userQuery.users().size() - 1).getId());
//
//        Long count = userQuery
//                .select(user.randomNumber.countDistinct())
//                .getSingle();
//        long count1 = userQuery.users()
//                .stream().mapToInt(User::getRandomNumber)
//                .distinct()
//                .count();
//        assertEquals(count1, count);
//    }
//
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testSelect(UserRepository userQuery) {
//        List<Tuple2<Integer, String>> qList = userQuery
//                .select(user.randomNumber, user.username)
//                .orderBy(user.username.asc(), user.randomNumber.asc())
//                .getList();
//
//        List<Tuple2<Integer, String>> fList = userQuery.users().stream()
//                .sorted(Comparator.comparingInt(User::getRandomNumber))
//                .sorted(Comparator.comparing(User::getUsername))
//                .map(it -> Tuples.of(it.getRandomNumber(), it.getUsername()))
//                .collect(Collectors.toList());
//
//        assertEquals(qList, fList);
//
//        qList = userQuery
//                .selectDistinct(user.randomNumber, user.username)
//                .getList();
//        fList = fList.stream().distinct().collect(Collectors.toList());
//        assertEquals(qList.size(), fList.size());
//        HashSet<Tuple2<Integer, String>> set = new HashSet<>(qList);
//        assertEquals(set.size(), fList.size());
//        assertEquals(set, new HashSet<>(fList));
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testTime(UserRepository userQuery) {
//        long start = System.currentTimeMillis();
//        userQuery
//                .orderBy(Arrays.asList(
//                        user.randomNumber.desc(),
//                        user.id.asc()
//                ))
//                .getList();
//        log.info("{}", System.currentTimeMillis() - start);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(UserQueryProvider.class)
//    public void testOrderBy(UserRepository userQuery) {
//        List<User> list = userQuery
//                .orderBy(Arrays.asList(
//                        user.randomNumber.desc(),
//                        user.id.asc()
//                ))
//
//                .getList();
//        ArrayList<User> sorted = new ArrayList<>(userQuery.users());
//        sorted.sort((a, b) -> Integer.compare(b.getRandomNumber(), a.getRandomNumber()));
//        Iterator<User> ia = list.iterator();
//        Iterator<User> ib = sorted.iterator();
//        while (ia.hasNext()) {
//            User ua = ia.next();
//            User ub = ib.next();
//            if (!Objects.equals(ua, ub)) {
//                boolean equals = ua.equals(ub);
//                log.info("{}", equals);
//            }
//        }
//        assertEquals(list, sorted);
//
//        list = userQuery
//                .orderBy(Arrays.asList(user.username.asc(),
//                        user.randomNumber.desc(),
//                        user.id.asc()))
//                .getList();
//        Comparator<User> comparator = Comparator.comparing(User::getUsername)
//                .thenComparing(Comparator.comparing(User::getRandomNumber).reversed())
//                .thenComparing(User::getId);
//        checkOrder(list, comparator);
//
//        list = userQuery
//                .orderBy(user.username.asc(),
//                        user.randomNumber.desc(),
//                        user.id.asc())
//                .getList();
//        checkOrder(list, comparator);
//
//        checkOrder(list, comparator);
//
//        list = userQuery
//                .orderBy(user.time.asc())
//                .getList();
//        checkOrder(list, Comparator.comparing(User::getTime));
//    }
//
//    public <T> void checkOrder(Iterable<T> list, Comparator<T> comparator) {
//        Iterator<T> iterator = list.iterator();
//        if (!iterator.hasNext()) {
//            return;
//        }
//        T pre = iterator.next();
//        while (iterator.hasNext()) {
//            T next = iterator.next();
//            int compare = comparator.compare(pre, next);
//            if (compare > 0) {
//                log.info("{}", "");
//            }
//            assertTrue(compare <= 0);
//            pre = next;
//        }
//    }
//
//    //
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testOrderBy2(UserRepository userQuery) {
////        List<User> list = userQuery
////                .orderBy(
////                        user.randomNumber.desc(),
////                        user.id.asc()
////                )
////                .getList();
////        Comparator<User> comparator = Comparator
////                .comparing(user.randomNumber)
////                .reversed()
////                .thenComparing(user.id);
////        checkOrder(list, comparator);
////
////        list = userQuery
////                .orderBy(
////                        user.username.asc(),
////                        user.randomNumber.desc(),
////                        user.id.asc()
////                )
////                .getList();
////
////        comparator = Comparator
////                .comparing(user.username)
////                .thenComparing(Comparator.comparing(user.randomNumber).reversed())
////                .thenComparing(user.id);
////        checkOrder(list, comparator);
////
////        list = userQuery
////                .orderBy(user.username)
////                .orderBy(user.randomNumber).desc()
////                .orderBy(user.id).asc()
////                .getList();
////        checkOrder(list, comparator);
////
////        list = userQuery
////                .orderBy(user.username, user.randomNumber, user.id).asc()
////                .getList();
////
////        comparator = Comparator
////                .comparing(user.username)
////                .thenComparing(user.randomNumber)
////                .thenComparing(user.id);
////        checkOrder(list, comparator);
////
////        list = userQuery
////                .orderBy(user.username, user.randomNumber, user.id).desc()
////                .getList();
////
////        comparator = Comparator
////                .comparing(user.username)
////                .thenComparing(user.randomNumber)
////                .thenComparing(user.id)
////                .reversed();
////        checkOrder(list, comparator);
////
////        list = userQuery
////                .orderBy(user.time)
////                .getList();
////        comparator = Comparator
////                .comparing(User::getTime);
////        checkOrder(list, comparator);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testPredicate(UserRepository userQuery) {
////        List<User> qList = userQuery
////                .where(not(user.randomNumber.ge(10)
////                        .or(user.randomNumber).lt(5)))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(user.username.ne("Jeremy Keynes").not())
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> (it.getUsername().equalsIgnoreCase("Jeremy Keynes")))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.eq("Jeremy Keynes"))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(
////                        not(user.username.eq("Jeremy Keynes")
////                                .or(user.id.eq(3)))
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
////                                || it.getId() == 3))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(not(user.username.eq("Jeremy Keynes")
////                        .and(user.id.eq(3))
////                ))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
////                                && it.getId() == 3))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testPredicate2(UserRepository userQuery) {
////        List<User> qList = userQuery
////                .where(or(
////                        user.randomNumber.ge(10),
////                        user.randomNumber.lt(5)
////                ).not())
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(user.username.eq("Jeremy Keynes").not())
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.eq("Jeremy Keynes")
////                        .not()
////                )
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(user.username.eq("Jeremy Keynes")
////                        .or(user.id.eq(3))
////                ))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
////                                || it.getId() == 3))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(and(
////                        user.username.eq("Jeremy Keynes"),
////                        user.id.eq(3)
////                ).not())
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getUsername().equalsIgnoreCase("Jeremy Keynes")
////                                && it.getId() == 3))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testGroupBy1(UserRepository userQuery) {
////        List<Tuple3<Boolean, Integer, Integer>> resultList = userQuery
////                .select(user.valid, user.randomNumber, User::getPid)
////                .groupBy(user.randomNumber, User::getPid, user.valid)
////                .getList();
////
////        List<Tuple3<Boolean, Integer, Integer>> resultList2 = userQuery
////                .select(user.valid, user.randomNumber, User::getPid)
////                .groupBy(user.randomNumber, User::getPid, user.valid)
////                .getList();
////        assertEquals(resultList, resultList2);
////        List<Tuple3<Boolean, Integer, Integer>> list = userQuery.users().stream()
////                .map(it -> Tuples.of(it.isValid(), it.getRandomNumber(), it.getPid()))
////                .distinct()
////                .collect(Collectors.toList());
////        assertEquals(sort(resultList), sort(list));
////
////
////    }
////
////    @NotNull
////    private static List<Tuple3<Boolean, Integer, Integer>> sort(List<Tuple3<Boolean, Integer, Integer>> resultList) {
////        return resultList.stream()
////                .sorted(Comparator.comparing(Object::toString))
////                .collect(Collectors.toList());
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testIsNull(UserRepository userQuery) {
////
////        List<User> qList = userQuery.where(get(User::getPid).isNotNull())
////                .orderBy(user.id)
////                .getList();
////
////        List<User> fList = userQuery.users().stream()
////                .filter(it -> it.getPid() != null)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(get(User::getPid).add(2).multiply(3).isNull())
////                .orderBy(user.id)
////                .getList();
////
////        fList = userQuery.users().stream()
////                .filter(it -> it.getPid() == null)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testOperatorIfNotNull(UserRepository userQuery) {
////        List<User> qList = userQuery.where(user.randomNumber).eq(10)
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.randomNumber).gtIfNotNull(null)
////                .where(user.randomNumber).geIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////
////
////        qList = userQuery.where(user.randomNumber.eqIfNotNull(null))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////        qList = userQuery.where(user.randomNumber.gtIfNotNull(null))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////        qList = userQuery.where(user.randomNumber.geIfNotNull(null))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////        qList = userQuery.where(user.randomNumber.ltIfNotNull(null))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////        qList = userQuery.where(user.randomNumber.leIfNotNull(null))
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, userQuery.users());
////
////
////        qList = userQuery.where(user.randomNumber.eqIfNotNull(20)).getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 20)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        Conjunction<User> predicate = user.randomNumber.eq(20).and(user.username).eqIfNotNull(null);
////        qList = userQuery.where(predicate).getList();
////
////        assertEquals(qList, fList);
////        qList = userQuery.where(user.randomNumber.gtIfNotNull(20)).getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() > 20)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////        qList = userQuery.where(user.randomNumber.geIfNotNull(20)).getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() >= 20)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////        qList = userQuery.where(user.randomNumber.ltIfNotNull(20))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 20)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////        qList = userQuery.where(user.randomNumber.leIfNotNull(20))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() <= 20)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).eq(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() == 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).eqIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).geIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() >= 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).gtIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() > 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).leIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() <= 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).ltIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).addIfNotNull(null).ltIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() < 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).addIfNotNull(3).ltIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() + 3 < 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).addIfNotNull(3).ltIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).multiplyIfNotNull(3).ltIfNotNull(50)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() * 3 < 50)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery
////                .where(User::getRandomUser).eqIfNotNull(null)
////                .where(user.id).addIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).subtractIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).multiplyIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).divideIfNotNull(null).eqIfNotNull(null)
////                .where(user.id).modIfNotNull(null).eqIfNotNull(null)
////                .where(user.username).eqIfNotNull(null)
////                .where(user.randomNumber).divideIfNotNull(3).ltIfNotNull(10)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream().filter(u -> u.getRandomNumber() / 3.0 < 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testOperator2(UserRepository userQuery) {
////        Predicate<User> isValid = user.valid;
////        userQuery.where(isValid
////                        .and(user.randomNumber).notBetween(10, 15)
////                        .and(user.id).mod(3).eq(0)
////                )
////                .getList();
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testOperator(UserRepository userQuery) {
////
////        Predicate<User> isValid = user.valid;
////        List<User> qList = userQuery.where(isValid)
////                .orderBy(user.id)
////                .getList();
////        List<User> validUsers = userQuery.users().stream().filter(user.valid)
////                .collect(Collectors.toList());
////        List<User> fList = validUsers;
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).eq(2))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() == 2)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(User::getPid).ne(2))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getPid() != 2)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).in(1, 2, 3))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> Arrays.asList(1, 2, 3).contains(user.getRandomNumber()))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).notIn(1, 2, 3))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> !Arrays.asList(1, 2, 3).contains(user.getRandomNumber()))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(User::getPid).isNull())
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() == null)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).ge(10))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() >= 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).gt(10))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() > 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).le(10))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() <= 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).lt(10))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() < 10)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).between(10, 15))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() >= 10 && user.getRandomNumber() <= 15)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).notBetween(10, 15))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getRandomNumber() < 10 || user.getRandomNumber() > 15)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid
////                        .and(user.randomNumber).notBetween(10, 15)
////                        .and(user.id).mod(3).eq(0)
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user ->
////                        !(user.getRandomNumber() >= 10 && user.getRandomNumber() <= 15)
////                        && user.getId() % 3 == 0)
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).ge(get(User::getPid)))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() >= user.getPid())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).gt(get(User::getPid)))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() > user.getPid())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).le(get(User::getPid)))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() <= user.getPid())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber).lt(get(User::getPid)))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream().filter(user -> user.getPid() != null && user.getRandomNumber() < user.getPid())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(isValid.and(user.randomNumber)
////                        .between(user.randomNumber, get(User::getPid)))
////                .orderBy(user.id)
////                .getList();
////        fList = validUsers.stream()
////                .filter(user -> user.getPid() != null && user.getRandomNumber() >= user.getRandomNumber() && user.getRandomNumber() <= user.getPid())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testPredicateAssembler(UserRepository userQuery) {
////
////        List<User> qList = userQuery.where(user.valid.eq(true)
////                        .and(User::getParentUser).user.username.eq(username))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                && user.getParentUser() != null
////                                && Objects.equals(user.getParentUser().getUsername(), username))
////                .collect(Collectors.toList());
////
////        assertEq(qList, fList);
////        qList = userQuery.where(user.valid).eq(true)
////                .where(User::getParentUser).get(User::getParentUser).user.username.eq(username)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                && user.getParentUser() != null
////                                && user.getParentUser().getParentUser() != null
////                                && Objects.equals(user.getParentUser().getParentUser().getUsername(), username))
////                .collect(Collectors.toList());
////        assertEq(qList, fList);
////
////        Path<User, Number> getUsername = user.randomNumber;
////        qList = userQuery.where(user.valid.eq(true)
////                        .and(getUsername).eq(10))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                && Objects.equals(user.getRandomNumber(), 10))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.valid.eq(true)
////                        .or(getUsername).eq(10))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                || Objects.equals(user.getRandomNumber(), 10))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.valid.eq(true)
////                        .and(getUsername).ne(10))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                && !Objects.equals(user.getRandomNumber(), 10))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.valid.eq(true)
////                        .or(getUsername).ne(10))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                || !Objects.equals(user.getRandomNumber(), 10))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        Date time = userQuery.users().get(20).getTime();
////
////        TypedExpression<User, Boolean> or = user.valid.eq(true)
////                .or(
////                        Paths.get(User::getParentUser)
////                                .user.username
////                                .eq(username)
////                                .and(user.time)
////                                .ge(time));
////        qList = userQuery.where(or)
////                .orderBy(user.id)
////                .getList();
////
////        List<User> jeremy_keynes = userQuery
////                .fetch(User::getParentUser)
////                .where(user.valid.eq(true)
////                        .or(Paths.get(User::getParentUser)
////                                .user.username.eq(username)
////                                .and(user.time).ge(time)
////                        ))
////                .orderBy(user.id)
////                .getList();
////
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                || (user.getParentUser() != null
////                                    && Objects.equals(user.getParentUser().getUsername(), username)
////                                    && user.getTime().getTime() >= time.getTime()))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////        assertEquals(qList, jeremy_keynes);
////
////        qList = userQuery.where(user.valid.eq(true)
////                        .and(user.randomNumber).ne(5))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                && user.getRandomNumber() != 5)
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.valid.eq(true)
////                        .or(user.randomNumber).eq(5))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.isValid()
////                                || user.getRandomNumber() == 5)
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.randomNumber.ne(6)
////                        .or(user.valid).eq(false))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() != 6
////                                || !user.isValid())
////                .collect(Collectors.toList());
////
////        assertEquals((qList), (fList));
////
////        qList = userQuery.where(user.randomNumber.ne(6)
////                        .and(User::getParentUser).user.valid.eq(true))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() != 6
////                                && (user.getParentUser() != null && user.getParentUser().isValid()))
////                .collect(Collectors.toList());
////
////        assertEquals((qList), (fList));
////
////        qList = userQuery.where(user.randomNumber.ne(6)
////                        .and(User::getParentUser).user.valid.ne(true))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() != 6
////                                && (user.getParentUser() != null && !user.getParentUser().isValid()))
////                .collect(Collectors.toList());
////
////        assertEquals((qList), (fList));
////
////        qList = userQuery.where(user.randomNumber.ne(6)
////                        .or(User::getParentUser).user.valid.ne(true))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() != 6
////                                || (user.getParentUser() != null && !user.getParentUser().isValid()))
////                .collect(Collectors.toList());
////
////        assertEquals((qList), (fList));
////
////        qList = userQuery.where(not(user.randomNumber.ge(10)
////                        .or(user.randomNumber).lt(5)
////                ))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 || it.getRandomNumber() < 5))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(user.randomNumber.ge(10)
////                                .and(user.randomNumber).le(15)
////                        )
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 && it.getRandomNumber() <= 15))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(
////                        user.randomNumber.ge(10)
////                                .and(user.username).eq(username)
////                ))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 && it.getUsername().equals(username)))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(user.randomNumber.ge(10)
////                                .or(user.username).eq(username)
////                        )
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> !(it.getRandomNumber() >= 10 || it.getUsername().equals(username)))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(user.randomNumber.ge(10)
////                        .and(user.username).eq(username))
////                        .not()
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> (it.getRandomNumber() >= 10 && it.getUsername().equals(username)))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(not(user.randomNumber.ge(10)
////                        .or(user.username).eq(username))
////                        .not()
////                )
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(it -> it.getRandomNumber() >= 10 || it.getUsername().equals(username))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////    }
////
////    private static void assertEq(List<User> qList, List<User> fList) {
////        assertEquals(qList.size(), fList.size());
////        for (int i = 0; i < qList.size(); i++) {
////            if (!qList.get(i).equals(fList.get(i))) {
////                throw new RuntimeException();
////            }
////        }
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    void testSubQuery(UserRepository userQuery) {
////        Date time = userQuery.users().get(20).getTime();
////
////        userQuery
////                .fetch(User::getParentUser)
////                .where(user.valid.eq(true)
////                        .or(Paths.get(User::getParentUser)
////                                .user.username.eq(username)
////                                .and(user.time).ge(time)
////                        ))
////                .count();
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testNumberPredicateTester(UserRepository userQuery) {
////        List<User> list = userQuery
////                .where(user.randomNumber.add(2).ge(4))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() + 2 >= 4)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.subtract(2).ge(4))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() - 2 >= 4)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.multiply(2).ge(4))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() * 2 >= 4)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.divide(2).ge(4))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() / 2 >= 4)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.mod(2).ge(1))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() % 2 == 1)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        ///
////        list = userQuery
////                .where(user.randomNumber.add(get(user.id)).ge(40))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() + user.getId() >= 40)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.subtract(get(user.id)).ge(40))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() - user.getId() >= 40)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.multiply(get(user.id)).ge(40))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getRandomNumber() * user.getId() >= 40)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.divide(get(user.id)).ge(40))
////                .where(user.id).ne(0)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getId() != 0 && user.getRandomNumber() / user.getId() >= 40)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////        list = userQuery
////                .where(user.randomNumber.mod(get(user.id)).ge(10))
////                .where(user.id).ne(0)
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getId() != 0 && user.getRandomNumber() % user.getId() >= 10)
////                .collect(Collectors.toList());
////
////        assertEquals(list, fList);
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testStringPredicateTester(UserRepository userQuery) {
////        String username = "Roy Sawyer";
////
////        List<User> qList = userQuery.where(user.username.substring(2).eq("eremy Keynes"))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().substring(1).equals("eremy Keynes"))
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.substring(1, 1).eq("M"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().charAt(0) == 'M')
////                .collect(Collectors.toList());
////
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.trim().like(username))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().trim().startsWith(username))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.trim().lower().notContains("i"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> !user.getUsername().toLowerCase().contains("i"))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.length().eq(username.length()))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().length() == username.length())
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.startsWith("M"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().startsWith("M"))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.endsWith("s"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().endsWith("s"))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.lower().contains("s"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().toLowerCase().contains("s"))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////
////        qList = userQuery.where(user.username.upper().contains("S"))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getUsername().toUpperCase().contains("S"))
////                .collect(Collectors.toList());
////        assertEquals(qList, fList);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testResultBuilder(UserRepository userQuery) {
////        List<User> resultList = userQuery
////                .orderBy(user.id)
////                .getList(5, 10);
////        List<User> subList = userQuery.users().subList(5, 5 + 10);
////        assertEquals(resultList, subList);
////
////        List<Integer> userIds = userQuery.select(user.id)
////                .orderBy(user.id)
////                .getList(5, 10);
////        List<Integer> subUserIds = userQuery.users().subList(5, 5 + 10)
////                .stream().map(user.id)
////                .collect(Collectors.toList());
////
////        assertEquals(userIds, subUserIds);
////
////        resultList = userQuery.where(user.id.in())
////                .orderBy(user.id)
////                .getList();
////        assertEquals(resultList.size(), 0);
////
////        resultList = userQuery.where(user.id.notIn())
////                .orderBy(user.id)
////                .getList();
////        assertEquals(resultList, userQuery.users());
////
////        long count = userQuery.count();
////        assertEquals(count, userQuery.users().size());
////
////        User first = userQuery
////                .orderBy(user.id)
////                .getFirst();
////        assertEquals(first, userQuery.users().get(0));
////
////        first = userQuery.where(user.id.eq(0)).requireSingle();
////        assertEquals(first, userQuery.users().get(0));
////
////        first = userQuery
////                .orderBy(user.id)
////                .getFirst(10);
////        assertEquals(first, userQuery.users().get(10));
////
////        assertThrowsExactly(IllegalStateException.class, userQuery::requireSingle);
////        assertThrowsExactly(NullPointerException.class, () -> userQuery.where(user.id.eq(-1)).requireSingle());
////
////        assertTrue(userQuery.exist());
////        assertTrue(userQuery.exist(userQuery.users().size() - 1));
////        assertFalse(userQuery.exist(userQuery.users().size()));
////
////        List<UserModel> userModels = userQuery.select(UserModel.class)
////                .orderBy(user.id)
////                .getList();
////
////        List<Map<String, Object>> l0 = userQuery.users().stream()
////                .map(UserModel::new)
////                .map(UserInterface::asMap)
////                .collect(Collectors.toList());
////
////        List<Map<String, Object>> l1 = userQuery.select(UserInterface.class)
////                .orderBy(user.id)
////                .getList()
////                .stream()
////                .map(UserInterface::asMap)
////                .collect(Collectors.toList());
////
////        List<Map<String, Object>> l2 = userModels.stream()
////                .map(UserInterface::asMap)
////                .collect(Collectors.toList());
////
////        assertEquals(l0, l1);
////        assertEquals(l0, l2);
////
////        User user = userQuery.users().stream()
////                .filter(it -> it.getParentUser() != null)
////                .findAny()
////                .orElse(userQuery.users().get(0));
////        UserInterface userInterface = userQuery.select(UserInterface.class)
////                .where(user.id).eq(user.getId())
////                .getSingle();
////
////        assertEquals(userInterface.getId(), user.getId());
////        assertEquals(userInterface.getRandomNumber(), user.getRandomNumber());
////        assertEquals(userInterface.getUsername(), user.getUsername());
////        assertEquals(userInterface.getPid(), user.getPid());
////        assertEquals(userInterface.isValid(), user.isValid());
////        assertEquals(userInterface.getParentUsername(), user.getParentUser().getUsername());
////
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testSlice(UserRepository userQuery) {
////        Slice<String> slice = userQuery.select(user.username)
////                .where(User::getParentUser).user.randomNumber.eq(10)
////                .groupBy(user.username)
////                .orderBy(user.username)
////                .slice(2, 10);
////        log.info("{}", slice);
////        long count = userQuery.users().stream()
////                .filter(user -> user.getParentUser() != null && user.getParentUser().getRandomNumber() == 10)
////                .map(user.username)
////                .distinct()
////                .count();
////
////        List<String> names = userQuery.users().stream()
////                .filter(user -> user.getParentUser() != null && user.getParentUser().getRandomNumber() == 10)
////                .map(user.username)
////                .sorted()
////                .distinct()
////                .skip(2)
////                .limit(10)
////                .toList();
////        assertEquals(slice.total(), count);
////        assertEquals(new HashSet<>(slice.data()), new HashSet<>(names));
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    void projection(UserRepository userQuery) throws JsonProcessingException {
////        List<UserInterface> list0 = userQuery.select(UserInterface.class)
////                .getList();
////        List<UserInterface> list1 = userQuery.select(UserInterface.class)
////                .getList();
////
////        log.info("{}", JsonSerializablePredicateValueTest.mapper.writeValueAsString(list0.get(0)));
////
////        assertEquals(list0, list1);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    void testInterfaceSelect(UserRepository userQuery) {
////        UserInterface list = userQuery.select(UserInterface.class)
////                .getFirst();
////        String string = list.toString();
////        log.info("{}", string);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testAttr(UserRepository userQuery) {
////        User first = userQuery.orderBy(user.id.desc()).getFirst();
////        ArrayList<User> users = new ArrayList<>(userQuery.users());
////        users.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
////        User f = users.stream().findFirst().orElse(null);
////        assertEquals(first, f);
////
////        first = userQuery.orderBy(user.username.desc(), user.id.asc())
////                .getFirst();
////
////        users = new ArrayList<>(userQuery.users());
////        users.sort((a, b) -> b.getUsername().compareTo(a.getUsername()));
////        f = users.stream().findFirst().orElse(null);
////        assertEquals(first, f);
////
////        first = userQuery.orderBy(user.valid.desc(), user.id.asc()).getFirst();
////        users = new ArrayList<>(userQuery.users());
////        users.sort((a, b) -> Boolean.compare(b.isValid(), a.isValid()));
////        f = users.stream().findFirst().orElse(null);
////        assertEquals(first, f);
////
////        first = userQuery
////                .where(user.valid.eq(true))
////                .orderBy(user.id)
////                .getFirst();
////
////        f = userQuery.users().stream()
////                .filter(user.valid)
////                .findFirst()
////                .orElse(null);
////        assertEquals(first, f);
////
////        List<User> resultList = userQuery
////                .where(Paths.get(User::getParentUser).user.valid
////                        .eq(true))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> user.getParentUser() != null && user.getParentUser().isValid())
////                .collect(Collectors.toList());
////
////        assertEquals(resultList, fList);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testWhere(UserRepository userQuery) {
////        List<User> resultList = userQuery
////                .where(Paths.get(User::getParentUser).user.username.eq(username))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> user.getParentUser() != null && username.equals(user.getParentUser().getUsername()))
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery
////                .where(Paths.get(User::getParentUser).user.username.ne(username))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> user.getParentUser() != null && !username.equals(user.getParentUser().getUsername()))
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery
////                .where(user.username.ne(username))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> !username.equals(user.getUsername()))
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery
////                .where(user.username.ne(username))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> !username.equals(user.getUsername()))
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery
////                .where(user.username.ne(username))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> !username.equals(user.getUsername()))
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testPathBuilder(UserRepository userQuery) {
////        List<User> resultList = userQuery.where(Paths.get(User::getParentUser)
////                        .get(User::getParentUser).user.username.eq(username))
////                .orderBy(user.id)
////                .getList();
////        List<User> fList = userQuery.users().stream()
////                .filter(user -> {
////                    User p = user.getParentUser();
////                    return p != null && p.getParentUser() != null && username.equals(p.getParentUser().getUsername());
////                })
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery.where(Paths.get(User::getParentUser)
////                        .user.randomNumber.eq(5))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> {
////                    User p = user.getParentUser();
////                    return p != null && p.getRandomNumber() == 5;
////                })
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////
////        resultList = userQuery.where(Paths.get(User::getParentUser)
////                        .user.randomNumber.eq(5))
////                .orderBy(user.id)
////                .getList();
////        fList = userQuery.users().stream()
////                .filter(user -> {
////                    User p = user.getParentUser();
////                    return p != null && p.getRandomNumber() == 5;
////                })
////                .collect(Collectors.toList());
////        assertEquals(resultList, fList);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void testBigNum(UserRepository userQuery) {
////        List<User> users = userQuery.where(get(User::getTimestamp).eq(Double.MAX_VALUE))
////                .getList();
////        log.info("{}", users);
////    }
////
////    @ParameterizedTest
////    @ArgumentsSource(UserQueryProvider.class)
////    public void subQueryTest(UserRepository userQuery) {
////        TypedExpression<User, List<Integer>> ids = userQuery
////                .select(user.id).where(user.id)
////                .in(1, 2, 3)
////                .asSubQuery();
////
////        List<User> result = userQuery.where(user.id).in(ids).getList();
////        log.info("{}", result);
////    }
////
//    private IntStream getUserIdStream(UserRepository userQuery) {
//        return userQuery.users().stream().mapToInt(User::getRandomNumber);
//    }
//
//    // @Test
//    // void test() {
//    //     test(UserQueryProvider.jpaQuery());
//    //     test(UserQueryProvider.jdbcQuery());
//    // }
//
//    // private static void test(Query query) {
//    //     Select<UserSummary> from = query.from(UserSummary.class);
//    //     List<UserSummary> list = from.where(UserSummary::getMaxRandomNumber).le(33).getList();
//    //     Map<String, List<User>> map = userQuery.users().stream().collect(Collectors.groupingBy(user.username));
//    //     Map<String, UserSummary> summaryMap = new HashMap<>();
//    //     map.forEach((k, v) -> {
//    //         UserSummary summary = new UserSummary();
//    //         summary.setCount((long) v.size());
//    //         summary.setUsername(k);
//    //         int maxRandomNumber = Integer.MIN_VALUE;
//    //         for (User user : v) {
//    //             maxRandomNumber = Math.max(maxRandomNumber, user.getRandomNumber());
//    //         }
//    //         summary.setMaxRandomNumber(maxRandomNumber);
//    //         summaryMap.put(k, summary);
//    //     });
//    //     for (UserSummary summary : list) {
//    //         UserSummary s = summaryMap.get(summary.getUsername());
//    //         assertEquals(s, summary);
//    //         assertTrue(summary.getMaxRandomNumber() <= 33);
//    //     }
//    // }
//}
