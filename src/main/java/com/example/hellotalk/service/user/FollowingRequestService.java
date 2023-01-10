package com.example.hellotalk.service.user;

import com.example.hellotalk.model.user.FollowingRequest;

import java.util.UUID;

public interface FollowingRequestService {

    FollowingRequest getFollowingRequest(UUID followingRequestId);

    FollowingRequest createFollowingRequest(FollowingRequest followingRequest);
}
