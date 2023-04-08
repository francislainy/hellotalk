package com.example.hellotalk.entity.moment;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.moment.Moment;
import lombok.*;

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

    public static MomentEntity buildMomentEntityFromModel(Moment moment) {
        return MomentEntity.builder()
                .id(moment.getId())
                .text(moment.getText())
                .creationDate(moment.getCreationDate())
                .lastUpdatedDate(moment.getLastUpdatedDate())
                .tags(moment.getTags())
                .numLikes(moment.getNumLikes())
                .build();
    }
}
