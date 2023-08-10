package com.example.hellotalk.model.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
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
public class Followship {

    private static ModelMapper modelMapper = new ModelMapper();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private UUID userToId;
    private UUID userFromId;

    public static Followship buildFollowshipFromEntity(FollowshipEntity followshipEntity) {
        return modelMapper.map(followshipEntity, Followship.class);
    }
}
