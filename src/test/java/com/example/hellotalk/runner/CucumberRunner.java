package com.example.hellotalk.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
//        plugin = {"pretty", "com.aventstack.extentreports.cucumber.adapter.ExtendCucumberAdapter:"},
        features = "src/test/resources/features",
        glue = {"com.example.hellotalk.steps", "com.example.hellotalk.config"},
        tags = "not @skip"
)
public class CucumberRunner {
}
