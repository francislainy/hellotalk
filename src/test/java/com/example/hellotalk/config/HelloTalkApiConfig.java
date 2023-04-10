package com.example.hellotalk.config;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.utils.ScenarioContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:environments/${env:dev}.properties"})
@ComponentScan(basePackages = {"com.example.hellotalk.config", "com.example.hellotalk.steps"})
public class HelloTalkApiConfig {

    @Bean
    RestClient restClient(Environment env) {
        return new RestClient(env);
    }

    @Bean
    Environment environment() {
        return new Environment();
    }

    @Bean
    ScenarioContext scenarioContext(Environment env) {
        return new ScenarioContext(env);
    }
}
