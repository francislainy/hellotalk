package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.HometownEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hometown {

    private static final ModelMapper modelMapper = new ModelMapper();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String city;
    private String country;

    public static Hometown buildHometownFromEntity(HometownEntity hometownEntity) {
        return new ModelMapper().map(hometownEntity, Hometown.class);
    }
}
