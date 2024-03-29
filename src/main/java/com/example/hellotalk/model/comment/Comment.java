package com.example.hellotalk.model.comment;

import com.example.hellotalk.model.user.UserSnippet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZonedDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime lastUpdatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID momentId;

    private UUID parentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserSnippet user;
}
