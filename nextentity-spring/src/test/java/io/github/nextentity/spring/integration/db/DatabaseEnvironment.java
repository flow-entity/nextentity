package io.github.nextentity.spring.integration.db;

import io.github.nextentity.spring.integration.db.env.DatabaseEnvironmentVariables;
import org.springframework.beans.factory.annotation.Value;

/// 所有数据库的环境变量聚合器。
///
/// @author HuangChengwei
public class DatabaseEnvironment {

    private DatabaseEnvironmentVariables variables;

    @Value("${application.env.name:}")
    public void setVariables(String name) {
        variables = DatabaseEnvironmentVariables.DBS.stream()
                .filter(db -> db.getName().equals(name))
                .findAny()
                .orElseThrow();
    }


    public DatabaseEnvironmentVariables get() {
        return variables;
    }

}