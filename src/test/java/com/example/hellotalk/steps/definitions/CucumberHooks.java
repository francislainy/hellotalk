package com.example.hellotalk.steps.definitions;

import org.junit.Before;

public class CucumberHooks extends BaseClass {

    @Before
    public void beforeScenario() {
        scenarioContext.setUpTestData();
    }
}
