package com.example.hellotalk.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "following_request")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowingRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_to_id", referencedColumnName = "id")
    private UserEntity userToEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_from_id", referencedColumnName = "id")
    private UserEntity userFromEntity;
}
