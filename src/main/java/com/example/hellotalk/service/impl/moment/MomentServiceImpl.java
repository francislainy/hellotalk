package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.moment.MomentService;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.entity.moment.MomentEntity.buildMomentEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;
import static com.example.hellotalk.model.moment.Moment.buildMomentFromEntity;

@Service
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final LikeRepository likeRepository;

    public MomentServiceImpl(MomentRepository momentRepository, LikeRepository likeRepository) {
        this.momentRepository = momentRepository;
        this.likeRepository = likeRepository;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public Moment getMoment(UUID momentId) {
        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        return buildMomentFromEntity(momentEntity);
    }

    @Override
    public List<Moment> getAllMoments() {

        List<MomentEntity> momentEntityList = momentRepository.findAll();

        return momentEntityList.stream()
                .map(Moment::buildMomentFromEntity)
                .toList();
    }

    @Override
    public List<Moment> getAllMomentsForUser(String authorization) {

        List<MomentEntity> momentEntityList = momentRepository.findAllByUserEntity_IdContains(authorization);

        return momentEntityList.stream()
                .map(Moment::buildMomentFromEntity)
                .toList();
    }

    @Override
    public Moment createMoment(Moment moment, String authorization) {
        UserEntity userEntity = UserEntity.builder().id(parseUUID(authorization)).build();

        MomentEntity momentEntity = buildMomentEntityFromModel(moment);
        momentEntity.setUserEntity(userEntity);
        momentEntity.setCreationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC));
        momentEntity = momentRepository.save(momentEntity);

        return buildMomentFromEntity(momentEntity);
    }

    @Override
    public Moment updateMoment(UUID momentId, Moment moment, String authorization) {

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        if (!momentEntity.getUserEntity().getId().toString().equals(authorization)) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime formattedDate = ZonedDateTime.parse(now.format(formatter));
        UserEntity userEntity = UserEntity.builder().id(moment.getUserCreatorId()).build();

        momentEntity = momentEntity.toBuilder()
                .text(moment.getText())
                .tags(moment.getTags())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        momentEntity = momentRepository.save(momentEntity);
        return buildMomentFromEntity(momentEntity);
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

    @Override
    public List<LikeEntity> getLikesByMoment(UUID momentId) {
        return likeRepository.findAllByMomentEntityIdContaining(momentId);
    }

    private UUID parseUUID(String uuidStr) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + uuidStr);
        }
    }
}
