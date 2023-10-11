package com.example.hellotalk.entity.message;

import com.example.hellotalk.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "message")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "content")
    private String content;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_to_id", referencedColumnName = "id")
    private UserEntity userToEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_from_id", referencedColumnName = "id")
    private UserEntity userFromEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private MessageEntity parentMessageEntity;

    @OneToMany(mappedBy = "parentMessageEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageEntity> replies;
}
