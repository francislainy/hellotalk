package com.example.hellotalk.service.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowshipAlreadyExistsException;
import com.example.hellotalk.exception.FollowshipDoesNotExistException;
import com.example.hellotalk.exception.FollowshipNotCreatedUserCantFollowThemselfException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.mapper.FollowshipMapper;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.service.impl.followship.FollowshipServiceImpl;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    UserService userService;

    @InjectMocks
    FollowshipServiceImpl followshipService;

    final FollowshipMapper followshipMapper = Mappers.getMapper(FollowshipMapper.class);

    final UUID followshipId = randomUUID();
    final UUID userToId = randomUUID();
    final UUID userFromId = randomUUID();

    FollowshipEntity followshipEntity;
    UserEntity userFromEntity;
    UserEntity userToEntity;

    @BeforeEach
    void setUp() {
        followshipService = new FollowshipServiceImpl(userRepository, followshipRepository, userService, followshipMapper);

        userFromEntity = UserEntity.builder().id(userFromId).build();
        userToEntity = UserEntity.builder().id(userToId).build();
        followshipEntity = followshipMapper.toEntity(Followship.builder().id(followshipId)
                .userFromId(userFromId)
                .userToId(userToId)
                .build());
    }

    @Test
    void testGetFollowship_ValidFollowshipId_ReturnsMoment() {
        when(followshipRepository.findById(any())).thenReturn(Optional.of(followshipEntity));

        Followship followship = followshipService.getFollowship(followshipId);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userToId, followship.getUserToId()),
                () -> assertEquals(userFromId, followship.getUserFromId()));
    }

    @Test
    void testGetAllFollowships_ReturnsListOfFollowships() {
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
    void testGetAllFollowships_UserWithNoFollowships_ReturnsEmptyList() {
        when(followshipRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(followshipService.getAllFollowships().isEmpty());
    }

    @Test
    void testGetAllFollowshipsFromUser_UserFollowingOtherUsers_ReturnsUsersFollowedByTheActionUser() {
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
    void testGetAllFollowshipsToUser_UserFollowedByOtherUsers_ReturnsUsersFollowersOfTheActionUser() {
        List<FollowshipEntity> followshipEntityList = new ArrayList<>();
        followshipEntityList.add(followshipEntity);
        when(followshipRepository.findFollowshipsByUserToId(any())).thenReturn(followshipEntityList);

        List<Followship> allFollowships = followshipService.getAllFollowshipsToUser(userToId);
        assertFalse(allFollowships.isEmpty());

        Followship followship = allFollowships.get(0);
        assertAll(
                () -> assertEquals(followshipId, followship.getId()),
                () -> assertEquals(userFromId, followship.getUserFromId()),
                () -> assertEquals(userToId, followship.getUserToId()));
    }

    @Test
    void testGetUser_FollowshipDoesNotExist_ThrowsFollowshipDoesNotExistException() {
        when(followshipRepository.findById(any())).thenThrow(new FollowshipDoesNotExistException(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION));
        FollowshipDoesNotExistException exception = assertThrows(FollowshipDoesNotExistException.class, () -> followshipService.getFollowship(followshipId));
        assertEquals(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION, exception.getMessage());
    }

    @Test
    void testCreateFollowship_ValidFollowshipBody_ReturnsCreatedFollowship() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);
        when(userRepository.findById(userToId)).thenReturn(Optional.of(userToEntity));
        when(followshipRepository.save(any())).thenReturn(followshipEntity);

        Followship followship = Followship.builder().id(followshipId).userToId(userToId).build();
        followship = followshipService.createFollowship(followship);

        Followship finalFollowship = followship;
        assertDoesNotThrow(() -> finalFollowship);

        assertEquals(userFromEntity.getId(), followship.getUserFromId());
        assertEquals(userToEntity.getId(), followship.getUserToId());
    }

    @Test
    void testCreateFollowship_FollowshipAlreadyExists_DeletesFollowshipAndUnfollowUser() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);
        when(userRepository.findById(userToId)).thenReturn(Optional.of(userToEntity));
        when(followshipRepository.findByUserFromIdAndUserToId(any(), any())).thenReturn(Optional.of(followshipEntity));

        Followship followship = Followship.builder().id(followshipId).userToId(userToId).build();

        assertThrows(FollowshipAlreadyExistsException.class, () -> followshipService.createFollowship(followship));
        verify(followshipRepository, never()).delete(followshipEntity);
    }

    @Test
    void testFollowUser_ToUserDoesNotExist_UserNotFoundException() {
        when(userService.getCurrentUser()).thenReturn(userFromEntity);
        when(userRepository.findById(userToEntity.getId())).thenReturn(Optional.empty());

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userToId).build();

        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followshipService.createFollowship(followship));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_FromUserDoesNotExist_ThrowsUserNotFoundException() {
        when(userService.getCurrentUser()).thenReturn(null);
        when(userRepository.findById(userToId)).thenReturn(Optional.empty());

        Followship followship = Followship.builder().userToId(userToId).build();
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> followshipService.createFollowship(followship));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testFollowUser_UserTriesToFollowThemself_ThrowsFollowshipNotCreatedUserCantFollowThemselfExceptionlf() {
        when(userRepository.findById(userFromId)).thenReturn(Optional.of(userFromEntity));
        when(userService.getCurrentUser()).thenReturn(userFromEntity);

        Followship followship = Followship.builder().userFromId(userFromId).userToId(userFromId).build();
        FollowshipNotCreatedUserCantFollowThemselfException exception =
                assertThrows(FollowshipNotCreatedUserCantFollowThemselfException.class, () -> followshipService.createFollowship(followship));

        assertEquals(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF, exception.getMessage());
    }

    @Test
    void testDeleteFollowship_ValidFollowship_FollowshipRemoved() {
        when(followshipRepository.findById(any())).thenReturn(Optional.of(followshipEntity));
        when(userService.getCurrentUser()).thenReturn(userFromEntity);

        assertDoesNotThrow(() -> followshipService.deleteFollowship(followshipId));

        verify(followshipRepository, times(1)).delete(followshipEntity);
    }
}
