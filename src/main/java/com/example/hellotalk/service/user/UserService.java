package com.example.hellotalk.service.user;

import com.example.hellotalk.model.user.User;

import java.util.UUID;

public interface UserService {

    User getUser(UUID uuid);

    User createUser(User user);

}
