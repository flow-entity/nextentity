package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author HuangChengwei
 * @since 2024-04-10 15:37
 */
public class DbConfig {

    private EntityManager entityManager;

    private List<User> users;
    private UserRepository jdbc, jpa;
    private final String setPidNullSql;
    private final JdbcTemplate jdbcTemplate;

    public DbConfig(DataSource getDataSource,
                    EntityManager entityManager,
                    String setPidNullSql) {
        this.entityManager = entityManager;
        this.setPidNullSql = setPidNullSql;
        this.jdbcTemplate = new JdbcTemplate(getDataSource);
        this.jdbc = new UserRepository(new JdbcTemplate(getDataSource));
        this.jpa = new UserRepository(entityManager, jdbcTemplate);

        this.users = new DbInitializer(this).initialize();

        Transaction transaction = new Transaction(this);
        jdbc.setUsers(users);
        jdbc.setTransaction(transaction);
        jpa.setUsers(users);
        jpa.setTransaction(transaction);
    }

    public Stream<UserRepository> repositories() {
        return Stream.of(jdbc, jpa);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<User> getUsers() {
        return users;
    }

    public UserRepository getJdbc() {
        return jdbc;
    }

    public UserRepository getJpa() {
        return jpa;
    }

    public String getSetPidNullSql() {
        return setPidNullSql;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
