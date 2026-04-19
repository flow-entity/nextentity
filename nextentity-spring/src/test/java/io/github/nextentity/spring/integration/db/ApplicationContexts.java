package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.db.env.DatabaseEnvironmentVariables;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 管理集成测试的 Spring Boot 应用程序上下文。
/// 为每个数据库（MySQL、PostgreSQL）创建并保持上下文。
///
/// @author HuangChengwei
public class ApplicationContexts {

    private static final Map<String, ConfigurableApplicationContext> CONTEXTS_MAP = new HashMap<>();

    public static List<ConfigurableApplicationContext> contexts(String... names) {
        return DatabaseEnvironmentVariables.dbs(names)
                .stream().map(ApplicationContexts::getApplicationContext)
                .toList();
    }

    private synchronized static @NonNull ConfigurableApplicationContext getApplicationContext(DatabaseEnvironmentVariables dbEnv) {
        ConfigurableApplicationContext context = CONTEXTS_MAP.get(dbEnv.getName());
        if (context == null) {
            context = SpringApplication.run(
                    SpringIntegrationTestApplication.class,
                    "--application.env.name=" + dbEnv.getName(),
                    "--spring.datasource.url=" + dbEnv.getJdbcUrl(),
                    "--spring.datasource.username=" + dbEnv.getUsername(),
                    "--spring.datasource.password=" + dbEnv.getPassword(),
                    "--spring.datasource.driver-class-name=" + dbEnv.getDriverClassName(),
                    "--spring.jpa.hibernate.ddl-auto=update",
                    "--spring.jpa.show-sql=false",
                    "--spring.jpa.properties.hibernate.format_sql=true"
            );
            CONTEXTS_MAP.put(dbEnv.getName(), context);
        }
        return context;
    }
}