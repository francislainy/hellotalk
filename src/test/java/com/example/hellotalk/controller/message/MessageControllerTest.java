package com.example.hellotalk.controller.message;

import com.example.hellotalk.config.BaseDocTestConfig;
import com.example.hellotalk.exception.ChatNotFoundException;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MessageNotFoundException;
import com.example.hellotalk.model.message.Chat;
import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.service.message.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.example.hellotalk.exception.AppExceptionHandler.ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION;
import static com.example.hellotalk.exception.AppExceptionHandler.MESSAGE_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageControllerTest extends BaseDocTestConfig {

    @MockBean
    MessageService messageService;

    @Test
    void testGetMessage() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        when(messageService.getMessage(any())).thenReturn(message);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages/{messageId}", messageId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringFromObject(message)))
                .andDo(document("get-message",
                        resource("Get a message's details")))
                .andReturn();

    }

    @Test
    void testGetMessage_MessageDoesNotExist_404NotFound() throws Exception {
        when(messageService.getMessage(any())).thenThrow(new MessageNotFoundException(MESSAGE_NOT_FOUND_EXCEPTION));
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages/{messageId}", randomUUID()))
                .andExpect(status().isNotFound())
                .andDo(document("get-message-throws-exception-when-message-does-not-exist",
                        resource("Get a message's details")))
                .andReturn();
    }

    @Test
    void testGetAllMessages() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        List<Message> messageList = List.of(message);
        when(messageService.getAllMessages()).thenReturn(messageList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringFromObject(messageList)))
                .andDo(document("get-all-messages",
                        resource("Get a list of messages")))
                .andReturn();
    }

    @Test
    void testGetAllMessages_WhenMessagesDoNotExist_ReturnsEmptyList() throws Exception {
        when(messageService.getAllMessages()).thenReturn(emptyList());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringFromObject(emptyList())))
                .andDo(document("get-all-messages",
                        resource("Get a list of messages")))
                .andReturn();
    }

    @Test
    void testCreateMessage() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        when(messageService.createMessage(any())).thenReturn(message);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/ht/messages/")
                .content(jsonStringFromObject(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonStringFromObject(message)))
                .andDo(document("create-message",
                        resource("Create a message")))
                .andReturn();

    }

    @Test
    void testUpdateMessage() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .content("updated content")
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        when(messageService.updateMessage(any(), any())).thenReturn(message);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/messages/{messageId}", messageId)
                .content(jsonStringFromObject(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonStringFromObject(message)))
                .andDo(document("update-message",
                        resource("Update a message")))
                .andReturn();

    }

    @Test
    void testUpdateMessage_MessageDoesNotExist_ReturnsNotFound() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .content("updated content")
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        when(messageService.updateMessage(any(), any())).thenThrow(MessageNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/messages/{messageId}", messageId)
                .content(jsonStringFromObject(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("update-message-404-when-message-not-found",
                        resource("Update a message 404 when message not found")))
                .andReturn();
    }

    @Test
    void testUpdateMessage_MessageDoesNotBelongToUser_ReturnsForbidden() throws Exception {
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .content("updated content")
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        when(messageService.updateMessage(any(), any())).thenThrow(EntityDoesNotBelongToUserException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/ht/messages/{messageId}", messageId)
                .content(jsonStringFromObject(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(document("update-message-403-when-message-does-not-belong-to-user",
                        resource("Update a message 403 when message does not belong to user")))
                .andReturn();
    }

    @Test
    void deleteMessage() throws Exception {
        UUID messageId = randomUUID();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/messages/{messageId}", messageId))
                .andExpect(status().isNoContent())
                .andDo(document("delete message",
                        resource("Delete a message")))
                .andReturn();
    }

    @Test
    void deleteMessage_WhenMessageDoesNotExist_Returns404NotFound() throws Exception {
        doThrow(MessageNotFoundException.class).when(messageService).deleteMessage(any());

        UUID messageId = randomUUID();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/messages/{messageId}", messageId))
                .andExpect(status().isNotFound())
                .andDo(document("delete-message-returns-404-when-message-is-not-found",
                        resource("Delete message returns 404 when message is not found")))
                .andReturn();
    }

    @Test
    void deleteMessage_WhenMessageDoesNotBelongToUser_Returns403Forbidden() throws Exception {
        doThrow(new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION)).when(messageService).deleteMessage(any());

        UUID messageId = randomUUID();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/ht/messages/{messageId}", messageId))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
                })
                .andDo(document("delete-message-returns-404-when-message-does-not-belong-to-user ",
                        resource("Delete message returns 403 when message does not belong to user")))
                .andReturn();
    }

    @Test
    void testGetChat() throws Exception {
        UUID chatId = randomUUID();
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        Chat chat = Chat.builder()
                .id(chatId)
                .messageList(List.of(message))
                .build();

        when(messageService.getChat(any())).thenReturn(chat);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages/chats/{chatId}", chatId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringFromObject(chat)))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.messages").exists())
                .andDo(document("get-chat",
                        resource("Get a chat's details")))
                .andReturn();

    }

    @Test
    void testGetChat_ChatDoesNotExist_HandleChatNotFoundException() throws Exception {
        when(messageService.getChat(any())).thenThrow(ChatNotFoundException.class);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages/chats/{chatId}", randomUUID()))
                .andExpect(status().isNotFound())
                .andDo(document("get-chat-exception-thrown_when-chat-not-found",
                        resource("Get chat - Exception thrown when chat not found")))
                .andReturn();
    }

    @Test
    void testGetChats() throws Exception {
        UUID chatId = randomUUID();
        UUID messageId = randomUUID();

        Message message = Message.builder()
                .id(messageId)
                .userFromId(randomUUID())
                .userToId(randomUUID())
                .build();

        Chat chat = Chat.builder()
                .id(chatId)
                .messageList(List.of(message))
                .build();

        when(messageService.getChats()).thenReturn(List.of(chat));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/ht/messages/chats"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringFromObject(List.of(chat))))
                .andDo(document("get-chats",
                        resource("Get chats")))
                .andReturn();

    }
}
