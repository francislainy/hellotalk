package com.example.hellotalk.entity.user;

import com.example.hellotalk.entity.moment.MomentEntity;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "moment_like")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "moment_id", referencedColumnName = "id")
    private MomentEntity momentEntity;
}
