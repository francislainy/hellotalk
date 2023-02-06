package com.example.hellotalk.service.moment;

import com.example.hellotalk.model.moment.Moment;

import java.util.List;
import java.util.UUID;

public interface MomentService {

    Moment getMoment(UUID momentId);

    List<Moment> getAllMoments();

    Moment createMoment(Moment moment);

    Moment updateMoment(UUID momentId, Moment moment);

    String deleteMoment(UUID momentId);
}
