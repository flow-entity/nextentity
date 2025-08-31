package io.github.nextentity.test.db;

import io.github.nextentity.core.util.Maps;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jdbc.JdbcQueryExecutor.QuerySqlBuilder;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.schema.Action;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Map;

import static org.hibernate.cfg.AvailableSettings.*;

/**
 * @author HuangChengwei
 * @since 2024-04-10 10:45
 */
public class Postgresql implements DbConfigProvider {

    private final String user = "postgres";
    private final String password = "root";
    private final String url = "jdbc:postgresql://localhost:5432/nextentity";


    @Override
    public QuerySqlBuilder querySqlBuilder() {
        return new PostgresqlQuerySqlBuilder();
    }

    @Override
    public JdbcUpdateSqlBuilder updateSqlBuilder() {
        return new PostgresqlUpdateSqlBuilder();
    }

    @Override
    public DataSource getDataSource() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setUrl(url);
        source.setUser(user);
        source.setPassword(password);
        return source;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        Map<String, Object> properties = Maps.<String, Object>hashmap()
                .put(JAKARTA_JDBC_DRIVER, "org.postgresql.Driver")
                .put(JAKARTA_JDBC_URL, url)
                .put(USER, user)
                .put(PASS, password)
                .put(DIALECT_RESOLVERS, StandardDialectResolver.class.getName())
                .put(HBM2DDL_AUTO, Action.UPDATE)
                .put(SHOW_SQL, false)
                .put(FORMAT_SQL, false)
                .put(QUERY_STARTUP_CHECKING, false)
                .put(GENERATE_STATISTICS, false)
//                .put(USE_REFLECTION_OPTIMIZER, false)
                .put(USE_SECOND_LEVEL_CACHE, false)
                .put(USE_QUERY_CACHE, false)
                .put(USE_STRUCTURED_CACHE, false)
                .put(STATEMENT_BATCH_SIZE, 2000)
                .put(PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class)
                .build();
        return new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new HibernateUnitInfo(), properties);
    }

    @Override
    public String setPidNullSql() {
        return "update \"user\" set pid = null";
    }

    @Override
    public String name() {
        return "postgresql";
    }
}
