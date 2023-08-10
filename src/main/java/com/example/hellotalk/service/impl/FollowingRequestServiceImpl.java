package com.example.hellotalk.service.impl;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowingRelationshipDeletedException;
import com.example.hellotalk.exception.FollowingRelationshipNotCreatedException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.repository.FollowingRequestRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.FollowingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.hellotalk.exception.AppExceptionHandler.*;
import static com.example.hellotalk.model.FollowingRequest.buildFollowingRequestFromEntity;

@RequiredArgsConstructor
@Service
public class FollowingRequestServiceImpl implements FollowingRequestService {

    final UserRepository userRepository;
    final FollowingRequestRepository followingRequestRepository;

    @Override
    public FollowingRequest getFollowingRequest(UUID followingRequestId) {

        Optional<FollowingRequestEntity> followingRequestEntity = followingRequestRepository.findById(followingRequestId);
        if (followingRequestEntity.isPresent()) {
            return FollowingRequest.builder()
                    .id(followingRequestId)
                    .userFromId(followingRequestEntity.get().getUserFromEntity().getId())
                    .userToId(followingRequestEntity.get().getUserToEntity().getId())
                    .build();
        } else {
            throw new FollowingRelationshipNotCreatedException(FOLLOWING_RELATIONSHIP_DOES_NOT_EXIST_EXCEPTION);
        }
    }

    @Override
    public List<FollowingRequest> getAllFollowingRequests() {

        List<FollowingRequest> followingRequestList = new ArrayList<>();
        List<FollowingRequestEntity> followingRequestEntityList = followingRequestRepository.findAll();

        if (!followingRequestEntityList.isEmpty()) {
            followingRequestEntityList.forEach(userEntity -> followingRequestList.add(buildFollowingRequestFromEntity(userEntity)));
        }

        return followingRequestList;
    }

    @Override
    public List<FollowingRequest> getAllFollowingRequestsFromUser(UUID userFromId) {
        List<FollowingRequest> followingRequestList = new ArrayList<>();
        List<FollowingRequestEntity> followingRequestEntityList = followingRequestRepository.findFollowingRequestEntitiesByUserFromId(userFromId);

        if (!followingRequestEntityList.isEmpty()) {
            followingRequestEntityList.forEach(userEntity -> followingRequestList.add(buildFollowingRequestFromEntity(userEntity)));
        }

        return followingRequestList;
    }

    @Override
    public List<FollowingRequest> getAllFollowingRequestsToUser(UUID userToId) {
        List<FollowingRequest> followingRequestList = new ArrayList<>();
        List<FollowingRequestEntity> followingRequestEntityList = followingRequestRepository.findFollowingRequestEntitiesByUserToId(userToId);

        if (!followingRequestEntityList.isEmpty()) {
            followingRequestEntityList.forEach(userEntity -> followingRequestList.add(buildFollowingRequestFromEntity(userEntity)));
        }

        return followingRequestList;
    }

    @Override
    public FollowingRequest createFollowingRequest(FollowingRequest followingRequest) {

        UUID userToId = followingRequest.getUserToId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userFromEntity = userRepository.findByUsername(username);

        Optional<UserEntity> userEntityOptionalTo = userRepository.findById(userToId);

        if (userFromEntity == null || userEntityOptionalTo.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        if (userFromEntity.getId().toString().equals(userEntityOptionalTo.get().getId().toString())) {
            throw new FollowingRelationshipNotCreatedException(USER_TO_AND_FROM_CANT_BE_THE_SAME);
        }

        UUID userFromId = userFromEntity.getId();
        Optional<FollowingRequestEntity> optionalFollowingRequest = followingRequestRepository.findByUserFromIdAndUserToId(userFromId, userToId);
        if (optionalFollowingRequest.isPresent()) {
            followingRequestRepository.delete(optionalFollowingRequest.get());
            throw new FollowingRelationshipDeletedException(FOLLOWING_RELATIONSHIP_ALREADY_EXISTS_EXCEPTION);
        }

        UserEntity userEntityTo = userEntityOptionalTo.get();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder().userFromEntity(userFromEntity).userToEntity(userEntityTo).build();
        followingRequestEntity = followingRequestRepository.save(followingRequestEntity);

        return FollowingRequest.builder()
                .id(followingRequestEntity.getId())
                .userFromId(followingRequestEntity.getUserFromEntity().getId())
                .userToId(followingRequestEntity.getUserToEntity().getId())
                .build();
    }
}
