package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowingRelationshipDoesNotExistException;
import com.example.hellotalk.exception.FollowingRelationshipNotCreatedException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.FollowingRequest;
import com.example.hellotalk.repository.FollowingRequestRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.user.FollowingRequestServiceImpl;
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

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowingRequestServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    FollowingRequestRepository followingRequestRepository;

    @InjectMocks
    FollowingRequestServiceImpl followingRequestService;

    private final UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
    private final UUID followingRequestId = UUID.fromString("2afff94a-b70e-4b39-bd2a-be1c0f898575");

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        followingRequestService = new FollowingRequestServiceImpl(userRepository, followingRequestRepository);
    }

    private UserEntity getUserEntity() {
        return UserEntity.builder()
                .id(userId)
                .build();
    }

    @Test
    void testGetUser() {

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
    void testGetUser_ThrowsExceptionRelationshipDoesNotExist() {

        when(followingRequestRepository.findById(any())).thenThrow(new FollowingRelationshipDoesNotExistException(FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION));

        FollowingRelationshipDoesNotExistException exception = assertThrows(FollowingRelationshipDoesNotExistException.class, () -> followingRequestService.getFollowingRequest(followingRequestId));

        assertEquals(FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION, exception.getMessage());
    }

    @Test
    void testCreateFollowingRequest() {

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        UUID followingRequestId = UUID.randomUUID();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userFromEntity(userFromEntity)
                .userToEntity(userToEntity)
                .build();

        FollowingRequest followingRequest = FollowingRequest.builder()
                .id(followingRequestId)
                .userFromId(userFromEntity.getId())
                .userToId(userToEntity.getId())
                .build();

        when(userRepository.findById(userFromEntity.getId())).thenReturn(Optional.of(userFromEntity));
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));
        when(followingRequestRepository.save(any())).thenReturn(followingRequestEntity);

        followingRequest = followingRequestService.createFollowingRequest(followingRequest);
        FollowingRequest finalFollowingRequest = followingRequest;
        assertDoesNotThrow(() -> finalFollowingRequest);

        assertEquals(userFromEntity.getId(), followingRequest.getUserFromId());
        assertEquals(userToEntity.getId(), followingRequest.getUserToId());
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserFromDoesNotExist() {

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        when(userRepository.findById(userFromEntity.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.of(userToEntity));

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userFromEntity.getId())
                .userToId(userToEntity.getId())
                .build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_ThrowsUserExceptionWhenUserToDoesNotExist() {

        UUID userFromId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");
        UUID userToId = UUID.fromString("2afff94a-b70e-4b39-bd2a-be1c0f898589");
        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(userFromId);

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(userToId);

        when(userRepository.findById(userFromId)).thenReturn(Optional.of(userFromEntity));
        when(userRepository.findById(userToId)).thenReturn(Optional.empty());

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userFromId)
                .userToId(userToId)
                .build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_ThrowsExceptionWhenFollowerIsNotFound() {

        UUID userFromId = randomUUID();
        UUID userToId = randomUUID();
        UserEntity userEntityFrom = getUserEntity();
        userEntityFrom.setId(userFromId);

        UserEntity userEntityTo = getUserEntity();
        userEntityTo.setId(userToId);

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .userFromEntity(UserEntity.builder().id(userFromId).build())
                .userToEntity(UserEntity.builder().id(userToId).build()).build();
        Set<FollowingRequestEntity> followingRequestSet = new HashSet<>();
        followingRequestSet.add(followingRequestEntity);
        userEntityTo.setFollowedByEntity(followingRequestSet);

        when(userRepository.findById(userFromId)).thenReturn(Optional.of(userEntityFrom));
        when(userRepository.findById(userToId)).thenReturn(Optional.of(userEntityTo));

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userFromId)
                .userToId(userToId)
                .build();

        FollowingRelationshipNotCreatedException exception =
                assertThrows(FollowingRelationshipNotCreatedException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

        assertEquals(FOLLOWING_RELATIONSHIP_ALREADY_EXISTS_EXCEPTION, exception.getMessage());
    }
}
