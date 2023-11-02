package com.example.hellotalk.service.message;

import com.example.hellotalk.entity.message.ChatEntity;
import com.example.hellotalk.entity.message.MessageEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.ChatNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MessageNotFoundException;
import com.example.hellotalk.mapper.ChatMapper;
import com.example.hellotalk.mapper.MessageMapper;
import com.example.hellotalk.model.message.Chat;
import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.repository.message.ChatRepository;
import com.example.hellotalk.repository.message.MessageRepository;
import com.example.hellotalk.service.impl.message.MessageServiceImpl;
import com.example.hellotalk.service.user.UserService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    MessageServiceImpl messageService;

    @Mock
    MessageRepository messageRepository;

    @Mock
    ChatRepository chatRepository;

    @Spy
    MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Spy
    ChatMapper chatMapper = Mappers.getMapper(ChatMapper.class);

    @Mock
    UserService userService;

    // Test data
    UUID chatId;
    UUID messageId;
    UUID userFromId;
    UUID userToId;
    UserEntity userFromEntity;
    UserEntity userToEntity;
    MessageEntity messageEntity;
    ChatEntity chatEntity;

    final ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    final ZonedDateTime creationDate = now;

    @BeforeEach
    void setUp() {
        // Initialize test data
        chatId = randomUUID();
        messageId = randomUUID();
        userFromId = randomUUID();
        userToId = randomUUID();
        userFromEntity = UserEntity.builder().id(userFromId).build();
        userToEntity = UserEntity.builder().id(userToId).build();
        messageEntity = getMessageEntity(messageId);
        chatEntity = ChatEntity.builder()
                .id(chatId)
                .messageEntityList(List.of(messageEntity)) // todo: convert to set - 16/10/2023
                .build();
    }

    @Test
    void testGetMessage_ValidMessageId_ReturnsComment() {
        // Execute the method under test
        when(messageRepository.findById(any())).thenReturn(Optional.of(messageEntity));
        Message message = messageService.getMessage(messageId);

        // Verify the results
        assertAll(
                () -> assertEquals(messageId, message.getId()),
                () -> assertEquals("anyText", message.getContent()),
                () -> assertEquals(userFromId, message.getUserFromId()),
                () -> assertEquals(userToId, message.getUserToId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(message.getCreationDate())));

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findById(messageId);
    }

    @Test
    void testGetMessage_InvalidMessageId_ThrowsCommentNotFoundException() {
        // Mock behavior for this specific test case
        when(messageRepository.findById(any())).thenReturn(Optional.empty());

        // Execute the method under test and verify the exception thrown
        MessageNotFoundException exception =
                assertThrows(MessageNotFoundException.class, () -> messageService.getMessage(messageId));

        assertEquals(MESSAGE_NOT_FOUND_EXCEPTION, exception.getMessage());

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findById(messageId);
    }

    @Test
    void testGetAllMessages_MessagesExist_ReturnsListOfMessages() {
        when(messageRepository.findAll()).thenReturn(List.of(messageEntity));

        List<Message> messageList = messageService.getAllMessages();

        Message message = messageList.get(0);

        // Verify the results
        assertAll(
                () -> assertEquals(messageId, message.getId()),
                () -> assertEquals("anyText", message.getContent()),
                () -> assertEquals(userFromId, message.getUserFromId()),
                () -> assertEquals(userToId, message.getUserToId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(message.getCreationDate())));

        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMessages_NoExistingMessages_ReturnsEmptyList() {
        List<Message> messageList = messageService.getAllMessages();
        assertTrue(messageList.isEmpty());
    }

    @Test
    void testGetChat_MessagesExist_ReturnsListOfMessages() {
        when(chatRepository.existsById(any())).thenReturn(true);

        when(messageRepository.findByChatEntity_Id(any())).thenReturn(List.of(messageEntity));

        Chat chat = messageService.getChat(chatId);

        Message message = chat.getMessageList().get(0);

        assertAll(
                () -> assertEquals(chatId, chat.getId()),
                () -> assertEquals(messageId, message.getId()),
                () -> assertEquals("anyText", message.getContent()),
                () -> assertEquals(userFromId, message.getUserFromId()),
                () -> assertEquals(userToId, message.getUserToId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(message.getCreationDate())));

        verify(messageRepository, times(1)).findByChatEntity_Id(chatId);
    }

    @Test
    void testGetChat_ChatDoesNotExist_ThrowsException() {
        when(chatRepository.existsById(any())).thenReturn(false);

        ChatNotFoundException exception = assertThrows(ChatNotFoundException.class, () -> messageService.getChat(chatId));

        assertEquals(CHAT_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(messageRepository, never()).findByChatEntity_Id(chatId);
    }

    @Test
    void testGetChats_MessagesExist_ReturnsListOfChats() {
        when(chatRepository.findAll()).thenReturn(List.of(chatEntity));

        //        Chat chat = chatMapper.toModel(chatEntity); //todo: check why this does not work
        Chat chat = Chat.builder()
                .id(chatId)
                .messageList(List.of(messageMapper.toModel(messageEntity)))
                .build();

        //        when(chatMapper.toModel(any())).thenReturn(chat); //todo: check why this also works even though it's a spy- 02/10/2023
        doReturn(chat).when(chatMapper).toModel(any());

        List<Chat> chatList = messageService.getChats();

        Message message = chatList.get(0).getMessageList().get(0);

        assertAll(
                () -> assertEquals(chatId, chatList.get(0).getId()),
                () -> assertEquals(messageId, message.getId()),
                () -> assertEquals("anyText", message.getContent()),
                () -> assertEquals(userFromId, message.getUserFromId()),
                () -> assertEquals(userToId, message.getUserToId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(message.getCreationDate())));

        verify(chatRepository, times(1)).findAll();
    }

    @Test
    void testCreateMessage_ValidMessage_ReturnsCreatedMessage() {
        // Mock behaviors
        when(userService.getCurrentUser()).thenReturn(userFromEntity);
        when(messageRepository.save(any())).thenReturn(messageEntity);

        // Mock current time
        ZonedDateTime fixedZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);

        // Execute the method under test
        Message message = messageService.createMessage(messageMapper.toModel(messageEntity));

        // Capture the argument
        ArgumentCaptor<MessageEntity> messageEntityCaptor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRepository).save(messageEntityCaptor.capture());

        // Get the captured argument
        MessageEntity capturedMessageEntity = messageEntityCaptor.getValue();

        // Verify that setUserFromEntity and setUserToEntity have been called
        assertEquals(userFromEntity, capturedMessageEntity.getUserFromEntity());

        // Verify that setCreationDate has been called with the correct date
        ZonedDateTime capturedCreationDate = capturedMessageEntity.getCreationDate();
        long diffInSeconds = Math.abs(capturedCreationDate.toEpochSecond() - fixedZonedDateTime.toEpochSecond());
        assertTrue(diffInSeconds < 5); // Allow for a small difference due to execution time

        // Verify the results
        assertAll(
                () -> assertEquals(messageId, message.getId()),
                () -> assertEquals("anyText", message.getContent()),
                () -> assertEquals(userFromId, message.getUserFromId()),
                () -> assertEquals(userToId, message.getUserToId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(message.getCreationDate())));

        // Verify interactions with mocks
        verify(userService, times(1)).getCurrentUser();
        verify(messageRepository, times(1)).save(any(MessageEntity.class));
    }

    @Test
    void testUpdateMessage_ValidMessageBody_ReturnsUpdatedMessage() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);

        MessageEntity messageEntity = getMessageEntity(messageId);
        messageEntity.setUserFromEntity(userFromEntity);
        messageEntity.setUserToEntity(userToEntity);

        MessageEntity messageEntityUpdated = getMessageEntity(messageId)
                .toBuilder().content("anyUpdatedText")
                .build();

        when(messageRepository.findById(any())).thenReturn(Optional.of(messageEntity));
        when(messageRepository.save(any())).thenReturn(messageEntityUpdated);

        Message message = messageMapper.toModel(messageEntity);
        message = messageService.updateMessage(messageId, message);

        Message finalMessage = message;
        assertAll(
                () -> assertEquals(messageId, finalMessage.getId()),
                () -> assertEquals("anyUpdatedText", finalMessage.getContent()),
                () -> assertEquals(userToId, finalMessage.getUserToId()),
                () -> assertEquals(userFromId, finalMessage.getUserFromId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalMessage.getCreationDate())));

        verify(userService, times(1)).getCurrentUser();
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(1)).save(any(MessageEntity.class));
    }

    @Test
    void testUpdateMessage_CommentNotFound_ThrowsCommentNotFoundException() {
        UUID messageId = randomUUID();
        Message comment = messageMapper.toModel(getMessageEntity(messageId));
        MessageNotFoundException exception =
                assertThrows(MessageNotFoundException.class, () -> messageService.updateMessage(messageId, comment));

        assertEquals(MESSAGE_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(userService, times(1)).getCurrentUser();
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    @Test
    void testUpdateMessage_UserDoesNotOwnMessage_ThrowsEntityDoesNotBelongToUserException() {
        UserEntity unauthorizedUserEntity = UserEntity.builder().id(randomUUID()).build();
        when(userService.getCurrentUser()).thenReturn(unauthorizedUserEntity);

        MessageEntity messageEntity = getMessageEntity(messageId);
        messageEntity.setUserFromEntity(userFromEntity);
        messageEntity.setUserToEntity(userToEntity);
        Message message = messageMapper.toModel(messageEntity);

        when(messageRepository.findById(any())).thenReturn(Optional.of(messageEntity));
        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> messageService.updateMessage(messageId, message));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());

        verify(userService, times(1)).getCurrentUser();
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    @Test
    void testDeleteMessage_ValidMessageId_DeletesMessage() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);
        when(messageRepository.findById(any())).thenReturn(Optional.of(messageEntity));

        assertDoesNotThrow(() -> messageService.deleteMessage(messageId));
        verify(messageRepository, times(1)).deleteById(messageId);
    }

    @Test
    void testDeleteMessage_InvalidMessageId_ThrowsMessageDoesNotExistException() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);

        when(messageRepository.findById(any())).thenReturn(Optional.empty());

        MessageNotFoundException exception =
                assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessage(messageId));

        assertEquals(MESSAGE_NOT_FOUND_EXCEPTION, exception.getMessage());

        verify(messageRepository, never()).deleteById(messageId);
    }

    @Test
    void testDeleteMessage_MessageDoesNotBelongToUser_ThrowsEntityDoesNotBelongToUserException() {
        UserEntity unauthorizedUserEntity = UserEntity.builder().id(randomUUID()).build();

        when(userService.getCurrentUser()).thenReturn(unauthorizedUserEntity);
        when(messageRepository.findById(any())).thenReturn(Optional.of(messageEntity));

        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> messageService.deleteMessage(messageId));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
        verify(messageRepository, never()).deleteById(messageId);
    }

    // Helpers
    private MessageEntity getMessageEntity(UUID commentId) {
        return MessageEntity.builder()
                .id(commentId)
                .content("anyText")
                .creationDate(creationDate)
                .userFromEntity(userFromEntity)
                .userToEntity(userToEntity)
                .build();
    }

}
