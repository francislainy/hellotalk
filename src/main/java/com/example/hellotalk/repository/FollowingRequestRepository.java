package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowingRequestRepository extends JpaRepository<FollowingRequestEntity, UUID> {
}
