package com.example.hellotalk.config;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.dbclient.DBClient;
import com.example.hellotalk.utils.ScenarioContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {"classpath:properties/${env:dev}.properties"})
@ComponentScan(basePackages = {"com.example.hellotalk.config", "com.example.hellotalk.steps"})
public class ApiConfig {

    JdbcTemplate jdbcTemplate;

    @Bean
    RestClient restClient(AppConfigProperties appConfigProperties) {
        return new RestClient(appConfigProperties);
    }

    @Bean
    AppConfigProperties appConfigProperties() {
        return new AppConfigProperties();
    }

    @Bean
    public DataSource dataSource(AppConfigProperties appConfigProperties) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.postgresql.Driver());
        dataSource.setUrl(appConfigProperties.getDatabaseUrl());
        dataSource.setUsername(appConfigProperties.getDatabaseUser());
        dataSource.setPassword(appConfigProperties.getDatabasePassword());
        dataSource.setSchema(appConfigProperties.getDatabaseSchema());
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
    ScenarioContext scenarioContext(AppConfigProperties env) {
        return new ScenarioContext(env);
    }
}