package com.example.hellotalk.entity.moment;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "moment")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MomentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String text;
}
