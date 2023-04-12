package com.example.hellotalk.utils;

import com.example.hellotalk.config.AppConfigProperties;
import lombok.Data;

@Data
public class ScenarioContext {

    private final AppConfigProperties appConfigProperties;

    public ScenarioContext(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    public void setUpTestData() {

    }

}
