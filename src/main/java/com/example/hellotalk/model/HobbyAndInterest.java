package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
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
public class HobbyAndInterest {

    private static final ModelMapper modelMapper = new ModelMapper();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String title;

    public static HobbyAndInterest fromEntity(HobbyAndInterestEntity hobbyAndInterestEntity) {
        return modelMapper.map(hobbyAndInterestEntity, HobbyAndInterest.class);
    }
}
