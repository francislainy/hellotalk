package com.example.hellotalk.service.impl.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowshipDeletedException;
import com.example.hellotalk.exception.FollowshipNotCreatedUserCantFollowThemselfException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.mapper.FollowshipMapper;
import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.repository.followship.FollowshipRepository;
import com.example.hellotalk.repository.user.UserRepository;
import com.example.hellotalk.service.followship.FollowshipService;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.*;

@RequiredArgsConstructor
@Service
public class FollowshipServiceImpl implements FollowshipService {

    private final UserRepository userRepository;
    private final FollowshipRepository followshipRepository;

    private final UserService userService;
    private final FollowshipMapper followshipMapper;

    @Override
    public Followship getFollowship(UUID followshipId) {
        FollowshipEntity followshipEntity = followshipRepository.findById(followshipId)
                .orElseThrow(() -> new FollowshipNotCreatedUserCantFollowThemselfException(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION));

        return followshipMapper.toModel(followshipEntity);

    }

    @Override
    public List<Followship> getAllFollowships() {
        return followshipRepository.findAll()
                .stream()
                .map(followshipMapper::toModel)
                .toList();
    }

    @Override
    public List<Followship> getAllFollowshipsFromUser(UUID userFromId) {
        return followshipRepository.findFollowshipsByUserFromId(userFromId)
                .stream()
                .map(followshipMapper::toModel)
                .toList();
    }

    @Override
    public List<Followship> getAllFollowshipsToUser(UUID userToId) {
        return followshipRepository.findFollowshipsByUserToId(userToId)
                .stream()
                .map(followshipMapper::toModel)
                .toList();
    }

    @Override
    public Followship createFollowship(Followship followship) {

        UUID userToId = followship.getUserToId();
        UserEntity userFromEntity = userService.getCurrentUser();

        UserEntity userToEntity = userRepository.findById(userToId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        if (userFromEntity.getId().equals(userToEntity.getId())) {
            throw new FollowshipNotCreatedUserCantFollowThemselfException(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF);
        }

        UUID userFromId = userFromEntity.getId();
        followshipRepository.findByUserFromIdAndUserToId(userFromId, userToId)
                .ifPresent(f -> {
                    followshipRepository.delete(f);
                    throw new FollowshipDeletedException(FOLLOWSHIP_ALREADY_EXISTS_EXCEPTION);
                });

        FollowshipEntity followshipEntity = followshipRepository.save(followshipMapper.fromUserEntities(userFromEntity, userToEntity));

        return followshipMapper.toModel(followshipEntity);
    }
}
