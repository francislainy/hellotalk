package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.UserDoesNotExistExistException;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.example.hellotalk.entity.user.HometownEntity.buildHometownEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testGetUser() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder().title("anyInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .selfIntroduction("anySelfIntroduction")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(buildHometownEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));

        User user = userService.getUser(UUID.randomUUID());
        assertAll(
                () -> assertEquals(userId, user.getId()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anyCreationDate", user.getCreationDate()),
                () -> assertEquals("anyStatus", user.getStatus()),
                () -> assertEquals("anyHandle", user.getHandle()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyNativeLanguage", user.getNativeLanguage()),
                () -> assertEquals("anyTargetLanguage", user.getTargetLanguage()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction()),
                () -> assertEquals("anyOccupation", user.getOccupation()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCountry", user.getHometown().getCountry()),
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit())
        );

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testCreateUser() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder().title("anyInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .selfIntroduction("anySelfIntroduction")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(buildHometownEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
        when(userRepository.save(any())).thenReturn(userEntity);

        User user = userService.createUser(User.buildUserFromEntity(userEntity));

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anyCreationDate", user.getCreationDate()),
                () -> assertEquals("anyStatus", user.getStatus()),
                () -> assertEquals("anyHandle", user.getHandle()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyNativeLanguage", user.getNativeLanguage()),
                () -> assertEquals("anyTargetLanguage", user.getTargetLanguage()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction()),
                () -> assertEquals("anyOccupation", user.getOccupation()),
                () -> assertEquals("anyCity", user.getHometown().getCity()),
                () -> assertEquals("anyCountry", user.getHometown().getCountry()),
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit())
        );

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testUpdateUserDetails() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder().title("anyInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .selfIntroduction("anySelfIntroduction")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(buildHometownEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();

        HobbyAndInterestEntity hobbyAndInterestEntityUpdated = HobbyAndInterestEntity.builder().title("anyUpdatedInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntitiesUpdated = new HashSet<>();
        hobbyAndInterestEntitiesUpdated.add(hobbyAndInterestEntityUpdated);
        Hometown updatedHometown = Hometown.builder().city("anyUpdatedCity").country("anyUpdatedCountry").build();
        UserEntity userEntityUpdated = UserEntity.builder()
                .id(userId)
                .name("anyUpdatedName")
                .dob("anyUpdatedDob")
                .gender("anyUpdatedGender")
                .selfIntroduction("anyUpdatedSelfIntroduction")
                .creationDate("anyUpdatedCreationDate")
                .handle("anyUpdatedHandle")
                .status("anyUpdatedStatus")
                .nativeLanguage("anyUpdatedNativeLanguage")
                .targetLanguage("anyUpdatedTargetLanguage")
                .occupation("anyUpdatedOccupation")
                .placesToVisit("anyUpdatedPlacesToVisit")
                .hometownEntity(buildHometownEntity(updatedHometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntitiesUpdated)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any())).thenReturn(userEntityUpdated);

        User user = User.buildUserFromEntity(userEntity);
        user = userService.updateUser(userId, user);

        User finalUser = user;
        assertAll(
                () -> assertEquals(userId, finalUser.getId()),
                () -> assertEquals("anyUpdatedName", finalUser.getName()),
                () -> assertEquals("anyUpdatedDob", finalUser.getDob()),
                () -> assertEquals("anyUpdatedGender", finalUser.getGender()),
                () -> assertEquals("anyUpdatedSelfIntroduction", finalUser.getSelfIntroduction()),
                () -> assertEquals("anyUpdatedCreationDate", finalUser.getCreationDate()),
                () -> assertEquals("anyUpdatedHandle", finalUser.getHandle()),
                () -> assertEquals("anyUpdatedStatus", finalUser.getStatus()),
                () -> assertEquals("anyUpdatedNativeLanguage", finalUser.getNativeLanguage()),
                () -> assertEquals("anyUpdatedTargetLanguage", finalUser.getTargetLanguage()),
                () -> assertEquals("anyUpdatedOccupation", finalUser.getOccupation()),
                () -> assertEquals("anyUpdatedCity", finalUser.getHometown().getCity()),
                () -> assertEquals("anyUpdatedCountry", finalUser.getHometown().getCountry())
        );

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyUpdatedInterest", h.getTitle()));
    }

    @Test
    void testUpdateUserDetails_ThrowsExceptionWhenUserIsNotFound() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder().title("anyInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("anyName")
                .dob("anyDob")
                .gender("anyGender")
                .selfIntroduction("anySelfIntroduction")
                .creationDate("anyCreationDate")
                .handle("anyHandle")
                .status("anyStatus")
                .nativeLanguage("anyNativeLanguage")
                .targetLanguage("anyTargetLanguage")
                .occupation("anyOccupation")
                .placesToVisit("anyPlacesToVisit")
                .hometownEntity(buildHometownEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();

        User user = User.buildUserFromEntity(userEntity);

        UserDoesNotExistExistException exception =
                assertThrows(UserDoesNotExistExistException.class, () -> userService.updateUser(userId, user));
        
        assertEquals("No user found with this id", exception.getMessage());
    }
}
