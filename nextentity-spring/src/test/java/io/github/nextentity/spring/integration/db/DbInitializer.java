package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.Users;
import io.github.nextentity.spring.integration.entity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HuangChengwei
 */
@Component
public class DbInitializer implements InitializingBean {

    List<User> allUsers;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    DatabaseEnvironment env;
    @Autowired
    @Qualifier("jdbcUserRepository")
    UserRepository userRepository;

    @Transactional
    public synchronized void initialize() {
        try {
            UserRepository query = userRepository;
            resetData();
            allUsers = queryAllUsers(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resetData() {
        UserRepository query = userRepository;
        String sql = env.get().getPidNullSql();
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


    @Override
    public void afterPropertiesSet() {
        initialize();
    }
}
