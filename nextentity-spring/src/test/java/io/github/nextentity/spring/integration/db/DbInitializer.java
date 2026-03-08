package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.Users;
import io.github.nextentity.spring.integration.entity.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HuangChengwei
 * @since 2024-04-10 15:43
 */
public class DbInitializer extends Transaction {
    List<User> allUsers;

    public DbInitializer(DbConfig config) {
        this.config = config;
    }

    public synchronized List<User> initialize() {
        doInTransaction(connection -> {
            try {
                UserRepository query = config.getJdbc();
//                resetData(connection, query);
                allUsers = queryAllUsers(query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return allUsers;
    }

    private void resetData(Connection connection, UserRepository query) throws SQLException {
        String sql = config.getSetPidNullSql();
        //noinspection SqlSourceToSinkFlow
        connection.createStatement().executeUpdate(sql);
        query.deleteAll(queryAllUsers(query));
        query.deleteAll(Users.getUsers());
    }

    private List<User> queryAllUsers(UserRepository query) {
        List<User> list = query.query().orderBy(User::getId).asc().getList();
        Map<Integer, User> map = list.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        for (User user : list) {
            Integer pid = user.getPid();
            User p = map.get(pid);
            user.setParentUser(p);
            user.setRandomUser(map.get(user.getRandomNumber()));
            user.setTestUser(map.get(user.getTestInteger()));
        }
        return list;
    }


}
