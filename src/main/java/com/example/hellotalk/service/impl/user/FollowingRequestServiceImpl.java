package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowerNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.FollowingRequest;
import com.example.hellotalk.repository.FollowingRequestRepository;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.FollowingRequestService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;

@Service
public class FollowingRequestServiceImpl implements FollowingRequestService {

    final UserRepository userRepository;
    final FollowingRequestRepository followingRequestRepository;

    public FollowingRequestServiceImpl(UserRepository userRepository, FollowingRequestRepository followingRequestRepository) {
        this.userRepository = userRepository;
        this.followingRequestRepository = followingRequestRepository;
    }

    @Override
    public FollowingRequest createFollowingRequest(FollowingRequest followingRequest) {

        UUID userToId = followingRequest.getUserToId();
        UUID userFromId = followingRequest.getUserFromId();

        Optional<UserEntity> userEntityOptionalFrom = userRepository.findById(userFromId);
        Optional<UserEntity> userEntityOptionalTo = userRepository.findById(userToId);

        if (userEntityOptionalFrom.isEmpty() || userEntityOptionalTo.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        if (userEntityOptionalTo.get().getFollowedByEntity() != null) {
            for (FollowingRequestEntity followingRequestEntity : userEntityOptionalTo.get().getFollowedByEntity()) {
                if (followingRequestEntity.getUserFromEntity().getId().equals(userFromId)) {
                    throw new FollowerNotFoundException("Already a Follower");
                }
            }
        }

        UserEntity userEntityTo = userEntityOptionalTo.get();
        UserEntity userEntityFrom = userEntityOptionalFrom.get();

        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder()
                .userToEntity(userEntityTo)
                .userFromEntity(userEntityFrom)
                .build();

        followingRequestEntity = followingRequestRepository.save(followingRequestEntity);

        return FollowingRequest.builder()
                .id(followingRequestEntity.getId())
                .userFromId(followingRequestEntity.getUserFromEntity().getId())
                .userToId(followingRequestEntity.getUserToEntity().getId())
                .build();
    }
}
