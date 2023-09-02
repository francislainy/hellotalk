package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentAlreadyLikedException;
import com.example.hellotalk.exception.MomentNotFoundException;
import com.example.hellotalk.exception.MomentNotYetLikedException;
import com.example.hellotalk.mapper.MomentMapper;
import com.example.hellotalk.model.ResultInfo;
import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.repository.LikeRepository;
import com.example.hellotalk.repository.moment.MomentRepository;
import com.example.hellotalk.service.moment.MomentService;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.*;

@RequiredArgsConstructor
@Service
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final LikeRepository likeRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final MomentMapper momentMapper;

    private final UserService userService;

    @Override
    public Moment getMoment(UUID momentId) {
        return momentRepository.findById(momentId)
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return momentMapper.toModel(momentEntity);
                })
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<Moment> getAllMoments() {
        return momentRepository.findAll().stream()
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return momentMapper.toModel(momentEntity);
                })
                .toList();
    }

    @Override
    public List<Moment> getAllMomentsForUser(UUID userId) {
        return momentRepository.findAllByUserEntityId(userId).stream()
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return momentMapper.toModel(momentEntity);
                })
                .toList();
    }

    @Override
    public Moment createMoment(Moment moment) {
        UserEntity userEntity = userService.getCurrentUser();

        MomentEntity momentEntity = momentMapper.toEntity(moment).toBuilder()
                .userEntity(userEntity)
                .creationDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC))
                .build();
        momentEntity = momentRepository.save(momentEntity);

        setLikesInfo(momentEntity); /// todo: check this: 25/08/2023

        return momentMapper.toModel(momentEntity);
    }

    @Override
    public Moment updateMoment(UUID momentId, Moment moment) {

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        UserEntity userEntity = userService.getCurrentUser();
        if (!userEntity.getId().equals(momentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));
        momentEntity = momentEntity.toBuilder()
                .content(moment.getContent())
                .tags(moment.getTags())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        momentEntity = momentRepository.save(momentEntity);

        setLikesInfo(momentEntity);

        return momentMapper.toModel(momentEntity);
    }

    @Override
    public void deleteMoment(UUID momentId) {

        MomentEntity momentEntity = momentRepository.findById(momentId).orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        UserEntity userEntity = userService.getCurrentUser();
        if (!userEntity.getId().equals(momentEntity.getUserEntity().getId())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        momentRepository.deleteById(momentId);
    }

    @Override
    public Map<String, Object> likeMoment(UUID momentId) {
        UserEntity userEntity = userService.getCurrentUser();
        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        Map<String, Object> map = new HashMap<>();
        likeRepository.findByUserEntityIdAndMomentEntityId(userEntity.getId(), momentId).ifPresentOrElse(
                likeEntity -> {
                    throw new MomentAlreadyLikedException(MOMENT_ALREADY_LIKED_EXCEPTION);
                },
                () -> {
                    LikeEntity likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();
                    likeEntity = likeRepository.save(likeEntity);
                    map.put("data", buildResultInfo(likeEntity));
                    map.put("message", "Moment liked successfully");
                });
        return map;
    }

    @Override
    public Map<String, Object> unlikeMoment(UUID momentId) {

        UserEntity userEntity = userService.getCurrentUser();

        if (!momentRepository.existsById(momentId)) {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }

        Map<String, Object> map = new HashMap<>();
        likeRepository.findByUserEntityIdAndMomentEntityId(userEntity.getId(), momentId).ifPresentOrElse(
                likeEntity -> {
                    likeRepository.delete(likeEntity);
                    map.put("data", buildResultInfo(likeEntity));
                    map.put("message", "Moment unliked successfully");
                },
                () -> {
                    throw new MomentNotYetLikedException(MOMENT_NOT_YET_LIKED_EXCEPTION);
                });

        return map;
    }

    @Override
    public List<LikeEntity> getLikesByMoment(UUID momentId) {
        return likeRepository.findAllByMomentEntityId(momentId);
    }

    private void setLikesInfo(MomentEntity momentEntity) {
        Integer numLikes = likeRepository.countByMomentEntityId(momentEntity.getId());
        momentEntity.setNumLikes(numLikes);
        List<LikeEntity> likeEntityList = likeRepository.findAllByMomentEntityId(momentEntity.getId());
        momentEntity.setLikes(new HashSet<>(likeEntityList));
    }

    private static ResultInfo buildResultInfo(LikeEntity likeEntity) {
        return ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();
    }
}
