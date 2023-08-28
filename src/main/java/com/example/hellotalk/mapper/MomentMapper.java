package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.model.moment.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(imports = Collectors.class)
public interface MomentMapper {

    @Mapping(source = "userId", target = "userEntity.id")
    MomentEntity toEntity(Moment moment);

    @Mapping(source = "userEntity.id", target = "userId")
    @Mapping(expression = "java(momentEntity.getLikes().stream().map(like -> like.getUserEntity().getId()).collect(Collectors.toSet()))", target = "likedByIds")
    Moment toModel(MomentEntity momentEntity);
}
