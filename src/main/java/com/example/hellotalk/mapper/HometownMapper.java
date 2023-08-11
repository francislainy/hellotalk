package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.model.Hometown;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HometownMapper {
    HometownMapper INSTANCE = Mappers.getMapper(HometownMapper.class);

    HometownEntity toEntity(Hometown hometown);
}
