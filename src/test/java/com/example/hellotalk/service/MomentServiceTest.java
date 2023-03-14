package com.example.hellotalk.service;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.entity.LikeEntity;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime creationDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime lastUpdatedDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    UUID userCreatorId = UUID.fromString("d3256c76-62d7-4481-9d1c-a0ccc4da380f");

    @Test
    void testGetMoment() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.getMoment(momentId);

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMoments() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);

        when(momentRepository.findAll()).thenReturn(List.of(momentEntity));

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
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_ReturnsOnlyMomentsThatBelongToTheAuthorizedUser() { // todo: refactor this test to check if we need the momentEntityDoesNotBelongToUser variable

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntityBelongsToUser = getMomentEntity(momentId);
        MomentEntity momentEntityDoesNotBelongToUser = getMomentEntity(momentId);
        momentEntityDoesNotBelongToUser.setUserEntity(UserEntity.builder().id(UUID.randomUUID()).build());

        when(momentRepository.findAllByUserEntity_IdContains(String.valueOf(userCreatorId))).thenReturn(List.of(momentEntityBelongsToUser));

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        List<Moment> allMoments = momentService.getAllMomentsForUser(String.valueOf(userCreatorId));
        assertEquals(1, allMoments.size());

        Moment moment = allMoments.get(0);

        assertAll(
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(moment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(moment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, moment.getTags()));
    }

    @Test
    void testGetAllMomentsForUser_ReturnsEmptyListForUserWithNoMoments() {

        List<Moment> moments = momentService.getAllMomentsForUser("invalidAuth");
        assertTrue(moments.isEmpty());
    }

    @Test
    void testCreateMoment() {

        UUID momentId = UUID.randomUUID();
        MomentEntity momentEntity = getMomentEntity(momentId);

        when(momentRepository.save(any())).thenReturn(momentEntity);

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        Moment moment = momentService.createMoment(buildMomentFromEntity(momentEntity), String.valueOf(userCreatorId));

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()),
                () -> assertEquals(userCreatorId, moment.getUserCreatorId()),
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

        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("updatedTag1");
        tagsSet.add("updatedTag2");

        MomentEntity momentEntityUpdated = MomentEntity.builder()
                .id(momentId)
                .text("anyUpdatedText")
                .userEntity(UserEntity.builder().id(userCreatorId).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .tags(tagsSet)
                .build();

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(momentRepository.save(any())).thenReturn(momentEntityUpdated);

        Moment moment = buildMomentFromEntity(momentEntity);
        moment = momentService.updateMoment(momentId, moment, String.valueOf(userCreatorId));

        Moment finalMoment = moment;
        assertAll(
                () -> assertEquals(momentId, finalMoment.getId()),
                () -> assertEquals("anyUpdatedText", finalMoment.getText()),
                () -> assertEquals(userCreatorId, finalMoment.getUserCreatorId()),
                () -> assertEquals(String.valueOf(creationDate), String.valueOf(finalMoment.getCreationDate())),
                () -> assertEquals(String.valueOf(lastUpdatedDate), String.valueOf(finalMoment.getLastUpdatedDate())),
                () -> assertEquals(tagsSet, finalMoment.getTags()));
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenMomentIsNotFound() {

        UUID momentId = randomUUID();
        Moment moment = buildMomentFromEntity(getMomentEntity(momentId));
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.updateMoment(momentId, moment, String.valueOf(randomUUID())));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenUserIsNotAuthorized() {

        UUID momentId = randomUUID();
        Moment moment = buildMomentFromEntity(getMomentEntity(momentId));
        when(momentRepository.findById(any())).thenReturn(Optional.of(getMomentEntity(momentId)));
        EntityDoesNotBelongToUserException exception =
                assertThrows(EntityDoesNotBelongToUserException.class, () -> momentService.updateMoment(momentId, moment, String.valueOf(randomUUID())));

        assertEquals(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION, exception.getMessage());
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
                .userEntity(UserEntity.builder().id(userCreatorId).build())
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .tags(tagsSet)
                .build();
    }
}
