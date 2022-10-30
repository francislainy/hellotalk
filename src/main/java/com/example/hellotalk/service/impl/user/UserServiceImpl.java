package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired UserRepository userRepository;

    @Override
    public User getUser(UUID uuid) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(uuid);

        if (userEntityOptional.isPresent()) {

            UserEntity userEntity = userEntityOptional.get();

            return User.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .selfIntroduction(userEntity.getSelfIntroduction())
                    .build();
        } else {
            return null;
        }

    }
}
