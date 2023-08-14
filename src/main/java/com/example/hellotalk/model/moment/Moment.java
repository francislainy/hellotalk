package com.example.hellotalk.model.moment;

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
    private UUID id;
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID userCreatorId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZonedDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime lastUpdatedDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> tags;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer numLikes = 0;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<UUID> likedByIds;

}
