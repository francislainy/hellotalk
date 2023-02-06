package com.example.hellotalk.model.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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
