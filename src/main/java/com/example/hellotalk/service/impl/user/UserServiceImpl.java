package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.HobbyAndInterest;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired UserRepository userRepository;

    @Override
    public User getUser(UUID uuid) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(uuid);

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
                    .gender(userEntity.getGender())
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
}
