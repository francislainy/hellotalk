package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.model.moment.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MomentMapper {
    MomentMapper INSTANCE = Mappers.getMapper(MomentMapper.class);

    @Mapping(source = "userCreatorId", target = "userEntity.id")
    MomentEntity toEntity(Moment moment);
}
