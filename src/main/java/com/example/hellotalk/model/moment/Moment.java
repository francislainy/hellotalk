package com.example.hellotalk.model.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moment {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    UUID id;
    String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ZonedDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    ZonedDateTime lastUpdatedDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Set<String> tags;

    public static Moment buildMomentFromEntity(MomentEntity momentEntity) {
        return Moment.builder()
                .id(momentEntity.getId())
                .text(momentEntity.getText())
                .creationDate(momentEntity.getCreationDate())
                .lastUpdatedDate(momentEntity.getLastUpdatedDate())
                .tags(momentEntity.getTags())
                .build();
    }
}
