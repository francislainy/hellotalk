package com.example.hellotalk.entity.user;

import com.example.hellotalk.model.user.HobbyAndInterest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "hobby_and_interest")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HobbyAndInterestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "hobbyAndInterestEntities")
    private Set<UserEntity> userEntities;

    public static HobbyAndInterestEntity buildHobbyAndInterestFromModel(HobbyAndInterest hobbyAndInterest) {
        return HobbyAndInterestEntity.builder()
                .id(hobbyAndInterest.getId())
                .title(hobbyAndInterest.getTitle())
                .build();
    }

    public static Set<HobbyAndInterestEntity> buildSetHobbyAndInterestFromEntity(Set<HobbyAndInterest> hobbyAndInterests) {

        Set<HobbyAndInterestEntity> hobbyAndInterestsEntity = new HashSet<>();
        hobbyAndInterests.forEach(
                h -> hobbyAndInterestsEntity.add(buildHobbyAndInterestFromModel(h))
        );

        return hobbyAndInterestsEntity;
    }
    
    
}

