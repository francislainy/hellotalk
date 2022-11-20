package com.example.hellotalk.service.user;

import com.example.hellotalk.model.user.User;

import java.util.UUID;

public interface UserService {

    User getUser(UUID userId);

    User createUser(User user);

    User updateUser(UUID userId, User user);

    void deleteUser(UUID userId) throws Exception;
    
    void followUser(UUID fromId, UUID toId) throws Exception;
}
