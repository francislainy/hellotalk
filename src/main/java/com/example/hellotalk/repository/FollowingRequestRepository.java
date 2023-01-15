package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowingRequestRepository extends JpaRepository<FollowingRequestEntity, UUID> {

    String QUERY_FOLLOWING_REQUESTS_BY_USER_TO_ID = "SELECT * FROM following_request WHERE user_to_id = :userToId ORDER BY id";

    @Query(value = QUERY_FOLLOWING_REQUESTS_BY_USER_TO_ID, nativeQuery = true)
    List<FollowingRequestEntity> findFollowingRequestEntitiesByUserToId(UUID userToId);

}
