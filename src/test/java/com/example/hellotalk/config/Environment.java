package com.example.hellotalk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class Environment {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.user}")
    private String databaseUser;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.schema}")
    private String databaseSchema;
}
