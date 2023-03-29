package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.*;
import com.example.hellotalk.exception.EntityBelongsToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.hellotalk.entity.user.UserEntity.buildUserEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.Hometown.buildHometownFromEntity;
import static com.example.hellotalk.model.user.User.buildUserFromEntity;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final HobbyAndInterestRepository hobbyAndInterestRepository;
    final HometownRepository hometownRepository;
    final LikeRepository likeRepository;
    final MomentRepository momentRepository;

    public UserServiceImpl(UserRepository userRepository, HobbyAndInterestRepository hobbyAndInterestRepository, HometownRepository hometownRepository, LikeRepository likeRepository, MomentRepository momentRepository) {
        this.userRepository = userRepository;
        this.hobbyAndInterestRepository = hobbyAndInterestRepository;
        this.hometownRepository = hometownRepository;
        this.likeRepository = likeRepository;
        this.momentRepository = momentRepository;
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

    @Override
    public Map<String, Object> likeMoment(UUID userId, UUID momentId) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .map(mEntity -> {
                    if (mEntity.getUserEntity().getId().equals(userId)) {
                        throw new EntityBelongsToUserException(ENTITY_BELONG_TO_USER_EXCEPTION);
                    }
                    return mEntity;
                })
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        Map<String, Object> map = new HashMap<>();
        Optional<LikeEntity> likeEntityOptional = Optional.ofNullable(likeRepository.findByUserEntity_IdAndMomentEntity_Id(userId, momentId));
        LikeEntity likeEntity;
        String resultMessage;

        if (likeEntityOptional.isPresent()) {
            likeEntity = likeEntityOptional.get();
            likeRepository.delete(likeEntity);
            resultMessage = "Moment unliked successfully";
        } else {
            likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();
            likeEntity = likeRepository.save(likeEntity);
            resultMessage = "Moment liked successfully";
        }

        ResultInfo resultInfo = ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();

        map.put("data", resultInfo);
        map.put("message", resultMessage);

        return map;
    }
}
