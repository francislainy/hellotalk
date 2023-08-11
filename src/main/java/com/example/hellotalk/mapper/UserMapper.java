package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "hobbyAndInterestEntities", source = "hobbyAndInterests")
    @Mapping(target = "hometownEntity", source = "hometown")
    UserEntity toEntity(User user);

    @Mapping(target = "hobbyAndInterests", source = "hobbyAndInterestEntities")
    @Mapping(target = "hometown", source = "hometownEntity")
    User toModel(UserEntity userEntity);
}
