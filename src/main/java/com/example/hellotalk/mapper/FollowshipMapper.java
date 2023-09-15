package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.followship.Followship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FollowshipMapper {

    @Mapping(target = "userToId", source = "userToEntity.id")
    @Mapping(target = "userFromId", source = "userFromEntity.id")
    Followship toModel(FollowshipEntity followshipEntity);

    @Mapping(target = "userToEntity.id", source = "userToId")
    @Mapping(target = "userFromEntity.id", source = "userFromId")
    FollowshipEntity toEntity(Followship followship);

    default FollowshipEntity fromUserEntities(UserEntity userFrom, UserEntity userTo) {
        return toEntity(Followship.builder()
                .userFromId(userFrom.getId())
                .userToId(userTo.getId())
                .build());
    }
}
