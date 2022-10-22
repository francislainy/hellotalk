package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser(UUID uuid) {
        return User.builder().name("anyName").build(); //todo: get this from repository class - 22/10/2022
    }
}
