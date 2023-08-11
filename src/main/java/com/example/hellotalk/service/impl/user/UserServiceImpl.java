package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.mapper.UserMapper;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.hellotalk.entity.user.UserEntity.buildUserEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.model.user.User.buildUserFromEntity;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final HobbyAndInterestRepository hobbyAndInterestRepository;
    final HometownRepository hometownRepository;
    final LikeRepository likeRepository;
    final MomentRepository momentRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    public User getUser(UUID userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return userMapper.userEntityToUser(userEntity);
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public List<User> getAllUsers() {

        List<User> userList = new ArrayList<>();
        List<UserEntity> userEntityList = userRepository.findAll();

        if (!userEntityList.isEmpty()) {
            userEntityList.forEach(userEntity -> userList.add(buildUserFromEntity(userEntity)));
        }

        return userList;
    }

    @Override
    public User createUser(User user) {

        UserEntity userEntity = buildUserEntityFromModel(user);

        List<HobbyAndInterestEntity> hobbyAndInterestEntityList = hobbyAndInterestRepository.saveAll(userEntity.getHobbyAndInterestEntities());
        HometownEntity hometownEntity = hometownRepository.save(userEntity.getHometownEntity());

        Set<HobbyAndInterestEntity> hobbyAndInterestEntitySet = new HashSet<>(hobbyAndInterestEntityList);
        userEntity.setHobbyAndInterestEntities(hobbyAndInterestEntitySet);
        userEntity.setHometownEntity(hometownEntity);

        userEntity = userRepository.save(userEntity);

        return buildUserFromEntity(userEntity);
    }

    @Override
    public User updateUser(UUID userId, User user) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = UserEntity.buildUserEntityFromModel(user);
            userEntity.setId(userId);

            HometownEntity hometownEntity = userEntityOptional.get().getHometownEntity();
            if (hometownEntity != null) {
                hometownRepository.save(hometownEntity);
            }

            Set<HobbyAndInterestEntity> hobbyAndInterestEntities = userEntityOptional.get().getHobbyAndInterestEntities();
            if (!hobbyAndInterestEntities.isEmpty()) {
                hobbyAndInterestRepository.saveAll(hobbyAndInterestEntities);
            }

            userEntity = userRepository.save(userEntity);
            return buildUserFromEntity(userEntity);
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public String deleteUser(UUID userId) {

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        String json = """
                {"message": "User Deleted"}
                """;

        if (optionalUserEntity.isPresent()) {
            userRepository.deleteById(userId);
            return json;
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
    }
}
