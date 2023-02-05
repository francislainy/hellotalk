package com.example.hellotalk.service;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.user.moment.Moment;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
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

}
