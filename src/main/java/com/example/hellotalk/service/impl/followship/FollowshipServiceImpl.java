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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        Optional<FollowshipEntity> followshipEntity = followshipRepository.findById(followshipId);
        if (followshipEntity.isPresent()) {
            return followshipMapper.toModel(followshipEntity.get());
        } else {
            throw new FollowshipNotCreatedUserCantFollowThemselfException(FOLLOWSHIP_DOES_NOT_EXIST_EXCEPTION);
        }
    }

    @Override
    public List<Followship> getAllFollowships() {

        List<Followship> followshipList = new ArrayList<>();
        List<FollowshipEntity> followshipEntityList = followshipRepository.findAll();

        if (!followshipEntityList.isEmpty()) {
            followshipEntityList.forEach(userEntity -> followshipList.add(followshipMapper.toModel(userEntity)));
        }

        return followshipList;
    }

    @Override
    public List<Followship> getAllFollowshipsFromUser(UUID userFromId) {
        List<Followship> followshipList = new ArrayList<>();
        List<FollowshipEntity> followshipEntityList = followshipRepository.findFollowshipsByUserFromId(userFromId);

        if (!followshipEntityList.isEmpty()) {
            followshipEntityList.forEach(userEntity -> followshipList.add(followshipMapper.toModel(userEntity)));
        }

        return followshipList;
    }

    @Override
    public List<Followship> getAllFollowshipsToUser(UUID userToId) {
        List<Followship> followshipList = new ArrayList<>();
        List<FollowshipEntity> followshipEntityList = followshipRepository.findFollowshipsByUserToId(userToId);

        if (!followshipEntityList.isEmpty()) {
            followshipEntityList.forEach(userEntity -> followshipList.add(followshipMapper.toModel(userEntity)));
        }

        return followshipList;
    }

    @Override
    public Followship createFollowship(Followship followship) {

        UUID userToId = followship.getUserToId();
        UserEntity userFromEntity = userService.getCurrentUser();

        Optional<UserEntity> userEntityOptionalTo = userRepository.findById(userToId);

        if (userFromEntity == null || userEntityOptionalTo.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        if (userFromEntity.getId().toString().equals(userEntityOptionalTo.get().getId().toString())) {
            throw new FollowshipNotCreatedUserCantFollowThemselfException(FOLLOWSHIP_NOT_CREATED_USER_CANT_FOLLOW_THEMSELF);
        }

        UUID userFromId = userFromEntity.getId();
        Optional<FollowshipEntity> optionalFollowship = followshipRepository.findByUserFromIdAndUserToId(userFromId, userToId);
        if (optionalFollowship.isPresent()) {
            followshipRepository.delete(optionalFollowship.get());
            throw new FollowshipDeletedException(FOLLOWSHIP_ALREADY_EXISTS_EXCEPTION);
        }

        UserEntity userToEntity = userEntityOptionalTo.get();
        FollowshipEntity followshipEntity = followshipRepository.save(followshipMapper.fromUserEntities(userFromEntity, userToEntity));

        return followshipMapper.toModel(followshipEntity);
    }
}
