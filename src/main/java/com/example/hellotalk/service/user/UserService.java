package com.example.hellotalk.service.user;

import com.example.hellotalk.entity.LikeEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User getUser(UUID userId);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(UUID userId, User user);

    String deleteUser(UUID userId);

    LikeEntity likeMoment(UUID userId, UUID momentId);
}
