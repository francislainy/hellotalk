package com.example.hellotalk.controller;

import com.example.hellotalk.exception.UserDoesNotExistExistException;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.example.hellotalk.util.Utils.jsonStringFromObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UserService userService;

    @Test
    void testGetUser() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        User user = User.builder()
                .name("anyName")
                .build();

        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/api/v1/ht/user/{actorId}", userId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testCreateUser() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        User user = User.builder()
                .name("anyName")
                .build();

        String json = jsonStringFromObject(user);
        ObjectMapper objectMapper = new ObjectMapper();
        User userWithId = objectMapper.readValue(json, User.class);
        userWithId.setId(userId);

        String jsonWithId = jsonStringFromObject(userWithId);

        when(userService.createUser(any())).thenReturn(userWithId);

        mockMvc.perform(post("/api/v1/ht/user").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonWithId));
    }

    @Test
    void testUpdateUser() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        User user = User.builder()
                .name("anyName")
                .build();

        String json = jsonStringFromObject(user);
        ObjectMapper objectMapper = new ObjectMapper();
        User userWithId = objectMapper.readValue(json, User.class);
        userWithId.setId(userId);

        String jsonWithId = jsonStringFromObject(userWithId);

        when(userService.updateUser(any(), any())).thenReturn(userWithId);

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonWithId));
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDoesNotExist() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        User user = User.builder()
                .name("anyName")
                .build();

        String json = jsonStringFromObject(user);

        when(userService.updateUser(any(), any())).thenThrow(new UserDoesNotExistExistException("No user found with this id"));

        mockMvc.perform(put("/api/v1/ht/user/{userId}", userId)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andExpect(content().string("No user found with this id"));
    }

    @Test
    void testDeleteUser() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        User user = User.builder()
                .id(userId)
                .name("anyName")
                .build();

        when(userService.getUser(any())).thenReturn(user);

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound() throws Exception {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
     
        doThrow(new UserDoesNotExistExistException("No user found with this id")).when(userService).deleteUser(any());

        mockMvc.perform(delete("/api/v1/ht/user/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("No user found with this id"));
    }

}
