package com.example.hellotalk.model.message;

import com.example.hellotalk.model.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    private UUID id;

    @JsonProperty("messages")
    private List<Message> messageList;

    @JsonProperty("participants")
    private List<User> participantList;
}
