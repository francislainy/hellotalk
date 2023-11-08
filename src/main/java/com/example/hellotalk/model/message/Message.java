package com.example.hellotalk.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private UUID id;
    private String content;
    private ZonedDateTime creationDate;
    private UUID parentId;
    private UUID chatId;
    private UUID userFromId;
    private UUID userToId;
}
