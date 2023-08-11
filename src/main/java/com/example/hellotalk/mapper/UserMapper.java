package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.HobbyAndInterest;
import com.example.hellotalk.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "hobbyAndInterests", source = "hobbyAndInterestEntities")
    @Mapping(target = "hometown", source = "hometownEntity")
    User userEntityToUser(UserEntity userEntity);

    default Set<HobbyAndInterest> mapHobbiesAndInterests(Set<HobbyAndInterestEntity> hobbyAndInterestEntities) {
        return hobbyAndInterestEntities.stream()
                .map(HobbyAndInterest::fromEntity)
                .collect(Collectors.toSet());
    }
}
