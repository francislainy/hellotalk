package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowerNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.example.hellotalk.entity.user.HometownEntity.buildHometownEntity;
import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    private final UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    private UserEntity getUserEntity() {
        Hometown hometown = Hometown.builder().city("anyCity").country("anyCountry").build();
        HobbyAndInterestEntity hobbyAndInterestEntity = HobbyAndInterestEntity.builder().title("anyInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = new HashSet<>();
        hobbyAndInterestEntities.add(hobbyAndInterestEntity);

        return UserEntity.builder()
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
                .subscriptionType("anySubscriptionType")
                .hometownEntity(buildHometownEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
    }

    @Test
    void testGetUser() {

        Optional<UserEntity> userEntity = Optional.of(getUserEntity());
        when(userRepository.findById(any())).thenReturn(userEntity);

        User user = userService.getUser(randomUUID());
        assertAll(
                () -> assertEquals(userId, user.getId()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anySubscriptionType", user.getSubscriptionType()),
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
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit()));

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testCreateUser() {

        UserEntity userEntity = getUserEntity();
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User user = userService.createUser(User.buildUserFromEntity(userEntity));
        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anyDob", user.getDob()),
                () -> assertEquals("anyGender", user.getGender()),
                () -> assertEquals("anySubscriptionType", user.getSubscriptionType()),
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
                () -> assertEquals("anyPlacesToVisit", user.getPlacesToVisit()));

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyInterest", h.getTitle()));
    }

    @Test
    void testUpdateUserDetails() {

        HobbyAndInterestEntity hobbyAndInterestEntityUpdated = HobbyAndInterestEntity.builder().title("anyUpdatedInterest").build();
        Set<HobbyAndInterestEntity> hobbyAndInterestEntitiesUpdated = new HashSet<>();
        hobbyAndInterestEntitiesUpdated.add(hobbyAndInterestEntityUpdated);
        Hometown updatedHometown = Hometown.builder().city("anyUpdatedCity").country("anyUpdatedCountry").build();
        UserEntity userEntityUpdated = UserEntity.builder()
                .id(userId)
                .name("anyUpdatedName")
                .dob("anyUpdatedDob")
                .gender("anyUpdatedGender")
                .subscriptionType("anyUpdatedSubscriptionType")
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

        when(userRepository.findById(any())).thenReturn(Optional.of(getUserEntity()));
        when(userRepository.save(any())).thenReturn(userEntityUpdated);

        User user = User.buildUserFromEntity(getUserEntity());
        user = userService.updateUser(userId, user);

        User finalUser = user;
        assertAll(
                () -> assertEquals(userId, finalUser.getId()),
                () -> assertEquals("anyUpdatedName", finalUser.getName()),
                () -> assertEquals("anyUpdatedDob", finalUser.getDob()),
                () -> assertEquals("anyUpdatedGender", finalUser.getGender()),
                () -> assertEquals("anyUpdatedSubscriptionType", finalUser.getSubscriptionType()),
                () -> assertEquals("anyUpdatedSelfIntroduction", finalUser.getSelfIntroduction()),
                () -> assertEquals("anyUpdatedCreationDate", finalUser.getCreationDate()),
                () -> assertEquals("anyUpdatedHandle", finalUser.getHandle()),
                () -> assertEquals("anyUpdatedStatus", finalUser.getStatus()),
                () -> assertEquals("anyUpdatedNativeLanguage", finalUser.getNativeLanguage()),
                () -> assertEquals("anyUpdatedTargetLanguage", finalUser.getTargetLanguage()),
                () -> assertEquals("anyUpdatedOccupation", finalUser.getOccupation()),
                () -> assertEquals("anyUpdatedCity", finalUser.getHometown().getCity()),
                () -> assertEquals("anyUpdatedCountry", finalUser.getHometown().getCountry()));

        user.getHobbyAndInterests().forEach(h -> assertEquals("anyUpdatedInterest", h.getTitle()));
    }

    @Test
    void testUpdateUserDetails_ThrowsExceptionWhenUserIsNotFound() {

        User user = User.buildUserFromEntity(getUserEntity());
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, user));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteUser() {

        when(userRepository.findById(any())).thenReturn(Optional.of(getUserEntity()));
        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @Test
    void testDeleteUser_ThrowsExceptionUserNotFound() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_DoesNotThrowExceptionWhenBothUsersExist() {

        UserEntity userEntity1 = getUserEntity();
        userEntity1.setId(randomUUID());

        UserEntity userEntity2 = getUserEntity();
        userEntity2.setId(randomUUID());

        when(userRepository.findById(userEntity1.getId())).thenReturn(Optional.of(userEntity1));
        when(userRepository.findById(userEntity2.getId())).thenReturn(Optional.of(userEntity2));
        when(userRepository.save(any())).thenReturn(userEntity1);

        assertDoesNotThrow(() -> userService.followUser(userEntity1.getId(), userEntity2.getId()));
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserFromDoesNotExist() {

        UserEntity userEntity1 = getUserEntity();
        userEntity1.setId(randomUUID());

        UserEntity userEntity2 = getUserEntity();
        userEntity2.setId(randomUUID());

        when(userRepository.findById(userEntity1.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userEntity2.getId())).thenReturn(Optional.of(userEntity2));

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.followUser(userEntity1.getId(), userEntity2.getId()));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserToDoesNotExist() {

        UUID userId1 = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        UUID userId2 = UUID.fromString("2afff94a-b70e-4b39-bd2a-be1c0f898589");
        UserEntity userEntity1 = getUserEntity();
        userEntity1.setId(userId1);

        UserEntity userEntity2 = getUserEntity();
        userEntity2.setId(userId2);

        when(userRepository.findById(userEntity1.getId())).thenReturn(Optional.of(userEntity1));
        when(userRepository.findById(userEntity2.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.followUser(userEntity1.getId(), userEntity2.getId()));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_DoesNotThrowExceptionWhenFollowerIsFound() {

        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();
        UserEntity userEntityFrom = getUserEntity();
        userEntityFrom.setId(userFromId);

        UserEntity userEntityTo = getUserEntity();
        userEntityTo.setId(userToId);

        Set<FollowingRequestEntity> followingRequestEntities = new HashSet<>();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder().userSenderEntity(userEntityFrom).userReceiverEntity(userEntityTo).build();
        followingRequestEntities.add(followingRequestEntity);

        when(userRepository.findById(userEntityFrom.getId())).thenReturn(Optional.of(userEntityFrom));
        when(userRepository.findById(userEntityTo.getId())).thenReturn(Optional.of(userEntityTo));

        userEntityTo.setFollowedByEntity(followingRequestEntities);

        when(userRepository.save(any())).thenReturn(userEntityTo);
        userEntityFrom = userRepository.save(userEntityFrom);

        assertDoesNotThrow(() -> userService.followUser(userFromId, userToId));
    }

    @Test
    void testFollowUser_ThrowsExceptionWhenFollowerIsNotFound() {

        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();
        UserEntity userEntityFrom = getUserEntity();
        userEntityFrom.setId(userFromId);

        UserEntity userEntityTo = getUserEntity();
        userEntityTo.setId(userToId);

        when(userRepository.findById(userEntityFrom.getId())).thenReturn(Optional.of(userEntityFrom));
        when(userRepository.findById(userEntityTo.getId())).thenReturn(Optional.of(userEntityTo));

        userEntityTo.setId(null);
        when(userRepository.save(userEntityTo)).thenReturn(userEntityTo);

        FollowerNotFoundException exception =
                assertThrows(FollowerNotFoundException.class, () -> userService.followUser(userFromId, userToId));

        assertEquals("Error saving follower", exception.getMessage());
    }
}
