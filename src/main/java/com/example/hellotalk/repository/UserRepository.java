package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    String SELECT_FROM_USERS_WHERE_FOLLOWED_BY_ID_ORDER_BY_ID = "SELECT * FROM users WHERE follower_id = :followerId ORDER BY id";
    String SELECT_FROM_USERS_WHERE_FOLLOWER_OF_ID_ORDER_BY_ID = "SELECT * FROM users WHERE followed_by_id = :followerId ORDER BY id";

    @Query(value = SELECT_FROM_USERS_WHERE_FOLLOWER_OF_ID_ORDER_BY_ID, nativeQuery = true)
    List<UserEntity> findAllByFollowerOf(UUID followerId);

    @Query(value = SELECT_FROM_USERS_WHERE_FOLLOWED_BY_ID_ORDER_BY_ID, nativeQuery = true)
    List<UserEntity> findAllByFollowedBy(UUID followerId);
}
