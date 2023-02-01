package com.example.hellotalk.service;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.user.moment.Moment;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.impl.moment.MomentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MomentServiceTest {

    @InjectMocks
    MomentServiceImpl momentService;

    @Mock
    MomentRepository momentRepository;

    @Test
    void testGetMoment() { //todo: fix text as this fails when running as part of this class

        UUID momentId = UUID.fromString("2afff94a-b70e-4b39-bd2a-be1c0f898589");
        MomentEntity momentEntity = MomentEntity.builder().id(momentId).text("anyMoment").build();
        Optional<MomentEntity> momentEntityOptional = Optional.of(momentEntity);
        when(momentRepository.findById(any())).thenReturn(momentEntityOptional);
        Moment moment = momentService.getMoment(momentId);

        assertNotNull(moment);

        assertAll(
                () -> assertEquals(momentId, moment.getId()),
                () -> assertEquals("anyMoment", moment.getText()));
    }

    @Test
    void testGetMoment_ThrowsExceptionMomentDoesNotExist() {

        UUID momentId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        MomentNotFoundException momentNotFoundException = assertThrows(MomentNotFoundException.class, () -> momentService.getMoment(momentId));

        assertEquals(MOMENT_NOT_FOUND_EXCEPTION, momentNotFoundException.getMessage());
    }

}
