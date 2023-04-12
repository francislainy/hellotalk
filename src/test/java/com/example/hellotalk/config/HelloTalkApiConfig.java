package com.example.hellotalk.config;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.utils.ScenarioContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {"classpath:environments/${env:dev}.properties"})
@ComponentScan(basePackages = {"com.example.hellotalk.config", "com.example.hellotalk.steps"})
public class HelloTalkApiConfig {

    JdbcTemplate jdbcTemplate;

    @Bean
    RestClient restClient(Environment env) {
        return new RestClient(env);
    }

    @Bean
    Environment environment() {
        return new Environment();
    }

    @Bean
    public DataSource dataSource(Environment env) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.postgresql.Driver());
        dataSource.setUrl(env.getDatabaseUrl());
        dataSource.setUsername(env.getDatabaseUser());
        dataSource.setPassword(env.getDatabasePassword());
        dataSource.setSchema(env.getDatabaseSchema());
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Bean
    DBClient dbClient() {
        return new DBClient();
    }

    @Bean
    ScenarioContext scenarioContext(Environment env) {
        return new ScenarioContext(env);
    }
}
