package com.example.hellotalk.entity.message;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @OneToMany(mappedBy = "chatEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageEntity> messageEntityList;
}
