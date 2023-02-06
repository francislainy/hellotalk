package com.example.hellotalk.model;

import com.example.hellotalk.entity.user.HobbyAndInterestEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HobbyAndInterest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;
    private String title;

    public static HobbyAndInterest buildHobbyAndInterestFromEntity(HobbyAndInterestEntity hobbyAndInterestEntity) {
        return HobbyAndInterest.builder()
                .id(hobbyAndInterestEntity.getId())
                .title(hobbyAndInterestEntity.getTitle())
                .build();
    }

    public static Set<HobbyAndInterest> buildSetHobbyAndInterestFromEntity(Set<HobbyAndInterestEntity> hobbyAndInterestEntities) {

        Set<HobbyAndInterest> hobbyAndInterests = new HashSet<>();
        hobbyAndInterestEntities.forEach(
                h -> hobbyAndInterests.add(buildHobbyAndInterestFromEntity(h)));

        return hobbyAndInterests;
    }

}
