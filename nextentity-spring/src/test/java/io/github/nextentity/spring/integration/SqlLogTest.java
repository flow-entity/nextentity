package io.github.nextentity.spring.integration;

import io.github.nextentity.spring.integration.db.DbConfig;
import io.github.nextentity.spring.integration.db.DbConfigs;
import io.github.nextentity.spring.integration.entity.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Test to verify SQL logging is working.
 */
public class SqlLogTest {

    private static final Logger log = LoggerFactory.getLogger(SqlLogTest.class);

    @Test
    void testSqlLogging() {
        log.info("=== Starting SQL logging test ===");

        for (DbConfig config : DbConfigs.CONFIGS) {
            log.info("Testing with JPA repository: {}", config.getJpa());

            // Test JPA (Hibernate) SQL logging
            List<User> users = config.getJpa()
                    .where(User::getUsername).eq("User1")
                    .getList();

            log.info("Found {} users", users.size());
        }

        log.info("=== SQL logging test completed ===");
    }
}
