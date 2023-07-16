package com.example.hellotalk.steps;

import com.example.hellotalk.client.RestClient;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@ScenarioScope
public class ApiContext {
    private final RestClient restClient;
}
