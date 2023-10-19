package com.example.hellotalk.service.impl.message;

import com.example.hellotalk.entity.message.MessageEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.ChatNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MessageNotFoundException;
import com.example.hellotalk.mapper.MessageMapper;
import com.example.hellotalk.model.message.Chat;
import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.repository.message.ChatRepository;
import com.example.hellotalk.repository.message.MessageRepository;
import com.example.hellotalk.service.message.MessageService;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;

    private final UserService userService;

    @Override
    public Message getMessage(UUID messageId) {
        MessageEntity messageEntity = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(MESSAGE_NOT_FOUND_EXCEPTION));
        return messageMapper.toModel(messageEntity);
    }

    @Override
    public List<Message> getAllMessages() {
        List<MessageEntity> messageEntityList = messageRepository.findAll();

        return messageEntityList.stream()
                .map(messageMapper::toModel)
                .toList();
    }

    @Override
    public Chat getChat(UUID chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new ChatNotFoundException(CHAT_NOT_FOUND_EXCEPTION);
        }

        List<Message> messageList = messageRepository.findByChatEntity_Id(chatId)
                .stream()
                .map(messageMapper::toModel)
                .toList();

        return Chat.builder()
                .id(chatId)
                .messageList(messageList)
                .build();
    }

    @Override
    public Message createMessage(Message message) {
        UserEntity userFromEntity = userService.getCurrentUser();

        MessageEntity messageEntity = messageMapper.toEntity(message);
        messageEntity.setUserFromEntity(userFromEntity);
        messageEntity.setCreationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC));

        messageEntity = messageRepository.save(messageEntity);

        return messageMapper.toModel(messageEntity);
    }

    @Override
    public Message updateMessage(UUID messageId, Message message) {
        UserEntity userFromEntity = userService.getCurrentUser();

        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(MESSAGE_NOT_FOUND_EXCEPTION));

        if (!userFromEntity.getId().equals(messageEntity.getUserFromEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        messageEntity.setContent(message.getContent());
        messageEntity = messageRepository.save(messageEntity);

        return messageMapper.toModel(messageEntity);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        UserEntity userEntity = userService.getCurrentUser();

        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(MESSAGE_NOT_FOUND_EXCEPTION));

        if (!userEntity.getId().equals(messageEntity.getUserFromEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        messageRepository.deleteById(messageId);
    }
}
