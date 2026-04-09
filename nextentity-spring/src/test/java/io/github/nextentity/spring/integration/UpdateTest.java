package io.github.nextentity.spring.integration;

import io.github.nextentity.api.EntityQuery;
import io.github.nextentity.spring.integration.db.UserQueryProvider;
import io.github.nextentity.spring.integration.db.UserRepository;
import io.github.nextentity.spring.integration.entity.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UpdateTest {

    EntityQuery<User> query(UserRepository updater) {
        return updater.query();
    }

    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void insert(UserRepository userUpdater) {
        userUpdater.doInTransaction(() -> doInsert(userUpdater));
        userUpdater.clear();
    }

    private void doInsert(UserRepository userUpdater) {
        List<User> existUsers = query(userUpdater).where(User::getId).in(10000000, 10000001, 10000002)
                .list();
        if (!existUsers.isEmpty()) {
            userUpdater.delete(existUsers);
        }
        List<User> exist = query(userUpdater).where(User::getId).in(10000000, 10000001, 10000002).list();
        assertTrue(exist.isEmpty());

        User newUser = newUser(10000000);
        userUpdater.insert(newUser);
        User single = query(userUpdater).where(User::getId).eq(10000000).single();
        assertEquals(newUser, single);
        List<User> users = Arrays.asList(newUser(10000001), newUser(10000002));
        userUpdater.insert(users);
        List<User> userList = query(userUpdater).where(User::getId).in(10000001, 10000002).list();
        assertEquals(userList, new ArrayList<>(users));
        userUpdater.delete(newUser);
        userUpdater.delete(users);
        exist = query(userUpdater).where(User::getId).in(10000000, 10000001, 10000002).list();
        assertTrue(exist.isEmpty());
    }

    private static User newUser(int id) {
        return Users.newUser(id, "Username-" + id, new Random());
    }


    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void update(UserRepository userUpdater) {
        userUpdater.doInTransaction(() -> testUpdate(userUpdater));
        userUpdater.clear();
    }

    private void testUpdate(UserRepository userUpdater) {
        List<User> users = query(userUpdater).where(User::getId).in(1, 2, 3).list();
        for (User user : users) {
            user.setRandomNumber(user.getRandomNumber() + 1);
        }
        userUpdater.update(users);
        assertEquals(users, query(userUpdater).where(User::getId).in(1, 2, 3).list());

        for (User user : users) {
            user.setRandomNumber(user.getRandomNumber() + 1);
            userUpdater.update(user);
        }
        assertEquals(users, query(userUpdater).where(User::getId).in(1, 2, 3).list());
    }

}

