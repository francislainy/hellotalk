package com.example.hellotalk.entity.moment;

import com.example.hellotalk.entity.comment.CommentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.mapper.MomentMapper;
import com.example.hellotalk.model.moment.Moment;
import jakarta.persistence.*;
import lombok.*;

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

    public static MomentEntity fromModel(Moment moment) {
        return MomentMapper.INSTANCE.toEntity(moment);
    }
}
