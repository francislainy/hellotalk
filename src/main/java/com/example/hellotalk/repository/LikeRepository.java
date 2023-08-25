package com.example.hellotalk.repository;

import com.example.hellotalk.entity.user.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<LikeEntity, UUID> {

    Optional<LikeEntity> findByUserEntityIdAndMomentEntityId(UUID momentId, UUID userId);

    List<LikeEntity> findAllByMomentEntityId(UUID momentId);

    int countByMomentEntityId(UUID momentId);
}
