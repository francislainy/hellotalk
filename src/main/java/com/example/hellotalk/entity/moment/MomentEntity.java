package com.example.hellotalk.entity.moment;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.moment.Moment;
import lombok.*;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "moment")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MomentEntity {

    private static final ModelMapper modelMapper = new ModelMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "text")
    private String text;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_updated_date")
    private ZonedDateTime lastUpdatedDate;

    @ElementCollection
    @CollectionTable(name = "moment_tags",
            joinColumns = @JoinColumn(name = "moment_id"))
    @Column(name = "tags")
    private Set<String> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @ElementCollection
    @CollectionTable(name = "moment_like",
            joinColumns = @JoinColumn(name = "moment_id"))
    @Column(name = "liked_by")
    private Set<UUID> likedBy = new HashSet<>();

    @Transient
    private Integer numLikes = 0;

    static {
        modelMapper.typeMap(Moment.class, MomentEntity.class)
                .addMappings(mapper -> {
                    mapper.map(Moment::getUserCreatorId, (dest, value) -> dest.setUserEntity(UserEntity.builder().id((UUID) value).build()));
                    mapper.map(Moment::getLikedByIds, MomentEntity::setLikedBy);
                });
    }

    public static MomentEntity buildMomentEntityFromModel(Moment moment) {
        return modelMapper.map(moment, MomentEntity.class);
    }
}
