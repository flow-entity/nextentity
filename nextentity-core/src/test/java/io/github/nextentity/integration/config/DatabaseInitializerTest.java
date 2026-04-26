package io.github.nextentity.integration.config;

import io.github.nextentity.core.event.EntityEventType;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

class DatabaseInitializerTest {

    @Test
    void init_ShouldExecuteDdlAndLoadFixtures() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        DatabaseEnvironmentVariables dbEnv = mock(DatabaseEnvironmentVariables.class);
        when(applicationContext.getBean(DatabaseEnvironmentVariables.class)).thenReturn(dbEnv);
        when(dbEnv.ddl()).thenReturn(List.of("DDL-1", "DDL-2"));

        IntegrationTestApplication.DatabaseInitializer initializer =
                new IntegrationTestApplication.DatabaseInitializer(jdbcTemplate, entityManager, applicationContext);

        initializer.init();

        verify(jdbcTemplate).execute("DDL-1");
        verify(jdbcTemplate).execute("DDL-2");
        verify(applicationContext).getBean(DatabaseEnvironmentVariables.class);
        verify(entityManager, atLeastOnce()).persist(any());
        verify(entityManager, atLeastOnce()).flush();
        verify(entityManager, atLeastOnce()).clear();
    }

    @Test
    void reset_ShouldReloadFixturesWithoutExecutingDdl() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        EntityManager entityManager = mock(EntityManager.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        IntegrationTestApplication.DatabaseInitializer initializer =
                new IntegrationTestApplication.DatabaseInitializer(jdbcTemplate, entityManager, applicationContext);

        initializer.changeTracker().on(Employee.class, EntityEventType.AFTER_UPDATED, List.of());

        initializer.reset();

        verify(applicationContext, never()).getBean(DatabaseEnvironmentVariables.class);
        verify(jdbcTemplate).execute("DELETE FROM employee");
        verify(jdbcTemplate).execute("DELETE FROM department");
        verify(jdbcTemplate, never()).execute("DELETE FROM sales_order");
        verify(jdbcTemplate, never()).execute("DELETE FROM customer");
        verify(jdbcTemplate, never()).execute("DELETE FROM category");
        verify(jdbcTemplate, never()).execute("DELETE FROM lockable_entity");
        verify(jdbcTemplate, never()).execute("DELETE FROM auto_increment_entity");
        verify(entityManager, atLeastOnce()).persist(any());
        verify(entityManager, atLeastOnce()).flush();
        verify(entityManager, atLeastOnce()).clear();
    }
}
