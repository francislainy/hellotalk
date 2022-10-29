package com.example.hellotalk.controller;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}
