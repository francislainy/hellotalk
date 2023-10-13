package com.example.hellotalk.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class BasePostgresConfig {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("latest"));
        postgres.start();
    }

}

