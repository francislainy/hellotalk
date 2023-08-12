package com.example.hellotalk.service.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowshipDeletedException;
import com.example.hellotalk.exception.FollowshipDoesNotExistException;
import com.example.hellotalk.exception.FollowshipNotCreatedUserCantFollowThemselfException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.mapper.FollowshipMapper;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.service.impl.followship.FollowshipServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor
class FollowshipServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    FollowshipRepository followshipRepository;

    FollowshipMapper followshipMapper = Mappers.getMapper(FollowshipMapper.class);

    @InjectMocks
    FollowshipServiceImpl followshipService;

    final UUID userId = randomUUID();
    final UUID followshipId = randomUUID();

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        followshipService = new FollowshipServiceImpl(userRepository, followshipRepository, followshipMapper);
    }

    @Test
    void testGetFollowship() {

        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();
        UUID followshipId = randomUUID();

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        when(followshipRepository.findById(any())).thenReturn(Optional.of(followshipEntity));

        Followship followship = followshipService.getFollowship(followshipId);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userToId, followship.getUserToId()),
                () -> assertEquals(userFromId, followship.getUserFromId()));
    }

    @Test
    void testGetAllFollowships() {

        UUID followshipId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        List<FollowshipEntity> followshipEntityList = new ArrayList<>();
        followshipEntityList.add(followshipEntity);
        when(followshipRepository.findAll()).thenReturn(followshipEntityList);

        List<Followship> allFollowships = followshipService.getAllFollowships();
        assertFalse(allFollowships.isEmpty());

        Followship followship = allFollowships.get(0);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userFromId, followship.getUserFromId()),
                () -> assertEquals(userToId, followship.getUserToId()));
    }

    @Test
    void testGetAllFollowships_ReturnsEmptyListWhenThereAreNoRequestsToBeReturned() {

        List<FollowshipEntity> followshipEntityList = new ArrayList<>();
        when(followshipRepository.findAll()).thenReturn(followshipEntityList);

        assertTrue(followshipService.getAllFollowships().isEmpty());
    }

    @Test
    void testGetAllFollowshipsFromUser() {

        UUID followshipId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        List<FollowshipEntity> followshipEntityList = new ArrayList<>();
        followshipEntityList.add(followshipEntity);
        when(followshipRepository.findFollowshipsByUserFromId(any())).thenReturn(followshipEntityList);

        List<Followship> allFollowships = followshipService.getAllFollowshipsFromUser(userToId);
        assertFalse(allFollowships.isEmpty());

        Followship followship = allFollowships.get(0);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userFromId, followship.getUserFromId()),
                () -> assertEquals(userToId, followship.getUserToId()));
    }

    @Test
    void testGetAllFollowshipsToUser() {

        UUID followshipId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        List<FollowshipEntity> followshipEntityList = new ArrayList<>();
        followshipEntityList.add(followshipEntity);
        when(followshipRepository.findFollowingsByUserToId(any())).thenReturn(followshipEntityList);

        List<Followship> allFollowships = followshipService.getAllFollowshipsToUser(userToId);
        assertFalse(allFollowships.isEmpty());

        Followship followship = allFollowships.get(0);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userFromId, followship.getUserFromId()),
                () -> assertEquals(userToId, followship.getUserToId()));
    }

    @Test
    void testGetUser_ThrowsExceptionRelationshipDoesNotExist() {

        when(followshipRepository.findById(any())).thenThrow(new FollowshipDoesNotExistException(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION));
        FollowshipDoesNotExistException exception = assertThrows(FollowshipDoesNotExistException.class, () -> followshipService.getFollowship(followshipId));
        assertEquals(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION, exception.getMessage());
    }

    @Test
    void testCreateFollowship() {

        setupAuthenticatedUser();
        UUID followshipId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(userFromId);

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(userToId);

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        Followship followship = buildfollowship(followshipId, userToEntity);

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(userRepository.findByUsername(any())).thenReturn(userFromEntity);
        when(followshipRepository.save(any())).thenReturn(followshipEntity);

        followship = followshipService.createFollowship(followship);
        Followship finalFollowship = followship;
        assertDoesNotThrow(() -> finalFollowship);

        assertEquals(userFromEntity.getId(), followship.getUserFromId());
        assertEquals(userToEntity.getId(), followship.getUserToId());
    }

    @Test
    void testCreateFollowship_UnfollowUserIfAlreadyFollowed() {

        setupAuthenticatedUser();
        UUID followshipId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(userFromId);

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(userToId);

        FollowshipEntity followshipEntity = buildFollowshipEntity(followshipId, userFromId, userToId);
        Followship followship = buildfollowship(followshipId, userToEntity);

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(userRepository.findByUsername(any())).thenReturn(userFromEntity);
        when(followshipRepository.findByUserFromIdAndUserToId(any(), any())).thenReturn(Optional.of(followshipEntity));

        assertThrows(FollowshipDeletedException.class, () -> followshipService.createFollowship(followship));
        verify(followshipRepository, times(1)).delete(followshipEntity);
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserFromDoesNotExist() {

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));

        Followship followship = Followship.builder().userFromId(userFromEntity.getId()).userToId(userToEntity.getId()).build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followshipService.createFollowship(followship));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserToDoesNotExist() {

        setupAuthenticatedUser();

        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();
        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(userFromId);

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(userToId);

        when(userRepository.findById(userToId)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(any())).thenReturn(null);

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userToId).build();
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followshipService.createFollowship(followship));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_ThrowsExceptionWhenUserTriesToFollowThemself() {

        setupAuthenticatedUser();

        UUID userToId = randomUUID();

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(userToId);

        when(userRepository.findById(userToId)).thenReturn(Optional.of(userToEntity));
        when(userRepository.findByUsername(any())).thenReturn(userToEntity);

        Followship followship = Followship.builder().userFromId(userToId).userToId(userToId).build();
        FollowshipNotCreatedUserCantFollowThemselfException exception =
                assertThrows(FollowshipNotCreatedUserCantFollowThemselfException.class, () -> followshipService.createFollowship(followship));

        assertEquals(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF, exception.getMessage());
    }

    // Helpers
    private Followship buildfollowship(UUID followshipId, UserEntity userToEntity) {
        return Followship.builder()
                .id(followshipId)
                .userToId(userToEntity.getId())
                .build();
    }

    private FollowshipEntity buildFollowshipEntity(UUID followshipId, UUID userFromId, UUID userToId) {
        return followshipMapper.toEntity(Followship.builder().id(followshipId)
                .userFromId(userFromId)
                .userToId(userToId)
                .build());
    }

    private UserEntity getUserEntity() {
        return UserEntity.builder().id(userId).build();
    }

    public static void setupAuthenticatedUser() {
        // Mocking the SecurityContextHolder and Authentication objects
        SecurityContextHolder.setContext(Mockito.mock(SecurityContext.class));
        Authentication authentication = Mockito.mock(Authentication.class);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("authorizedUser");
    }

}
