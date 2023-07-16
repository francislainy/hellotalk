package com.example.hellotalk.steps;

import com.example.hellotalk.client.RestClient;
import com.example.hellotalk.entity.user.UserEntity;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ScenarioScope
public class ApiContext {
    private final RestClient restClient;
}
