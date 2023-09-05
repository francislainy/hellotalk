package com.example.hellotalk.service.followship;

import com.example.hellotalk.model.followship.Followship;

import java.util.List;
import java.util.UUID;

public interface FollowshipService {

    Followship getFollowship(UUID followshipId);

    List<Followship> getAllFollowships();

    List<Followship> getAllFollowshipsFromUser(UUID userFromId);

    List<Followship> getAllFollowshipsToUser(UUID userToId);

    Followship createFollowship(Followship followship);

    void deleteFollowship(UUID followshipId);
}
