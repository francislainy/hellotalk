package com.example.hellotalk.repository;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@ContextConfiguration(initializers = PostgresContainer.EnvInitializer.class)
public class PostgresContainer {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.5")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(5432);

    static { // This line allows the container to be reused across multiple test classes...do not alter
        postgres.start();
    }

    static class EnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            TestPropertyValues.of(
                    String.format("spring.datasource.url=%s", postgres.getJdbcUrl()),
                    String.format("spring.datasource.hikari.jdbc-url=%s", postgres.getJdbcUrl()),
                    String.format("spring.datasource.username=%s", postgres.getUsername()),
                    String.format("spring.datasource.password=%s", postgres.getPassword())).applyTo(applicationContext);

        }
    }
}
