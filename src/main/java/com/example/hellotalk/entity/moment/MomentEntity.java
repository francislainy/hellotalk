package com.example.hellotalk.entity.moment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.moment.Moment;
import lombok.*;
import org.modelmapper.ModelMapper;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

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

    @OneToMany(mappedBy = "momentEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<CommentEntity> commentEntitySet;

    @OneToMany(mappedBy = "momentEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<LikeEntity> likes = new HashSet<>();

    @Transient
    private Integer numLikes = 0;

    public static MomentEntity buildMomentEntityFromModel(Moment moment) {
        MomentEntity momentEntity = new MomentEntity();
        copyProperties(moment, momentEntity);
        momentEntity.setUserEntity(UserEntity.builder().id(moment.getUserCreatorId()).build());
        return momentEntity;
    }
}
