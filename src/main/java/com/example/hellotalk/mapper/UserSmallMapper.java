package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.UserSmall;
import org.mapstruct.Mapper;

@Mapper
public interface UserSmallMapper {
    UserSmall toModel(UserEntity userEntity);
}
