package com.example.hellotalk.entity.comment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.comment.Comment;
import com.example.hellotalk.model.moment.Moment;
import lombok.*;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "moment_id", referencedColumnName = "id")
    private MomentEntity momentEntity;

    static {
        modelMapper.typeMap(Moment.class, CommentEntity.class)
                .addMappings(mapper -> {
                    mapper.map(Moment::getUserCreatorId, (dest, value) -> dest.setUserEntity(UserEntity.builder().id((UUID) value).build()));
                });
    }

    public static CommentEntity buildCommentEntityFromModel(Comment comment) {
        return modelMapper.map(comment, CommentEntity.class);
    }
}
