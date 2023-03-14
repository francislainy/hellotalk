package com.example.hellotalk.entity;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "moment_like")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    private UserEntity userEntity;

    @ManyToOne
    private MomentEntity momentEntity;
}
