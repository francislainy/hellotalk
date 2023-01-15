package com.example.hellotalk.service.user;

import com.example.hellotalk.model.user.FollowingRequest;

import java.util.List;
import java.util.UUID;

public interface FollowingRequestService {

    FollowingRequest getFollowingRequest(UUID followingRequestId);

    List<FollowingRequest> getAllFollowingRequests();

    List<FollowingRequest> getAllFollowingRequestsFromUser(UUID userFromId);

    List<FollowingRequest> getAllFollowingRequestsToUser(UUID userToId);

    FollowingRequest createFollowingRequest(FollowingRequest followingRequest);
}
