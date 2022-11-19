package com.example.hellotalk.model.user;

import com.example.hellotalk.entity.user.HometownEntity;
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

    private UUID id;
    private String city;
    private String country;

    public static Hometown buildHometownFromEntity(HometownEntity hometownEntity) {
        return Hometown.builder()
                .id(hometownEntity.getId())
                .city(hometownEntity.getCity())
                .country(hometownEntity.getCountry())
                .build();
    }
}
