package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.hellotalk.entity.user.UserEntity.buildUserEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.model.Hometown.buildHometownFromEntity;
import static com.example.hellotalk.model.user.User.buildUserFromEntity;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final HobbyAndInterestRepository hobbyAndInterestRepository;
    final HometownRepository hometownRepository;

    public UserServiceImpl(UserRepository userRepository, HobbyAndInterestRepository hobbyAndInterestRepository, HometownRepository hometownRepository) {
        this.userRepository = userRepository;
        this.hobbyAndInterestRepository = hobbyAndInterestRepository;
        this.hometownRepository = hometownRepository;
    }

    @Override
    public User getUser(UUID userId) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        if (userEntityOptional.isPresent()) {

            UserEntity userEntity = userEntityOptional.get();

            Set<HobbyAndInterest> hobbyAndInterestEntities =
                    userEntity.getHobbyAndInterestEntities().stream()
                            .map(HobbyAndInterest::buildHobbyAndInterestFromEntity)
                            .collect(Collectors.toSet());

            return User.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .dob(userEntity.getDob())
                    .status(userEntity.getStatus())
                    .gender(userEntity.getGender())
                    .subscriptionType(userEntity.getSubscriptionType())
                    .creationDate(userEntity.getCreationDate())
                    .handle(userEntity.getHandle())
                    .nativeLanguage(userEntity.getNativeLanguage())
                    .targetLanguage(userEntity.getTargetLanguage())
                    .selfIntroduction(userEntity.getSelfIntroduction())
                    .occupation(userEntity.getOccupation())
                    .placesToVisit(userEntity.getPlacesToVisit())
                    .hometown(buildHometownFromEntity(userEntity.getHometownEntity()))
                    .hobbyAndInterests(hobbyAndInterestEntities)
                    .build();
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
