package com.example.hellotalk.entity.followship;

import com.example.hellotalk.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "followship")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowshipEntity {

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
