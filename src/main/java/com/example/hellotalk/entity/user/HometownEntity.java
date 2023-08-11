package com.example.hellotalk.entity.user;

import com.example.hellotalk.mapper.HometownMapper;
import com.example.hellotalk.model.Hometown;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "hometown")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HometownEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "hometownEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<UserEntity> userEntitySet;

    public static HometownEntity fromEntity(Hometown hometown) {
        return HometownMapper.INSTANCE.toEntity(hometown);
    }
}
