package com.example.hellotalk.entity.moment;

import com.example.hellotalk.model.moment.Moment;
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

    @Column(name = "text")
    private String text;

    public static MomentEntity buildMomentEntityFromModel(Moment moment) {

        return MomentEntity.builder()
                .id(moment.getId())
                .text(moment.getText())
                .build();
    }
}
