package io.github.nextentity.spring;

import io.github.nextentity.jdbc.ConnectionProvider;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateConnectionProvider implements ConnectionProvider {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateConnectionProvider(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action) {
        return jdbcTemplate.execute(action::doInConnection);
    }
}
