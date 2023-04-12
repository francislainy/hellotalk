package com.example.hellotalk.steps.definitions;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.config.DBClient;
import com.example.hellotalk.utils.ScenarioContext;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseClass {

    @Autowired
    public RestClient restClient;

    @Autowired
    public ScenarioContext scenarioContext;

    @Autowired
    public DBClient dbClient;
}
