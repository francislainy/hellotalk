package com.example.hellotalk.service.moment;

import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.model.moment.Moment;

import java.util.List;
import java.util.UUID;

public interface MomentService {

    Moment getMoment(UUID momentId);

    List<Moment> getAllMoments();

    List<Moment> getAllMomentsForUser(UUID userId);

    Moment createMoment(Moment moment, String authorization);

    Moment updateMoment(UUID momentId, Moment moment);

    String deleteMoment(UUID momentId);

    List<LikeEntity> getLikesByMoment(UUID momentId);
}
