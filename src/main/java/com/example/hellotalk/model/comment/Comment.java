package com.example.hellotalk.model.comment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.model.user.UserSmall;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private static final ModelMapper modelMapper = new ModelMapper();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZonedDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime lastUpdatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID momentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserSmall user;

    public static Comment buildCommentFromEntity(CommentEntity commentEntity) {
        return modelMapper.map(commentEntity, Comment.class);
    }
}
