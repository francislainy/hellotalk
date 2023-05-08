package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.hellotalk.Constants.USER_ID;
import static com.example.hellotalk.entity.moment.MomentEntity.buildMomentEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.moment.Moment.buildMomentFromEntity;
import static com.example.hellotalk.util.Utils.parseUUID;

@RequiredArgsConstructor
@Service
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public Moment getMoment(UUID momentId) {
        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        setLikesInfo(momentEntity);

        return buildMomentFromEntity(momentEntity);
    }

    @Override
    public List<Moment> getAllMoments() {

        List<MomentEntity> momentEntityList = momentRepository.findAll();

        return momentEntityList.stream()
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return buildMomentFromEntity(momentEntity);
                })
                .toList();
    }

    @Override
    public List<Moment> getAllMomentsForUser(UUID userId) {

        List<MomentEntity> momentEntityList = momentRepository.findAllByUserEntity_IdContains(userId);

        return momentEntityList.stream()
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return buildMomentFromEntity(momentEntity);
                })
                .toList();
    }

    @Override
    public Moment createMoment(Moment moment, String authorization) {

        UserEntity userEntity = userRepository.findById(parseUUID(authorization))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        MomentEntity momentEntity = buildMomentEntityFromModel(moment).toBuilder()
                .userEntity(userEntity)
                .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .build();
        momentEntity = momentRepository.save(momentEntity);

        setLikesInfo(momentEntity);

        return buildMomentFromEntity(momentEntity);
    }

    @Override
    public Moment updateMoment(UUID momentId, Moment moment) {

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        if (!momentEntity.getUserEntity().getId().toString().equals(USER_ID.toString())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));
        UserEntity userEntity = userRepository.findById(momentEntity.getUserEntity().getId()).get();

        momentEntity = momentEntity.toBuilder()
                .text(moment.getText())
                .tags(moment.getTags())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        momentEntity = momentRepository.save(momentEntity);

        setLikesInfo(momentEntity);

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

    private void setLikesInfo(MomentEntity momentEntity) {
        Integer numLikes = likeRepository.countLikesByMomentId(momentEntity.getId());
        momentEntity.setNumLikes(numLikes);

        List<LikeEntity> likeEntityList = likeRepository.findAllByMomentEntity_Id(momentEntity.getId());
        Set<UUID> likedByIds = new HashSet<>();
        likeEntityList.forEach(
                likeEntity -> likedByIds.add(likeEntity.getUserEntity().getId()));

        momentEntity.setLikedBy(likedByIds);
    }
}
