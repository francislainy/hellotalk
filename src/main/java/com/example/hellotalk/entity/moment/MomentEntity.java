package com.example.hellotalk.entity.moment;

import com.example.hellotalk.model.moment.Moment;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
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

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_updated_date")
    private ZonedDateTime lastUpdatedDate;

    public static MomentEntity buildMomentEntityFromModel(Moment moment) {

        return MomentEntity.builder()
                .id(moment.getId())
                .text(moment.getText())
                .creationDate(moment.getCreationDate())
                .lastUpdatedDate(moment.getLastUpdatedDate())
                .build();
    }
}
