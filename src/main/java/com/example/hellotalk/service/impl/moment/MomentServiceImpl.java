package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.moment.MomentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.entity.moment.MomentEntity.buildMomentEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.model.moment.Moment.buildMomentFromEntity;

@Service
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
        } else {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public List<Moment> getAllMoments() {

        List<Moment> momentList = new ArrayList<>();
        List<MomentEntity> momentEntityList = momentRepository.findAll();

        if (!momentEntityList.isEmpty()) {
            momentEntityList.forEach(momentEntity -> momentList.add(buildMomentFromEntity(momentEntity)));
        }

        return momentList;
    }

    @Override
    public Moment createMoment(Moment moment) {

        MomentEntity momentEntity = buildMomentEntityFromModel(moment);
        momentEntity = momentRepository.save(momentEntity);

        return buildMomentFromEntity(momentEntity);
    }

    @Override
    public Moment updateMoment(UUID momentId, Moment moment) {

        Optional<MomentEntity> momentEntityOptional = momentRepository.findById(momentId);
        if (momentEntityOptional.isPresent()) {
            MomentEntity momentEntity = buildMomentEntityFromModel(moment);
            momentEntity.setId(momentId);
            momentEntity = momentRepository.save(momentEntity);
            return buildMomentFromEntity(momentEntity);
        } else {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public String deleteMoment(UUID momentId) {

        Optional<MomentEntity> optionalMomentEntity = momentRepository.findById(momentId);
        String json = """
                {"message": "Moment Deleted"}
                """;

        if (optionalMomentEntity.isPresent()) {
            momentRepository.deleteById(momentId);
            return json;
        } else {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }
    }

}
