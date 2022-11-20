package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowerNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.HobbyAndInterest;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.hellotalk.entity.user.UserEntity.buildUserEntityFromModel;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(UUID userId) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        if (userEntityOptional.isPresent()) {

            UserEntity userEntity = userEntityOptional.get();

            Set<HobbyAndInterest> hobbyAndInterestEntities =
                    userEntity.getHobbyAndInterestEntities().stream()
                            .map(h -> HobbyAndInterest.builder()
                                    .id(h.getId())
                                    .title(h.getTitle()).build())
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
                    .hometown(Hometown.builder()
                            .city(userEntity.getHometownEntity().getCity())
                            .country(userEntity.getHometownEntity().getCountry())
                            .build())
                    .hobbyAndInterests(hobbyAndInterestEntities)
                    .build();
        } else {
            return null;
        }

    }

    @Override public User createUser(User user) {

        UserEntity userEntity = userRepository.save(buildUserEntityFromModel(user));
        user.setId(userEntity.getId());
        return user;
    }

    @Override public User updateUser(UUID userId, User user) {

        if (userRepository.findById(userId).isPresent()) {
            UserEntity userEntity = UserEntity.buildUserEntityFromModel(user);
            userEntity.setId(userId);

            userEntity = userRepository.save(userEntity);
            return User.buildUserFromEntity(userEntity);
        } else {
            throw new UserNotFoundException("No user found with this id");
        }
    }

    @Override public void deleteUser(UUID userId) {

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        if (optionalUserEntity.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("No user found with this id");
        }
    }

    @Override public void followUser(UUID fromId, UUID toId) throws FollowerNotFoundException {

        Optional<UserEntity> userEntityOptionalFrom = userRepository.findById(fromId);
        Optional<UserEntity> userEntityOptionalTo = userRepository.findById(toId);

        if (userEntityOptionalFrom.isEmpty() || userEntityOptionalTo.isEmpty()) {
            throw new UserNotFoundException("No user found with this id");
        }

        UserEntity userEntityTo = userEntityOptionalTo.get();
        UserEntity userEntityFrom = userEntityOptionalFrom.get();

        Set<FollowingRequestEntity> followingRequestEntities = new HashSet<>();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder().userSenderEntity(userEntityFrom).userReceiverEntity(userEntityTo).build();
        followingRequestEntities.add(followingRequestEntity);

        userEntityTo.setFollowedByEntity(followingRequestEntities);

        userEntityTo = userRepository.save(userEntityTo);
        
        
        if (userEntityTo.getFollowedByEntity() == null || userEntityTo.getFollowedByEntity().isEmpty()) {
            throw new FollowerNotFoundException("Follower Not Found");
        }
    }

    public UserEntity setFollower(UserEntity userEntityTo, UserEntity userEntityFrom) {
        Set<FollowingRequestEntity> followingRequestEntities = new HashSet<>();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder().userSenderEntity(userEntityFrom).userReceiverEntity(userEntityTo).build();
        followingRequestEntities.add(followingRequestEntity);

        userEntityTo.setFollowedByEntity(followingRequestEntities);
        
        return userEntityTo;
    }
}
