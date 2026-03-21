package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.Users;
import io.github.nextentity.spring.integration.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HuangChengwei
 */
public class DbInitializer extends Transaction {
    List<User> allUsers;

    public DbInitializer(DbConfig config) {
        super(config);
    }

    public synchronized List<User> initialize() {
        doInJdbcTransaction(() -> {
            try {
                UserRepository query = config.getJdbc();
                resetData(config.getJdbcTemplate(), query);
                allUsers = queryAllUsers(query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return allUsers;
    }

    private void resetData(JdbcTemplate jdbcTemplate, UserRepository query) {
        String sql = config.getSetPidNullSql();
        jdbcTemplate.execute(sql);
        query.deleteAll(queryAllUsers(query));
        query.insertAll(Users.getUsers());
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
