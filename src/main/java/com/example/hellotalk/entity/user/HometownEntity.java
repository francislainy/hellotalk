package com.example.hellotalk.entity.user;

import com.example.hellotalk.model.Hometown;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "hometown")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HometownEntity {

    private static final ModelMapper modelMapper = new ModelMapper();

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

    public static HometownEntity buildHometownEntity(Hometown hometown) {
        return modelMapper.map(hometown, HometownEntity.class);
    }
}
