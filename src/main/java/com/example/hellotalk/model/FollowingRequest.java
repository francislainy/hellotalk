package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowingRequest {

    private static ModelMapper modelMapper = new ModelMapper();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private UUID userToId;
    private UUID userFromId;

    public static FollowingRequest buildFollowingRequestFromEntity(FollowingRequestEntity followingRequestEntity) {
        return modelMapper.map(followingRequestEntity, FollowingRequest.class);
    }
}
