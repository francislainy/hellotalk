package com.example.hellotalk.steps.user;

import com.example.hellotalk.entity.user.UserEntity;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ScenarioScope
public class UserContext {

    private List<UserEntity> userListDB;
    private UserEntity userDB;
}
