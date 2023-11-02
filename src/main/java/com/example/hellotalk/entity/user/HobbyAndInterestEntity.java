package com.example.hellotalk.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
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
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "hobbyAndInterestEntities")
    private Set<UserEntity> userEntities;
}
