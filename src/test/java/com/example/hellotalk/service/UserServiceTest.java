package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @MockBean
    UserRepository userRepository;

    @Test
    void testUserHasName() {

        UserService userService = new UserServiceImpl();
        User user = userService.getUser(UUID.randomUUID());

        when(userRepository.findById(any())).thenReturn(Optional.of(UserEntity.builder().name("anyName").build()));

        assertEquals("anyName", user.getName());
    }

}
