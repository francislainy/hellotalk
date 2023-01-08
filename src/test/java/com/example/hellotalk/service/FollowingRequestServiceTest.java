package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowerNotFoundException;
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

import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;
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
    void testCreateFollowingRequest() {

        UserEntity userFromEntity = getUserEntity();
        userFromEntity.setId(randomUUID());

        UserEntity userToEntity = getUserEntity();
        userToEntity.setId(randomUUID());

        UUID followingRequestId = UUID.randomUUID();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .id(followingRequestId)
                .userSenderEntity(userFromEntity)
                .userReceiverEntity(userToEntity)
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

        UserEntity userEntity1 = getUserEntity();
        userEntity1.setId(randomUUID());

        UserEntity userEntity2 = getUserEntity();
        userEntity2.setId(randomUUID());

        when(userRepository.findById(userEntity1.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userEntity2.getId())).thenReturn(Optional.of(userEntity2));

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userEntity1.getId())
                .userToId(userEntity2.getId())
                .build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

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

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userEntity1.getId())
                .userToId(userEntity2.getId())
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
                .userSenderEntity(UserEntity.builder().id(userFromId).build())
                .userReceiverEntity(UserEntity.builder().id(userToId).build()).build();
        Set<FollowingRequestEntity> followingRequestSet = new HashSet<>();
        followingRequestSet.add(followingRequestEntity);
        userEntityTo.setFollowedByEntity(followingRequestSet);

        when(userRepository.findById(userEntityFrom.getId())).thenReturn(Optional.of(userEntityFrom));
        when(userRepository.findById(userEntityTo.getId())).thenReturn(Optional.of(userEntityTo));

        FollowingRequest followingRequest = FollowingRequest.builder()
                .userFromId(userFromId)
                .userToId(userToId)
                .build();

        FollowerNotFoundException exception =
                assertThrows(FollowerNotFoundException.class, () -> followingRequestService.createFollowingRequest(followingRequest));

        assertEquals("Already a Follower", exception.getMessage());
    }
}
