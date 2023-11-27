package com.example.hellotalk.entity.message;

import com.example.hellotalk.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "chat")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @OneToMany(mappedBy = "chatEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("creationDate")
    private List<MessageEntity> messageEntityList;

    @ManyToMany
    @JoinTable(
            name = "chat_user",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> participantEntityList; //todo: replace by set - 07/11/2023
}

