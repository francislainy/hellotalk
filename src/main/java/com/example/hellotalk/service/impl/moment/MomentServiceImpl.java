package com.example.hellotalk.service.impl.moment;

import com.example.hellotalk.entity.moment.MomentEntity;
import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.EntityDoesNotBelongToUserException;
import com.example.hellotalk.exception.MomentNotFoundException;
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

import static com.example.hellotalk.exception.AppExceptionHandler.ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION;
import static com.example.hellotalk.exception.AppExceptionHandler.MOMENT_NOT_FOUND_EXCEPTION;

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
        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        setLikesInfo(momentEntity);

        return momentMapper.toModel(momentEntity);
    }

    @Override
    public List<Moment> getAllMoments() {

        List<MomentEntity> momentEntityList = momentRepository.findAll();

        return momentEntityList.stream()
                .map(momentEntity -> {
                    setLikesInfo(momentEntity);
                    return momentMapper.toModel(momentEntity);
                })
                .toList();
    }

    @Override
    public List<Moment> getAllMomentsForUser(UUID userId) {

        List<MomentEntity> momentEntityList = momentRepository.findAllByUserEntityId(userId);

        return momentEntityList.stream()
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

        setLikesInfo(momentEntity);

        return momentMapper.toModel(momentEntity);
    }

    @Override
    public Moment updateMoment(UUID momentId, Moment moment) {

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        UserEntity userEntity = userService.getCurrentUser();

        if (!userEntity.getId().toString().equals(momentEntity.getUserEntity().getId().toString())) {
            throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
        }

        ZonedDateTime formattedDate = ZonedDateTime.parse(ZonedDateTime.now().format(formatter));

        momentEntity = momentEntity.toBuilder()
                .text(moment.getText())
                .tags(moment.getTags())
                .userEntity(userEntity)
                .lastUpdatedDate(formattedDate)
                .build();

        momentEntity = momentRepository.save(momentEntity);

        setLikesInfo(momentEntity);

        return momentMapper.toModel(momentEntity);
    }

    @Override
    public String deleteMoment(UUID momentId) {

        Optional<MomentEntity> optionalMomentEntity = momentRepository.findById(momentId);
        String json = """
                {"message": "Moment Deleted"}
                """;

        if (optionalMomentEntity.isPresent()) {
            MomentEntity momentEntity = optionalMomentEntity.get();
            UserEntity userEntity = userService.getCurrentUser();

            if (!userEntity.getId().toString().equals(momentEntity.getUserEntity().getId().toString())) {
                throw new EntityDoesNotBelongToUserException(ENTITY_DOES_NOT_BELONG_TO_USER_EXCEPTION);
            } else {
                momentRepository.deleteById(momentId);
                return json;
            }

        } else {
            throw new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public Map<String, Object> likeMoment(UUID momentId) {

        UserEntity userEntity = userService.getCurrentUser();

        MomentEntity momentEntity = momentRepository.findById(momentId)
                .orElseThrow(() -> new MomentNotFoundException(MOMENT_NOT_FOUND_EXCEPTION));

        Map<String, Object> map = new HashMap<>();
        Optional<LikeEntity> likeEntityOptional = Optional.ofNullable(likeRepository.findByUserEntity_IdAndMomentEntity_Id(userEntity.getId(), momentId));
        LikeEntity likeEntity;
        String resultMessage;

        Set<LikeEntity> likes = momentEntity.getLikes();
        if (likeEntityOptional.isPresent()) {
            likeEntity = likeEntityOptional.get();
            likeRepository.delete(likeEntity);
            likes.removeIf(like -> like.getUserEntity().getId().equals(userEntity.getId()));
            resultMessage = "Moment unliked successfully";
        } else {
            likeEntity = LikeEntity.builder().userEntity(userEntity).momentEntity(momentEntity).build();
            likeEntity = likeRepository.save(likeEntity);
            likes.add(likeEntity);
            resultMessage = "Moment liked successfully";
        }
        momentEntity.setLikes(likes);
        momentRepository.save(momentEntity);

        ResultInfo resultInfo = ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();

        map.put("data", resultInfo);
        map.put("message", resultMessage);

        return map;
    }

    @Override
    public List<LikeEntity> getLikesByMoment(UUID momentId) {
        return likeRepository.findAllByMomentEntityIdContaining(momentId);
    }

    private void setLikesInfo(MomentEntity momentEntity) {
        Integer numLikes = likeRepository.countLikesByMomentId(momentEntity.getId());
        momentEntity.setNumLikes(numLikes);
        List<LikeEntity> likeEntityList = likeRepository.findAllByMomentEntity_Id(momentEntity.getId());
        momentEntity.setLikes(new HashSet<>(likeEntityList));
    }
}
