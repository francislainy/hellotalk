package com.example.hellotalk.repository.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowshipRepository extends JpaRepository<FollowshipEntity, UUID> {

    List<FollowshipEntity> findByUserFromEntityIdOrderByUserFromEntityId(UUID userFromId);

    default List<FollowshipEntity> findFollowshipsByUserFromId(UUID userFromId) {
        return findByUserFromEntityIdOrderByUserFromEntityId(userFromId);
    }

    List<FollowshipEntity> findByUserToEntityIdOrderByUserToEntityId(UUID userToId);

    default List<FollowshipEntity> findFollowshipsByUserToId(UUID userFromId) {
        return findByUserToEntityIdOrderByUserToEntityId(userFromId);
    }

    Optional<FollowshipEntity> findByUserFromEntityIdAndUserToEntityId(UUID userFromId, UUID userToId);

    default Optional<FollowshipEntity> findByUserFromIdAndUserToId(UUID userFromId, UUID userToId) {
        return findByUserFromEntityIdAndUserToEntityId(userFromId, userToId);
    }

}
