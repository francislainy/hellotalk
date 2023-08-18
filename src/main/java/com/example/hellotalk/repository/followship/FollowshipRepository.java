package com.example.hellotalk.repository.followship;

import com.example.hellotalk.entity.followship.FollowshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowshipRepository extends JpaRepository<FollowshipEntity, UUID> {

    String QUERY_FOLLOWSHIPS_SENT_BY_USER_FROM_ID = "SELECT * FROM FOLLOWSHIP WHERE user_from_id = :userFromId ORDER BY id";
    @Query(value = QUERY_FOLLOWSHIPS_SENT_BY_USER_FROM_ID, nativeQuery = true)
    List<FollowshipEntity> findFollowshipsByUserFromId(@Param("userFromId") UUID userFromId);

    String QUERY_FOLLOWSHIPS_RECEIVED_BY_USER_TO_ID = "SELECT * FROM FOLLOWSHIP WHERE user_to_id = :userToId ORDER BY id";
    @Query(value = QUERY_FOLLOWSHIPS_RECEIVED_BY_USER_TO_ID, nativeQuery = true)
    List<FollowshipEntity> findFollowingsByUserToId(@Param("userToId") UUID userToId);

    String QUERY_FOLLOWSHIPS_FROM_USER_ID_TO_USER_ID = "SELECT * FROM FOLLOWSHIP WHERE user_from_id = :userFromId and user_to_id = :userToId";
    @Query(value = QUERY_FOLLOWSHIPS_FROM_USER_ID_TO_USER_ID, nativeQuery = true)
    Optional<FollowshipEntity> findByUserFromIdAndUserToId(@Param("userFromId") UUID userFromId, @Param("userToId") UUID userToId);

}
