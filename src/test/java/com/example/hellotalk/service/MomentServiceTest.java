package com.example.hellotalk.service;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
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

    @Test
    void testGetMoment() {

        UUID momentId = randomUUID();
        MomentEntity momentEntity = MomentEntity.builder()
                .id(momentId)
                .text("anyMoment")
                .build();

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(momentEntity));
        Moment moment = momentService.getMoment(momentId);

        assertNotNull(moment);

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyMoment", moment.getText()));
    }

    @Test
    void testGetMoment_ThrowsExceptionMomentDoesNotExist() {

        UUID momentId = randomUUID();

        MomentNotFoundException momentNotFoundException = assertThrows(MomentNotFoundException.class, () -> momentService.getMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, momentNotFoundException.getMessage());
    }

    @Test
    void testGetAllMoments() {

        UUID momentId = randomUUID();
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).text("anyText").build();
        List<MomentEntity> momentEntityList = new ArrayList<>();
        momentEntityList.add(momentEntity);
        when(momentRepository.findAll()).thenReturn(momentEntityList);

        List<Moment> allMoments = momentService.getAllMoments();
        assertFalse(allMoments.isEmpty());

        Moment moment = allMoments.get(0);
        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyText", moment.getText()));
    }

    @Test
    void testGetAllMoments_ReturnsEmptyListIfThereAreNoMomentsToBeReturned() {

        List<Moment> allMoments = momentService.getAllMoments();
        assertTrue(allMoments.isEmpty());
    }

    @Test
    void testCreateMoment() {

        MomentEntity momentEntity = MomentEntity.builder().id(randomUUID()).text("anyText").build();
        when(momentRepository.save(any())).thenReturn(momentEntity);

        Moment moment = momentService.createMoment(buildMomentFromEntity(momentEntity));
        assertAll(
                () -> assertNotNull(moment.getId()),
                () -> assertEquals("anyText", moment.getText()));
    }

    @Test
    void testUpdateMomentDetails() {

        UUID momentId = randomUUID();
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).text("anyText").build();

        MomentEntity momentEntityUpdated = MomentEntity.builder()
                .id(momentId)
                .text("anyUpdatedText")
                .build();

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(momentRepository.save(any())).thenReturn(momentEntityUpdated);

        Moment moment = Moment.buildMomentFromEntity(momentEntity);
        moment = momentService.updateMoment(momentId, moment);

        Moment finalMoment = moment;
        assertAll(
                () -> assertEquals(momentId, finalMoment.getId()),
                () -> assertEquals("anyUpdatedText", finalMoment.getText()));
    }

    @Test
    void testUpdateMomentDetails_ThrowsExceptionWhenMomentIsNotFound() {

        UUID momentId = randomUUID();
        Moment moment = Moment.buildMomentFromEntity(MomentEntity.builder().id(momentId).text("anyText").build());
        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.updateMoment(momentId, moment));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }

    @Test
    void testDeleteMoment() {

        String json = """
                {"message": "Moment Deleted"}
                """;
        UUID momentId = randomUUID();
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(MomentEntity.builder().id(momentId).text("anyText").build()));
        assertEquals(json, assertDoesNotThrow(() -> momentService.deleteMoment(momentId)));
    }

    @Test
    void testDeleteMoment_ThrowsExceptionMomentNotFound() {

        UUID momentId = randomUUID();

        MomentNotFoundException exception =
                assertThrows(MomentNotFoundException.class, () -> momentService.deleteMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, exception.getMessage());
    }
}
