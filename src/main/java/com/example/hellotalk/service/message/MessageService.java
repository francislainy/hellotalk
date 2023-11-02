package com.example.hellotalk.service.message;

import com.example.hellotalk.model.message.Chat;
import com.example.hellotalk.model.message.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message getMessage(UUID messageId);

    List<Message> getAllMessages();

    Chat getChat(UUID chatId);

    List<Chat> getChats();

    Message createMessage(Message message);

    Message updateMessage(UUID messageId, Message message);

    void deleteMessage(UUID messageId);
}
