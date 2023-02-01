package com.example.hellotalk.service.moment;

import com.example.hellotalk.model.user.moment.Moment;

import java.util.UUID;

public interface MomentService {

    Moment getMoment(UUID momentId);
}
