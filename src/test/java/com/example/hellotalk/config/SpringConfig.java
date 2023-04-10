package com.example.hellotalk.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = HelloTalkApiConfig.class)
@CucumberContextConfiguration
public class SpringConfig {

}
