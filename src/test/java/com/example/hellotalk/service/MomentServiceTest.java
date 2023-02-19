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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime creationDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
    ZonedDateTime lastUpdatedDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

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
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .tags(tagsSet)
                .build();

        when(momentRepository.findById(any())).thenReturn(Optional.of(momentEntity));
        when(momentRepository.save(momentEntity)).thenReturn(momentEntityUpdated);

        Moment moment = buildMomentFromEntity(momentEntity);
        moment = momentService.updateMoment(momentId, moment);

        Moment finalMoment = moment;
        assertAll(
                () -> assertEquals(momentId, finalMoment.getId()),
                () -> assertEquals("anyUpdatedText", finalMoment.getText()),
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

    private MomentEntity getMomentEntity(UUID momentId) {

        ZonedDateTime now = ZonedDateTime.now();
        creationDate = ZonedDateTime.parse(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        Set<String> tagsSet = new HashSet<>();
        tagsSet.add("anyTag1");
        tagsSet.add("anyTag2");

        return MomentEntity.builder()
                .id(momentId)
                .text("anyText")
                .creationDate(creationDate)
                .lastUpdatedDate(lastUpdatedDate)
                .tags(tagsSet)
                .build();
    }

}
