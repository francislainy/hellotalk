package com.example.hellotalk.steps.message;

import com.example.hellotalk.model.message.Message;
import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ScenarioScope
public class MessageContext {

    private List<Message> messageList;
    private Message message;
}
