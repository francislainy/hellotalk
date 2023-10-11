package com.example.hellotalk.service.message;

import com.example.hellotalk.model.message.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message getMessage(UUID messageId);

    List<Message> getAllMessages();

    Message createMessage(Message message);

    Message updateMessage(UUID messageId, Message message);

    void deleteMessage(UUID messageId);
}
