package io.github.nextentity.examples.repository;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.NextEntityExampleApplication;
import io.github.nextentity.spring.AbstractRepository;
import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.function.Supplier;

public class BaseRepository<T, ID> extends AbstractRepository<T, ID> {
    private final EntityManager entityManager;

    protected BaseRepository() {
        ApplicationContext context = NextEntityExampleApplication.context();
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        EntityManager entityManager = context.getBean(EntityManager.class);
        super(jdbcTemplate, jpa(entityManager, jdbcTemplate));
        this.entityManager = entityManager;
    }

    @Override
    public Select<T> query() {
        return super.query();
    }

    @Override
    public <X> X doInTransaction(Supplier<X> command) {
        return super.doInTransaction(command);
    }

    public void doInTransaction(Runnable command) {
        super.doInTransaction(() -> {
            command.run();
            return null;
        });
    }

    protected EntityManager entityManager() {
        return entityManager;
    }
}
