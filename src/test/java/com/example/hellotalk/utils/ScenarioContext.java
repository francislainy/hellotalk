package com.example.hellotalk.utils;

import com.example.hellotalk.config.Environment;
import lombok.Data;

@Data
public class ScenarioContext {

    private final Environment environment;

    public ScenarioContext(Environment environment) {
        this.environment = environment;
    }

    public void setUpTestData() {

    }

}
