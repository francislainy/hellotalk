package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.UserSmall;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserSmallMapper {
    UserSmallMapper INSTANCE = Mappers.getMapper(UserSmallMapper.class);

    UserSmall toModel(UserEntity userEntity);
}
