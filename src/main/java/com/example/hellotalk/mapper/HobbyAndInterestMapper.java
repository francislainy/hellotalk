package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.example.hellotalk.model.HobbyAndInterest;
import org.mapstruct.Mapper;

@Mapper
public interface HobbyAndInterestMapper {

    HobbyAndInterest toModel(HobbyAndInterestEntity hobbyAndInterestEntity);

    HobbyAndInterestEntity toEntity(HobbyAndInterest hobbyAndInterest);
}
