package com.example.hellotalk.service;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.hellotalk.Constants.USER_ID;
import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.moment.Moment.buildMomentFromEntity;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @InjectMocks
    MomentServiceImpl momentService;

    @Mock
    MomentRepository momentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LikeRepository likeRepository;

    ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime creationDate = now;
    ZonedDateTime lastUpdatedDate = now;
    UUID userCreatorId = USER_ID;

    @Test
    void testGetMoment() {

        UUID momentId = randomUUID();
        UUID userId = randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(likeRepository.countLikesByMomentId(any())).thenReturn(1);

        LikeEntity likeEntity = LikeEntity.builder().userEntity(UserEntity.builder().id(userId).build()).momentEntity(momentEntity).build();
        when(likeRepository.findAllByMomentEntity_Id(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.getMoment(momentId);

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetMoment_ThrowsExceptionMomentDoesNotExist() {

        UUID momentId = randomUUID();
        when(momentRepository.findById(any())).thenReturn(Optional.empty());

        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.getMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testGetAllMoments() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        UUID userId = randomUUID();

        when(momentRepository.findAll()).thenReturn(List.of(momentEntity));
        when(likeRepository.countLikesByMomentId(any())).thenReturn(1);

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();
        when(likeRepository.findAllByMomentEntity_Id(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        List<Moment> allMoments = momentService.getAllMoments();
        assertEquals(1, allMoments.size());

        Moment moment = allMoments.get(0);
        assertAll(
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_ReturnsOnlyMomentsThatBelongToTheAuthorizedUser() { // todo: refactor this test to check if we need the momentEntityDoesNotBelongToUser variable

        UUID momentId = UUID.randomUUID();
        UUID momentIdDoesNotBelongToUser = UUID.randomUUID();
        MomentEntity momentEntityBelongsToUser = getMomentEntity(momentId);
        MomentEntity momentEntityDoesNotBelongToUser = getMomentEntity(momentIdDoesNotBelongToUser);
        UUID userId = randomUUID();
        momentEntityDoesNotBelongToUser.setUserEntity(UserEntity.builder().id(userId).build());

        when(momentRepository.findAllByUserEntity_IdContains((any()))).thenReturn(List.of(momentEntityBelongsToUser));
        when(likeRepository.countLikesByMomentId(any())).thenReturn(1);
        LikeEntity likeEntity = LikeEntity.builder().userEntity(UserEntity.builder().id(userId).build()).momentEntity(momentEntityDoesNotBelongToUser).build();
        when(likeRepository.findAllByMomentEntity_Id(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        List<Moment> allMoments = momentService.getAllMomentsForUser(userCreatorId);
        assertEquals(1, allMoments.size());

        Moment moment = allMoments.get(0);
        assertAll(
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_ReturnsEmptyListForUserWithNoMoments() {

        List<Moment> moments = momentService.getAllMomentsForUser(randomUUID());
        assertTrue(moments.isEmpty());
    }

    @Test
    void testCreateMoment() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setNumLikes(0);

        when(momentRepository.save(any())).thenReturn(momentEntity);
        UserEntity userEntity = UserEntity.builder().id(userCreatorId).build();
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.createMoment(buildMomentFromEntity(momentEntity), String.valueOf(userCreatorId));

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(0, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().isEmpty()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testCreateMoment_ThrowsErrorIfAuthorizationInvalid() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);

        assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(buildMomentFromEntity(momentEntity), "invalidAuth"));
    }

    @Test
    void testUpdateMomentDetails() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        UUID userId = randomUUID();

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("updatedTag1");
        tagsSet.add("updatedTag2");

        MomentEntity momentEntityUpdated = MomentEntity.builder()
                .id(momentId)
                .text("anyUpdatedText")
                .userEntity(UserEntity.builder().id(userCreatorId).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .numLikes(1)
                .tags(tagsSet)
                .build();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userEntity));
        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(momentRepository.save(any())).thenReturn(momentEntityUpdated);
        when(likeRepository.countLikesByMomentId(any())).thenReturn(1);
        when(likeRepository.findAllByMomentEntity_Id(any())).thenReturn(List.of(likeEntity));

        Moment moment = buildMomentFromEntity(momentEntity);
        moment = momentService.updateMoment(momentId, moment);

        Moment finalMoment = moment;
        assertAll(
                () -> assertEquals(momentId, finalMoment.getId()),
                () -> assertEquals("anyUpdatedText", finalMoment.getText()),
                () -> assertEquals(userCreatorId, finalMoment.getUserCreatorId()),
                () -> assertEquals(1, finalMoment.getNumLikes()),
                () -> assertTrue(finalMoment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalMoment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(finalMoment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, finalMoment.getTags()));
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenMomentIsNotFound() {

        UUID momentId = randomUUID();
        Moment moment = buildMomentFromEntity(getMomentEntity(momentId));
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenDoesNotExist() {

        UUID momentId = randomUUID();
        Moment moment = buildMomentFromEntity(getMomentEntity(momentId));

        MomentEntity momentEntity = getMomentEntity(momentId);

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        UserNotFoundException exception =
                assertThrows(UserNotFoundException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(USER_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenUserIsNotAuthorized() {

        UUID momentId = randomUUID();
        Moment moment = buildMomentFromEntity(getMomentEntity(momentId));

        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(UserEntity.builder().id(randomUUID()).build());

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteMoment() {

        String json = """
                {"message": "Moment Deleted"}
                """;

        UUID momentId = randomUUID();
        when(momentRepository.findById(any())).thenReturn(Optional.of(getMomentEntity(momentId)));
        assertEquals(json, assertDoesNotThrow(() -> momentService.deleteMoment(momentId)));
    }

    @Test
    void testDeleteMoment_ThrowsExceptionWhenMomentIsNotFound() {

        UUID momentId = randomUUID();
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.deleteMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    private MomentEntity getMomentEntity(UUID momentId) {

        ZonedDateTime now = ZonedDateTime.now();
        creationDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        return MomentEntity.builder()
                .id(momentId)
                .text("anyText")
                .userEntity(UserEntity.builder().id(USER_ID).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .numLikes(10)
                .tags(tagsSet)
                .build();
    }
}
