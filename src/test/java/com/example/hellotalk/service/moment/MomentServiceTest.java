package com.example.hellotalk.service.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.MomentNotYetLikedException;
import com.example.hellotalk.mapper.MomentMapper;
import com.example.hellotalk.model.ResultInfo;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import com.example.hellotalk.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static java.time.format.DateTimeFormatter.ofPattern;
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
    LikeRepository likeRepository;

    @Mock
    UserService userService;

    @Spy
    MomentMapper momentMapper = Mappers.getMapper(MomentMapper.class);

    final ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now().format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime creationDate = now;
    ZonedDateTime lastUpdatedDate = now;

    @Test
    void testGetMoment_ValidMomentId_ReturnsMoment() {

        UUID momentId = randomUUID();
        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(likeRepository.countByMomentEntityId(any())).thenReturn(1);

        LikeEntity likeEntity = LikeEntity.builder().userEntity(UserEntity.builder().id(userId).build()).momentEntity(momentEntity).build();
        when(likeRepository.findAllByMomentEntityId(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.getMoment(momentId);

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getContent()),
                () -> assertEquals(userEntity.getId(), moment.getUserId()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetMoment_MomentDoesNotExist_ThrowsMomentNotFoundException() {

        UUID momentId = randomUUID();
        when(momentRepository.findById(any())).thenReturn(Optional.empty());

        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.getMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testGetAllMoments_ReturnsListOfMoments() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        UUID momentId = randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);

        when(momentRepository.findAll()).thenReturn(List.of(momentEntity));
        when(likeRepository.countByMomentEntityId(any())).thenReturn(1);

        LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();
        when(likeRepository.findAllByMomentEntityId(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        List<Moment> allMoments = momentService.getAllMoments();
        assertEquals(1, allMoments.size());

        Moment moment = allMoments.get(0);
        assertAll(
                () -> assertEquals(userEntity.getId(), moment.getUserId()),
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getContent()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_ValidUserId_ReturnsListOfMomentsForUser() {

        UUID momentId = randomUUID();
        UUID momentDoesNotBelongToUserId = randomUUID();

        UUID userId = randomUUID();
        UUID anotherUserId = randomUUID();

        MomentEntity momentEntityBelongsToUser = getMomentEntity(momentId);
        momentEntityBelongsToUser.setUserEntity(UserEntity.builder().id(userId).build());

        MomentEntity momentEntityDoesNotBelongToUser = getMomentEntity(momentDoesNotBelongToUserId);
        momentEntityDoesNotBelongToUser.setUserEntity(UserEntity.builder().id(anotherUserId).build());

        when(momentRepository.findAllByUserEntityId((any()))).thenReturn(List.of(momentEntityBelongsToUser));
        when(likeRepository.countByMomentEntityId(any())).thenReturn(1);
        LikeEntity likeEntity = LikeEntity.builder().userEntity(UserEntity.builder().id(userId).build()).momentEntity(momentEntityDoesNotBelongToUser).build();
        when(likeRepository.findAllByMomentEntityId(any())).thenReturn(List.of(likeEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        List<Moment> allMoments = momentService.getAllMomentsForUser(userId);
        assertEquals(1, allMoments.size());

        Moment moment = allMoments.get(0);
        assertAll(
                () -> assertEquals(userId, moment.getUserId()),
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getContent()),
                () -> assertEquals(1, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_UserWithNoMoments_ReturnsEmptyList() {

        List<Moment> moments = momentService.getAllMomentsForUser(randomUUID());
        assertTrue(moments.isEmpty());
    }

    @Test
    void testCreateMoment_ValidMomentBody_ReturnsCreatedMoment() {

        UUID momentId = randomUUID();
        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);
        momentEntity.setNumLikes(0);
        momentEntity.setLikes(new HashSet<>());

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(momentRepository.save(any())).thenReturn(momentEntity);
        when(momentMapper.toEntity(any())).thenReturn(momentEntity);

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.createMoment(momentMapper.toModel(momentEntity));

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getContent()),
                () -> assertEquals(userEntity.getId(), moment.getUserId()),
                () -> assertEquals(0, moment.getNumLikes()),
                () -> assertTrue(moment.getLikedByIds().isEmpty()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testUpdateMoment_ValidMomentIdAndMomentBody_ReturnsUpdatedMoment() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("updatedTag1");
        tagsSet.add("updatedTag2");

        MomentEntity momentEntityUpdated = MomentEntity.builder()
                .id(momentId)
                .content("anyUpdatedText")
                .userEntity(userEntity)
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .numLikes(1)
                .tags(tagsSet)
                .build();

        LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(momentRepository.save(any())).thenReturn(momentEntityUpdated);
        when(likeRepository.countByMomentEntityId(any())).thenReturn(1);
        when(likeRepository.findAllByMomentEntityId(any())).thenReturn(List.of(likeEntity));

        Moment moment = momentMapper.toModel(momentEntity);
        moment = momentService.updateMoment(momentId, moment);

        Moment finalMoment = moment;
        assertAll(
                () -> assertEquals(momentId, finalMoment.getId()),
                () -> assertEquals("anyUpdatedText", finalMoment.getContent()),
                () -> assertEquals(userEntity.getId().toString(), finalMoment.getUserId().toString()),
                () -> assertEquals(1, finalMoment.getNumLikes()),
                () -> assertTrue(finalMoment.getLikedByIds().contains(userId)),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalMoment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(finalMoment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, finalMoment.getTags()));
    }

    @Test
    void testUpdateMoment_MomentNotFound_ThrowsMomentNotFoundException() {

        UUID momentId = randomUUID();
        Moment moment = momentMapper.toModel(getMomentEntity(momentId));
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testUpdateMoment_MomentDoesNotBelongToUser_ThrowsEntityDoesNotBelongToUserException() {

        UserEntity unauthorizedUserEntity = UserEntity.builder().id(randomUUID()).build();
        when(userService.getCurrentUser()).thenReturn(unauthorizedUserEntity);

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();

        UUID momentId = randomUUID();
        Moment moment = momentMapper.toModel(getMomentEntity(momentId));

        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
    }

    @Test
    void testLikeMoment_MomentNotFound_ThrowsMomentNotFoundException() {

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID momentId = randomUUID();
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.likeMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testLikeMoment_MomentBelongsToTheSameActionUser_DoesNotThrowException() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID momentId = randomUUID();

        UserEntity userEntityMomentCreator = UserEntity.builder().id(userId).build();
        LikeEntity likeEntity = LikeEntity.builder().id(randomUUID()).userEntity(userEntity).build();
        Set<LikeEntity> likeEntities = new HashSet<>();
        likeEntities.add(LikeEntity.builder().userEntity(userEntity).build());
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).userEntity(userEntityMomentCreator).likes(likeEntities).build();

        likeEntity.setMomentEntity(momentEntity);

        when(momentRepository.findById(any())).thenReturn(Optional.ofNullable(momentEntity));
        when(likeRepository.save(any())).thenReturn(likeEntity);

        assertDoesNotThrow(() -> momentService.likeMoment(momentId));
    }

    @Test
    void testLikeMoment_ValidMoment_ReturnsSuccessMessage() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID userIdMomentCreator = randomUUID();
        UUID momentId = randomUUID();
        UUID likeEntityId = randomUUID();

        UserEntity userEntityMomentCreator = UserEntity.builder().id(userIdMomentCreator).build();
        LikeEntity likeEntity = LikeEntity.builder().id(likeEntityId).userEntity(userEntity).build();
        Set<LikeEntity> likeEntities = new HashSet<>();
        likeEntities.add(LikeEntity.builder().userEntity(userEntity).build());
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).userEntity(userEntityMomentCreator).likes(likeEntities).build();

        likeEntity.setMomentEntity(momentEntity);

        when(momentRepository.findById(any())).thenReturn(Optional.ofNullable(momentEntity));
        when(likeRepository.save(any())).thenReturn(likeEntity);

        Map<String, Object> responseMap = momentService.likeMoment(momentId);
        ResultInfo resultInfo = (ResultInfo) responseMap.get("data");

        assertAll("Like added",
                () -> assertEquals(likeEntityId, resultInfo.getId()),
                () -> assertEquals(userId, resultInfo.getUserId()),
                () -> assertEquals(momentId, resultInfo.getMomentId()),
                () -> assertEquals("Moment liked successfully", responseMap.get("message")));
    }

    @Test
    void testUnlikeMoment_MomentNotFound_ThrowsMomentNotFoundException() {
        UUID momentId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(momentRepository.existsById(momentId)).thenReturn(false);

        assertThrows(MomentNotFoundException.class, () -> momentService.unlikeMoment(momentId));
    }

    @Test
    void testUnlikeMoment_MomentNotYetLiked_ThrowsMomentNotYetLikedException() {
        UUID momentId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());

        when(userService.getCurrentUser()).thenReturn(userEntity);
        when(momentRepository.existsById(momentId)).thenReturn(true);
        when(likeRepository.findByUserEntityIdAndMomentEntityId(userEntity.getId(), momentId)).thenReturn(Optional.empty());

        assertThrows(MomentNotYetLikedException.class, () -> momentService.unlikeMoment(momentId));
    }

    @Test
    void testDeleteMoment_ValidMoment_ReturnsSuccessMessage() {

        UUID userId = randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userService.getCurrentUser()).thenReturn(userEntity);

        UUID momentId = randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        assertDoesNotThrow(() -> momentService.deleteMoment(momentId));
    }

    @Test
    void testDeleteMoment_MomentNotFound_ThrowsMomentNotFoundException() {

        UUID momentId = randomUUID();
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.deleteMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteMoment_MomentDoesNotBelongToUser_ThrowsEntityDoesNotBelongToUserException() {

        UserEntity userEntityNotOwner = UserEntity.builder().id(randomUUID()).build();
        when(userService.getCurrentUser()).thenReturn(userEntityNotOwner);

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();
        UUID momentId = randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);
        momentEntity.setUserEntity(userEntity);
        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));

        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> momentService.deleteMoment(momentId));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
    }

    // Helpers
    private MomentEntity getMomentEntity(UUID momentId) {

        ZonedDateTime now = ZonedDateTime.now();
        creationDate = ZonedDateTime.parse(now.format(ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        UserEntity userEntity = UserEntity.builder().id(randomUUID()).build();

        return MomentEntity.builder()
                .id(momentId)
                .content("anyText")
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .numLikes(0)
                .likes(new HashSet<>())
                .userEntity(userEntity)
                .tags(tagsSet)
                .build();
    }
}
