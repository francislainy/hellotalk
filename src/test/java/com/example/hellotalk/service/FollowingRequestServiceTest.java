package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowingRelationshipDeletedException;
import com.example.hellotalk.exception.FollowingRelationshipDoesNotExistException;
import com.example.hellotalk.exception.FollowingRelationshipNotCreatedException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.repository.FollowingRequestRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.FollowingRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
class FollowingRequestServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    FollowingRequestRepository followingRequestRepository;

    @InjectMocks
    FollowingRequestServiceImpl followingRequestService;

    final UUID userId = randomUUID();
    final UUID followingRequestId = randomUUID();

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        followingRequestService = new FollowingRequestServiceImpl(userRepository, followingRequestRepository);
    }

    @Test
    void testGetFollowingRequest() {

        UUID userToId = randomUUID();
        UUID userFromId = randomUUID();
        UUID followingRequestId = randomUUID();

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(UserEntity.builder().id(userFromId).build())
                .userToEntity(UserEntity.builder().id(userToId).build())
                .build();

        when(followingRequestRepository.findById(any())).thenReturn(Optional.of(followingRequestEntity));

        FollowingRequest followingRequest = followingRequestService.getFollowingRequest(followingRequestId);

        assertAll(
                () -> assertEquals(followingRequestId, followingRequest.getId()),
                () -> assertEquals(userToId, followingRequest.getUserToId()),
                () -> assertEquals(userFromId, followingRequest.getUserFromId()));
    }

    @Test
    void testGetAllFollowingRequests() {

        UUID followingRequestId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(UserEntity.builder().id(userFromId).build())
                .userToEntity(UserEntity.builder().id(userToId).build())
                .build();
        List<FollowingRequestEntity> followingEntityList = new ArrayList<>();
        followingEntityList.add(followingRequestEntity);
        when(followingRequestRepository.findAll()).thenReturn(followingEntityList);

        List<FollowingRequest> allFollowingRequests = followingRequestService.getAllFollowingRequests();
        assertFalse(allFollowingRequests.isEmpty());

        FollowingRequest followingRequest = allFollowingRequests.get(0);
        assertAll(
                () -> assertEquals(followingRequestId, followingRequest.getId()),
                () -> assertEquals(userFromId, followingRequest.getUserFromId()),
                () -> assertEquals(userToId, followingRequest.getUserToId()));
    }

    @Test
    void testGetAllFollowingRequests_ReturnsEmptyListWhenThereAreNoRequestsToBeReturned() {

        List<FollowingRequestEntity> followingEntityList = new ArrayList<>();
        when(followingRequestRepository.findAll()).thenReturn(followingEntityList);

        assertTrue(followingRequestService.getAllFollowingRequests().isEmpty());
    }

    @Test
    void testGetAllFollowingRequestsFromUser() {

        UUID followingRequestId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(UserEntity.builder().id(userFromId).build())
                .userToEntity(UserEntity.builder().id(userToId).build())
                .build();
        List<FollowingRequestEntity> followingEntityList = new ArrayList<>();
        followingEntityList.add(followingRequestEntity);
        when(followingRequestRepository.findFollowingRequestEntitiesByUserFromId(any())).thenReturn(followingEntityList);

        List<FollowingRequest> allFollowingRequests = followingRequestService.getAllFollowingRequestsFromUser(userToId);
        assertFalse(allFollowingRequests.isEmpty());

        FollowingRequest followingRequest = allFollowingRequests.get(0);
        assertAll(
                () -> assertEquals(followingRequestId, followingRequest.getId()),
                () -> assertEquals(userFromId, followingRequest.getUserFromId()),
                () -> assertEquals(userToId, followingRequest.getUserToId()));
    }

    @Test
    void testGetAllFollowingRequestsToUser() {

        UUID followingRequestId = randomUUID();
        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(UserEntity.builder().id(userFromId).build())
                .userToEntity(UserEntity.builder().id(userToId).build())
                .build();
        List<FollowingRequestEntity> followingEntityList = new ArrayList<>();
        followingEntityList.add(followingRequestEntity);
        when(followingRequestRepository.findFollowingRequestEntitiesByUserToId(any())).thenReturn(followingEntityList);

        List<FollowingRequest> allFollowingRequests = followingRequestService.getAllFollowingRequestsToUser(userToId);
        assertFalse(allFollowingRequests.isEmpty());

        FollowingRequest followingRequest = allFollowingRequests.get(0);
        assertAll(
                () -> assertEquals(followingRequestId, followingRequest.getId()),
                () -> assertEquals(userFromId, followingRequest.getUserFromId()),
                () -> assertEquals(userToId, followingRequest.getUserToId()));
    }

    @Test
    void testGetUser_ThrowsExceptionRelationshipDoesNotExist() {

        when(followingRequestRepository.findById(any())).thenThrow(new FollowingRelationshipDoesNotExistException(FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION));

        FollowingRelationshipDoesNotExistException exception = assertThrows(FollowingRelationshipDoesNotExistException.class, () -> followingRequestService.getFollowingRequest(followingRequestId));

        assertEquals(FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION, exception.getMessage());
    }

    @Test
    void testCreateFollowingRequest() {

        setupAuthenticatedUser();

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        UUID followingRequestId = UUID.randomUUID();
        FollowingRequestEntity followingRequestEntity = buildFollowingRequestEntity(followingRequestId, userFromEntity, userToEntity);
        FollowingRequest followingRequest = buildFollowingRequest(followingRequestId, userToEntity);

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(userRepository.findByUsername(any())).thenReturn(userFromEntity);
        when(followingRequestRepository.save(any())).thenReturn(followingRequestEntity);

        followingRequest = followingRequestService.createFollowingRequest(followingRequest);
        FollowingRequest finalFollowingRequest = followingRequest;
        assertDoesNotThrow(() -> finalFollowingRequest);

        assertEquals(userFromEntity.getId(), followingRequest.getUserFromId());
        assertEquals(userToEntity.getId(), followingRequest.getUserToId());
    }

    @Test
    void testCreateFollowingRequest_UnfollowUserIfAlreadyFollowed() {

        setupAuthenticatedUser();

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        UUID followingRequestId = UUID.randomUUID();
        FollowingRequestEntity followingRequestEntity = buildFollowingRequestEntity(followingRequestId, userFromEntity, userToEntity);
        FollowingRequest followingRequest = buildFollowingRequest(followingRequestId, userToEntity);

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(userRepository.findByUsername(any())).thenReturn(userFromEntity);
        when(followingRequestRepository.findByUserFromIdAndUserToId(any(), any())).thenReturn(Optional.of(followingRequestEntity));

        assertThrows(FollowingRelationshipDeletedException.class, () -> followingRequestService.createFollowingRequest(followingRequest));
        verify(followingRequestRepository, times(1)).delete(followingRequestEntity);
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserFromDoesNotExist() {

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userFromEntity.getId()).userToId(userToEntity.getId()).build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

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

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userFromId).userToId(userToId).build();
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

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

        FollowingRequest followingRequest = FollowingRequest.builder().userFromId(userToId).userToId(userToId).build();
        FollowingRelationshipNotCreatedException exception =
                assertThrows(FollowingRelationshipNotCreatedException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

        assertEquals(USER_TO_AND_FROM_CANT_BE_THE_SAME, exception.getMessage());
    }

    // Helpers
    private FollowingRequest buildFollowingRequest(UUID followingRequestId, UserEntity userToEntity) {
        return FollowingRequest.builder()
                .id(followingRequestId)
                .userToId(userToEntity.getId())
                .build();
    }

    private FollowingRequestEntity buildFollowingRequestEntity(UUID followingRequestId, UserEntity userFromEntity, UserEntity userToEntity) {
        return FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(userFromEntity)
                .userToEntity(userToEntity)
                .build();
    }

    private UserEntity getUserEntity() {
        return UserEntity.builder()
                .id(userId)
                .build();
    }

    public static void setupAuthenticatedUser() {
        // Mocking the SecurityContextHolder and Authentication objects
        SecurityContextHolder.setContext(Mockito.mock(SecurityContext.class));
        Authentication authentication = Mockito.mock(Authentication.class);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("authorizedUser");
    }

}
