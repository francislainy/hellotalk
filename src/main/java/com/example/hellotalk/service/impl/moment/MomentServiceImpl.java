package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.user.moment.Moment;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.moment.MomentService;

import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;

public class MomentServiceImpl implements MomentService {

    final MomentRepository momentRepository;

    public MomentServiceImpl(MomentRepository momentRepository) {
        this.momentRepository = momentRepository;
    }

    @Override
    public Moment getMoment(UUID momentId) {

        Optional<MomentEntity> momentEntityOptional = momentRepository.findById(momentId);

        if (momentEntityOptional.isPresent()) {
            MomentEntity momentEntity = momentEntityOptional.get();
            return Moment.builder()
                    .id(momentEntity.getId())
                    .text(momentEntity.getText())
                    .build();
        }

        else {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }
    }
}
