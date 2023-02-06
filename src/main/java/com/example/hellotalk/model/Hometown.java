package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.HometownEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hometown {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String city;
    private String country;

    public static Hometown buildHometownFromEntity(HometownEntity hometownEntity) {

        if (hometownEntity != null) {

            return Hometown.builder()
                    .id(hometownEntity.getId())
                    .city(hometownEntity.getCity())
                    .country(hometownEntity.getCountry())
                    .build();
        } else {
            return null;
        }
    }
}
