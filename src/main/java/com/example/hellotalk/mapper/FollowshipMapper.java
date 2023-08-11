package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.model.followship.Followship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FollowshipMapper {

    FollowshipMapper INSTANCE = Mappers.getMapper(FollowshipMapper.class);

    @Mapping(target = "userToId", source = "userToEntity.id")
    @Mapping(target = "userFromId", source = "userFromEntity.id")
    Followship toModel(FollowshipEntity followshipEntity);

    @Mapping(target = "userToEntity.id", source = "userToId")
    @Mapping(target = "userFromEntity.id", source = "userFromId")
    FollowshipEntity toEntity(Followship followship);
}
