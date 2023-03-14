package com.example.hellotalk.service;

import com.example.hellotalk.entity.LikeEntity;
import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.example.hellotalk.entity.user.HometownEntity.buildHometownEntity;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
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

    @Mock
    MomentRepository momentRepository;

    @Mock
    LikeRepository likeRepository;

    @Mock
    HobbyAndInterestRepository hobbyAndInterestRepository;

    @Mock
    HometownRepository hometownRepository;

    @InjectMocks
    UserServiceImpl userService;

    private final UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        likeRepository = Mockito.mock(LikeRepository.class);
        userService = new UserServiceImpl(userRepository, hobbyAndInterestRepository, hometownRepository, likeRepository, momentRepository);
    }

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
    void testGetAllUsers() {

        UserEntity userEntity = getUserEntity();
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(userEntity);
        when(userRepository.findAll()).thenReturn(userEntityList);

        List<User> allUsers = userService.getAllUsers();
        assertFalse(allUsers.isEmpty());

        User user = allUsers.get(0);
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
    void testGetAllUsers_ReturnsEmptyListIfThereAreNoUsersToBeReturned() {

        List<UserEntity> userEntityList = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(userEntityList);

        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void testCreateUser() {

        UserEntity userEntity = getUserEntity();
        when(hometownRepository.save(any())).thenReturn(userEntity.getHometownEntity());

        Set<HobbyAndInterestEntity> hobbyAndInterestEntities = userEntity.getHobbyAndInterestEntities();
        List<HobbyAndInterestEntity> hobbyAndInterestEntityList = new ArrayList<>(hobbyAndInterestEntities);
        when(hobbyAndInterestRepository.saveAll(any())).thenReturn(hobbyAndInterestEntityList);
        when(userRepository.save(any())).thenReturn(userEntity);

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

        String json = """
                {"message": "User Deleted"}
                """;
        when(userRepository.findById(any())).thenReturn(Optional.of(getUserEntity()));
        assertEquals(json, assertDoesNotThrow(() -> userService.deleteUser(userId)));
    }

    @Test
    void testDeleteUser_ThrowsExceptionUserNotFound() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testLikeMoment_ThrowsExceptionUserNotFound() {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.likeMoment(userId, momentId));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testLikeMoment_ThrowsExceptionMomentNotFound() {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(UserEntity.builder().id(userId).build()));

        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> userService.likeMoment(userId, momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testLikeMoment() {

        UUID userId = randomUUID();
        UUID momentId = randomUUID();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).build();
        LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity));
        when(momentRepository.findById(any())).thenReturn(Optional.ofNullable(momentEntity));
        when(likeRepository.save(any())).thenReturn(likeEntity);

        likeEntity = userService.likeMoment(userId, momentId);

        LikeEntity finalLikeEntity = likeEntity;
        assertAll("Like added",
                () -> assertEquals(userEntity, finalLikeEntity.getUserEntity()),
                () -> assertEquals(momentEntity, finalLikeEntity.getMomentEntity()));
    }
}
