package com.example.hellotalk.mapper;

import com.example.hellotalk.entity.user.HometownEntity;
import com.example.hellotalk.model.Hometown;
import org.mapstruct.Mapper;

@Mapper
public interface HometownMapper {

    HometownEntity toEntity(Hometown hometown);

    Hometown toModel(HometownEntity hometownEntity);
}
