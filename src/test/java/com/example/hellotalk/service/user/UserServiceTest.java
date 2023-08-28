package com.example.hellotalk.service.user;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.mapper.HometownMapper;
import com.example.hellotalk.mapper.UserMapper;
import com.example.hellotalk.model.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.HobbyAndInterestRepository;
import com.example.hellotalk.repository.HometownRepository;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HobbyAndInterestRepository hobbyAndInterestRepository;

    @Mock
    private HometownRepository hometownRepository;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final HometownMapper hometownMapper = Mappers.getMapper(HometownMapper.class);

    private final UUID userId = randomUUID();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, hobbyAndInterestRepository, hometownRepository, userMapper);
    }

    @Test
    void testGetUser_ValidUserId_ReturnsUser() {

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
    void testGetAllUsers_ExistingUsersAlreadyLoadedToTheSystem_ReturnsListWithDetailsForEachUser() {

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
    void testGetAllUsers_NoExistentUsers_ReturnsEmptyList() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void testCreateUser_ValidUserBody_ReturnsValidUser() {

        UserEntity userEntity = getUserEntity();
        when(userRepository.save(any())).thenReturn(userEntity);

        User user = userService.createUser(userMapper.toModel(userEntity));
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
    void testUpdateUserDetails_ValidUserBody_UpdatesAndReturnsTheNewUserDetails() {

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
                .hometownEntity(hometownMapper.toEntity(updatedHometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntitiesUpdated)
                .build();

        UserEntity userEntity = getUserEntity();
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any())).thenReturn(userEntityUpdated);

        User user = userMapper.toModel(userEntity);
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
    void testUpdateUserDetails_UserNotFound_ThrowsUserNotFoundException() {

        User user = userMapper.toModel(getUserEntity());
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, user));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteUser_DeletionSuccessful_DoesNotThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(getUserEntity()));
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_ThrowsExceptionUserNotFound() {

        UUID userId = randomUUID();
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
        verify(userRepository, never()).deleteById(userId);
    }

    // Helpers
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
                .hometownEntity(hometownMapper.toEntity(hometown))
                .hobbyAndInterestEntities(hobbyAndInterestEntities)
                .build();
    }
}
