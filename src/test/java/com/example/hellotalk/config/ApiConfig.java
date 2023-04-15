package com.example.hellotalk.config;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.dbclient.DBClient;
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

    //todo: temporarily disabling this to enable ApplicationTests to run - 15/04/2023
    //    @Bean
    //    RestClient restClient(AppConfigProperties appConfigProperties) {
    //        return new RestClient(appConfigProperties);
    //    }

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

}
