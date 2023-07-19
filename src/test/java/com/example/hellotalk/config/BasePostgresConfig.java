package com.example.hellotalk.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;

@ActiveProfiles("test")
public class BasePostgresConfig {

    @Container
    public static PostgresSqlContainer postgres = PostgresSqlContainer.getInstance();

    @BeforeAll
    public static void init() {
        postgres.start();
    }
}
