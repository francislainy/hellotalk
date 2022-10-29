package com.example.hellotalk.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * The type Postgresql container.
 * Please refer : https://www.testcontainers.org/
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostgresSqlContainer extends PostgreSQLContainer<PostgresSqlContainer>{

    private static final String IMAGE_VERSION = "postgres:13.5";

    private static PostgresSqlContainer container;

    private PostgresSqlContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresSqlContainer getInstance() {
        if (container == null) {
            container = new PostgresSqlContainer().withReuse(true);//.withInitScript("db-table-structure.sql");
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
