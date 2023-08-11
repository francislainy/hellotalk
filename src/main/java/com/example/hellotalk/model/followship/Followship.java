package com.example.hellotalk.model.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.mapper.FollowshipMapper;
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
public class Followship {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private UUID userToId;
    private UUID userFromId;

    public static Followship fromEntity(FollowshipEntity followshipEntity) {
        return FollowshipMapper.INSTANCE.toModel(followshipEntity);
    }
}
