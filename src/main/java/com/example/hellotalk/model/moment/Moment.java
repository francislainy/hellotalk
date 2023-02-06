package com.example.hellotalk.model.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Moment {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    UUID id;
    String text;

    public static Moment buildMomentFromEntity(MomentEntity momentEntity) {

        return Moment.builder()
                .id(momentEntity.getId())
                .text(momentEntity.getText())
                .build();
    }
}
