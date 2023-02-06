package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowingRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private UUID userToId;
    private UUID userFromId;

    public static FollowingRequest buildFollowingRequestFromEntity(FollowingRequestEntity followingRequestEntity) {

        return FollowingRequest.builder()
                .id(followingRequestEntity.getId())
                .userFromId(followingRequestEntity.getUserFromEntity().getId())
                .userToId(followingRequestEntity.getUserToEntity().getId())
                .build();
    }
}
